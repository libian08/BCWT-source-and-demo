package y0.utils;

public class Stopwatch {
    long startTime;
    long stopTime;

    public Stopwatch() {
        clear();
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        stopTime = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return (stopTime - startTime);
    }
    
    public long getCurrentTime() {
        return (System.currentTimeMillis() - startTime);
    }

    public void clear() {
        startTime = 0;
        stopTime = 0;
    }
}