package ch.epfl.javass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Server which manages the chatroom
 * @author Remi Delacourt, Liam Mouzaoui
 */
public class ChatServer { 

    public final static int PORT = 5100;
    
    //Saves the connections to have access to their DataOutputStream
    private static List<ClientHandler> threadList = new ArrayList<>();

    /**
     * Empty constructor
     */
    public ChatServer() {}
    
    /**
     * Runs a server which accepts connections, to connect the players
     * @throws IOException when connection error
     */
    public void run() throws IOException {

        ServerSocket ss = new ServerSocket(PORT); 

        // client request 
        while (true) {
            Socket s = null; 

            try { 
                s = ss.accept(); 
                System.out.println("Connection"); 

                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 

                ClientHandler t = new ClientHandler(s, dis, dos); 

                threadList.add(t);

                t.start();

            } catch (Exception e) { 
                s.close(); 
                ss.close();
                e.printStackTrace(); 
            } 
        } 
    }

    /**
     * Method which allows to send messages received to all the players
     * @param message (String) : the message to send to all the players
     */
    public static void writeOnThreads(String message) {
        try {
            for(ClientHandler t : threadList) 
                t.dos().writeUTF(message);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
} 
