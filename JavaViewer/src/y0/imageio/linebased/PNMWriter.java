package y0.imageio.linebased;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Vector;

import y0.imageio.ROI;
import y0.utils.Task;

public class PNMWriter extends ImageOpImpl implements Task {

    private DataOutput output;
    private byte[] lineBuf;
    private int rowCount;
    public static final int PIXEL_MAX_VALUE = 255;
    public static final char EOL = 10;
    
    private DataInbox[] inboxes;
	private ROI outputROI;
	private boolean isCompleted;
    
    public void initialize(ImageOp caller) {
		inboxes = new DataInbox[componentNum];
		for (int comp = 0; comp < componentNum; comp++) {
			inboxes[comp] = new DataInbox();
		}
		
		super.initialize(caller);
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
    	
    	lineBuf = new byte[componentNum * outputROI.width];
    	
    	for (int comp = 0; comp < componentNum; comp++) {
			source.getOutputPins(comp).setSendTo(inboxes[comp]);
		}
    
    	writeHeader();
        
        rowCount = 0;
    	
    	super.online(caller);
    }
    
    private void writeHeader() {
        StringBuffer header = new StringBuffer();
        
        if (componentNum == 1)
            header.append("P5");
        else
            header.append("P6");
        header.append(EOL);

        header.append(outputROI.width);
        header.append(" ");
        header.append(outputROI.height);
        header.append(EOL);
        header.append(PIXEL_MAX_VALUE);
        header.append(EOL);
        
        try {
			output.writeBytes(header.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	public void setOutput(DataOutput output) throws IOException {
		this.output = output;
	}

	public DataReceiver getInputPins(int tag) {
		return inboxes[tag];
	}

	public DataSender getOutputPins(int tag) {
		return null;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void runOnce() {
		boolean isReady = true;
		for (int comp = 0; comp < componentNum && isReady; comp++) {
			isReady &= (!inboxes[comp].isEmpty());
		}
		if (!isReady)
			return;
		
		for (int comp = 0; comp < componentNum; comp++) {
			DataInbox.Item incoming = inboxes[comp].poll();
			if (incoming.data == null) {
				setCompleted(true);
				break;
			}
			
			switch (incoming.dataType) {
			case DataReceiver.TYPE_FLOAT:
				convertFloat((float[]) incoming.data, incoming.offset, incoming.len, comp);
				break;
			}
			incoming.sender.ackData(incoming.tag);
		}
		
		if (!isCompleted()) {
			try {
				output.write(lineBuf);
			} catch (IOException e) {
				e.printStackTrace();
			}
			rowCount++;
			if (rowCount >= outputROI.height)
				setCompleted(true);
		}
	}

	private void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
		finish(this);
	}

	private void convertFloat(float[] data, int offset, int len, int component) {
        int p = component;
        int valueInt;
        for (int x = 0; x < len; x++, p += componentNum) {
			valueInt = (int) (data[x + offset] + 0.5);
			valueInt = (valueInt < 0 ? 0 : (valueInt > 255 ? 255 : valueInt));
            lineBuf[p] = (byte) valueInt;
        }
	}


	public void run() {
	}

	public Vector getTasks() {
		Vector tasks = new Vector();
		tasks.addElement(this);
		return tasks;
	}
    
	public float getProgress() {
		return (float) rowCount / outputROI.height;
	}
}
