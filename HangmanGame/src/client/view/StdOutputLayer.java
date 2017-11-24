
package client.view;

/**
 * @author giorgost
 * @author koszio
 */
public class StdOutputLayer {
    
    synchronized void print(String out) {
        System.out.print(out);
    }

  
    synchronized void println(String out) {
        System.out.println(out);
    }
    
}
