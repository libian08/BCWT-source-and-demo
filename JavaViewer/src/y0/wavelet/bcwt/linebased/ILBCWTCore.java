package y0.wavelet.bcwt.linebased;

import java.util.Vector;

import y0.imageio.ROI;
import y0.imageio.linebased.DataInbox;
import y0.imageio.linebased.DataOutbox;
import y0.imageio.linebased.DataReceiver;
import y0.imageio.linebased.DataSender;
import y0.imageio.linebased.ImageOp;
import y0.imageio.linebased.ImageOpImpl;
import y0.utils.Task;
import y0.utils.TaskGroup;
import y0.wavelet.DWTProp;
import y0.wavelet.DWTTag;
import y0.wavelet.WTNode;
import y0.wavelet.bcwt.linebased.ILBCWTInput.DataHandler;

public class ILBCWTCore extends ImageOpImpl implements TaskGroup {

	private abstract class StripeBase implements Task {

		protected short[][][] coeff;
		protected byte[][][] mqd;

		protected int bandNum;
		protected int coeffWidth;
		protected int dwtLevel;

		protected int blockWidth;
		protected int blockHeight;
		protected int blockX;
		protected int remainingWidth;
		protected int remainingHeight;

		protected int coeffYOffset;
		protected ROI coeffROI;

		protected IBCWTUnit ibcwtUnit;
		protected WTNode coeffUnit;
		protected WTNode mqdUnit;

		protected ILBCWTInput.DataHandler source;
		protected int numOfUnits;
		protected boolean isCompleted;
		protected int component;
		protected int coeffHeight;
		protected int mqdHeight;

		protected final static int BITS_QMAX = 3;

		protected DataInbox inboxMQD;
		protected DataOutbox outbox;
		protected DataOutbox outboxMQD;

		public StripeBase(int dwtLevel, int component, int bandNum,
				int coeffWidth, int coeffHeight, int mqdHeight,
				StripeBase stripeLower, int blockWidth, int blockHeight) {
			this.dwtLevel = dwtLevel;
			this.component = component;
			this.bandNum = bandNum;
			this.coeffWidth = coeffWidth;
			this.coeffHeight = coeffHeight;
			this.mqdHeight = mqdHeight;
			this.numOfUnits = coeffWidth >> 1;
			this.blockWidth = blockWidth;
			this.blockHeight = blockHeight;
			remainingHeight = blockHeight;

			if (stripeLower != null) {
				outboxMQD = new DataOutbox();
				outboxMQD.setTag(getTag());
				outboxMQD.setSendTo(stripeLower.getInputMQD());
			}
			outbox = new DataOutbox();
			outbox.setTag(getTag());
			
			inboxMQD = new DataInbox();

			coeff = new short[coeffHeight][bandNum][coeffWidth];

			if (mqdHeight > 0) {
				mqd = new byte[mqdHeight][bandNum][numOfUnits];
			}

			// Initialize coding unit.
			ibcwtUnit = new IBCWTUnit();
			coeffUnit = ibcwtUnit.coeffNode;
			mqdUnit = ibcwtUnit.mqdNode;
			coeffUnit.dwtLevel = dwtLevel + 1;
			mqdUnit.dwtLevel = dwtLevel + 1;
			coeffUnit.y = 0;
			mqdUnit.y = 0;

			isCompleted = false;
		}

		/**
		 * Calculate the blockROI for this level. The final blockROI is the
		 * union of all blockROIs calculated by all levels.
		 * 
		 * @param coeffROI
		 *            ROI of the coefficients of this level, which is calculated
		 *            by IDWT.
		 * @return The blockROI which contains all the coefficients in the
		 *         coeffROI.
		 */
		public ROI calcBlockROI(ROI coeffROI) {
			ROI blockROI = new ROI();
			blockROI.x = coeffROI.x / blockWidth;
			blockROI.y = coeffROI.y / blockHeight;
			blockROI.width = (coeffROI.x + coeffROI.width - 1) / blockWidth
					- blockROI.x + 1;
			blockROI.height = (coeffROI.y + coeffROI.height - 1) / blockHeight
					- blockROI.y + 1;
			return blockROI;
		}

