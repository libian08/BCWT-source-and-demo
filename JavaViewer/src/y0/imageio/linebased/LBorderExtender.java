package y0.imageio.linebased;

import java.util.Hashtable;
import java.util.Vector;

import y0.utils.Task;

public class LBorderExtender extends ImageOpImpl {
	
	private class Stripe implements Task {
		private DataInbox inbox;
		private DataOutbox outbox;
		private boolean isCompleted;
		private int component;
		private int outputY;
		private float[][] buf;
		private boolean isVertialExtMode;
		private int bufferHeight;
		private int bufPointer;
		
		public Stripe(int component, int bufferHeight) {
			this.component = component;
			this.bufferHeight = bufferHeight;
			
			buf = new float[bufferHeight][outputWidth];
			bufPointer = 0;
			
			inbox = new DataInbox();
			outbox = new DataOutbox();
			outbox.setTag(this.component);
			
			outputY = 0;
			isVertialExtMode = false;
		}

		public boolean isCompleted() {
			return isCompleted;
		}

		public void runOnce() {
			if (!outbox.isEmpty())
				return;
			
			if (inbox.isEmpty()) {
				if (isVertialExtMode) {
					extendVertialAndSend();
				}
				return;
			}
			
			DataInbox.Item incoming = inbox.poll();
			if (incoming.isEOF()) {
				isVertialExtMode = true;
				return;
			}
			
			System.arraycopy((float[]) incoming.data, incoming.offset, buf[bufPointer], 0, incoming.len);
			incoming.sender.ackData(incoming.tag);
			
			extendHorizontalAndSend(incoming);
		}

		private void extendHorizontalAndSend(DataInbox.Item incoming) {
			// Even mirroring at the right border.
			int c = (incoming.len << 1) - 1; // c = 2*len - 1
			for (int x = incoming.len; x < outputWidth; x++) {
				// x0 = (len - 1) - (x - len) ==> x0 = 2*len-1-x ==> x0 = c - x
				buf[bufPointer][x] = buf[bufPointer][c - x];
			}
			
			outbox.send(buf[bufPointer], 0, outputWidth, DataReceiver.TYPE_FLOAT);
			outputY++;
			if (++bufPointer == bufferHeight)
				bufPointer = 0;
		}

		private void extendVertialAndSend() {
			// Even periodical mirroring at the bottom border (with period of bufferHeight).
			if (outputY < outputHeight) {
				if (--bufPointer < 0)
					bufPointer = bufferHeight - 1;
				outbox.send(buf[bufPointer], 0, outputWidth, DataReceiver.TYPE_FLOAT);
				outputY++;
			} else {
				outbox.sendEOF();
				isCompleted = true;
			}
		}

		public void run() {
		}
		
		public DataReceiver getInputPin() {
			return inbox;
		}
		
		public DataSender getOutputPin() {
			return outbox;
		}

	}
	
	private Stripe[] stripes;
	private int outputWidth;
	private int outputHeight;
	private int verticalPeriod = 1;

	public void initialize(ImageOp caller) {
		stripes = new Stripe[componentNum];
		for (int comp = 0; comp < componentNum; comp++) {
			stripes[comp] = new Stripe(comp, verticalPeriod);
		}
	
		super.initialize(caller);
	}

	public void online(ImageOp caller) {
		for (int comp = 0; comp < componentNum; comp++) {
			getOutputPins(comp).setSendTo(sink.getInputPins(comp));
		}
		
		super.online(caller);
	}

	public DataReceiver getInputPins(int tag) {
		return stripes[tag].getInputPin();
	}

	public DataSender getOutputPins(int tag) {
		return stripes[tag].getOutputPin();
	}

	public Vector getTasks() {
		Vector tasks = new Vector();
		for (int comp = 0; comp < componentNum; comp++) {
			tasks.addElement(stripes[comp]);
		}
		return tasks;
	}

	public void setParam(Hashtable param) {
		super.setParam(param);
		
		outputWidth = ImageProp.getWidth(param);
		outputHeight = ImageProp.getHeight(param);
		if (outputWidth == ImageProp.NAN || outputHeight == ImageProp.NAN)
			throw new IllegalArgumentException("Must set the height & width");
		
		ImageProp.setWidthReal(props, width);
		ImageProp.setHeightReal(props, height);
		ImageProp.setWidth(props, outputWidth);
		ImageProp.setHeight(props, outputHeight);
	}

}
