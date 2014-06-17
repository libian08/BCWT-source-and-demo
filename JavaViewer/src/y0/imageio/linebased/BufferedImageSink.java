package y0.imageio.linebased;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Vector;

import y0.imageio.ROI;
import y0.utils.Task;

public class BufferedImageSink extends ImageOpImpl implements Task {
	private BufferedImage image;
	private WritableRaster tempRaster;
	private int rowCount;
	private int[] buf;
	
	private DataInbox[] inboxes;
	
	public BufferedImageSink() {
	}
	
	public BufferedImageSink(BufferedImage image) {
		setImage(image);
	}
	
	public BufferedImage getImage() {
		return image;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	private ROI outputROI;
	private boolean isCompleted;
	private boolean convertGrayToRGB;
	
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
    	
    	
		if (image == null) {
			image = new BufferedImage(outputROI.width, outputROI.height,
					componentNum == 1 ? BufferedImage.TYPE_BYTE_GRAY
							: BufferedImage.TYPE_INT_RGB);
		}
		
		// Check if the properties of image match with the decoded image.
		if (image.getWidth() < outputROI.width || image.getHeight() < outputROI.height)
			throw new IllegalStateException("image size is too small");
		if (image.getColorModel().getNumColorComponents() == 3 && componentNum == 1) {
			// A special case, allowing converting a gray scale image into RGB.
			convertGrayToRGB = true;
			buf = new int[3 * outputROI.width];
		} else if (image.getColorModel().getNumColorComponents() != componentNum) {
			throw new IllegalStateException("image must have exactly " + componentNum + " component(s).");
		} else {
			buf = new int[componentNum * outputROI.width];
		}
			
		tempRaster = image.getRaster();
		rowCount = 0;
    	
    	for (int comp = 0; comp < componentNum; comp++) {
			source.getOutputPins(comp).setSendTo(inboxes[comp]);
		}
		
		super.online(caller);
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
				if (convertGrayToRGB) {
					convertFloat((float[]) incoming.data, incoming.offset, incoming.len, 0, 3);
					convertFloat((float[]) incoming.data, incoming.offset, incoming.len, 1, 3);
					convertFloat((float[]) incoming.data, incoming.offset, incoming.len, 2, 3);
				} else {
					convertFloat((float[]) incoming.data, incoming.offset, incoming.len, comp, componentNum);
				}
				break;
			}
			incoming.sender.ackData(incoming.tag);
		}
		
		if (!isCompleted()) {
			tempRaster.setPixels(0, rowCount, outputROI.width, 1, buf);
			rowCount++;
			if (rowCount >= outputROI.height)
				setCompleted(true);
		}

	}
	
	private void convertFloat(float[] data, int offset, int len, int component, int componentNum) {
		int valueInt;
		int xi, xia, p;
		for (xi = 0, xia = 0, p = component; xia < len; ++xi, ++xia, p += componentNum) {
			valueInt = (int) (data[xi + offset] + 0.5);
			valueInt = (valueInt < 0 ? 0 : (valueInt > 255 ? 255 : valueInt));
			buf[p] = valueInt;
		}
	}


	private void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
		finish(this);
	}

	public void run() {
	}

	public DataReceiver getInputPins(int tag) {
		return inboxes[tag];
	}

	public DataSender getOutputPins(int tag) {
		return null;
	}

	public Vector getTasks() {
		Vector tasks = new Vector();
		tasks.addElement(this);
		return tasks;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public float getProgress() {
		return (float) rowCount / outputROI.height;
	}
}
