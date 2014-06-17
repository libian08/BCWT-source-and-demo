package y0.color.transform.linebased;

import java.util.Vector;

import y0.imageio.linebased.DataInbox;
import y0.imageio.linebased.DataOutbox;
import y0.imageio.linebased.DataReceiver;
import y0.imageio.linebased.DataSender;
import y0.imageio.linebased.ImageOp;
import y0.imageio.linebased.ImageOpImpl;
import y0.utils.Task;

public abstract class LColorTransform extends ImageOpImpl implements Task {
	protected boolean isCompleted;
	protected DataInbox[] inboxes;
	protected DataOutbox[] outboxes;
	protected DataInbox.Item[] incomings;
	
	public void initialize(ImageOp caller) {
		isCompleted = false;
		
		inboxes = new DataInbox[componentNum];
		for (int comp = 0; comp < componentNum; comp++) {
			inboxes[comp] = new DataInbox();
		}
		incomings = new DataInbox.Item[componentNum];
		
		outboxes = new DataOutbox[componentNum];
		for (int comp = 0; comp < componentNum; comp++) {
			outboxes[comp] = new DataOutbox();
			outboxes[comp].setTag(comp);
		}
		
		super.initialize(caller);
	}

	public void online(ImageOp caller) {
		boolean toSink = true;
		if (source == null) {
			toSink = true;
		} else if (source.isOnline()) {
			toSink = true;
		} else {
			toSink = false;
		}
		
		if (toSink) {
			for (int comp = 0; comp < componentNum; comp++) {
				getOutputPins(comp).setSendTo(sink.getInputPins(comp));
			}
		} else {
			for (int comp = 0; comp < componentNum; comp++) {
				source.getOutputPins(comp).setSendTo(getInputPins(comp));
			}
		}

		super.online(caller);
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void runOnce() {
		boolean isReady = true;
		for (int comp = 0; comp < componentNum && isReady; comp++) {
			isReady &= (outboxes[comp].isEmpty());
			isReady &= (!inboxes[comp].isEmpty());
		}
		if (!isReady)
			return;

		for (int comp = 0; comp < componentNum; comp++) {
			incomings[comp] = inboxes[comp].poll();
			if (incomings[comp].isEOF()) {
				setCompleted(true);
				return;
			}
		}
		
		ColorTransform(incomings);
		for (int comp = 0; comp < componentNum; comp++) {
			incomings[comp].sender.ackData(incomings[comp].tag);
		}
		sendData();

	}

	protected abstract void sendData();
	
	protected void setCompleted(boolean isCompleted) {
		this.isCompleted = true;
		for (int comp = 0; comp < componentNum; comp++) {
			outboxes[comp].sendEOF();
		}
	}

	public void run() {
	}
	
	protected abstract void ColorTransform(DataInbox.Item[] incomings);

	public DataReceiver getInputPins(int tag) {
		return inboxes[tag];
	}

	public DataSender getOutputPins(int tag) {
		return outboxes[tag];
	}

	public Vector getTasks() {
		Vector tasks = new Vector();
		tasks.addElement(this);
		return tasks;
	}
}
