package y0.wavelet.dwt2d.linebased;

import java.util.Vector;

import y0.imageio.ROI;
import y0.imageio.linebased.DataInbox;
import y0.imageio.linebased.DataOutbox;
import y0.imageio.linebased.DataReceiver;
import y0.imageio.linebased.DataSender;
import y0.imageio.linebased.ImageOp;
import y0.imageio.linebased.ImageOpImpl;
import y0.imageio.linebased.ImageProp;
import y0.utils.Task;
import y0.utils.TaskGroup;
import y0.wavelet.Const;
import y0.wavelet.DWTProp;
import y0.wavelet.DWTTag;

public abstract class ILDWT2D extends ImageOpImpl implements TaskGroup {
	
	public abstract class Stripe implements Task {

		protected int bufferWidth;
	    protected int bufferWidthHalf;
	    protected int bufferDataType;

		protected int outputLinePointer;
		protected int outputLineLast;

	    protected int modeVerticalTrans;
	    protected final static int MODE_VERT_MIDDLE = 0;
	    protected final static int MODE_VERT_FIRST = 1;
	    protected final static int MODE_VERT_LAST = 2;

	    protected DataInbox inbox;
	    protected DataInbox inboxLL;
	    protected DataOutbox outbox;
		protected boolean isCompleted;
		protected int dwtLevel;
		protected int component;

		protected int outputY;
		protected ROI outputROI;
		protected int outputROIYBottom;
		protected int outputXRight;
		protected int outputX;

	    public Stripe(int dwtLevel, int component, Stripe sinkLL, ROI outputROI, ROI dwtROI) {
	        this.bufferWidth = dwtROI.width;
	        this.dwtLevel = dwtLevel;
	        this.component = component;
	        this.outputROI = outputROI;
	        this.outputY = dwtROI.y;
	        this.outputX = outputROI.x - dwtROI.x;
	        this.outputXRight = outputX + outputROI.width - 1;
	        
			outputROIYBottom = outputROI.y + outputROI.height - 1;
			
	        outbox = new DataOutbox();
	        outbox.setTag(DWTTag.getTag(dwtLevel, component, Const.BAND_LL));
	        if (sinkLL != null)
	        	outbox.setSendTo(sinkLL.getInputLL());
	        
	        inbox = new DataInbox();
	        inboxLL = new DataInbox();
	        
	        bufferWidthHalf = bufferWidth >> 1;
	        modeVerticalTrans = MODE_VERT_FIRST;
	    }
	    
	    public DataReceiver getInputLL() {
	    	return inboxLL;
	    }

	    public DataReceiver getInputPin() {
	    	return inbox;
	    }

		protected abstract Object getOutputLine(int line);
		
		public DataSender getOutputPin() {
	    	return outbox;
	    }
		
		public boolean isCompleted() {
			return isCompleted;
		}
		protected abstract boolean isReady(int modeVerticalTrans);
		protected abstract void pushIntoBuffer(DataInbox.Item incomingLL, DataInbox.Item incoming);

		public void run() {
		}
		
		public void runOnce() {
			if (!outbox.isEmpty() || inboxLL.isEmpty() || inbox.isEmpty())
				return;
			
			DataInbox.Item incomingLL = inboxLL.poll();
			DataInbox.Item incoming = inbox.poll();
			
			if (incomingLL.data == null) {
				if (incoming.data != null)
					throw new IllegalStateException("Both should be null");
				modeVerticalTrans = MODE_VERT_LAST;
			} else {
				pushIntoBuffer(incomingLL, incoming);
				incomingLL.sender.ackData(incomingLL.tag);
				incoming.sender.ackData(incoming.tag);
			}

			if (isReady(modeVerticalTrans)) {
                switch (modeVerticalTrans) {
                case MODE_VERT_MIDDLE:
                	transVerticalMiddle();
                    break;
                case MODE_VERT_FIRST:
                	transVerticalFirst();
                	modeVerticalTrans = MODE_VERT_MIDDLE;
                    break;
                case MODE_VERT_LAST:
                	transVerticalLast();
                	break;
                }
                
                sendData();
                if (modeVerticalTrans == MODE_VERT_LAST) {
                	outbox.send(null, 0, 0, DataReceiver.TYPE_UNKNOWN);
                	isCompleted = true;
                }
			}
		}
		
