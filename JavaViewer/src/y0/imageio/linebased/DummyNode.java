package y0.imageio.linebased;

import java.util.Vector;

import y0.imageio.ROI;
import y0.utils.Task;

public class DummyNode extends ImageOpImpl implements Task {
	
	private DataInbox inbox;
	private int inputPinCompleted;
	private int inputPinNum;
	private boolean isCompleted;
	private ROI outputROI;
	private int rowCount;
	private int rowEnd;
	
	public DummyNode() {
		inbox = new DataInbox();
		inputPinCompleted = 0;
		inputPinNum = 0;
		isCompleted = false;
	}
	
	public void runOnce() {
		if (inbox.isEmpty())
			return;

		while (!inbox.isEmpty()) {
			DataInbox.Item incoming = inbox.poll();
			if (incoming.isEOF()) {
				if (++inputPinCompleted >= inputPinNum) {
					isCompleted = true;
					return;
				}
			}
			incoming.sender.ackData(incoming.tag);
			if (++rowCount >= rowEnd) {
				finish(this);
			}
		}
	}
	
	public void run() {
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public DataReceiver getInputPins(int tag) {
		inputPinNum++;
		return inbox;
	}
	
	public DataSender getOutputPins(int tag) {
		return null;
	}

	public Vector getTasks() {
		Vector tasks = new Vector();
		tasks.addElement(this);
		return tasks;
	}
	
	public void online(ImageOp caller) {
    	outputROI = ImageProp.getROI(result);
    	if (outputROI == null) {
    		outputROI = new ROI();
    		outputROI.x = 0;
    		outputROI.y = 0;
    		outputROI.width = width;
    		outputROI.height = height;
    	}
    	
    	rowCount = 0;
    	rowEnd = outputROI.height * componentNum;

    	// Connect the source to this, only required when in decoding graph.
		for (int comp = 0; comp < componentNum; comp++) {
			source.getOutputPins(comp).setSendTo(getInputPins(comp));
			inputPinNum++;
		}

		super.online(caller);
	}

	public void finish(ImageOp caller) {
		isCompleted = true;
		
		super.finish(caller);
	}


}
