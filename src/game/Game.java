package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class Game extends Canvas implements Runnable {
	
	private static final long serialVersionUID = -3520908160442010701L;
	public static final int WIDTH = 800, HEIGHT = WIDTH / 12 * 9;
	
	private Thread thread;
	private boolean running = false;

	private Random r;
	private Handler handler;
	private Shack shack;
	private Stats stats;

	public Game() throws InterruptedException {
		new TestWindow(WIDTH, HEIGHT, "RestaurantSim", this);
		
		handler = new Handler();
		r = new Random();
		shack = new Shack(520, 50, 60, 250);
		stats = new Stats(0);

		while(running == true) {
			handler.addObject(new Customer(50, 250, CustomerStates.IN_LINE, 0, handler, stats));
			Thread.sleep(r.nextInt(3000) + 2000);
		}
		
	}

	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}

	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				delta--;
			}
			if (running)
				render();
			frames++;
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames);
				frames = 0;
			}
		}
		stop();
	}

	private void tick() {
		handler.tick();
		stats.tick();
	}

	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();

		g.setColor(new Color(59, 59, 59)); // DRAW BACKGROUND
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(new Color(145, 102, 33)); // DRAW BOTTOM MENU
		g.fillRect(0, 350, WIDTH, 250);
		g.setColor(new Color(53, 125, 11)); // DRAW SIDE MENU
		g.fillRect(600, 0, 200, 350);
		
		handler.render(g);
		shack.render(g);
		stats.render(g);

		g.dispose();
		bs.show();
	}

	public static void main(String args[]) throws InterruptedException {
		new Game();
	}

}
