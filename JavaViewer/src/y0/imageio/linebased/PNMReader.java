package y0.imageio.linebased;

import java.io.DataInput;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;

import y0.utils.Task;

public class PNMReader extends ImageOpImpl implements Task {
	private DataOutbox[] outboxes;
    private DataInput input;
    private byte[] lineBuf;
    private float[][] floatBuf;
    
    private int rowIndex;

	private boolean isCompleted;
	
	public PNMReader(Hashtable props, Hashtable param, Hashtable result) {
		this.props = props;
		this.param = param;
		this.result = result;
	}
    
    public void initialize(ImageOp caller) {
        outboxes = new DataOutbox[componentNum];
        for (int comp = 0; comp < componentNum; comp++) {
			outboxes[comp] = new DataOutbox();
			outboxes[comp].setTag(comp);
		}
        
        lineBuf = new byte[width * componentNum];
        floatBuf = new float[componentNum][width];
        rowIndex = 0;
        
        super.initialize(caller);
    }
    
    private void readHeader() {
        // Using a Scanner directly on InputStream will cause the stream
        // pointer to move too far due to its buffering.
        int readValue;
        StringBuilder header = new StringBuilder();
        try {
			for (int newLineCount = 0; newLineCount < 3;) {
				readValue = input.readByte();
				header.append((char) readValue);
				if (readValue == 0xa)
					newLineCount++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        Scanner scanner = new Scanner(header.toString());
        componentNum = (scanner.next("P\\d").equalsIgnoreCase("P5")) ? 1 : 3;
        // TODO: Test the following: skip comments if exists.
        scanner.skip("#*");
        width = scanner.nextInt();
        height = scanner.nextInt();
        // Pixel-max-value, not very useful.
        scanner.nextInt();

        ImageProp.setWidth(props, width);
        ImageProp.setWidthReal(props, width);
        ImageProp.setHeight(props, height);
        ImageProp.setHeightReal(props, height);
        ImageProp.setComponentNum(props, componentNum);
        ImageProp.setColorSpace(props, componentNum == 3 ? ImageProp.COLORSPACE_RGB : ImageProp.COLORSPACE_GRAY);
    }
    
    public void online(ImageOp caller) {
    	for (int comp = 0; comp < componentNum; comp++) {
			getOutputPins(comp).setSendTo(sink.getInputPins(comp));
		}
    	
    	super.online(caller);
    }

	public void setInput(DataInput input) throws IOException {
		this.input = input;
		readHeader();
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void runOnce() {
		boolean isReady = true;
		for (int comp = 0; comp < componentNum && isReady; comp++) {
			isReady &= (outboxes[comp].isEmpty());
		}
		if (!isReady)
			return;
		
		// Read in one line.
    	if (rowIndex++ < height) {
    		try {
				input.readFully(lineBuf);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	sendDataFloat();
    	} else {
    		for (int comp = 0; comp < componentNum; comp++) {
    			outboxes[comp].send(null, 0, 0, DataReceiver.TYPE_UNKNOWN);
    		}
			isCompleted = true;
    	}
	}

	private void sendDataFloat() {
		int p = 0;
		for (int x = 0; x < width; x++) {
		    for (int c = 0; c < componentNum; c++) {
		    	floatBuf[c][x] = (float) (lineBuf[p++] & 0xFF);
		    }
		}
		for (int comp = 0; comp < componentNum; comp++) {
			outboxes[comp].send(floatBuf[comp], 0, width, DataReceiver.TYPE_FLOAT);
		}
	}

	public void run() {
	}

	public DataReceiver getInputPins(int tag) {
		return null;
	}

	public DataSender getOutputPins(int tag) {
		return outboxes[tag];
	}

	public Vector getTasks() {
		Vector tasks = new Vector();
		tasks.addElement(this);
		return tasks;
	}
 
	public float getProgress() {
		return (float) rowIndex / height;
	}
}
