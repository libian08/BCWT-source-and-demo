package y0.wavelet.dwt2d.linebased;

import y0.imageio.linebased.DataInbox;
import y0.imageio.linebased.DataReceiver;
import y0.imageio.linebased.ImageOp;
import y0.wavelet.Const;
import y0.wavelet.DWTProp;

public class LDWT2D97Float extends LDWT2D {
	private class Stripe extends LDWT2D.Stripe {

		private final static int BUFFER_HEIGHT = 6;
		
	    private final static float L_FACTOR = Const.DWT97_K_INVERSE;
	    private final static float H_FACTOR = Const.DWT97_K;

	    private int bufferCount;
	    private float[][] buffer;
	    private float[][] outputBuf;

	    // Temporary buffer for horizontal transform
	    private float[] transHorizontalBuffer;
	    private int width;
	    private int halfWidth;
	    private int width_minus_3;
	    private int width_minus_2;
	    private int halfWidth_minus_1;

		public Stripe(int dwtLevel, int component, int bufferWidth,
				LDWT2D.Stripe sinkLL) {
			super(dwtLevel, component, bufferWidth, sinkLL);
			
			bufferDataType = DataReceiver.TYPE_FLOAT;
	        buffer = new float[BUFFER_HEIGHT][bufferWidth];
	        bufferCount = 1;
	    
	        newOutputBuf();
	        
	        // Fields for 1D-DWT.
	        width = bufferWidth;
	        halfWidth = bufferWidth >> 1;
	        width_minus_3 = bufferWidth - 3;
	        width_minus_2 = bufferWidth - 2;
	        halfWidth_minus_1 = halfWidth - 1;
	        transHorizontalBuffer = new float[bufferWidth];
		}
		
		private void newOutputBuf() {
	        outputBuf = new float[3][];
	        outputBuf[Const.BAND_LH] = new float[bufferWidth];
		}
		
	    protected void transHorizontal() {
	        int i, j;
	        float[] X = buffer[bufferCount];

	        // Step 1
	        for (j = 0; j < width_minus_3; j += 2)
	            X[j + 1] += Const.DWT97_ALPHA * (X[j] + X[j + 2]);
	        X[j + 1] += Const.DWT97_ALPHA2 * X[j];

	        // Step 2
	        X[0] += Const.DWT97_BETA2 * X[1];
	        for (j = 1; j < width_minus_2; j += 2)
	            X[j + 1] += Const.DWT97_BETA * (X[j] + X[j + 2]);
	        
	        // Step 3
	        for (j = 0; j < width_minus_3; j += 2)
	            X[j + 1] += Const.DWT97_GAMMA * (X[j] + X[j + 2]);
	        X[j + 1] += Const.DWT97_GAMMA2 * X[j];
	        
	        // Step 4
	        X[0] += Const.DWT97_DELTA2 * X[1];
	        for (j = 1; j < width_minus_2; j += 2)
	            X[j + 1] += Const.DWT97_DELTA * (X[j] + X[j + 2]);
	        
	        // Step 5
	        for (j = 0; j < width; j++) {
	            X[j] *= L_FACTOR;
	            X[++j] *= H_FACTOR;
	        }

	        // Splitting: Move the low-frequency coefficients to the front half and
	        // the high-frequency coefficients to the back half.
	        System.arraycopy(X, 0, transHorizontalBuffer, 0, width);
	        for (i = 0, j = 1; i < halfWidth_minus_1; j++) {
	            X[halfWidth + i] = transHorizontalBuffer[j++];
	            X[++i] = transHorizontalBuffer[j];
	        }

	    }

		protected Object getOutputLL(int line) {
			return buffer[line];
		}
		
		protected Object getOutputH(int line) {
	        // buffer[line]       |<------LL------>|<------HL------>|<------+
	        // buffer[line+1]     |<------LH------>|<------HH------>|<---+  |
	        //                                                           |  |
	        // outputBuf[BAND_HL] |<------LL------>|<------HL------>|----|--+
	        // outputBuf[BAND_LH] |     (empty)    |<---LH(copy)--->|    |
	        // outputBuf[BAND_HH] |<------LH------>|<------HH------>|----+

			outputBuf[Const.BAND_HL] = buffer[line];
			line++;
			System.arraycopy(buffer[line], 0, outputBuf[Const.BAND_LH], bufferWidthHalf, bufferWidthHalf);
			outputBuf[Const.BAND_HH] = buffer[line];
			
			return outputBuf;
		}
		
		protected void sendDataLast() {
			sendData();
			
			newOutputBuf();
			sendData();
		}
		
