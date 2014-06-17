package y0.color.transform.linebased;

import y0.imageio.linebased.DataReceiver;
import y0.imageio.linebased.ImageOp;
import y0.imageio.linebased.ImageProp;
import y0.imageio.linebased.DataInbox.Item;

public class LRGB2YCbCrICT extends LColorTransform {
	
	private float[][] data = new float[3][];
	private int[] offset = new int[3];
	private int len;

	public void initialize(ImageOp caller) {
		ImageProp.setColorSpace(props, ImageProp.COLORSPACE_YCBCRICT);
		
		super.initialize(caller);
	}

	protected void ColorTransform(Item[] incomings) {
		float r, g, b;
		
		for (int comp = 0; comp < componentNum; comp++) {
			offset[comp] = incomings[0].offset;
			data[comp] = (float[]) incomings[comp].data;
		}
		len = incomings[0].len;
		
        for (int x = 0; x < len; x++) {
            r = data[0][x + offset[0]];
            g = data[1][x + offset[1]];
            b = data[2][x + offset[2]];
            data[0][x + offset[0]] = 0.2989f * r + 0.5866f * g + 0.1145f * b;
            data[1][x + offset[1]] = -0.1687f * r - 0.3312f * g + 0.5000f * b + 127.5f;
            data[2][x + offset[2]] = 0.5000f * r - 0.4183f * g - 0.0816f * b + 127.5f;
        }
	}

	protected void sendData() {
		for (int comp = 0; comp < componentNum; comp++) {
			outboxes[comp].send(data[comp], offset[comp], len, DataReceiver.TYPE_FLOAT);
		}
	}

}
