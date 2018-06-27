import java.awt.Rectangle;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

public class GameServer {
    private ServerSocket serverSockets[] = new ServerSocket[2];
    private Socket playerSockets[] = new Socket[2];
    Player players[] = new Player[2];
    Platform platforms[] = new Platform[7];
    int playerIndex = 0;
    ArrayList<Session> playerSessions = new ArrayList<>();
    ArrayList<InetAddress> playerIPs = new ArrayList<>();
    boolean isGameStart = false;

    public GameServer(int port, InfoPacket p) {
        /* Initialize */
        System.out.println("Server started!");
        for (int i = 0; i < players.length; i++) 
            this.players[i] = p.players[i];
        for (int i = 0; i < platforms.length; i++)
            this.platforms[i] = p.platforms[i];
        for (int i = 0; i < serverSockets.length; i++)
            try {
                serverSockets[i] = new ServerSocket(port+i);
            } catch(IOException e) {
                System.out.println(e.getMessage());
            }
            
        /* Start connecting */
        try {
            while(true) {
                playerSockets[playerIndex] = serverSockets[playerIndex].accept();
                playerSockets[playerIndex].setSoTimeout(5);
                playerIPs.add(playerSockets[playerIndex].getInetAddress());

                System.out.println("Accept: " + playerSockets[playerIndex].getInetAddress());
                playerIndex++;

                if (playerIndex == 2) {
                    System.out.println(playerIPs.get(0));
                    System.out.println(playerIPs.get(1));
                    System.out.println("There are two players, Game start!!!");
                    break;
                }
            } 

            // let server engine start
            this.isGameStart = true;
            // let client threads works
            p.isPlaying = true;

            /* Session start */
            for (int i = 0; i < playerSockets.length; i++) {
                playerSessions.add(new Session(playerSockets[i], this.players, this.platforms, p, i));
                Thread gameThread = new Thread(playerSessions.get(i));
                System.out.println("Thread-" + String.valueOf(i) + " start!");
                gameThread.start();
            }

        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

class Session implements Runnable {
    private Socket socket;
    private Player players[] = new Player[2];
    private Platform platforms[] = new Platform[7];
    private InfoPacket p;
    private int playerIndex = -1;
    ObjectInputStream ois;
    ObjectOutputStream oos;

    public Session(Socket socket, Player players[], Platform platforms[], InfoPacket p, int pIndex) {
        this.socket = socket;
        for (int i = 0; i < players.length; i++) 
            this.players[i] = players[i];
        for (int i = 0; i < platforms.length; i++)
            this.platforms[i] = platforms[i];
        this.p = p;
        this.playerIndex = pIndex;
    }

    public void run() {

        try {
            oos = new ObjectOutputStream(this.socket.getOutputStream());
            ois = new ObjectInputStream(this.socket.getInputStream());
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("isPlaying: " + p.isPlaying);
        while(p.isPlaying) {
            
            
            try {
                /* send packet to player */
                Packet dataToSend = new Packet(this.players, this.platforms, p.isPlaying, p.winner, this.playerIndex);
                oos.writeObject(dataToSend);
                oos.flush();

                /* Read movement of player, send location of all objects to player */
                if (ois != null) {
                    Object dataReceived = ois.readObject();
                    if (dataReceived instanceof String) {
                        String str = (String) dataReceived;

                        if (str.equals("RIGHT")) {
                            // moveRight
                            //p.updataPlayer(playerIndex, 1);
                            p.players[playerIndex].curDirection = 1;
                            System.out.println(String.valueOf(playerIndex) + " moves RIGHT");
                        }
                        
                        if (str.equals("LEFT")) {
                            // moveLeft
                            //p.updataPlayer(playerIndex, 0);
                            p.players[playerIndex].curDirection = 0;
                            //System.out.println(p.players[playerIndex].curDirection);
                            System.out.println(String.valueOf(playerIndex) + " moves LEFT");
                        }
                        
                        if (str.equals("right")) {
                            // other message
                            p.players[playerIndex].curDirection = -1;
                            
                        }
                        
                        if (str.equals("left")) {
                            p.players[playerIndex].curDirection = -1;
                            System.out.println(String.valueOf(playerIndex) + " moves left");
                        }
                    }

                }
            } catch (IOException e) {
                //System.out.println("this? " + e.getMessage());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } 

            for (int i = 0; i < platforms.length; i++)
                this.platforms[i] = p.platforms[i];
        }
    }
}

class Packet implements Serializable {
    public Player players[] = new Player[2];
    public Platform platforms[] = new Platform[7];
    public boolean isPlaying;
    public int winner;
    public int playerIndex = -1;
    public int platformX[] = new int[7];
    public int platformY[] = new int[7];
    public int playerX[] = new int[2];
    public int playerY[] = new int[2];
    public static final long serialVersionUID = 1L;
    public Packet(Player pls[], Platform pl[], boolean isPlaying, int winner,int pIndex) {
        for (int i = 0; i < players.length; i++) 
            this.players[i] = pls[i];
        for (int i = 0; i < platforms.length; i++)
            this.platforms[i] = pl[i];

        for (int i = 0; i < 7; i++) {
            this.platformX[i] = this.platforms[i].getX();
            this.platformY[i] = this.platforms[i].getY();
        }

        for (int i = 0; i < 2; i++) {
            this.playerX[i] = this.players[i].getX();
            this.playerY[i] = this.players[i].getY();
        }

        this.isPlaying = isPlaying;
        this.winner = winner;
        this.playerIndex = pIndex;
        //System.out.println(this.platforms[0].getY());
    }
}