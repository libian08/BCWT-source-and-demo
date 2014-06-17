package edu.ttu.cvial.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 2.0
 */

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