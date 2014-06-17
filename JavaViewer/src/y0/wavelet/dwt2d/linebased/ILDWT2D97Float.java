package y0.wavelet.dwt2d.linebased;

import java.util.logging.Logger;

import y0.imageio.ROI;
import y0.imageio.linebased.DataInbox;
import y0.imageio.linebased.DataReceiver;
import y0.imageio.linebased.ImageOp;
import y0.imageio.linebased.ImageProp;
import y0.wavelet.Const;
import y0.wavelet.DWTProp;

public class ILDWT2D97Float extends ILDWT2D {

	private static Logger logger = Logger.getLogger(ILDWT2D97Float.class
			.getName());

	private class Stripe extends ILDWT2D.Stripe {
		private final static float L_FACTOR = Const.DWT97_K_INVERSE;
		private final static float H_FACTOR = Const.DWT97_K;

		private final static int BUFFER_HEIGHT = 6;

		protected float[][] buffer;
		protected int bufferCount;
		private int[] bufferReadyTrigger;

		private float[] transHorizontalBuffer;
		private int width_minus_3;
		private int width_minus_2;
		private int halfWidth_minus_1;

		public Stripe(int dwtLevel, int component, ILDWT2D.Stripe sinkLL,
				ROI outputROI, ROI dwtROI) {
			super(dwtLevel, component, sinkLL, outputROI, dwtROI);

			bufferCount = 0;
			bufferReadyTrigger = new int[3];
			bufferReadyTrigger[MODE_VERT_FIRST] = 4;
			bufferReadyTrigger[MODE_VERT_MIDDLE] = 5;
			bufferReadyTrigger[MODE_VERT_LAST] = 3;
			bufferDataType = DataReceiver.TYPE_FLOAT;

			buffer = new float[BUFFER_HEIGHT][bufferWidth];
			transHorizontalBuffer = new float[bufferWidth];
			width_minus_3 = bufferWidth - 3;
			width_minus_2 = bufferWidth - 2;
			halfWidth_minus_1 = bufferWidthHalf - 1;
		}

		protected Object getOutputLine(int line) {
			return buffer[line];
		}

		protected boolean isReady(int modeVerticalTrans) {
			return (bufferCount >= bufferReadyTrigger[modeVerticalTrans]);
		}

		protected void pushIntoBuffer(DataInbox.Item incomingLL,
				DataInbox.Item incoming) {
			bufferCount++;

			switch (incomingLL.dataType) {
			case DataReceiver.TYPE_SHORT:
				// From iBCWT, so short[][].
				short[][] shortLL = (short[][]) incomingLL.data;
				for (int x = 0; x < bufferWidthHalf; x++)
					buffer[bufferCount][x] = (float) shortLL[0][x
							+ incomingLL.offset];
				break;
			case DataReceiver.TYPE_FLOAT:
				// From upper level iDWT stripe, so float[].
				float[] floatLL = (float[]) incomingLL.data;
				System.arraycopy(floatLL, incomingLL.offset,
						buffer[bufferCount], 0, bufferWidthHalf);
				break;
			default:
				throw new IllegalArgumentException("Wrong data type : "
						+ incomingLL.dataType);
			}

			switch (incoming.dataType) {
			case DataReceiver.TYPE_SHORT:
				short[][] shortLine = (short[][]) incoming.data;
				for (int x = 0; x < bufferWidthHalf; x++)
					buffer[bufferCount][x + bufferWidthHalf] = (float) shortLine[Const.BAND_HL][x
							+ incoming.offset];
				bufferCount++;
				for (int x = 0; x < bufferWidthHalf; x++) {
					buffer[bufferCount][x] = (float) shortLine[Const.BAND_LH][x
							+ incoming.offset];
					buffer[bufferCount][x + bufferWidthHalf] = (float) shortLine[Const.BAND_HH][x
							+ incoming.offset];
				}
				break;
			case DataReceiver.TYPE_FLOAT:
				float[][] floatLine = (float[][]) incoming.data;
				System.arraycopy(floatLine[Const.BAND_HL], incoming.offset,
						buffer[bufferCount], bufferWidthHalf, bufferWidthHalf);
				bufferCount++;
				System.arraycopy(floatLine[Const.BAND_LH], incoming.offset,
						buffer[bufferCount], 0, bufferWidthHalf);
				System.arraycopy(floatLine[Const.BAND_HH], incoming.offset,
						buffer[bufferCount], bufferWidthHalf, bufferWidthHalf);
				break;
			default:
				throw new IllegalArgumentException("Wrong data type : "
						+ incoming.dataType);
			}
		}

