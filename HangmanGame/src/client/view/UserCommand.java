/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

/**
 * @author giorgost 
 * @author koszio
 */
public enum UserCommand {
        /**
         * Guess command
         */
        GUESS ,
        
        /** 
         * Command for connection establishment with the server. Its done by giving an IP address and the port number.
         */
        CONNECT ,
        
        /**
         * quit for quitting the game.
         */
        QUIT ,
    
    
}
