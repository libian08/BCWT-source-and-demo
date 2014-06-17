package y0.wavelet.dwt2d.linebased;

import java.util.Vector;

import y0.imageio.linebased.DataInbox;
import y0.imageio.linebased.DataOutbox;
import y0.imageio.linebased.DataReceiver;
import y0.imageio.linebased.DataSender;
import y0.imageio.linebased.ImageOp;
import y0.imageio.linebased.ImageOpImpl;
import y0.utils.Task;
import y0.utils.TaskGroup;
import y0.wavelet.Const;
import y0.wavelet.DWTProp;
import y0.wavelet.DWTTag;

public abstract class LDWT2D extends ImageOpImpl implements TaskGroup {
	
	public abstract class Stripe implements Task {
		
		protected DataInbox inbox;
		protected DataOutbox outbox;
		protected DataOutbox outboxLL;

		protected int bufferWidth;
		protected int bufferWidthHalf;
		protected int dwtLevel;
		protected int component;
		protected boolean isCompleted;
		
	    protected int modeVerticalTrans;
	    protected final static int MODE_VERT_MIDDLE = 0;
	    protected final static int MODE_VERT_FIRST = 1;
	    protected final static int MODE_VERT_LAST = 2;

		public Stripe(int dwtLevel, int component, int bufferWidth, Stripe sinkLL) {
			this.bufferWidth = bufferWidth;
			this.dwtLevel = dwtLevel;
			this.component = component;
			
	    	isCompleted = false;
	        bufferWidthHalf = bufferWidth >> 1;
	        
	        inbox = new DataInbox();
	        outbox = new DataOutbox();
	        outbox.setTag(DWTTag.getTag(dwtLevel, component, 0));
	        outboxLL = new DataOutbox();
	        outboxLL.setTag(DWTTag.getTag(dwtLevel, component, Const.BAND_LL));
	        if (sinkLL != null) {
	        	outboxLL.setSendTo(sinkLL.getInputPin());
	        }
	        
	        modeVerticalTrans = MODE_VERT_FIRST;
	        
		}
		
		protected int outputLinePointer;
		protected int outputLineLast;
		protected int bufferDataType;
		
		protected abstract void transHorizontal();
		
	    protected abstract void transVerticalFirst();

	    protected abstract void transVerticalLast();

	    protected abstract void transVerticalMiddle();
		
		protected void sendData() {
			Object line;
			line = getOutputLL(outputLinePointer);
			outboxLL.send(line, 0, bufferWidthHalf, bufferDataType);
			
			line = getOutputH(outputLinePointer);
			outbox.send(line, bufferWidthHalf, bufferWidthHalf, bufferDataType);
			
			outputLinePointer += 2;
		}
		
		protected abstract void sendDataLast();

		protected abstract Object getOutputLL(int line);
		protected abstract Object getOutputH(int line);

		public boolean isCompleted() {
			return isCompleted;
		}

		public void runOnce() {
			if (inbox.isEmpty() || !outbox.isEmpty() || !outboxLL.isEmpty())
				return;
			
			DataInbox.Item incoming = inbox.poll();
			if (incoming.data == null) {
				transVerticalLast();
				sendDataLast();
				outboxLL.send(null, 0, 0, DataReceiver.TYPE_UNKNOWN);
				outbox.send(null, 0, 0, DataReceiver.TYPE_UNKNOWN);
				isCompleted = true;
				return;
			}
			
			downloadAndTransHorizontal(incoming);
			if (isReady()) {
				switch (modeVerticalTrans) {
				case MODE_VERT_MIDDLE:
					transVerticalMiddle();
					break;
				case MODE_VERT_FIRST:
					transVerticalFirst();
					modeVerticalTrans = MODE_VERT_MIDDLE;
					break;
				}
				sendData();
			}
		}

		protected abstract void downloadAndTransHorizontal(DataInbox.Item incoming);
		
		protected abstract boolean isReady();

		public void run() {
		}
		
	    public DataReceiver getInputPin() {
	    	return inbox;
	    }

		public DataSender getOutputPin() {
	    	return outbox;
	    }

		public DataSender getOutputLL() {
	    	return outboxLL;
	    }
	}
	
	protected Stripe[][] stripes;
	protected DWTProp dwtParam;

	public Vector getTasks() {
		Vector tasks = new Vector();
		for (int comp = 0; comp < componentNum; comp++) {
			for (int level = dwtParam.levelNum; level > 0; level--) {
				tasks.addElement(stripes[comp][level]);
			}
		}
		return tasks;
	}

	public DataReceiver getInputPins(int tag) {
		return stripes[tag][1].getInputPin();
	}

	public DataSender getOutputPins(int tag) {
		int dwtLevel = DWTTag.getDwtLevel(tag);
		int component = DWTTag.getComponent(tag);
		int band = DWTTag.getDwtBand(tag);
		if (band == Const.BAND_LL)
			return stripes[component][dwtLevel].getOutputLL();
		else
			return stripes[component][dwtLevel].getOutputPin();
	}

	public void online(ImageOp caller) {
		int level;
		int tag;
		for (int comp = 0; comp < componentNum; comp++) {
			level = dwtParam.levelNum;
			tag = DWTTag.getTag(level, comp, Const.BAND_LL);
			getOutputPins(tag).setSendTo(sink.getInputPins(tag));
			
			for (level = dwtParam.levelNum; level > 0; level--) {
				tag = DWTTag.getTag(level, comp, 0);
				getOutputPins(tag).setSendTo(sink.getInputPins(tag));
			}
		}
		
		super.online(caller);
	}
	
}
