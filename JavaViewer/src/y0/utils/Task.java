package y0.utils;

public interface Task extends Runnable {
	public abstract void runOnce();
	public abstract boolean isCompleted();
}
