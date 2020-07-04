package ch.epfl.javass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import ch.epfl.javass.gui.GraphicalPlayer;

/**
 * Class which represents a client
 * @author Remi Delacourt, Liam Mouzaoui
 */
public class ChatClient {

    private Socket s;
    private DataInputStream dis;
    private DataOutputStream dos;
    private GraphicalPlayer m;

    /**
     * Constructor of ChatClient
     * @param m (GraphicalPlayer): the GraphicalPlayer to have access to method writeOnPane
     * @throws IOException when Input/Output error
     */
    public ChatClient(GraphicalPlayer m, String str) throws IOException {
        s = new Socket(str, ChatServer.PORT);
        dis = new DataInputStream(s.getInputStream());
        dos = new DataOutputStream(s.getOutputStream()); 
        this.m = m;
    }

    /**
     * Infinite loop to read the messages received
     */
    public void run() {
        String received;
        try { 
            while (true) {
                received  = dis.readUTF();
                m.writeOnPane(received);
            }
        } catch(IOException e) { 
            e.printStackTrace(); 
        } 
    } 

    /**
     * Sends a message to the server
     * @param s (String): the message to send
     */
    public void writeSomething(String s) {
        try {
            dos.writeUTF(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

} 
