package server.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author giorgost
 * @author koszio
 */
public class WordGenerator {

    private final String path = "/home/koszio/NetworkProgramming_ID1212_SourceCodes/Assignment_2/HangmanGame/src/server/model/words.txt";

    public String guessingWord() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        ArrayList<String> words = new ArrayList();

        while (br.readLine() != null) {
            words.add(br.readLine().toString());
        }

        br.close();
        String word = getRandomWord(words).toLowerCase();
        return word;
    }

    private String getRandomWord(ArrayList<String> list) {
        return list.get((int) (Math.random() * list.size()));
    }
}