/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import java.util.Arrays;

/**
 * @author giorgost 
 * @author koszio
 */
public class UserInput {
    private final String givenInput;
    private static final String PARAM_DELIMITER = " ";

    private String userCmd;
    private String[] params;
    private UserCommand userCommand;

    UserInput(String userInput) throws UnknownCommandException, BadIputException {
        this.givenInput = userInput;
        params = new String[3];
        splitedParts(givenInput);
        parseCommand(userCmd);
    }

    private void splitedParts(String enteredInput) {
        if(enteredInput == null) {
            userCmd = null;
            params = null;
            return;
        }
        String[] part = enteredInput.split(PARAM_DELIMITER);
        userCmd = part[0].toUpperCase();
        params = Arrays.copyOfRange(part,1,part.length);
    }

    private void parseCommand(String userCmd) throws UnknownCommandException, BadIputException {
        if(userCmd == null) {
            throw new  BadIputException("Incorrect input, please enter COMMAND parameter");
        }

        switch (userCmd) {

            case "CONNECT":
                userCommand = UserCommand.CONNECT;
                break;

            case "GUESS":
                userCommand = UserCommand.GUESS;
                break;
            case "QUIT":
                userCommand = UserCommand.QUIT;
                break;
            default:
                throw new UnknownCommandException("Unknown command " + userCmd + " Try again");
        }
    }

    UserCommand getCommand() {
        return userCommand;
    }

    String getIPAddress() {
        return params[0];
    }

    String getPortNumber() {
        return params[1];
    }
    
}
