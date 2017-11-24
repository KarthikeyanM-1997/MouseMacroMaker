import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.ArrayList;

public class Player implements Runnable {
	ArrayList<Point> macroPoints;

	volatile boolean running = false;
	
	Player(ArrayList<Point> mP) {
		macroPoints = mP;
	}

	@Override
	public void run() {
		try {
			Robot bot = new Robot();
			while (running) {
				for (Point p : macroPoints) {
					bot.mouseMove(p.x, p.y);
					bot.mousePress(InputEvent.BUTTON1_MASK);
					bot.mouseRelease(InputEvent.BUTTON1_MASK);
					Thread.sleep(1000);
				}
			}

		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop(){
        running = false;
    }
}
