import java.awt.Rectangle;
import java.util.Random;
import javax.swing.ImageIcon;

public class SpringPlatform extends Platform {
	Random rand = new Random();
	
	public SpringPlatform() {
		this.image = new ImageIcon("img/spring_platform.png");
		this.width = image.getIconWidth();
		this.height = image.getIconHeight();
		xPos = rand.nextInt(605 - width);
		yPos = 550;
	}

	
	public Rectangle getRect() {
		return new Rectangle(xPos, yPos, width, height);
	}
	
	@Override
	public void interactWithPlayer(Player p) {
		p.changeLive(1, this.id);
		p.setY(p.getY() - 50);
	}
}
