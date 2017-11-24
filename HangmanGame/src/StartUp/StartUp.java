/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StartUp;

import client.view.InputTranslator;

/**
 * @author giorgost
 * @author koszio
 */
public class StartUp {
    
        public static void main(String[] args) {
        new InputTranslator().start();
    }

    /*public static void main(String[] args) throws IOException, ClassNotFoundException {
        ClientConnection cc = new ClientConnection();
        cc.connect("127.0.0.1", 8888);
        cc.playGame();   
    }*/
    

}
