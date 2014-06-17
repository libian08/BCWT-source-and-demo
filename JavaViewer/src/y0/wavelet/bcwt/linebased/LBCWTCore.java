package y0.wavelet.bcwt.linebased;

import java.util.Vector;

import y0.imageio.linebased.DataInbox;
import y0.imageio.linebased.DataReceiver;
import y0.imageio.linebased.DataSender;
import y0.imageio.linebased.ImageOp;
import y0.imageio.linebased.ImageOpImpl;
import y0.utils.ArrayOp;
import y0.utils.Task;
import y0.utils.TaskGroup;
import y0.wavelet.DWTProp;
import y0.wavelet.DWTTag;
import y0.wavelet.WTNode;
import y0.wavelet.bcwt.linebased.LBCWTOutput.DataHandler;

public class LBCWTCore extends ImageOpImpl implements TaskGroup {
    
    private abstract class StripeBase implements Task {

        // Coefficient buffer
        protected short[][][] coeff;
        protected int coeffLineCount;

        // MQD buffer
        protected byte[][][] mqd;
        protected int mqdLineCount;
        
        protected DataInbox inbox;
        
        protected int bandNum;
        protected int coeffWidth;
        protected int dwtLevel;
        protected int blockWidth;
        protected int blockHeight;
        protected int blockX;
        protected int remainingWidth;
        protected int remainingHeight;

        protected WTNode coeffUnit;
        protected WTNode mqdUnit;
    
        protected int qMin;
        protected int qMinMask;
        
        protected LBCWTOutput.DataHandler sink;
        protected int trigger;
		protected int numOfUnits;
		protected byte[][][] mqdLower;
		protected StripeBase encoderLower;
		protected boolean isCompleted;
    
        protected final static int BITS_QMAX = 3;
    
        protected int completedBand;

        public StripeBase(int dwtLevel, int bandNum, int coeffWidth, int coeffHeight, int mqdHeight, int trigger, StripeBase encoderLower, int blockWidth, int blockHeight) {
            this.dwtLevel = dwtLevel;
        	this.bandNum = bandNum;
            this.coeffWidth = coeffWidth;
            this.trigger = trigger;
            this.numOfUnits = coeffWidth >> 1;
            this.blockWidth = blockWidth;
            this.blockHeight = blockHeight;
            
            inbox = new DataInbox();
            
            remainingHeight = blockHeight;
            if (encoderLower != null) {
	            this.encoderLower = encoderLower;
	            this.mqdLower = encoderLower.getMQD();
            }
            
            coeff = new short[bandNum][coeffHeight][coeffWidth];
            coeffLineCount = 0;
            
            if (mqdHeight > 0) {
            	mqd = new byte[bandNum][mqdHeight][numOfUnits];
            	mqdLineCount = 0;
            }
            
            // Initialize coding unit.
            coeffUnit = new WTNode();
            mqdUnit = new WTNode();
            WTNode cTop = coeffUnit;
            WTNode mTop = mqdUnit;
            cTop.offspring = new WTNode[4];
            mTop.offspring = new WTNode[4];
            for (int i = 0; i < 4; ++i) {
                cTop.offspring[i] = new WTNode();
                mTop.offspring[i] = new WTNode();
            }
            coeffUnit.dwtLevel = dwtLevel + 1;
            mqdUnit.dwtLevel = dwtLevel + 1;
			coeffUnit.y = 0;
			mqdUnit.y = 0;

            completedBand = 0;
            isCompleted = false;
        }

        protected boolean isNewBlockX() {
			// Check if we are stepping into next block.
			remainingWidth -= 2;
			if (remainingWidth == 0) {
				remainingWidth = blockWidth;
				sink.setBlockX(++blockX);
				return true;
			}
			return false;
		}

        protected boolean isBlockYCompleted() {
			// Two more lines are encoded, check if a block is completed.
			remainingHeight -= 2;
			if (remainingHeight == 0) {
				remainingHeight = blockHeight;
				sink.signalBlocksCompleted();
				return true;
			}
			return false;
		}

        protected abstract void encode();
        
		protected void getCoeffOffspring(WTNode cNode, short[][][] coeff) {
            WTNode.initOffspring(cNode, cNode.offspring);
            for (int i = 0; i < 4; ++i) {
                WTNode cPos = cNode.offspring[i];
                int value = coeff[cPos.subbandType][cPos.y][cPos.x];
                cPos.absValueInt = Math.abs(value) & qMinMask;
                cPos.sign = (value >= 0);
            }
        }

		protected byte[][][] getMQD() {
			return mqd;
		}
		
		public int getQMin() {
            return qMin;
        }

		protected void initBlockX() {
			blockX = -1;
			remainingWidth = 2;
		}