		private void transHorizontal(int line) {
			int i, j;

			// IDWT horizontally of the last 2 lines in the buffer.
			// for (int line = bufferCount - 1; line <= bufferCount; line++) {
			// for (int line = 1; line <= 2; line++) {
			float[] X = buffer[line];

			// Merge (interleave) the low- and high- frequency coefficients.
			System.arraycopy(X, 0, transHorizontalBuffer, 0, bufferWidth);
			for (i = 0, j = 1; i < halfWidth_minus_1; j++) {
				X[j++] = transHorizontalBuffer[bufferWidthHalf + i];
				X[j] = transHorizontalBuffer[++i];
			}

			// Step 5
			for (j = 0; j < bufferWidth; j++) {
				X[j] *= H_FACTOR;
				X[++j] *= L_FACTOR;
			}

			// Step 4
			X[0] -= Const.DWT97_DELTA2 * X[1];
			for (j = 1; j < width_minus_2; j += 2)
				X[j + 1] -= Const.DWT97_DELTA * (X[j] + X[j + 2]);

			// Step 3
			for (j = 0; j < width_minus_3; j += 2)
				X[j + 1] -= Const.DWT97_GAMMA * (X[j] + X[j + 2]);
			X[j + 1] -= Const.DWT97_GAMMA2 * X[j];

			// Step 2
			X[0] -= Const.DWT97_BETA2 * X[1];
			for (j = 1; j < width_minus_2; j += 2)
				X[j + 1] -= Const.DWT97_BETA * (X[j] + X[j + 2]);

			// Step 1
			for (j = 0; j < width_minus_3; j += 2)
				X[j + 1] -= Const.DWT97_ALPHA * (X[j] + X[j + 2]);
			X[j + 1] -= Const.DWT97_ALPHA2 * X[j];
			// }
		}

		protected void transVerticalFirst() {
			// First vertical transform with odd mirroring
			for (int i = 0; i < bufferWidth; i++) {
				buffer[1][i] *= H_FACTOR;
				buffer[2][i] *= L_FACTOR;
				buffer[3][i] *= H_FACTOR;
				buffer[4][i] *= L_FACTOR;

				buffer[1][i] -= Const.DWT97_DELTA2 * buffer[2][i];
				buffer[3][i] -= Const.DWT97_DELTA
						* (buffer[2][i] + buffer[4][i]);
				buffer[2][i] -= Const.DWT97_GAMMA
						* (buffer[1][i] + buffer[3][i]);
				buffer[1][i] -= Const.DWT97_BETA2 * buffer[2][i];

				// Cache line-1 for later vertical transform.
				buffer[0][i] = buffer[1][i];
			}

			transHorizontal(1);

			// Rotate the buffer by 1 line.
			float[] tmp1 = buffer[1];
			buffer[1] = buffer[2];
			buffer[2] = buffer[3];
			buffer[3] = buffer[4];
			buffer[4] = buffer[5];
			buffer[5] = tmp1;

			outputLinePointer = 5;
			outputLineLast = 5;
			bufferCount = 3;
		}

