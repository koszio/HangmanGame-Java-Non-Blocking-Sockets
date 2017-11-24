package client.view;

import client.net.ConnectionWithServer;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;
import client.net.CommunicationListener;

/**
 * @author giorgost
 * @author koszio
 */
public class InputTranslator implements Runnable{

  private static final String PROMPTLINE = "command line > ";
    private final Scanner in = new Scanner(System.in);
    private boolean receivingCommandsTrue = false;
    private ConnectionWithServer server;
    private final StdOutputLayer outGoingMessage = new StdOutputLayer();

    /**
     * The interpreter waits for client/user input 
     */
    public void start() {
        if (receivingCommandsTrue) {
            return;
        }
        System.out.println(welcomeMsg());
        receivingCommandsTrue = true;
        server = new ConnectionWithServer();
        new Thread(this).start();
    }

    @Override
    public void run() {
        while(receivingCommandsTrue) {
            try {
                UserInput userInput = new UserInput(readingInput());
                switch(userInput.getCommand()) {
                    case CONNECT :
                        server.addOutputHandler(new OutputHandler());
                        server.connectToServer("127.0.0.1", Integer.parseInt(userInput.getPortNumber()));
                        break;
                    case GUESS:
                        server.sendClientInput(userInput.getIPAddress());
                        break;
                    case QUIT:
                        receivingCommandsTrue = false;
                        server.disconnect();
                        break;
                }
            } catch (UnknownCommandException | BadIputException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readingInput() {
        outGoingMessage.print(PROMPTLINE);
        return in.nextLine();
    }
    
    
    
    

    private class OutputHandler implements CommunicationListener {
        @Override
        public void printToTerminal(String output) {
            outGoingMessage.println(output);
            outGoingMessage.print(PROMPTLINE);

        }

        @Override
        public void connected(InetSocketAddress address) {
            printToTerminal("Connected to " + address.getHostName() + ":" + address.getPort());
        }

        @Override
        public void disconnected() {
            printToTerminal("Game over!");
        }
    }

    private String welcomeMsg() {
        return "Connect by typing the following: connect \"ip-adress\" \"55555\"\n" +
               "Play the game by guessing a letter or word by typing the word Guess followed by the letter"
                + "or the word you want to Guessa word by writing guess plus the\"letter/word you want to guess\"\n" +
                "Finally, if you wish to exit type quit ";
    }
}