		protected abstract void decode();

		public DataReceiver getInputMQD() {
			return inboxMQD;
		}

		public DataSender getOutputPin() {
			return outbox;
		}

		private int getTag() {
			int tag;
			if (bandNum == 1) {
				tag = DWTTag.getTag(dwtLevel, component,
						y0.wavelet.Const.BAND_LL);
			} else {
				tag = DWTTag.getTag(dwtLevel, component, 0);
			}
			return tag;
		}

		protected void initBlockX() {
			blockX = -1;
			remainingWidth = 2;
		}

		protected boolean isBlockYCompleted() {
			// Two more lines are decoded, check if a block is completed.
			remainingHeight -= 2;
			if (remainingHeight == 0) {
				remainingHeight = blockHeight;
				source.signalBlocksCompleted();
				return true;
			}
			return false;
		}

		public boolean isCompleted() {
			return isCompleted;
		}

		protected boolean isNewBlockX(int step) {
			// Check if we are stepping into next block.
			remainingWidth -= step;
			if (remainingWidth <= 0) {
				remainingWidth = blockWidth;
				source.setBlockX(++blockX);
				return true;
			}
			return false;
		}

		protected void putCoeffOffspring(WTNode cNode, short[][][] coeff) {
			for (int i = 0; i < 4; ++i) {
				WTNode cPos = cNode.offspring[i];
				coeff[cPos.y][cPos.subbandType][cPos.x] = (short) (cPos.sign ? cPos.absValueInt
						: -cPos.absValueInt);
			}
		}

		protected void putMQDOffspring(WTNode mqdNode, byte[][][] mqd) {
			for (int i = 0; i < 4; ++i) {
				WTNode node = mqdNode.offspring[i];
				mqd[node.y][node.subbandType][node.x] = (byte) node.absValueInt;
			}
		}

		public void run() {
		}
		
		private int outputX;
		protected int qMin;
		private int outputYBottom;

		protected void sendCoeff() {
			for (int line = 0; line < coeffHeight; line++) {
				if (coeffYOffset >= coeffROI.y) {
					outbox.send(coeff[line], outputX, coeffROI.width, DataReceiver.TYPE_SHORT);
				}
				if (++coeffYOffset >= outputYBottom) {
					// All coefficients inside the coeffROI has been decoded.
					// Send out EOF.
					outbox.send(null, 0, 0, DataReceiver.TYPE_SHORT);
					isCompleted = true;
					break;
				}
			}
		}

		protected void sendMQD() {
			for (int line = 0; line < mqdHeight; line++) {
				outboxMQD.send(mqd[line], 0, numOfUnits, DataReceiver.TYPE_BYTE);
			}
		}

		public void setCoeffROI(ROI coeffROI) {
			this.coeffROI = coeffROI;
			coeffYOffset = bcwtParam.blockROI.y * blockHeight;
			outputX = coeffROI.x - bcwtParam.blockROI.x * blockWidth;
			outputYBottom = coeffROI.y + coeffROI.height;
		}

		public void setQMin(final int qMin) {
			this.qMin = qMin;
			ibcwtUnit.setQMin(qMin);
		}

		public void setSource(ILBCWTInput.DataHandler source) {
			this.source = source;
			ibcwtUnit.setInput(source);
		}
	}

	private class StripeLL extends StripeBase {
		private final static int BUF_BAND_NUM = 1;
		private final static int BUF_COEFF_HEIGHT = 2;
		private final static int BUF_MQD_HEIGHT = 0;

		public StripeLL(int dwtLevel, int component, int coeffWidth,
				int blockWidth, int blockHeight) {
			super(dwtLevel, component, BUF_BAND_NUM, coeffWidth,
					BUF_COEFF_HEIGHT, BUF_MQD_HEIGHT, null, blockWidth,
					blockHeight);
		}