		protected void transVerticalLast() {
			// Last vertical transform with odd mirroring
			for (int i = 0; i < bufferWidth; i++) {
				buffer[3][i] -= Const.DWT97_GAMMA2 * buffer[2][i];
				buffer[2][i] -= Const.DWT97_BETA
						* (buffer[1][i] + buffer[3][i]);
				buffer[1][i] -= Const.DWT97_ALPHA
						* (buffer[0][i] + buffer[2][i]);
				buffer[3][i] -= Const.DWT97_ALPHA2 * buffer[2][i];
			}
			outputLinePointer = 1;
			outputLineLast = 3;
			bufferCount = 0;

			transHorizontal(1);
			transHorizontal(2);
			transHorizontal(3);
		}

		protected void transVerticalMiddle() {
			for (int i = 0; i < bufferWidth; i++) {
				buffer[4][i] *= H_FACTOR;
				buffer[5][i] *= L_FACTOR;

				buffer[4][i] -= Const.DWT97_DELTA
						* (buffer[3][i] + buffer[5][i]);
				buffer[3][i] -= Const.DWT97_GAMMA
						* (buffer[2][i] + buffer[4][i]);
				buffer[2][i] -= Const.DWT97_BETA
						* (buffer[1][i] + buffer[3][i]);
				buffer[1][i] -= Const.DWT97_ALPHA
						* (buffer[0][i] + buffer[2][i]);

				// Cache line-2 for later vertical transform.
				buffer[0][i] = buffer[2][i];
			}

			transHorizontal(1);
			transHorizontal(2);

			// Rotate the buffer by 2 line.
			float[] tmp1 = buffer[1];
			float[] tmp2 = buffer[2];
			buffer[1] = buffer[3];
			buffer[2] = buffer[4];
			buffer[3] = buffer[5];
			buffer[4] = tmp1;
			buffer[5] = tmp2;

			outputLinePointer = 4;
			outputLineLast = 5;
			bufferCount = 3;
		}

	}

	private class StripeBottom extends Stripe {

		private float scalingFactor;

		public StripeBottom(int dwtLevel, int component, ROI outputROI,
				ROI dwtROI) {
			super(dwtLevel, component, null, outputROI, dwtROI);

			scalingFactor = 1.0f / (1 << (dwtLevel - 1));
		}

		protected Object getOutputLine(int line) {
			float[] data = buffer[line];

			if (dwtLevel > 1) {
				for (int x = outputX; x <= outputXRight; x++)
					data[x] *= scalingFactor;
			}

			return data;
		}

	}

	private class StripeLL extends Stripe {

		private float scalingFactor;

		public StripeLL(int dwtLevel, int component, ROI outputROI, ROI dwtROI) {
			super(dwtLevel, component, null, outputROI, dwtROI);

			scalingFactor = 1.0f / (1 << dwtLevel);
		}

		protected void pushIntoBuffer(DataInbox.Item incomingLL) {
			// From iBCWT, so short[][].
			short[][] shortLL = (short[][]) incomingLL.data;
			for (int x = 0; x < bufferWidth; x++)
				buffer[0][x] = (float) shortLL[0][x + incomingLL.offset];

			outputLinePointer = 0;
			outputLineLast = 0;
		}

		public void runOnce() {
			if (!outbox.isEmpty() || inboxLL.isEmpty())
				return;

			DataInbox.Item incomingLL = inboxLL.poll();

			if (incomingLL.isEOF()) {
				outbox.sendEOF();
				isCompleted = true;
				return;
			}

			pushIntoBuffer(incomingLL);
			incomingLL.sender.ackData(incomingLL.tag);
			sendData();
		}

		protected Object getOutputLine(int line) {
			float[] data = buffer[line];

			for (int x = outputX; x <= outputXRight; x++)
				data[x] *= scalingFactor;

			return data;
		}

	}

