import java.awt.Rectangle;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TempPlatform extends Platform implements ActionListener{
	
	private Random rand = new Random();
	private Timer timer = new Timer(40, this);
	private int firstTouch = 0, t = 0;
	private Player currPlayer;
	
	public TempPlatform() {
		this.image = new ImageIcon("img/temp_platform.png");
		this.width = image.getIconWidth();
		this.height = image.getIconHeight();
		xPos = rand.nextInt(605 - width);
		yPos = 550;
	}

	@Override
	public Rectangle getRect() {
		return new Rectangle(xPos, yPos, width, height);
	}
	
	@Override
	public void interactWithPlayer(Player p) {
		p.changeLive(1, this.id);
		currPlayer = p;
		if(firstTouch == 0) {
			this.resetDown();
			firstTouch = 1;
		}
	}
	
	public void resetDown() {
		t = 0;
		timer.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == timer) {
			t++;
			if (t > 20) {
				currPlayer.setY(currPlayer.getY() + 30);
				timer.stop();
				t = 0;
			} 
		}
	}
}
