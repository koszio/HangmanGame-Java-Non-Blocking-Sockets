/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.net;

import java.net.InetSocketAddress;

/**
 * @author giorgost
 * @author koszio
 */
public interface CommunicationListener {

    /**
     *
     * Outgoing messages towards client from server
     */
    public void printToTerminal(String output);

    public void connected(InetSocketAddress address);

    public void disconnected();

}