		protected void decode() {
			for (int b = 0; b < bandNum; ++b) {
				coeffUnit.subbandType = b;

				int qMax = 0;

				initBlockX();
				for (int x = 0; x < numOfUnits; ++x) {
					if (isNewBlockX(2))
						qMax = source.readBits(0, BITS_QMAX, BCWTIO.TYPE_COEFF);

					coeffUnit.x = x;
					WTNode.initOffspring(coeffUnit, coeffUnit.offspring);
					if (qMax >= qMin)
						ibcwtUnit.decodeUnitTopOrLL(qMax);
					putCoeffOffspring(coeffUnit, coeff);
				}
			}
			isBlockYCompleted();
		}

		public void runOnce() {
			if (!outbox.isEmpty())
				return;

			decode();
			sendCoeff();
		}

	}

	private class StripeTop extends StripeBase {
		private final static int BUF_BAND_NUM = 3;
		private final static int BUF_COEFF_HEIGHT = 2;
		private final static int BUF_MQD_HEIGHT = 1;

		public StripeTop(int dwtLevel, int component, int coeffWidth,
				StripeBase stripeLower, int blockWidth, int blockHeight) {
			super(dwtLevel, component, BUF_BAND_NUM, coeffWidth,
					BUF_COEFF_HEIGHT, BUF_MQD_HEIGHT, stripeLower, blockWidth,
					blockHeight);
		}

		protected void decode() {
			for (int b = 0; b < bandNum; ++b) {
				coeffUnit.subbandType = b;
				mqdUnit.subbandType = b;

				int qMax = 0;

				initBlockX();
				for (int x = 0; x < numOfUnits; ++x) {
					if (isNewBlockX(2))
						qMax = source.readBits(0, BITS_QMAX, BCWTIO.TYPE_COEFF);
					
					coeffUnit.x = x;
					mqdUnit.x = x;

					WTNode.initOffspring(coeffUnit, coeffUnit.offspring);
					if (qMax >= qMin) {
						ibcwtUnit.decodeUnitTopOrLL(qMax);
					}
					putCoeffOffspring(coeffUnit, coeff);
					mqd[0][b][x] = (byte) mqdUnit.absValueInt;
				}
			}
			isBlockYCompleted();
		}

		public void runOnce() {
			if (outbox.isEmpty()) {
				if (outboxMQD != null) {
					if (!outboxMQD.isEmpty())
						return;
				}
				
				decode();
				
				if (outboxMQD != null)
					sendMQD();
				
				sendCoeff();
			}
		}

	}

	private class StripeMiddle extends StripeBase {

		private final static int BUF_BAND_NUM = 3;
		private final static int BUF_COEFF_HEIGHT = 2;
		private final static int BUF_MQD_HEIGHT = 2;

		// 0: waiting; 1: decoded 2 rows of coefficients and MQDs.
		protected int decodeStage;
		
		private byte[][] mqdHigher;

//        private DataOutputStream debugOut;
		
		public StripeMiddle(int dwtLevel, int component, int coeffWidth,
				StripeBase stripeLower, int blockWidth, int blockHeight) {
			super(dwtLevel, component, BUF_BAND_NUM, coeffWidth,
					BUF_COEFF_HEIGHT, BUF_MQD_HEIGHT, stripeLower, blockWidth,
					blockHeight);
			decodeStage = 0;
			
			// TODO debug
//			try {
//				debugOut = new DataOutputStream(new FileOutputStream("E:/home/gjl/Research/images/bike-de.mqd"));
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}

		}

		protected void decode() {
			
			int mqdCurrent;

			if (decodeStage == 0) {
				// Decode MQD.
				for (int b = 0; b < bandNum; ++b) {
					mqdUnit.subbandType = b;

					initBlockX();
					for (int x = 0; x < numOfUnits; x += 2) {
						isNewBlockX(4);

						int xHigher = x >> 1;
						mqdUnit.x = xHigher;
						mqdUnit.absValueInt = mqdHigher[b][xHigher];

						ibcwtUnit.decodeMQD();

						putMQDOffspring(mqdUnit, mqd);
					}

//					if (dwtLevel == 4) {
//						try {
//							debugOut.writeInt(remainingHeight >> 1);
//							debugOut.writeInt(b);
//							debugOut.write(mqd[0][b]);
//							debugOut.writeBytes("-EOL-");
//							debugOut.writeInt(remainingHeight >> 1);
//							debugOut.writeInt(b);
//							debugOut.write(mqd[1][b]);
//							debugOut.writeBytes("-EOL-");
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}

				}
				decodeStage = 1;
				mqdCurrent = 0;
				
			} else {
				mqdCurrent = 1;
				decodeStage = 0;
			}

