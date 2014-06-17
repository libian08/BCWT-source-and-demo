package y0.imageio.linebased;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Hashtable;
import java.util.Vector;

import y0.utils.Task;

public class RenderedImageSource extends ImageOpImpl implements Task {
	
	private Raster tempRaster;
	private int[] buf;
	private float[][] bufFloat;
	private int row;
	
	private boolean isCompleted = false;
	private DataOutbox[] outboxes;
	
	public RenderedImageSource(RenderedImage image) {
		tempRaster = image.getData();
		width = image.getWidth();
		height = image.getHeight();
		componentNum = image.getColorModel().getNumComponents();
		
		props = new Hashtable();
		ImageProp.setWidth(props, width);
		ImageProp.setWidthReal(props, width);
		ImageProp.setHeight(props, height);
		ImageProp.setHeightReal(props, height);
		ImageProp.setComponentNum(props, componentNum);
		ImageProp.setColorSpace(props, componentNum == 3 ? ImageProp.COLORSPACE_RGB : ImageProp.COLORSPACE_GRAY);
		
		outboxes = new DataOutbox[componentNum];
		for (int comp = 0; comp < componentNum; comp++) {
			outboxes[comp] = new DataOutbox();
			outboxes[comp].setTag(comp);
		}
		
		buf = new int[width * componentNum];
		bufFloat = new float[componentNum][width];
		row = 0;
	}
	
	public DataReceiver getInputPins(int tag) {
		return null;
	}

	public DataSender getOutputPins(int tag) {
		return outboxes[tag];
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void run() {
		while (!isCompleted()) {
			runOnce();
		}
	}

	public void runOnce() {
		boolean isReady = true;
		for (int comp = 0; comp < componentNum; comp++) {
			isReady &= outboxes[comp].isEmpty(); 
		}
		if (!isReady)
			return;
		
		// Read in one line.
		if (row < height) {
			tempRaster.getPixels(0, row++, width, 1, buf);
		} else {
			for (int comp = 0; comp < componentNum; comp++) {
				outboxes[comp].sendEOF();
			}
			isCompleted = true;
			return;
		}
	
		// Convert and copy this line.
		for (int bi = 0; bi < componentNum; bi++) {
			for (int xi = 0, xia = 0, bufi = bi; xia < width; ++xi, ++xia, bufi += componentNum) {
				bufFloat[bi][xi] = (float) buf[bufi];
			}
		}
		for (int comp = 0; comp < componentNum; comp++) {
			outboxes[comp].send(bufFloat[comp], 0, width, DataReceiver.TYPE_FLOAT);
		}
		
	}

	public void online(ImageOp caller) {
		for (int comp = 0; comp < componentNum; comp++) {
			getOutputPins(comp).setSendTo(sink.getInputPins(comp));
		}
		
		super.online(caller);
	}

	public Vector getTasks() {
		Vector tasks = new Vector();
		tasks.addElement(this);
		return tasks;
	}

	public float getProgress() {
		return (float) row / height;
	}

}
