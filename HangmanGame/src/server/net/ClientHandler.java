
package server.net;



import com.sun.corba.se.spi.activation.Server;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import java.util.Queue;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.controller.Controller;

import java.util.ArrayDeque;

/**
 * @author koszio
 * @author giorgost
 */
public class ClientHandler implements Runnable {

    private final ByteBuffer inputFromClient = ByteBuffer.allocateDirect(10000);
    private final Queue<String> inputReadyToProcess = new ArrayDeque<>();
    private final Queue<ByteBuffer> outputReadyForClient = new ArrayDeque<>();
    private SocketChannel clientChannel;
    private Controller controller;
    private Selector selector;
    private SelectionKey selectionKey;
    
    
    ClientHandler(SocketChannel clientChannel) throws IOException {
        controller = new Controller();
        this.clientChannel = clientChannel;
    }
    
    @Override
    public void run() {
        //System.out.println(inputReadyForHandling.remove());
        while (!inputReadyToProcess.isEmpty()) {
            try {
                outputReadyForClient.add(ByteBuffer.wrap(controller.sendInput(inputReadyToProcess.remove()).getBytes()));
                selectionKey.interestOps(selectionKey.OP_WRITE);
                selector.wakeup();
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * 
     * @throws IOException If failed to read message
     */
    void receiveInput(SelectionKey selectionKey, Selector selector) throws IOException {
        this.selectionKey = selectionKey;
        this.selector = selector;
        inputFromClient.clear();
        int numOfReadBytes;
        numOfReadBytes = clientChannel.read(inputFromClient);
        if(numOfReadBytes == -1) {
            throw new IOException("The connection with the Client has been closed");
        }
        String receivedInput = extractMessageFromBuffer();
        inputReadyToProcess.add(receivedInput);
        ForkJoinPool.commonPool().execute(this);
    }

    private String extractMessageFromBuffer() {
        inputFromClient.flip();
        byte[] bytes = new byte[inputFromClient.remaining()];
        inputFromClient.get(bytes);
        return new String(bytes);
    }

    
    void sendServerOutput() throws IOException {
        while(!outputReadyForClient.isEmpty()) {
            ByteBuffer output = outputReadyForClient.remove();
            clientChannel.write(output);
            if(output.hasRemaining()) {
                throw new IOException("Could not send message");
            }
        }
        //clientChannel.write(msg);
        //msg.hasRemaining();
             
    }

    void disconnectClient() throws IOException {
        clientChannel.close();
    }
    
    
}   