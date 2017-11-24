/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import java.io.IOException;

/**
 * @author koszio
 * @author giorgost
 */
public class HangmanGame {
    
    private int remainingMisses;
    private int score = 0;
    private String word;
    private String result;  
    private StringBuilder sb;
    private WordGenerator wordGenerator;
    Status playerStatus;
    
    public HangmanGame() throws IOException {
        wordGenerator = new WordGenerator();
        score = 0;
        generateWord();
        dashedWord();
        
        System.out.println("wordgenerated is : "+ word );
    
    }
    
    
    private void dashedWord() { 
        sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            sb.append('-');
        }
       
        playerStatus = new Status(remainingMisses, sb.toString());
    }
        
    private void generateWord() throws IOException {
        word = wordGenerator.guessingWord();
        remainingMisses = word.length();
    }
    
    
    
    private void pickNewWord() throws IOException {
        generateWord();
        dashedWord();
    }

    public String playGame(String guessFromClient) throws IOException {
        System.out.println("Word chosen: " + word);
        System.out.println("User typed: " + guessFromClient);
 if(playerStatus.getMisses()!=0){
                if (guessFromClient.length() == 1) {
                    
                    if (word.contains(guessFromClient)) {
                        char[] arrayWord = playerStatus.getWord().toLowerCase().toCharArray();
                        for (int i = 0; i < word.length(); i++) {
                            if (word.charAt(i) == guessFromClient.charAt(0)) {
                                arrayWord[i] = guessFromClient.charAt(0);
                                
                            }
                        }
                        guessFromClient = new String(arrayWord);
                        if (guessFromClient.equals(word)) {
                            score++;
                            playerStatus = new Status(remainingMisses,word);
                            result = "You won. Word: " + this.word +" | " + "no value" + " | " + score;
                            pickNewWord();
                            return result;
                        }
                        playerStatus = new Status(remainingMisses, guessFromClient);
                        result = playerStatus.getWord()+ " | " + playerStatus.getMisses() + " | " + score;
                    } else {
                        remainingMisses--;
                        guessFromClient = playerStatus.getWord();
                        playerStatus = new Status(remainingMisses, guessFromClient);
                        result = playerStatus.getWord()+ " | " + playerStatus.getMisses() + " | " + score;
                    }
                    return result;
                } else if (word.equals(guessFromClient)) {
                    guessFromClient = word;
                    score++;
                    playerStatus = new Status(remainingMisses,word);
                    result = "You won. Word: " + this.word+  " | " + "no value" + " | " + score;
                    pickNewWord();
                    return result;
                } else if (!word.equals(guessFromClient)) {
                    remainingMisses--;
                    playerStatus = new Status(remainingMisses,playerStatus.getWord());
                    result = playerStatus.getWord()+ " | " + playerStatus.getMisses() + " | " + score;
                    return result;
                }
        }
            score--;
            result = "You lost. The word was: " +playerStatus.getWord() +  " | " + "no value" + " | " + score;
            pickNewWord();
            return result;         
    }   
    
      
}