		protected void sendData() {
			for (;outputLinePointer <= outputLineLast; outputLinePointer++, outputY++) {
				if (outputY < outputROI.y)
					continue;
				
				if (outputY > outputROIYBottom) {
					break;
				}
				
				Object line = getOutputLine(outputLinePointer);
				outbox.send(line, outputX, outputROI.width, bufferDataType);
			}
			
			if (outputY > outputROIYBottom) {
				outbox.sendEOF();
				isCompleted = true;
			}
		}

	    protected abstract void transVerticalFirst();

	    protected abstract void transVerticalLast();

	    protected abstract void transVerticalMiddle();
	}

	protected Stripe[][] stripes;
    
	protected DWTProp dwtProp;
	protected DWTProp dwtParam;
	protected ImageProp imgParam;
    
    protected abstract ROI[] getDWTROI();
    
	public DataReceiver getInputPins(int tag) {
		DataReceiver inputPin = null;
		
		int dwtLevel = DWTTag.getDwtLevel(tag);
		int component = DWTTag.getComponent(tag);
		int band = DWTTag.getDwtBand(tag);
		
		if (dwtParam.levelNum < dwtProp.levelNum) {
			if (band == Const.BAND_LL)
				inputPin = stripes[component][dwtLevel].getInputLL();
			else
				inputPin = stripes[component][dwtLevel].getInputPin();
		} else {
			inputPin = stripes[component][dwtProp.levelNum + 1].getInputLL();
		}
		
		return inputPin;
	}

	public DataSender getOutputPins(int tag) {
		return stripes[tag][dwtParam.levelNum + 1].getOutputPin();
	}

	protected ROI getOutputImageROI() {
		ROI outputImageROI = new ROI();
		outputImageROI.x = imgParam.roi.x >> dwtParam.levelNum;
		outputImageROI.y = imgParam.roi.y >> dwtParam.levelNum;
		outputImageROI.width = imgParam.roi.width >> dwtParam.levelNum;
		outputImageROI.height = imgParam.roi.height >> dwtParam.levelNum;
		return outputImageROI;
	}

	public void online(ImageOp caller) {
		int level;
		int tag;
		for (int comp = 0; comp < componentNum; comp++) {
			level = dwtProp.levelNum;
			tag = DWTTag.getTag(level, comp, Const.BAND_LL);
			source.getOutputPins(tag).setSendTo(getInputPins(tag));
			
			for (level = dwtProp.levelNum; level > dwtParam.levelNum; level--) {
				tag = DWTTag.getTag(level, comp, 0);
				source.getOutputPins(tag).setSendTo(getInputPins(tag));
			}
		}
		super.online(caller);
	}

	public Vector getTasks() {
		Vector tasks = new Vector();
		for (int comp = 0; comp < componentNum; comp++) {
			for (int level = dwtProp.levelNum + 1; level > dwtParam.levelNum; level--) {
				if (stripes[comp][level] != null) {
					tasks.addElement(stripes[comp][level]);
				}
			}
		}
		
		return tasks;
	}

	public void finish(ImageOp caller) {
		for (int comp = 0; comp < componentNum; comp++) {
			if (dwtParam.levelNum < dwtProp.levelNum) {
				for (int level = dwtProp.levelNum; level > dwtParam.levelNum; level--) {
					stripes[comp][level].isCompleted = true;
				}
			} else {
				stripes[comp][dwtProp.levelNum + 1].isCompleted = true;
			}
		}
		
		super.finish(caller);
	}
   
}
