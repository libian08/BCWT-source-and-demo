package y0.wavelet.bcwt.linebased;

import java.io.DataOutput;
import java.io.IOException;

import y0.imageio.linebased.DataReceiver;
import y0.imageio.linebased.DataSender;
import y0.imageio.linebased.ImageOp;
import y0.utils.BitStream;
import y0.wavelet.DWTProp;
import y0.wavelet.DWTTag;

public class LBCWTOutput extends LBCWTIOBase {
	public class DataHandler implements BCWTOutput, DataReceiver {
		private int dwtLevel;
		private int component;
		private int blockX;
		
		public DataHandler(int component, int dwtLevel) {
			this.component = component;
			this.dwtLevel = dwtLevel;
		}
		public void setBlockX(int blockX) {
			this.blockX = blockX;
		}
		public void signalBlocksCompleted() {
			for (int blockX = 0; blockX < blockXNum; blockX++) {
				// Swap the bufWorking and bufCompleted
				if (dwtLevel > 1) {
					// Swap the lower level's MQD buffer.
					BitStream tmp = bufCompleted[blockX][dwtLevel - 1][component][TYPE_MQD];
					bufCompleted[blockX][dwtLevel - 1][component][TYPE_MQD] = bufWorking[blockX][dwtLevel - 1][component][TYPE_MQD]; 
					bufWorking[blockX][dwtLevel - 1][component][TYPE_MQD] = tmp;
					tmp.reset();
				}
				// Swap this level's coefficient buffer.
				BitStream tmp = bufCompleted[blockX][dwtLevel][component][TYPE_COEFF];
				bufCompleted[blockX][dwtLevel][component][TYPE_COEFF] = bufWorking[blockX][dwtLevel][component][TYPE_COEFF]; 
				bufWorking[blockX][dwtLevel][component][TYPE_COEFF] = tmp;
				tmp.reset();
				
			}
			
			if (blockParts.addAndCheck(dwtLevel, component)) {
				flushBlocks();
				blockParts.reduceOne();
			}
		}
		/* (non-Javadoc)
		 * @see y0.wavelet.bcwt.linebased.BCWTOutput#writeBit(boolean, int)
		 */
		public void writeBit(boolean bit, int type) {
			bufWorking[blockX][type == TYPE_COEFF ? dwtLevel : (dwtLevel - 1)][component][type]
					.appendBitFast(bit);
		}
		/* (non-Javadoc)
		 * @see y0.wavelet.bcwt.linebased.BCWTOutput#writeBits(int, int, int, int)
		 */
		public void writeBits(int bits, int qLower, int qUpper, int type) {
			bufWorking[blockX][type == TYPE_COEFF ? dwtLevel : (dwtLevel - 1)][component][type]
					.appendIntFast(bits >> qLower, qUpper - qLower + 1);
		}
		public void receiveData(DataSender source, Object data, int offset,
				int len, int tag, int dataType) {
			throw new IllegalStateException("Should not call me.");
		}
		
	}

	private int blockWidth;

    private DataOutput output;
    private DataHandler[][] dataHandler;

    private int outputSize;
	public int blockFlushed;

	private BCWTProp bcwtParam;
    
	private synchronized void flushBlocks() {
    	for (int blockX = 0; blockX < blockXNum; blockX++) {
			try {
				// Write the X,Y of this block.
				output.writeShort(blockX);
				output.writeShort(blockY);
				outputSize += 4;

				// Write the header.
				for (int comp = 0; comp < componentNum; comp++) {
					writeSize(bufCompleted[blockX][Const.BCWT_LEVEL_LL][comp][TYPE_COEFF]);
				}
				for (int comp = 0; comp < componentNum; comp++) {
					writeSize(bufCompleted[blockX][dwtProp.levelNum][comp][TYPE_COEFF]);
				}
				for (int level = dwtProp.levelNum - 1; level > 0; level--) {
					for (int comp = 0; comp < componentNum; comp++) {
						writeSize(bufCompleted[blockX][level][comp][TYPE_MQD]);
						writeSize(bufCompleted[blockX][level][comp][TYPE_COEFF]);
					}
				}

				// Write the data.
				for (int comp = 0; comp < componentNum; comp++) {
					bufCompleted[blockX][Const.BCWT_LEVEL_LL][comp][TYPE_COEFF]
							.dump(output);
				}
				for (int comp = 0; comp < componentNum; comp++) {
					bufCompleted[blockX][dwtProp.levelNum][comp][TYPE_COEFF]
							.dump(output);
				}
				for (int level = dwtProp.levelNum - 1; level > 0; level--) {
					for (int comp = 0; comp < componentNum; comp++) {
						bufCompleted[blockX][level][comp][TYPE_MQD]
								.dump(output);
						bufCompleted[blockX][level][comp][TYPE_COEFF]
								.dump(output);
					}
				}

				blockFlushed++;

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	blockY++;
	}

    public int getOutputSize() {
		return outputSize;
	}

	public void initialize(ImageOp caller) {
		bcwtParam = new BCWTProp(param);
    	dwtProp = new DWTProp(props);
    	
		blockWidth = bcwtParam.blockWidth;
		blockXNum = (width >> dwtProp.levelNum) / blockWidth;
		blockY = 0;
    	outputSize = 0;
		
		blockParts = new BlockParts(dwtProp.levelNum, 1);
		bufWorking = createBuf(blockXNum, dwtProp.levelNum, 1, componentNum);
		bufCompleted = createBuf(blockXNum, dwtProp.levelNum, 1, componentNum);
    	
    	// Allocate dataHandler.
    	dataHandler = new DataHandler[dwtProp.levelNum + 1][componentNum];
		for (int comp = 0; comp < componentNum; comp++) {
			for (int level = 1; level <= dwtProp.levelNum; level++) {
				dataHandler[level][comp] = new DataHandler(comp, level);
			}
			dataHandler[Const.BCWT_LEVEL_LL][comp] = new DataHandler(comp, Const.BCWT_LEVEL_LL);
		}
		
		super.initialize(caller);
    }

    public void setOutput(DataOutput output) {
		this.output = output;
	}
    
    private void writeSize(BitStream buf) throws IOException {
		int bufSize = buf.getCountInByte();
		outputSize += bufSize + 4;
		output.writeInt(bufSize);
	}

	public DataReceiver getInputPins(int tag) {
		int component = DWTTag.getComponent(tag);
		int dwtLevel = DWTTag.getDwtLevel(tag);
		int band = DWTTag.getDwtBand(tag);
		return dataHandler[band == y0.wavelet.Const.BAND_LL ? Const.BCWT_LEVEL_LL : dwtLevel][component];
	}

	public DataSender getOutputPins(int tag) {
		return null;
	}
	
	public void finish(ImageOp caller) {
		BCWTProp.setDataBitNum(props, outputSize);
	}
}
