package com.mygdx.game;

public abstract class GameLoop {
    // Whether gameloop is running
    private boolean running = false;
    // Target FPS
    private final int TARGET_FPS = 60;
    // Time in nanoseconds per frame to achieve target fps
    private final long NS_PER_FRAME = 1000000000 / TARGET_FPS;

    protected abstract void update(double deltaTime);

    protected abstract void render();

    public void start() {
        running = true;
        long lastTime = System.nanoTime(); // Get initial time in nanoseconds
        long timer = System.currentTimeMillis(); // Used to track FPS updates
        int frames = 0; // Frame counter

        while (running) {
            long now = System.nanoTime(); // Current time in nanoseconds
            double delta = (now - lastTime) / (double) NS_PER_FRAME; // Time difference between frames
            lastTime = now; // Update lastTime for the next frame

            update(delta);
            render();

            frames++; // Increment frame counter

            // Improved FPS control - calculates remaining time to maintain FPS consistency
            // Example: If each frame should take 16ms but the frame finished in 10ms, sleep
            // for 6ms to ensure that we maintain 60 FPS
            long timeToSleep = (lastTime + NS_PER_FRAME) - System.nanoTime();
            if (timeToSleep > 0) {
                try {
                    Thread.sleep(timeToSleep / 1000000); // Convert to millisecond
                } catch (InterruptedException e) {
                    e.printStackTrace(); // Handle exception if sleep is interrupted
                }
            }

            // FPS counter - prints FPS once per second
            if (System.currentTimeMillis() - timer > 1000) {
                System.out.println("FPS: " + frames);
                frames = 0; // Reset frame counter
                timer += 1000; // Move timer forward by 1 second
            }
        }
    }

    public void stop() {
        running = false;
    }
}