		public boolean isCompleted() {
			return isCompleted;
		}

		protected void downloadFloat(float[] data, int offset, int len, int band) {
			short[] coeffLine = coeff[band][coeffLineCount];
            for (int i = 0; i < coeffWidth; i++) {
                coeffLine[i] = (short) (data[i + offset]);
            }
		}
		
		protected void processInboxItem(DataInbox.Item incoming) {
			// Download and convert the incoming data.
			int dwtBand = DWTTag.getDwtBand(incoming.tag);
			switch (incoming.dataType) {
			case DataReceiver.TYPE_FLOAT:
				if (dwtBand == y0.wavelet.Const.BAND_LL) {
					downloadFloat((float[])incoming.data, incoming.offset, incoming.len, Const.BCWT_LEVEL_LL);
				} else {
					float[][] dataFloat = (float[][]) incoming.data;
					for (int band = 0; band < 3; band++) {
						downloadFloat(dataFloat[band], incoming.offset, incoming.len, band);
					}
				}
				coeffLineCount++;
	            break;
	        default:
	        	throw new IllegalStateException("Unknow data type : " + incoming.dataType);
			}
	        incoming.sender.ackData(incoming.tag);
	        
            if (coeffLineCount >= trigger) {
            	encode();
            }
		    
		}

		protected void recycleMQD() {
        	// Recycle top two lines.
        	synchronized (mqd) {
	        	for (int b = 0; b < bandNum; b++) {
	        		byte[] tmp0 = mqd[b][0];
	        		byte[] tmp1 = mqd[b][1];
	        		mqd[b][0] = mqd[b][2];
	        		mqd[b][1] = tmp0;
	        		mqd[b][2] = tmp1;
	        	}
	        	mqdLineCount -= 2;
        	}
		}

		public void run() {
		}

		public void runOnce() {
			if (inbox.isEmpty()) {
				return;
			}

			// Get one incoming into our buffer and acknowledge the sender.
			DataInbox.Item incoming = inbox.poll();
			if (incoming.data == null) {
				isCompleted = true;
				return;
			}
			
	        processInboxItem(incoming);
		}

		public void setQMin(final int qMin) {
            this.qMin = qMin;
            qMinMask = (-1 << qMin);
        }

		public void setSink(DataHandler sink) {
			this.sink = sink; 
		}
		
		public DataReceiver getInputPin() {
			return inbox;
		}
    }//private abstract class StripeBase implements Task

    private class StripeBottom extends StripeBase {
        private final static int BUF_BAND_NUM = 3;
        private final static int BUF_COEFF_HEIGHT = 2;
        private final static int BUF_COEFF_TRIGGER = 2;
        private final static int BUF_MQD_HEIGHT = 3;

		public StripeBottom(int dwtLevel, int coeffWidth, int blockWidth, int blockHeight) {
			super(dwtLevel, BUF_BAND_NUM, coeffWidth, BUF_COEFF_HEIGHT, BUF_MQD_HEIGHT, BUF_COEFF_TRIGGER, null, blockWidth, blockHeight);
		}

		protected void encode() {
			byte[] mqdCurrent;

			for (int b = 0; b < bandNum; ++b) {
				coeffUnit.subbandType = b;
				mqdUnit.subbandType = b;
				
				// Get a reference to a new line in MQD.
				// Higher level encoder may be recycling my MQD, thus sync is needed.
				synchronized (mqd) {
					mqdCurrent = mqd[b][mqdLineCount];
				}

				initBlockX();
				for (int x = 0; x < numOfUnits; ++x) {
					isNewBlockX();
					
					coeffUnit.x = x;
					mqdUnit.x = x;

					// Put the coefficients and lower level's MQDs into the coding unit.
					getCoeffOffspring(coeffUnit, coeff);

					// Encode the coding unit.
					BCWTUnit.encodeUnitBottom(coeffUnit, mqdUnit, qMin, sink);
					
					// Put the MQD into my MQD buffer.
					mqdCurrent[x] = (byte) mqdUnit.absValueInt;

				}
			}
			coeffLineCount -= 2;
			
			// Higher level encoder may be recycling my MQD, thus sync is needed.
			synchronized (mqd) {
				mqdLineCount++;
			}

			isBlockYCompleted();
		}
    	
    }
    
    private class StripeLL extends StripeBase {
        private final static int BUF_BAND_NUM = 1;
        private final static int BUF_COEFF_HEIGHT = 2;
        private final static int BUF_COEFF_TRIGGER = 2;
        private final static int BUF_MQD_HEIGHT = 0;

		public StripeLL(int dwtLevel, int coeffWidth, int blockWidth, int blockHeight) {
			super(dwtLevel, BUF_BAND_NUM, coeffWidth, BUF_COEFF_HEIGHT, BUF_MQD_HEIGHT, BUF_COEFF_TRIGGER, null, blockWidth, blockHeight);
		}
	
