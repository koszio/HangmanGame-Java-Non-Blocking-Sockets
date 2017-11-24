package server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import server.net.ClientHandler;
import server.model.WordGenerator;

/**
 * @author koszio
 * @author giorgost
 */
public class ServerConnection {

    private static final int LINGER_TIME = 5000;
    private int defaultPort = 55555;
    private Selector selector;
    private ServerSocketChannel listeningSocketChannel;
    WordGenerator model = new WordGenerator();

    public static void main(String[] args) {
        ServerConnection sc = new ServerConnection();
        sc.serve();
    }

    /*  One thread to rule them all!!Only one thread handles all the communication. 
     */
    private void serve() {
        try {
            initSelector();
            initListeningSocketChannel();

            while (true) {
                selector.select(); 
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        startClientHandler(key);
                    } else if (key.isReadable()) {
                        fromClient(key); 
                    } else if (key.isWritable()) {
                        toClient(key);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    
    //Creates the selector
    private void initSelector() throws IOException {
        selector = Selector.open();
    }

   
    private void initListeningSocketChannel() throws IOException {
        listeningSocketChannel = ServerSocketChannel.open();
        listeningSocketChannel.configureBlocking(false);
        listeningSocketChannel.bind(new InetSocketAddress(defaultPort));
        listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void startClientHandler(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        ClientHandler clientHandler = new ClientHandler(clientChannel);
        clientChannel.register(selector, SelectionKey.OP_READ, new Client(clientHandler));
        clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME);
    }

    private void fromClient(SelectionKey key) throws IOException {
        Client client = (Client) key.attachment();
        try {
            client.clientHandler.receiveInput(key,selector);
            //key.interestOps(SelectionKey.OP_WRITE);
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }

    }

    private void toClient(SelectionKey key) {
        Client client = (Client) key.attachment();
        try {
            client.clientHandler.sendServerOutput();
            key.interestOps(SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeClient(SelectionKey clientKey) throws IOException {
        Client client = (Client) clientKey.attachment();
        client.clientHandler.disconnectClient();
        clientKey.cancel();
    }

    // referece to the ClientHandler object.
    private class Client {

        private final ClientHandler clientHandler;

        private Client(ClientHandler clientHandler) {
            this.clientHandler = clientHandler;
        }
    }
}
