package y0.utils;

import java.util.Vector;

public class TaskRunner extends Thread {
	
	protected Vector taskList = new Vector(); 
	
	public void addTask(Task task) {
		taskList.add(task);
	}
	
	public void addTask(TaskGroup taskGroup) {
		addTask(taskGroup.getTasks());
	}
	
	public void addTask(Vector list) {
		int len = list.size();
		for (int i = 0; i < len; i++) {
			addTask((Task) list.elementAt(i));
		}
	}
	
	public void setTaskList(Vector list) {
		this.taskList = list;
	}
	
	protected boolean allowNextRun = true;

	public void run() {
		int taskIndex = 0;
                
                //Test
                int test_totalRunNum = 0;

		while (allowNextRun) {
                    
                    //Test
                    test_totalRunNum = test_totalRunNum + 1;
                    
			if (taskList.size() == 0)
				break;

			if (taskIndex >= taskList.size()) {
				taskIndex = 0;
			}

			Task currentTask = (Task) taskList.get(taskIndex);

			currentTask.runOnce();
			if (currentTask.isCompleted()) {
				taskList.removeElementAt(taskIndex);
			} else {
				taskIndex++;
			}
			Thread.yield();
		}

		// Will exit for now, but allow next run if being started again.
		allowNextRun = true;
	}
	
	public void abort() {
		allowNextRun = false;
	}

}