			// Decode coefficients.
			for (int b = 0; b < bandNum; ++b) {
				mqdUnit.subbandType = b;
				coeffUnit.subbandType = b;

				initBlockX();
				for (int x = 0; x < numOfUnits; ++x) {
					isNewBlockX(2);

					mqdUnit.x = x;
					coeffUnit.x = x;
					mqdUnit.absValueInt = mqd[mqdCurrent][b][x];
					
					WTNode.initOffspring(coeffUnit, coeffUnit.offspring);
					ibcwtUnit.decodeCoeffOffspring();

					putCoeffOffspring(coeffUnit, coeff);
				}
			}
			isBlockYCompleted();
		}

		public void runOnce() {
			if (!outbox.isEmpty())
				return;

			if (decodeStage == 0) {
				if (inboxMQD.isEmpty())
					return;
				
				if (outboxMQD != null) {
					if (!outboxMQD.isEmpty())
						return;
				}

				DataInbox.Item incoming = inboxMQD.poll();
				mqdHigher = (byte[][]) incoming.data;
				decode();
				incoming.sender.ackData(incoming.tag);

				if (outboxMQD != null)
					sendMQD();
			} else {
				decode();
			}
			sendCoeff();
		}
	}

	private DWTProp dwtParam;
	private DWTProp dwtProp;
	private StripeBase[][] stripe;
	private BCWTProp bcwtParam;
	private BCWTProp bcwtProp;

	public DataReceiver getInputPins(int tag) {
		return null;
	}

	public DataSender getOutputPins(int tag) {
		int component = DWTTag.getComponent(tag);
		int dwtLevel = DWTTag.getDwtLevel(tag);
		int band = DWTTag.getDwtBand(tag);
		return stripe[component][band == y0.wavelet.Const.BAND_LL ? Const.BCWT_LEVEL_LL
				: dwtLevel].getOutputPin();
	}