	protected ROI[] getDWTROI() {
		ROI[] dwtROI = new ROI[dwtProp.levelNum + 1];

		int imgWidthReal = ImageProp.getWidthReal(props);
		int imgHeightReal = ImageProp.getHeightReal(props);

		// Check parameters.
		if (imgParam.roi.x < 0 || imgParam.roi.x >= imgWidthReal) {
			logger.warning("ROI.x is out of bound: " + imgParam.roi.x
					+ ", must be within (0, " + (imgWidthReal - 1)
					+ "), setting to 0.");
			imgParam.roi.x = 0;
		}
		if (imgParam.roi.y < 0 || imgParam.roi.y >= imgHeightReal) {
			logger.warning("ROI.y is out of bound: " + imgParam.roi.y
					+ ", must be within (0, " + (imgHeightReal - 1)
					+ "), setting to 0.");
			imgParam.roi.y = 0;
		}
		if (imgParam.roi.x + imgParam.roi.width > imgWidthReal) {
			logger.warning("ROI.x + ROI.width is out of bound: "
					+ (imgParam.roi.x + imgParam.roi.width)
					+ ", must be within (1, " + imgWidthReal + "), setting to "
					+ imgWidthReal);
			imgParam.roi.width = imgWidthReal - imgParam.roi.x;
		}
		if (imgParam.roi.y + imgParam.roi.height > imgHeightReal) {
			logger.warning("ROI.y + ROI.height is out of bound: "
					+ (imgParam.roi.y + imgParam.roi.height)
					+ ", must be within (1, " + imgHeightReal
					+ "), setting to " + imgHeightReal);
			imgParam.roi.height = imgHeightReal - imgParam.roi.y;
		}

		// (imgROIExt-imgParam.roi) are the pixels must be computed but
		// discarded to ensure the correctness in imgParam.roi.
		//
		// | xDif |
		// |<---->|
		// ``````````````````````......-----
		// `````imgROIExt````````...... ^
		// ``````````````````````...... | yDif
		// ``````````````````````...... v
		// ```````OOOOOOOOOOOOOOOO.....-----
		// ```````OOOOOOOOOOOOOOOO.....
		// ```````OOOOOOOOOOOOOOOO.....
		// ```````OOOOOOOOOOOOOOOO.....
		// ```````O imgParam.roi O.....
		// ```````OOOOOOOOOOOOOOOO.....
		// ```````OOOOOOOOOOOOOOOO.....
		// ```````OOOOOOOOOOOOOOOO.....
		// ```````OOOOOOOOOOOOOOOO.....
		// ............................
		// ............................
		// ............................
		// ............................
		ROI imgROIExt = new ROI();

		// Compute imgROIExt.
		{
			// Compute the imgROIExt.x
			int dwtPower2 = 1 << dwtProp.levelNum;
			imgROIExt.x = imgParam.roi.x - 3 * (dwtPower2 - 1);
			// x0 must be multiples of 2^dwtLevel and <= current x0
			imgROIExt.x = imgROIExt.x / dwtPower2 * dwtPower2;
			if (imgROIExt.x < 0)
				imgROIExt.x = 0;
			// Compute imgROIExt.width
			int xDif = imgParam.roi.x - imgROIExt.x;
			imgROIExt.width = xDif + imgParam.roi.width;
			if (imgROIExt.width % 2 == 1)
				imgROIExt.width++;

			// Compute the imgROIExt.y
			imgROIExt.y = imgParam.roi.y - 3 * (dwtPower2 - 1);
			// y0 must be multiples of 2^dwtLevel and <= current y0
			imgROIExt.y = imgROIExt.y / dwtPower2 * dwtPower2;
			if (imgROIExt.y < 0)
				imgROIExt.y = 0;
			// Compute imgROIExt.height
			int yDif = imgParam.roi.y - imgROIExt.y;
			imgROIExt.height = yDif + imgParam.roi.height;
			if (imgROIExt.height % 2 == 1)
				imgROIExt.height++;
		}

		// Compute the width of each IDWT ceoffROI.
		{
			// level-0
			int level = 0;
			dwtROI[level] = new ROI();
			dwtROI[level].x = imgROIExt.x;
			dwtROI[level].y = imgROIExt.y;

			// Should be 4 pixels larger so that the last coefficient
			// can be computed correctly (boundary condition).
			// But it should not exceed the boundary.
			dwtROI[level].width = imgROIExt.width + 4;
			if (dwtROI[level].width + imgROIExt.x > width)
				dwtROI[level].width = width - imgROIExt.x;

			dwtROI[level].height = imgROIExt.height + 4;
			if (dwtROI[level].height + imgROIExt.y > height)
				dwtROI[level].height = height - imgROIExt.y;

			// level-1 to level-(dwtProp.levelNum-1)
			for (level = 1; level < dwtProp.levelNum; level++) {
				dwtROI[level] = new ROI();
				dwtROI[level].x = imgROIExt.x >> level;
				dwtROI[level].y = imgROIExt.y >> level;

				int thisLevelWidth = width >> level;
				dwtROI[level].width = (dwtROI[level - 1].width >> 1) + 4;
				if (dwtROI[level].width + dwtROI[level].x > thisLevelWidth)
					dwtROI[level].width = thisLevelWidth - dwtROI[level].x;
				else if (dwtROI[level].width % 2 == 1)
					dwtROI[level].width++;

				int thisLevelHeight = height >> level;
				dwtROI[level].height = (dwtROI[level - 1].height >> 1) + 4;
				if (dwtROI[level].height + dwtROI[level].y > thisLevelHeight)
					dwtROI[level].height = thisLevelHeight - dwtROI[level].y;
				else if (dwtROI[level].height % 2 == 1)
					dwtROI[level].height++;

			}

			// level-(dwtProp.levelNum)
			level = dwtProp.levelNum;
			dwtROI[level] = new ROI();
			dwtROI[level].x = imgROIExt.x >> level;
			dwtROI[level].y = imgROIExt.y >> level;
			dwtROI[level].height = dwtROI[level - 1].height >> 1;
			dwtROI[level].width = dwtROI[level - 1].width >> 1;
		}

		return dwtROI;
	}

