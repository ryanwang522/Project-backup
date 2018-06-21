import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.io.*;

public class NSShaft extends JPanel implements ActionListener, KeyListener {
	private JLabel lbLives, lbLevel, lbChoosedDifficulty, lbRecord;
	private JButton btnHelp, btnPlay, btnExit, btnClearRecord;
	private int level = 0, lives = 12, seconds = 0, bestLevel = 0, s = 0,
			platformPlayerIsOn = -1, prevSec = 0;
	private JMenuBar menuBar;
	private JMenu menu;
	private JRadioButtonMenuItem rbEasy, rbMedium, rbHard;
	private JCheckBoxMenuItem cbSpring, cbTemp, cbRolling;
	private Player player;
	private boolean start = false, moveRight = false, moveLeft = false, pause = false, isSet = false;
	private Timer gameTimer, platformTimer;
	private Platform[] platforms;
	private static final int platformTypes = 5;
	private Random rnd;
	private TopSpike topSpike;
	private File highscores = new File("highscores.txt");
	private String name = "", line = "";

	public static void main(String[] args) {
		new NSShaft();
	}

	public NSShaft() {
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

		// difficulty
		JLabel lbDifficulty = new JLabel();
		Font f3 = new Font("Britannic Bold", Font.PLAIN, 24);
		lbDifficulty.setFont(f3);
		lbDifficulty.setForeground(c1);
		lbDifficulty.setText("DIFFICULTY:");
		lbDifficulty.setPreferredSize(new Dimension(190, 25));
		lbDifficulty.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbChoosedDifficulty = new JLabel();
		lbChoosedDifficulty.setFont(f3);
		lbChoosedDifficulty.setForeground(c1);
		lbChoosedDifficulty.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbChoosedDifficulty.setPreferredSize(new Dimension(190, 25));
		lbChoosedDifficulty.setText("Medium");

		// highest record
		JLabel lbRecordTitle = new JLabel();
		lbRecordTitle.setFont(f3);
		lbRecordTitle.setForeground(c1);
		lbRecordTitle.setPreferredSize(new Dimension(190, 25));
		lbRecordTitle.setText("RECORD:");
		lbRecordTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

		lbRecord = new JLabel();
		lbRecord.setText("0");
		lbRecord.setFont(f3);
		lbRecord.setForeground(c1);
		lbRecord.setPreferredSize(new Dimension(190, 25));
		lbRecord.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// uses file to read the highest record that was saved before
		try {
			BufferedReader in = new BufferedReader(new FileReader(highscores));
			line = in.readLine();
			bestLevel = Integer.parseInt(line);
			line = in.readLine();
			name = line;
			lbRecord.setText(String.valueOf(bestLevel) + " by " + name);
			in.close();
		} catch (IOException e) {
			bestLevel = 0;
			name = "";
		}
		
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
		
		// JMenu
		menuBar = new JMenuBar();
		menu = new JMenu("Options");
		menuBar.add(menu);

		// Radio buttons
		ButtonGroup difficulty = new ButtonGroup();
		rbEasy = new JRadioButtonMenuItem("Easy");
		difficulty.add(rbEasy);
		rbEasy.addActionListener(this);
		menu.add(rbEasy);
		rbMedium = new JRadioButtonMenuItem("Medium");
		rbMedium.setSelected(true);
		rbMedium.addActionListener(this);
		difficulty.add(rbMedium);
		menu.add(rbMedium);
		rbHard = new JRadioButtonMenuItem("Hard");
		rbHard.addActionListener(this);
		difficulty.add(rbHard);
		menu.add(rbHard);

		// checkboxes
		cbSpring = new JCheckBoxMenuItem("Spring platform");
		cbSpring.setSelected(true);
		cbSpring.addActionListener(this);
		menu.add(cbSpring);
		cbTemp = new JCheckBoxMenuItem("Temp platform");
		cbTemp.setSelected(true);
		cbTemp.addActionListener(this);
		menu.add(cbTemp);
		cbRolling = new JCheckBoxMenuItem("Rolling platform");
		cbRolling.setSelected(true);
		cbRolling.addActionListener(this);
		menu.add(cbRolling);

		gameTimer = new Timer(3, this);
		rnd = new Random();
		topSpike = new TopSpike();

		// panels
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 20));
		topPanel.setBackground(Color.BLACK);
		topPanel.add(lbLives);
		topPanel.add(lbLevel);
		topPanel.add(lbTitle);
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBackground(Color.BLACK);
		rightPanel.add(Box.createVerticalStrut(50));
		rightPanel.add(lbDifficulty);
		rightPanel.add(Box.createVerticalStrut(10));
		rightPanel.add(lbChoosedDifficulty);
		rightPanel.add(Box.createVerticalStrut(50));
		rightPanel.add(lbRecordTitle);
		rightPanel.add(Box.createVerticalStrut(10));
		rightPanel.add(lbRecord);
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
		addKeyListener(this);
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
		frame.setJMenuBar(menuBar);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		if (start) {
			player.draw(g2);
			for (int i = 0; i < platforms.length; i++) {
				platforms[i].draw(g2);
			}
		}
		topSpike.draw(g2);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getSource() == btnHelp) {
			if (start) {
				// help is pressed in the middle of the game
				if (!pause) {
					pause = true;
					gameTimer.stop();
					platformTimer.stop();
					JOptionPane.showMessageDialog(
									null,
									"Use left and right arrow keys to control the player.\nTry to get as deep in the cave as possible.\nTo continue, press the help button.",
									"NS-Shaftt",
									JOptionPane.INFORMATION_MESSAGE);
				} else {
					pause = false;
					gameTimer.start();
					platformTimer.start();
				}
			} else {
				// the game hasn't started and the pause button is pressed
				JOptionPane.showMessageDialog(
								null,
								"Use left and right arrow keys to control the player.\nTry to get as deep in the cave as possible.\nYou can change settings by pressing the options button on the top left corner.\n If you want to pause the game, press the help button or press P.",
								"NS-Shaftt", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (arg0.getSource() == btnClearRecord) {
			int option = JOptionPane.showConfirmDialog(null,
					"Are you sure you want to clear the record?",
					"NS-Shaftt",
					JOptionPane.YES_NO_OPTION);
			if (option == 0) {
				// clearing the record
				if (highscores.delete()) {
					JOptionPane.showMessageDialog(null,
							"File deleted!",
							"NS-Shaftt",
							JOptionPane.INFORMATION_MESSAGE);
					highscores = new File("highscores.txt");
					bestLevel = 0;
					name = "";
					lbRecord.setText("0");
				} else {
					JOptionPane.showMessageDialog(null, "File not deleted!",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (arg0.getSource() == btnPlay) {
			this.gameStart();
		} else if (arg0.getSource() == btnExit) {
			int option = JOptionPane.showConfirmDialog(null,
					"Are you sure you want to exit?", "NS-Shaftt",
					JOptionPane.YES_NO_OPTION);
			if (option == 0) {
				System.exit(0);
			}
		} else if (rbEasy.isSelected()) {
			lbChoosedDifficulty.setText("Easy");
		} else if (rbMedium.isSelected()) {
			lbChoosedDifficulty.setText("Medium");
		} else if (rbHard.isSelected()) {
			lbChoosedDifficulty.setText("Hard");
		}
		
		if (arg0.getSource() == gameTimer) {
			int x = player.getX();
			if (moveRight) {
				if (x < 605 - player.getWidth()) player.setX(++x);
			} else if (moveLeft) {
				if (x > 5) player.setX(--x);
			}
			
			seconds++;
			if (seconds == 100) {
				// the difficulty can be changed by pressing different radio
				// buttons
				if (rbEasy.isSelected()) {
					platformTimer = new Timer(15, this);
				} else if (rbMedium.isSelected()) {
					platformTimer = new Timer(10, this);
				} else if (rbHard.isSelected()) {
					platformTimer = new Timer(8, this);
				}
				platformTimer.restart();
			}
			if (seconds % 1000 == 0 && seconds != 0) {
				// when an amount of time has passed, the level will increase
				level++;
				lbLevel.setText("Level " + level);
			}
			
			// if player does not intersect any platforms, he moves down
			// else if player intersects any platforms, he moves up
			if (isOnPlatform() == -1)
				player.moveDown();
			else {
				player.moveUp();
				
			}
		}

		if (arg0.getSource() == platformTimer) {
			for (int i = 0; i < platforms.length; i++) {
				platforms[i].move();
				int platformObjectID = System.identityHashCode(platforms[i]);
				checkCollision(player, platforms[i], platformObjectID);
				// generate a new platform when it leaves the screen
				if (platforms[i].getY() + platforms[i].getHeight() <= 0) {
					platforms[i] = getRandomPlatform(4);
					platforms[i].setID(System.identityHashCode(platforms[i]));
				}
			}
		}
		
		/* Show injured image */
		if (player.isInjured) {
			if (!isSet) {
				isSet = true;
				prevSec = seconds;
			}
			else 
				if (seconds - prevSec >= 10) {
					player.isInjured = false;
					isSet = false;
				}
		}
		
		/* Check player's live is zero or not.*/
		if (player.live == 0 || player.getY() >= 600) { 
			endGame();
		}
		repaint();
	}
	
	public void gameStart() {
		resetEnv();
		this.start = true;
		generatePlatforms();
		gameTimer.restart();
		menu.setEnabled(false);
		repaint();
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
		} 
		else {
			platform = new NormalPlatform();
		}
		

		return platform;
	}
	
	public void resetEnv() {
		this.s = 0;
		this.moveRight = false;
		this.moveLeft = false;
		this.start = false;
		this.isSet = false;
		this.platforms = new Platform[7]; 
		
		this.player = new Player();
		this.player.live = 12;
		
		this.seconds = 0;
		this.level = 0;
		this.lbLives.setIcon(new ImageIcon("img/lives" + lives + ".png"));
		this.lbLevel.setText("Level " + level);
	}
	
	public int isOnPlatform() {
		for (int i = 0; i < platforms.length; i++) {
			if (player.getRectBottom().intersects(platforms[i].getRect()))
				return i;
		}

		return -1;
	}

	@Override
	// I use key pressed and key released to control the movement of player as
	// suggested by Taiki
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if (start) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT && !pause) {
				//p.setDirection(Player.WEST);
				moveLeft = true;
				player.curDirection = 0;
			}
			// User clicks the right arrow key
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT && !pause) {
				//p.setDirection(Player.EAST);
				moveRight = true;
				player.curDirection = 1;
			} else if (e.getKeyCode() == KeyEvent.VK_P) {
				// if player presses "p" during game, the game pauses
				if (!pause) {
					pause = true;
					gameTimer.stop();
					platformTimer.stop();
				} else {
					pause = false;
					gameTimer.start();
					platformTimer.start();
				}
			}
			repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (start) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				moveLeft = false;
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				moveRight = false;
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

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
		lbLives.setIcon(new ImageIcon("img/lives" + player.live + ".png"));
		player.setY(470);
		for (int j = 0; j < platforms.length; j++) {
			platforms[j].setY(430);
		}
		platformTimer.stop();
		gameTimer.stop();
		if (level == 1) {
			JOptionPane.showMessageDialog(null, "You have completed " + level
					+ " level.", "NS-Shaft", JOptionPane.INFORMATION_MESSAGE);
		} else if (level != 1) {
			JOptionPane.showMessageDialog(null, "You have completed " + level
					+ " levels.", "NS-Shaft", JOptionPane.INFORMATION_MESSAGE);
		}
		// if player breaks the record
		if (level > bestLevel) {
			if (level != 1) {
				JOptionPane.showMessageDialog(null,
						"Congratulations! You have set a new record of "
								+ level + " levels!", "NS-Shaft",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null,
						"Congratulations! You have set a new record of "
								+ level + " level!", "NS-Shaft",
						JOptionPane.INFORMATION_MESSAGE);
			}
			bestLevel = level;
			// writing the new record and player's name into the file
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(
						highscores));
				name = JOptionPane.showInputDialog("Please enter your name: ");
				if (name == null) {
					name = "Anonymous";
				}
				if (name.length() == 0) {
					name = "Anonymous";
				}
				out.write(String.valueOf(bestLevel));
				out.newLine();
				out.write(name);
				out.close();
				JOptionPane.showMessageDialog(null,
						"Data has been written to the file!", "Finished!",
						JOptionPane.INFORMATION_MESSAGE);
				lbRecord.setText(String.valueOf(bestLevel) + " by " + name);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage() + "!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		btnPlay.setEnabled(true);
		menu.setEnabled(true);
	}
}