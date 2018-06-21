import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.io.*;

public class GameEngine extends JPanel implements ActionListener {
    private JLabel lbLives, lbLevel, lbChoosedDifficulty, lbRecord;
	private JButton btnHelp, btnPlay, btnExit, btnClearRecord;
	private int level = 0, lives = 12, seconds = 0, bestLevel = 0, s = 0,
			platformPlayerIsOn = -1, prevSec = 0, winner = -1;
	private Player[] players;
	private boolean start = false, moveRight = false, moveLeft = false, pause = false, isSet = false;
	private Timer gameTimer, platformTimer;
	private Platform[] platforms;
	private static final int platformTypes = 5;
	private Random rnd;
	private TopSpike topSpike;
    private InfoPacket infoPacket = new InfoPacket(this.players, this.platforms, this.start, this.winner);


    public GameEngine() {
        /* Set up basic GUI for game enviornment */

        // lives
		lbLives = new JLabel();
		lbLives.setPreferredSize(new Dimension(140, 85));
		lbLives.setHorizontalAlignment(SwingConstants.CENTER);
		lbLives.setIcon(new ImageIcon("img/lives" + lives + ".png"));

		// level
		lbLevel = new JLabel();
		lbLevel.setFont(new Font("Times New Roman", Font.BOLD, 36));
		lbLevel.setForeground(Color.YELLOW);
		lbLevel.setText("Level " + level);
		lbLevel.setPreferredSize(new Dimension(290, 30));
		lbLevel.setHorizontalAlignment(SwingConstants.CENTER);

		// title
		JLabel lbTitle = new JLabel();
		Font f2 = new Font("Britannic Bold", Font.PLAIN, 36);
		lbTitle.setFont(f2);
		Color c1 = new Color(122, 122, 122);
		lbTitle.setForeground(c1);
		lbTitle.setText("NS-Shaft");
		lbTitle.setPreferredSize(new Dimension(190, 30));
		lbTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // JButtons
		btnClearRecord = new JButton("CLEAR RECORD");
		btnClearRecord.setFocusable(false);
		btnClearRecord.addActionListener(this);

		btnHelp = new JButton("HELP");
		btnHelp.setFocusable(false);
		btnHelp.addActionListener(this);

		btnPlay = new JButton("PLAY");
		btnPlay.setFocusable(false);
		btnPlay.addActionListener(this);

		btnExit = new JButton("EXIT");
		btnExit.setFocusable(false);
		btnExit.addActionListener(this);

		// set buttons alignment
		btnClearRecord.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnHelp.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnPlay.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // panel
        JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 20));
		topPanel.setBackground(Color.BLACK);
		topPanel.add(lbLives);
		topPanel.add(lbLevel);
        topPanel.add(lbTitle);
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.BLACK);
        rightPanel.setBackground(Color.BLACK);
		rightPanel.add(Box.createVerticalStrut(10));
		rightPanel.add(btnClearRecord);
		rightPanel.add(Box.createVerticalStrut(70));
		rightPanel.add(btnHelp);
		rightPanel.add(Box.createVerticalStrut(10));
		rightPanel.add(btnPlay);
		rightPanel.add(Box.createVerticalStrut(10));
		rightPanel.add(btnExit);

        setBackground(Color.black);
		setFocusable(true);
		setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));
		//addKeyListener(this);
		JFrame frame = new JFrame();
		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(rightPanel, BorderLayout.EAST);
		frame.add(this, BorderLayout.CENTER);
		frame.setTitle("NS-Shaft");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setSize(810, 710);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);


        /* Game timer */
        gameTimer = new Timer(3, this);
		rnd = new Random();
        topSpike = new TopSpike();
        
        /* Initialize players */
        for (int i = 0; i < players.length; i++)
            players[i] = new Player();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == gameTimer) {

            seconds++;
            if (seconds % 1000 == 0 && seconds != 0) {
                level++;
                lbLevel.setText("Level" + level);
            }

            for (int i = 0; i < players.length; i++) {
                Player player = players[i];
                int x = player.getX();

                // TODO: set player.curDirection when recive move action from client
                // current direction 1 -> move right, 0 -> move left
                if (player.curDirection == 1)
                    if (x < 605 - player.getWidth()) player.setX(++x);
                else if (player.curDirection == 0)
                    if (x > 5) player.setX(--x);

                // if player does not intersect any platforms, he moves down
                // else if player intersects any platforms, he moves up
                checkCurrentPlatform(player);
                if (player.curPlatform == -1)
                    player.moveDown();
                else 
                    player.moveUp();
            }

        }

        if (e.getSource() == platformTimer) {
            for (int i = 0; i < platforms.length; i++) {
				platforms[i].move();
				int platformObjectID = System.identityHashCode(platforms[i]);
                checkCollision(players[0], platforms[i], platformObjectID);
                checkCollision(players[1], platforms[i], platformObjectID);
				// generate a new platform when it leaves the screen
				if (platforms[i].getY() + platforms[i].getHeight() <= 0) {
					platforms[i] = getRandomPlatform(4);
					platforms[i].setID(System.identityHashCode(platforms[i]));
				}
			}
        }

        /* Check player's live is zero or not.*/
        for (int i = 0; i < players.length; i++)
		    if (players[i].live == 0 || players[i].getY() >= 600) {
                winner = ~i;
                endGame();
                break;
            }

        /* Update current game info */
        this.infoPacket.update(this.start, this.winner);
    }

    public void gameStart() {
		resetEnv();
		this.start = true;
		generatePlatforms();
		gameTimer.restart();
		System.out.println("In start...");
	}

    public void generatePlatforms() {
		// NOTICE: Reset the env first then call this function
		
		for (int i = 0 ; i < platforms.length; i++) {
			/* Generate the platform for player to stand on when beginning */
			if (i == platforms.length / 2) {
				platforms[3] = new NormalPlatform();
				platforms[3].setLocation(260, 225);
				continue;
			}

			platforms[i] = getRandomPlatform(4);
			platforms[i].setID(System.identityHashCode(platforms[i]));
			platforms[i].setY(45 + i * 70);
		}
		
	}
	
	public Platform getRandomPlatform(int numOfType) {
		Platform platform = null;
		if (numOfType > 5) numOfType = 5;
		
		int n = rnd.nextInt(numOfType);
		if (n == 0) {
			platform = new NormalPlatform();
		} else if (n == 1) {
			platform = new SpikePlatform();
		} else if (n == 2) {
			platform = new SpringPlatform();
		} else if (n == 3) {
			platform = new TempPlatform();
		} else {
			platform = new NormalPlatform();
		}
		
		return platform;
	}

    public void resetEnv() {
		this.s = 0;
		//this.moveRight = false;
		//this.moveLeft = false;
		this.start = false;
		this.isSet = false;
        this.platforms = new Platform[7];
        this.winner = -1;
		
		for (int i = 0; i < players.length; i++)
            players[i] = new Player();
		
		this.seconds = 0;
		this.level = 0;

	}

    /* Check whether a player p stands on a platform */
    public void checkCurrentPlatform(Player p) {
        p.curPlatform = -1;
        for (int i = 0; i < platforms.length; i++) 
			if (p.getRectBottom().intersects(platforms[i].getRect())) {
                p.curPlatform = i;
                return;
            }
    }
    
    public void checkCollision(Player player, Platform platform, int platformIndex) {
		/* Player interacts with platform */
		if (platform.getRect().intersects(player.getRectBottom()) ) {
			//System.out.println("intersect platform: " + platformIndex);
			platform.interactWithPlayer(player);
			player.previousPlatform = platformIndex;
			lbLives.setIcon(new ImageIcon("img/lives" + player.live + ".png"));
		}
		
		/* Player interacts with top-spike */
		if (player.getRectTop().intersects(topSpike.getRect())) {
			player.changeLive(-5);
			lbLives.setIcon(new ImageIcon("img/lives" + player.live + ".png"));
			player.setY(player.getY() + 40);
		}
		
    }
    
    public void endGame() {
        start = false;
        platformTimer.stop();
        gameTimer.stop();
        this.infoPacket.update(this.start, this.winner);
    }

    public InfoPacket getInfoPacket() {
        return infoPacket;
    }

    public static void main(String[] args) {
        GameEngine env = new GameEngine();
        new GameServer(8765, env.getInfoPacket());
    }
}

