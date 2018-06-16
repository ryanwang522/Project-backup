import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.*;

public class Player {
	private ImageIcon imgPlayerEast, imgPlayerWest, imgInjuredEast, imgInjuredWest;
	private int xPos, yPos, height, width, curDirection;
	protected int live = 12, previousPlatform = -1;
	public static final int EAST = 1, WEST = 0;
	public boolean isInjured;
	
	// TODO: Add a attribute for current platform
	
	public Player() {
		this.curDirection = EAST;
		this.imgPlayerEast = new ImageIcon("img/caveman" + EAST + ".png");
		this.imgPlayerWest = new ImageIcon("img/caveman" + WEST + ".png");
		this.imgInjuredEast = new ImageIcon("img/caveman" + EAST + "_injured.png");
		this.imgInjuredWest = new ImageIcon("img/caveman" + WEST + "_injured.png");
		this.width = imgPlayerEast.getIconWidth();
		this.height = imgPlayerEast.getIconHeight();
		this.xPos = 310 - width/2;
		this.yPos = 205 - height/2;
		this.live = 12;
		this.isInjured = false;
		this.previousPlatform = -1;
	}
	
	public void draw(Graphics2D g) {
		if (isInjured) {
			if (curDirection == EAST)
				g.drawImage(imgInjuredEast.getImage(), xPos, yPos, null);
			else 
				g.drawImage(imgInjuredWest.getImage(), xPos, yPos, null);
		} else {
			if (curDirection == EAST)
				g.drawImage(imgPlayerEast.getImage(), xPos, yPos, null);
			else 
				g.drawImage(imgPlayerWest.getImage(), xPos, yPos, null);
		}
	}
	
	public int getHeight() { return height; }
	public int getWidth() { return width; }
	public int getX() { return xPos; }
	public int getY() { return yPos; }
	public int getDirection() { return curDirection; }
	
	public void moveDown() {
		yPos++;
	}
	
	public void moveUp() {
		yPos--;
	}
	
	public void setLocation(int x, int y) {
		xPos = x;
		yPos = y;
	}
	
	public void setDirection(int dir) {
		curDirection = dir;
	}
	public void setX(int x) {
		xPos = x;
	}
	public void setY(int y) {
		yPos = y;
	}
	
	public void changeLive(int changeNumber) {
		this.live += changeNumber;
		if (this.live > 12) this.live = 12;
		if (this.live < 0 ) this.live = 0;
	}
	
	public void changeLive(int changeNumber, int platformID) {
		/*System.out.print("current platfromID: ");
		System.out.println(platformID);
		System.out.print("previousPlatform: ");
		System.out.println(this.previousPlatform);
		System.out.print("Live: ");
		System.out.println(this.live);*/

		if (platformID != this.previousPlatform ) this.live += changeNumber; 
		if (this.live > 12) this.live = 12;
		if (this.live < 0 ) this.live = 0;
	}
	
	// bottom rectangle checks collision with platforms
	public Rectangle getRectBottom() {
		Rectangle r = new Rectangle(xPos + 20, yPos + height, 10, 1);
		return r;
	}
	
	// top rectangle checks collision with spikes at the top
	public Rectangle getRectTop() {
		Rectangle r = new Rectangle(xPos + 2, yPos, 10, 1);
		return r;
	}
	
	public void injured() {
		isInjured = true;
	}
	
	public void notInjured() {
		isInjured = false;
	}
	
}
