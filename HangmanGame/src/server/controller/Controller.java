/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import java.io.IOException;
import server.model.HangmanGame;
/**
 * @author koszio
 * @author giorgost
 */
public class Controller {
    
 private HangmanGame hg;

    public Controller() throws IOException {
        hg = new HangmanGame();
    }

    public String sendInput(String input) throws IOException {
       return hg.playGame(input);       
    }
    
}