	public void initialize(ImageOp caller) {
		dwtProp = new DWTProp(props);

		dwtParam = new DWTProp(param);
		if (dwtParam.levelNum == DWTProp.NAN) {
			dwtParam.levelNum = 0;
		}

		imgParam = new ImageProp(param);
		if (imgParam.roi == null) {
			imgParam.roi = new ROI();
			imgParam.roi.x = 0;
			imgParam.roi.y = 0;
			imgParam.roi.width = ImageProp.getWidthReal(props);
			imgParam.roi.height = ImageProp.getHeightReal(props);
			ImageProp.setROI(param, imgParam.roi);
		}

		dwtParam.roi = getDWTROI();
		dwtParam.setProp(param);

		ROI outputROI = new ROI();
		outputROI.x = imgParam.roi.x >> dwtParam.levelNum;
		outputROI.y = imgParam.roi.y >> dwtParam.levelNum;
		outputROI.width = imgParam.roi.width >> dwtParam.levelNum;
		outputROI.height = imgParam.roi.height >> dwtParam.levelNum;
		ImageProp.setROI(result, outputROI);

		stripes = new Stripe[componentNum][dwtProp.levelNum + 2];
		if (dwtParam.levelNum < dwtProp.levelNum) {
			// Create the IDWT stripes and link them up.
			// Note: level-n IDWT stripe's buffer size is dwtROI[n-1].
			int level = dwtParam.levelNum + 1;

			for (int comp = 0; comp < componentNum; comp++) {
				ROI dwtROI = dwtParam.roi[level - 1];
				stripes[comp][level] = new StripeBottom(level, comp, outputROI,
						dwtROI);
			}
			for (int comp = 0; comp < componentNum; comp++) {
				for (level = dwtParam.levelNum + 2; level <= dwtProp.levelNum; level++) {
					ROI dwtROI = dwtParam.roi[level - 1];
					stripes[comp][level] = new Stripe(level, comp,
							stripes[comp][level - 1], dwtROI, dwtROI);
				}
			}
		} else {
			for (int comp = 0; comp < componentNum; comp++) {
				stripes[comp][dwtProp.levelNum + 1] = new StripeLL(
						dwtProp.levelNum, comp, outputROI, outputROI);
			}
		}

		super.initialize(caller);
	}
}
