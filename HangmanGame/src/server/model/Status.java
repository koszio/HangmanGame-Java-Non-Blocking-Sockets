/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

/**
 *
 * @author giorgost
 * @author koszio
 */

/**
 * This class holds the number of the remaining misses of the player and the
 * letters that he has found in the chosen guessing word.
 */
public class Status {

    private final int remainingMisses;
    private final String word;

    public Status(int remainingMisses, String word) {
        this.remainingMisses = remainingMisses;
        this.word = word;
    }

    public int getMisses() {
        return remainingMisses;
    }

    public String getWord() {
        return word;
    }
}