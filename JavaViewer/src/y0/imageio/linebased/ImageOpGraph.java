package y0.imageio.linebased;

import java.util.Hashtable;
import java.util.Vector;

import y0.utils.Task;
import y0.utils.TaskRunner;

public class ImageOpGraph implements Runnable {
	private Vector graph;
	private Vector tasks;
	
	private Hashtable props;
	private Hashtable param;
	private Hashtable result;
	
	// Actions include initialize(), online() and finish().
	public static final int ACTION_FROM_FIRST = 0;
	public static final int ACTION_FROM_LAST = 1;

	private int actionStartPoint;
	
	public int size() {
		return graph.size();
	}

	private ImageOp opFirst;
	private ImageOp opLast;
	
	public ImageOpGraph() {
		props = null;
		param = null;
		result = null;
		init();
	}
	
	public ImageOpGraph(Hashtable props, Hashtable param, Hashtable result) {
		this.props = props;
		this.param = param;
		this.result = result;
		init();
	}
	
	private void init() {
		graph = new Vector();
		tasks = new Vector();
		opFirst = null;
		opLast = null;
		actionStartPoint = ACTION_FROM_FIRST;
	}
	
	public ImageOp add(ImageOp op) {
		graph.addElement(op);
		if (graph.size() == 1) {
			opFirst = op;
			opLast = op;
			if (props != null)
				opFirst.setProperties(props);
			if (param != null)
				opFirst.setParam(param);
			if (result != null)
				opFirst.setResult(result);
		} else {
			op.setSource(opLast);
			opLast = op;
		}
		
		return op;
	}
	
	public void addTask(Task task) {
		tasks.add(task);
	}

	public ImageOp getOpFirst() {
		return opFirst;
	}
	
	public void initialize() {
		if (actionStartPoint == ACTION_FROM_FIRST)
			opFirst.initialize(null);
		else
			opLast.initialize(null);
	}
	
	public void online() {
		if (actionStartPoint == ACTION_FROM_FIRST)
			opFirst.online(null);
		else
			opLast.online(null);
		
		for (int i = 0; i < graph.size(); i++) {
			ImageOp op = (ImageOp) graph.elementAt(i);

			Vector thisOpTasks = op.getTasks();
			if (thisOpTasks == null)
				continue;

			for (int j = 0; j < thisOpTasks.size(); j++)
				tasks.add(thisOpTasks.elementAt(j));
		}

	}
	public void finish() {
		if (actionStartPoint == ACTION_FROM_FIRST)
			opFirst.finish(null);
		else
			opLast.finish(null);
	}
	
	public Vector getTasks() {
		return tasks;
	}
	
	private TaskRunner runner;
	
	public void run() {
		initialize();
		online();
		
		runner = new TaskRunner();
		runner.setTaskList(getTasks());
		
		runner.run();
		
		finish();
	}
	
	public void abort() {
		runner.abort();
	}

	public ImageOp getOpLast() {
		return opLast;
	}

	public void setActionStartPoint(int actionStartPoint) {
		this.actionStartPoint = actionStartPoint;
	}

	public int getActionStartPoint() {
		return actionStartPoint;
	}
}