/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.net;


import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.net.InetSocketAddress;
import static java.nio.channels.SelectionKey.OP_CONNECT;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ForkJoinPool;
import java.util.ArrayDeque;
import java.util.Queue;

import java.util.concurrent.Executor;

/**
 * @author giorgost
 * @author koszio
 */
public class ConnectionWithServer implements Runnable{

 private InetSocketAddress serverAddress;
    private final Queue<ByteBuffer> incomingQueue = new ArrayDeque<>();
    private final ByteBuffer outputFromServer = ByteBuffer.allocateDirect(10000);
    private final Queue<String> outcomingToClient = new ArrayDeque<>();
    private CommunicationListener comListen;
   
    private SocketChannel socketChannel;
    private Selector selector;
    private boolean inputReadyForSending = false;
        private boolean connected;



    @Override
    public void run() {
        try {
            initConnection();
            initSelector();

            while(connected) {
             if(inputReadyForSending) {
                 socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                 inputReadyForSending = false;
             }

             selector.select();
             for(SelectionKey key : selector.selectedKeys()) {
                 selector.selectedKeys().remove(key);
                if(!key.isValid()) {
                     continue;
                 }
                 if(key.isConnectable()) {
                    completeConnection(key);
                 } else if(key.isWritable()) {
                     sendInputToServer(key);
                 } else if(key.isReadable()) {
                    rcvFromServer();
                 }
             }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectToServer(String host, int port){
        serverAddress = new InetSocketAddress(host, port);
        new Thread(this).start();
    }

    private void initSelector() throws IOException {
        selector = Selector.open();
        socketChannel.register(selector, OP_CONNECT);
    }

    private void initConnection() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
        connected = true;
    }

    private void completeConnection(SelectionKey key) throws IOException {
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_READ);
        try {
            InetSocketAddress remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
            notifyConnectionDone(remoteAddress);
        } catch (IOException CouldNotGetRemote) {
            notifyConnectionDone(serverAddress);
        }
    }
    
    private void sendInputToServer(SelectionKey key) throws IOException {
        ByteBuffer input;
        synchronized(incomingQueue) {
            while((input = incomingQueue.peek()) != null) {
                socketChannel.write(input);
                if(input.hasRemaining()) {
                    return;
                }
                incomingQueue.remove();
            }
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    public void sendClientInput(String input) {
        synchronized (incomingQueue) {
            incomingQueue.add(ByteBuffer.wrap(input.getBytes()));
        }
        inputReadyForSending = true;
        selector.wakeup();
    }

    private void rcvFromServer() throws IOException {
        outputFromServer.clear();
        int numOfReadBytes;
        numOfReadBytes = socketChannel.read(outputFromServer);
        if(numOfReadBytes == -1) {
            throw new IOException("Error while receiving message.");
        }
        String receivedInput = extractOutputFromBuffer();
        outcomingToClient.add(receivedInput);
        while(!outcomingToClient.isEmpty()) {
            sendServerOutput(outcomingToClient.remove());
        }
    }

    private String extractOutputFromBuffer() {
        outputFromServer.flip();
        byte[] bytes = new byte[outputFromServer.remaining()];
        outputFromServer.get(bytes);
        return new String(bytes); 
        
    }
        
        
    public void disconnect() throws IOException {
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
        connected = false;
        notifyDisconnectionDone();

    }

    public void addOutputHandler(CommunicationListener comListen) {
        this.comListen = comListen;
    }

    private void sendServerOutput(String output) {
        Executor pool = ForkJoinPool.commonPool();
        pool.execute(() -> {
            comListen.printToTerminal(output);
        });
    }

    private void notifyConnectionDone(InetSocketAddress address) {
        Executor pool = ForkJoinPool.commonPool();
        pool.execute(() -> comListen.connected(address));
    }

    private void notifyDisconnectionDone() {
        Executor pool = ForkJoinPool.commonPool();
        pool.execute(() -> comListen.disconnected());}


    
}