	    private void shift2() {
	        float[] b1 = buffer[1];
	        float[] b2 = buffer[2];
	        buffer[1] = buffer[3];
	        buffer[2] = buffer[4];
	        buffer[3] = buffer[5];
	        buffer[4] = b1;
	        buffer[5] = b2;
	    }
		
		protected void transVerticalMiddle() {
	        for (int i = 0; i < bufferWidth; i++) {
	            buffer[4][i] += Const.DWT97_ALPHA * (buffer[3][i] + buffer[5][i]);
	            buffer[3][i] += Const.DWT97_BETA * (buffer[2][i] + buffer[4][i]);
	            buffer[2][i] += Const.DWT97_GAMMA * (buffer[1][i] + buffer[3][i]);
	            buffer[1][i] += Const.DWT97_DELTA * (buffer[0][i] + buffer[2][i]);

	            // Cache line 2 before multiplying the factor.
	            buffer[0][i] = buffer[2][i]; 

	            buffer[2][i] *= H_FACTOR;
	            buffer[1][i] *= L_FACTOR;
	        }
	        shift2();
	        bufferCount = 4;
	        outputLinePointer = 4;
	        outputLineLast = 5;
	    }
	    
		protected void transVerticalFirst() {
	        // First vertical transform with odd mirroring
	        for (int i = 0; i < bufferWidth; i++) {
	            buffer[2][i] += Const.DWT97_ALPHA * (buffer[1][i] + buffer[3][i]);
	            buffer[4][i] += Const.DWT97_ALPHA * (buffer[3][i] + buffer[5][i]);
	            buffer[1][i] += Const.DWT97_BETA2 * buffer[2][i];
	            buffer[3][i] += Const.DWT97_BETA * (buffer[2][i] + buffer[4][i]);
	            buffer[2][i] += Const.DWT97_GAMMA * (buffer[1][i] + buffer[3][i]);
	            buffer[1][i] += Const.DWT97_DELTA2 * buffer[2][i];

	            // Cache line 2 before multiplying the factor.
	            buffer[0][i] = buffer[2][i]; 
	            
	            buffer[1][i] *= L_FACTOR;
	            buffer[2][i] *= H_FACTOR;
	        }
	        shift2();
	        bufferCount = 4;
	        outputLinePointer = 4;
	        outputLineLast = 5;
	    }

	    
		protected void transVerticalLast() {
	        // Last vertical transform with odd mirroring
	        for (int i = 0; i < bufferWidth; i++) {
	            buffer[4][i] += Const.DWT97_ALPHA2 * buffer[3][i];
	            buffer[3][i] += Const.DWT97_BETA * (buffer[2][i] + buffer[4][i]);
	            buffer[2][i] += Const.DWT97_GAMMA * (buffer[1][i] + buffer[3][i]);
	            buffer[4][i] += Const.DWT97_GAMMA2 * buffer[3][i];
	            buffer[1][i] += Const.DWT97_DELTA * (buffer[0][i] + buffer[2][i]);
	            buffer[3][i] += Const.DWT97_DELTA * (buffer[2][i] + buffer[4][i]);
	            
	            buffer[1][i] *= L_FACTOR;
	            buffer[2][i] *= H_FACTOR;
	            buffer[3][i] *= L_FACTOR;
	            buffer[4][i] *= H_FACTOR;
	        }
	        outputLinePointer = 1;
	        outputLineLast = 4;
	    }

		protected void downloadAndTransHorizontal(DataInbox.Item incoming) {
			System.arraycopy((float[])incoming.data, incoming.offset, buffer[bufferCount], 0, incoming.len);
			incoming.sender.ackData(incoming.tag);
			transHorizontal();
			bufferCount++;
		}

		protected boolean isReady() {
			return (bufferCount == BUFFER_HEIGHT);
		}
	}

	public void initialize(ImageOp caller) {
		dwtParam = new DWTProp(param);
		
		dwtParam.setProp(props);

		// Create the DWT stripes and link them up.
		stripes = new Stripe[componentNum][dwtParam.levelNum + 1];
		int level = dwtParam.levelNum;
		for (int comp = 0; comp < componentNum; comp++) {
			stripes[comp][level] = new Stripe(level, comp, width >> (level - 1), null);
		}
		for (int comp = 0; comp < componentNum; comp++) {
			for (level = dwtParam.levelNum - 1; level > 0; level--) {
				stripes[comp][level] = new Stripe(level, comp, width >> (level - 1), 
						stripes[comp][level + 1]);
			}
		}

		super.initialize(caller);
	}

}
