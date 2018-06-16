import java.awt.Rectangle;
import java.util.Random;
import javax.swing.ImageIcon;

public class SpikePlatform extends Platform {
	Random rand = new Random();
	
	public SpikePlatform() {
		this.image = new ImageIcon("img/spike_platform.png");
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
		p.changeLive(-5, this.id);
	}
}
