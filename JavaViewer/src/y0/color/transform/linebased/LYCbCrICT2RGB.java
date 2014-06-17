package y0.color.transform.linebased;

import y0.imageio.linebased.DataReceiver;
import y0.imageio.linebased.ImageOp;
import y0.imageio.linebased.ImageProp;
import y0.imageio.linebased.DataInbox.Item;

public class LYCbCrICT2RGB extends LColorTransform {
	
	private float[][] data = new float[3][];
	private int[] offset = new int[3];
	private int len;

	public void initialize(ImageOp caller) {
		ImageProp.setColorSpace(props, ImageProp.COLORSPACE_RGB);
		
		super.initialize(caller);
	}

	protected void ColorTransform(Item[] incomings) {
		float Y, Cb, Cr;
		
		for (int comp = 0; comp < componentNum; comp++) {
			offset[comp] = incomings[0].offset;
			data[comp] = (float[]) incomings[comp].data;
		}
		len = incomings[0].len;
		
        for (int x = 0; x < len; x++) {
            Y = data[0][x + offset[0]];
            Cb = data[1][x + offset[1]] - 127.5f;
            Cr = data[2][x + offset[2]] - 127.5f;
            data[0][x + offset[0]] = Y + 1.4022f * Cr;
            data[1][x + offset[1]] = Y - 0.3456f * Cb - 0.7145f * Cr;
            data[2][x + offset[2]] = Y + 1.7710f * Cb;
        }
	}

	protected void sendData() {
		for (int comp = 0; comp < componentNum; comp++) {
			outboxes[comp].send(data[comp], offset[comp], len, DataReceiver.TYPE_FLOAT);
		}
	}

}