		protected void encode() {
		    for (int b = 0; b < bandNum; ++b) {
		        coeffUnit.subbandType = b;
		        
		        // Find the qMax of these LL coefficients.
		        short valueAbsMax = ArrayOp.getAbsMax(coeff[b]);
		        int qMax = BCWTUnit.getQMax(valueAbsMax);
		
		        initBlockX();
		        for (int x = 0; x < numOfUnits; ++x) {
		            if (isNewBlockX())
		            	sink.writeBits(qMax, 0, BITS_QMAX, LBCWTOutput.TYPE_COEFF);
		            
		            if (qMax < qMin)
		            	continue;
		            
		            coeffUnit.x = x;
		            
		            getCoeffOffspring(coeffUnit, coeff);
		            BCWTUnit.encodeUnitLL(coeffUnit, qMin, qMax, sink);
		        }
		    }
	        coeffLineCount -= 2;
		    isBlockYCompleted();
		}

	}
    
    private class StripeMiddle extends StripeBase {
        private final static int BUF_BAND_NUM = 3;
        private final static int BUF_COEFF_HEIGHT = 2;
        private final static int BUF_COEFF_TRIGGER = 2;
        private final static int BUF_MQD_HEIGHT = 3;

		public StripeMiddle(int dwtLevel, int coeffWidth, StripeBase encoderLower, int blockWidth, int blockHeight) {
			super(dwtLevel, BUF_BAND_NUM, coeffWidth, BUF_COEFF_HEIGHT, BUF_MQD_HEIGHT, BUF_COEFF_TRIGGER, encoderLower, blockWidth, blockHeight);
		}
		
        protected void encode() {
			byte[] mqdCurrent;
			
			for (int b = 0; b < bandNum; ++b) {
				coeffUnit.subbandType = b;
				mqdUnit.subbandType = b;
				
				// Get a reference to a new line in MQD.
				// Higher level encoder may be recycling my MQD, thus sync is needed.
				synchronized (mqd) {
					mqdCurrent = mqd[b][mqdLineCount];
				}

				initBlockX();
				for (int x = 0; x < numOfUnits; ++x) {
					isNewBlockX();
					
					coeffUnit.x = x;
					mqdUnit.x = x;

					// Put the coefficients and lower level's MQDs into the coding unit.
					getCoeffOffspring(coeffUnit, coeff);
					WTNode.initOffspring(mqdUnit, mqdUnit.offspring);
					for (int i = 0; i < 4; ++i) {
						WTNode mOffs = mqdUnit.offspring[i];
						mOffs.absValueInt = mqdLower[mOffs.subbandType][mOffs.y][mOffs.x];
					}

					// Encode the coding unit.
					BCWTUnit.encodeUnitMiddle(coeffUnit, mqdUnit, qMin, sink);
					
					// Put the MQD into my MQD buffer.
					mqdCurrent[x] = (byte) mqdUnit.absValueInt;
				}
			}
			coeffLineCount -= 2;
			
			// The top two lines of lower level's MQD is not needed, recycle them.
			encoderLower.recycleMQD();
			
			// Higher level encoder may be recycling my MQD, thus sync is needed.
			synchronized (mqd) {
				mqdLineCount++;
			}
			
			isBlockYCompleted();
		}
    	
    }

    private class StripeTop extends StripeBase {
        private final static int BUF_BAND_NUM = 3;
        private final static int BUF_COEFF_HEIGHT = 2;
        private final static int BUF_COEFF_TRIGGER = 2;
        private final static int BUF_MQD_HEIGHT = 0;
        
//        private DataOutputStream debugOut;

		public StripeTop(int dwtLevel, int coeffWidth, StripeBase encoderLower, int blockWidth, int blockHeight) {
			super(dwtLevel, BUF_BAND_NUM, coeffWidth, BUF_COEFF_HEIGHT, BUF_MQD_HEIGHT, BUF_COEFF_TRIGGER, encoderLower, blockWidth, blockHeight);
			
//			try {
//				debugOut = new DataOutputStream(new FileOutputStream("E:/home/gjl/Research/images/bike-en.mqd"));
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
		}

