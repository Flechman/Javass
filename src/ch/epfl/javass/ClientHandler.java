package ch.epfl.javass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Intermediate class to link the ChatServer and the ChatClients
 * @author Remi Delacourt, Liam Mouzaoui
 */
class ClientHandler extends Thread { 

    private final DataInputStream dis; 
    private final DataOutputStream dos; 
    private final Socket s; 

    /**
     * Constructor of ClientHandler
     * @param s (Socket): socket of the player
     * @param dis (DataInputStream): reader of the player
     * @param dos (DataOutputStream): writer of the player
     */
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
    } 

    /**
     * Override of the method run of Thread
     */
    @Override
    public void run() { 
        
        String received; 
        while (true) { 
            try { 

                // receive the messages from clients
                received = dis.readUTF();
                ChatServer.writeOnThreads(received);

            } catch (IOException e) {
                try { 
                    this.dis.close(); 
                    this.dos.close(); 

                } catch(IOException e1) { 
                    e1.printStackTrace(); 
                } 
            } 
        } 
    } 

    /**
     * Getter for the writer of the player
     * @return (DataOutputStream): the writer of the player
     */
    public DataOutputStream dos() {
        return dos;
    }
}