//	public Task[] getTaskList() {
//		Task[] taskList = new Task[(dwtProp.levelNum - dwtParam.levelNum + 1)
//				* componentNum];
//
//		int taskIndex = 0;
//		for (int comp = 0; comp < componentNum; comp++) {
//			taskList[taskIndex++] = stripe[comp][Const.BCWT_LEVEL_LL];
//			for (int level = dwtProp.levelNum; level > dwtParam.levelNum; level--) {
//				taskList[taskIndex++] = stripe[comp][level];
//			}
//		}
//
//		return taskList;
//	}

	private int getBlockWidth(int level) {
		return bcwtProp.blockWidth << (dwtProp.levelNum - level);
	}

	private int getBlockHeight(int level) {
		return bcwtProp.blockHeight << (dwtProp.levelNum - level);
	}

	public void initialize(ImageOp caller) {
		dwtProp = new DWTProp(props);
		bcwtProp = new BCWTProp(props);
		dwtParam = new DWTProp(param);
		bcwtParam = new BCWTProp(param);
		
		// Compute the blockROI from the coeffROI computed by DWT.
		// Each level's ceoffROI may be different, thus the blockROI may be
		// different too.
		// Find the union of all the blockROIs of all levels.
		{
			ROI blockROI = calcBlockROI(dwtParam.roi[dwtProp.levelNum], dwtProp.levelNum);
			for (int level = dwtProp.levelNum - 1; level > dwtParam.levelNum; level--) {
				ROI roi = calcBlockROI(dwtParam.roi[level], level);
				ROI.union(roi, blockROI, blockROI);
			}
			bcwtParam.blockROI = blockROI;
			BCWTProp.setBlockROI(param, blockROI);
		}

		// Create all stripes.
		{
			stripe = new StripeBase[componentNum][dwtProp.levelNum + 1];
			for (int comp = 0; comp < componentNum; comp++) {
				int level;
				int blockWidth;
				int coeffWidth;
				
				if (dwtParam.levelNum + 1 < dwtProp.levelNum) {
					// Middle levels
					for (level = dwtParam.levelNum + 1; level < dwtProp.levelNum; level++) {
						blockWidth = getBlockWidth(level);
						coeffWidth = blockWidth * bcwtParam.blockROI.width;
						stripe[comp][level] = new StripeMiddle(level, comp,
								coeffWidth, stripe[comp][level - 1], blockWidth,
								getBlockHeight(level));
						stripe[comp][level].setQMin(bcwtProp.qMin);
						stripe[comp][level].setCoeffROI(dwtParam.roi[level]);
					}
				}

				if (dwtParam.levelNum < dwtProp.levelNum) {
					// Top level
					level = dwtProp.levelNum;
					blockWidth = getBlockWidth(level);
					coeffWidth = blockWidth * bcwtParam.blockROI.width;
					stripe[comp][level] = new StripeTop(level, comp, coeffWidth,
							stripe[comp][level - 1], blockWidth,
							getBlockHeight(level));
					stripe[comp][level].setQMin(bcwtProp.qMin);
					stripe[comp][level].setCoeffROI(dwtParam.roi[level]);
				}

				// LL
				level = dwtProp.levelNum;
				blockWidth = getBlockWidth(level);
				coeffWidth = blockWidth * bcwtParam.blockROI.width;
				stripe[comp][Const.BCWT_LEVEL_LL] = new StripeLL(level, comp,
						coeffWidth, blockWidth, getBlockHeight(level));
				stripe[comp][Const.BCWT_LEVEL_LL].setQMin(bcwtProp.qMin);
				stripe[comp][Const.BCWT_LEVEL_LL].setCoeffROI(dwtParam.roi[dwtProp.levelNum]);
			}
		}

		super.initialize(caller);
	}
	
	private ROI calcBlockROI(ROI coeffROI, int level) {
		ROI blockROI = new ROI();
		blockROI.x = coeffROI.x / getBlockWidth(level);
		blockROI.y = coeffROI.y / getBlockHeight(level);
		blockROI.width = (coeffROI.x + coeffROI.width - 1) / getBlockWidth(level)
				- blockROI.x + 1;
		blockROI.height = (coeffROI.y + coeffROI.height - 1) / getBlockHeight(level)
				- blockROI.y + 1;
		return blockROI;
	}

	public void online(ImageOp caller) {
		for (int comp = 0; comp < componentNum; comp++) {
			// Connect the LL stripe with its source.
			stripe[comp][Const.BCWT_LEVEL_LL].setSource((DataHandler) source
					.getOutputPins(DWTTag.getTag(dwtProp.levelNum, comp,
							y0.wavelet.Const.BAND_LL)));

			// Connect all other stripes with their sources.
			for (int level = dwtProp.levelNum; level > dwtParam.levelNum; level--) {
				stripe[comp][level].setSource((DataHandler) source
						.getOutputPins(DWTTag.getTag(level, comp, 0)));
			}
		}

		super.online(caller);
	}

	public void finish(ImageOp caller) {
		for (int comp = 0; comp < componentNum; comp++) {
			stripe[comp][Const.BCWT_LEVEL_LL].isCompleted = true;
			for (int level = dwtProp.levelNum; level > dwtParam.levelNum; level--) {
				stripe[comp][level].isCompleted = true;
			}
		}
		
		super.finish(caller);
	}

	public Vector getTasks() {
		Vector tasks = new Vector();
		for (int comp = 0; comp < componentNum; comp++) {
			tasks.addElement(stripe[comp][Const.BCWT_LEVEL_LL]);
			for (int level = dwtProp.levelNum; level > dwtParam.levelNum; level--) {
				tasks.addElement(stripe[comp][level]);
			}
		}

		return tasks;
	}

}
