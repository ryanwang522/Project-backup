import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class GameServer {
    private ServerSocket serverSock;
    Player players[];
    ArrayList<Session> playerSessions = new ArrayList<>();
    int playerCnt = 0;
    

    public GameServer(int port, InfoPacket p) {
        this.players = p.players;

        try {
            serverSock = new ServerSocket(port);
            System.out.println("Server started!");
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    
        try {
            while(true) {
                Socket playerSocket = serverSock.accept();
                
                playerSessions.add(new Session(playerSocket, this.players[playerCnt], p));
                Thread gameThread = new Thread(playerSessions.get(playerCnt));
                gameThread.start();
                playerCnt++;

                if (playerCnt == 2) {
                    System.out.println("There are two players.");
                    break;
                }
            } 
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

class Session implements Runnable {
    private Socket socket;
    private Player player;
    private InfoPacket p;
    ObjectInputStream ois;
    ObjectOutputStream oos;

    public Session(Socket socket, Player player, InfoPacket p) {
        this.socket = socket;
        this.player = player;
        this.p = p;
    }

    public void run() {
        try {
            ois = new ObjectInputStream(this.socket.getInputStream());
            oos = new ObjectOutputStream(this.socket.getOutputStream());
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }

        while(p.isPlaying) {
            try {
                /* Read movement of player, send location of all objects to player */
                Object dataReceived = ois.readObject();
                if (dataReceived instanceof String) {
                    String str = (String) dataReceived;

                    if (str.equals("RIGHT")) {
                        // moveRight
                        player.curDirection = 1;
                        System.out.println("RIGHT~~");
                    } else if (str.equals("LEFT")) {
                        // moveLeft
                        player.curDirection = 0;
                        System.out.println("LEFT~~");
                    } else {
                        // other message
                    }
                }

                /* TODO: send packet to player */
                Object dataToSend = new Packet(this.player, p.platforms, p.isPlaying, p.winner);
                oos.writeObject(dataToSend);
            
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } 
        }
    }
}

class Packet {
    public Player player;
    public Platform platforms[];
    public boolean isPlaying;
    public int winner;
    
    public Packet(Player p, Platform pl[], boolean isPlaying, int winner) {
        this.player = p;
        this.platforms = pl;
        this.isPlaying = isPlaying;
        this.winner = winner;
    }
}