		protected void encode() {
			for (int b = 0; b < bandNum; ++b) {
				coeffUnit.subbandType = b;
				mqdUnit.subbandType = b;
				
		        short valueAbsMax = ArrayOp.getAbsMax(coeff);
		        byte mqdMax = ArrayOp.getMax(mqdLower[b], 0, 0, coeffWidth, 2);
		        int qMax = Math.max(BCWTUnit.getQMax(valueAbsMax), mqdMax);

		        initBlockX();
				for (int x = 0; x < numOfUnits; ++x) {
					if (isNewBlockX())
				        sink.writeBits(qMax, 0, BITS_QMAX, LBCWTOutput.TYPE_COEFF);

					if (qMax < qMin)
						continue;
					
					coeffUnit.x = x;
					mqdUnit.x = x;

					// Put the coefficients and lower level's MQDs into the coding unit.
					getCoeffOffspring(coeffUnit, coeff);
					WTNode.initOffspring(mqdUnit, mqdUnit.offspring);
					for (int i = 0; i < 4; ++i) {
						WTNode mOffs = mqdUnit.offspring[i];
						mOffs.absValueInt = mqdLower[mOffs.subbandType][mOffs.y][mOffs.x];
					}

					// Encode the coding unit.
					BCWTUnit.encodeUnitTop(coeffUnit, mqdUnit, qMin, qMax, sink);
				}
			}
			coeffLineCount -= 2;
			
			// The top two lines of lower level's MQD is not needed, recycle them.
			encoderLower.recycleMQD();
			
			isBlockYCompleted();
		}
    	
    }
    private int dwtLevelNum;
    
	private StripeBase[][] stripe;

	private BCWTProp bcwtParam;

//    public void finish() throws IOException {
//		if (!hasFinished) {
//			hasFinished = true;
//		}
//	}
//
    private int getBlockWidth(int level) {
		return bcwtParam.blockWidth << (dwtLevelNum - level);
	}

    private int getBlockHeight(int level) {
		return bcwtParam.blockHeight << (dwtLevelNum - level);
	}

	public void initialize(ImageOp caller) {
		dwtLevelNum = DWTProp.getDwtLevelNum(props);

		bcwtParam = new BCWTProp(param);
		bcwtParam.setProps(props);
		
        // Create all stripes.
		stripe = new StripeBase[componentNum][dwtLevelNum + 1];
		for (int comp = 0; comp < componentNum; comp++) {
			int level = 1;
			int coeffWidth = width >> 1;
			stripe[comp][level] = new StripeBottom(level, coeffWidth,
					getBlockWidth(level), getBlockHeight(level));
			stripe[comp][level].setQMin(bcwtParam.qMin);

			for (level = 2; level < dwtLevelNum; level++) {
				coeffWidth >>= 1;
				stripe[comp][level] = new StripeMiddle(level, coeffWidth,
						stripe[comp][level - 1], getBlockWidth(level), getBlockHeight(level));
				stripe[comp][level].setQMin(bcwtParam.qMin);
			}
			coeffWidth >>= 1;
			stripe[comp][level] = new StripeTop(level, coeffWidth,
					stripe[comp][level - 1], getBlockWidth(level), getBlockHeight(level));
			stripe[comp][level].setQMin(bcwtParam.qMin);

			stripe[comp][Const.BCWT_LEVEL_LL] = new StripeLL(dwtLevelNum, coeffWidth,
					getBlockWidth(dwtLevelNum), getBlockHeight(dwtLevelNum));
			stripe[comp][Const.BCWT_LEVEL_LL].setQMin(bcwtParam.qMin);
		}
		
		super.initialize(caller);
    }
	
	public void online(ImageOp caller) {
		
		for (int comp = 0; comp < componentNum; comp++) {
			for (int level = 1; level <= dwtLevelNum; level++) {
				stripe[comp][level].setSink((DataHandler) sink
						.getInputPins(DWTTag.getTag(level, comp, 0)));
			}
			stripe[comp][Const.BCWT_LEVEL_LL].setSink((DataHandler) sink
					.getInputPins(DWTTag.getTag(dwtLevelNum, comp,
							y0.wavelet.Const.BAND_LL)));
		}
		
		super.online(caller);
	}
	

	public DataReceiver getInputPins(int tag) {
		DWTTag dwtTag = new DWTTag(tag);
		DataReceiver node;
		if (dwtTag.dwtBand == y0.wavelet.Const.BAND_LL) {
			node = stripe[dwtTag.component][Const.BCWT_LEVEL_LL].getInputPin();
		} else {
			node = stripe[dwtTag.component][dwtTag.dwtLevel].getInputPin(); 
		}
		return node;
	}

	public DataSender getOutputPins(int tag) {
		return null;
	}

	public Vector getTasks() {
		Vector tasks = new Vector();
		for (int comp = 0; comp < componentNum; comp++) {
			tasks.addElement(stripe[comp][Const.BCWT_LEVEL_LL]);
			for (int level = dwtLevelNum; level > 0 ; level--) {
				tasks.addElement(stripe[comp][level]);
			}
		}
		return tasks;
	}

}//public class LBCWTCore extends ImageOpImpl implements TaskGroup

