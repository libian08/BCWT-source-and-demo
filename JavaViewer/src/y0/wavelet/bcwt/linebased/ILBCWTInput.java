package y0.wavelet.bcwt.linebased;

import java.io.DataInput;
import java.io.IOException;

import y0.imageio.linebased.DataReceiver;
import y0.imageio.linebased.DataSender;
import y0.imageio.linebased.ImageOp;
import y0.utils.BitStream;
import y0.wavelet.DWTProp;
import y0.wavelet.DWTTag;

public class ILBCWTInput extends LBCWTIOBase {
	public class DataHandler implements BCWTInput, DataSender {
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
				BitStream[] tmp = bufCompleted[blockX][dwtLevel][component];
				bufCompleted[blockX][dwtLevel][component] = bufWorking[blockX][dwtLevel][component];
				bufWorking[blockX][dwtLevel][component] = tmp;
			}
			
			if (blockParts.addAndCheck(dwtLevel, component)) {
				readBlocks(bufCompleted);
				blockParts.reduceOne();
			}
		}
		/* (non-Javadoc)
		 * @see y0.wavelet.bcwt.linebased.BCWTInput#readBit(int)
		 */
		public boolean readBit(int type) {
			return bufWorking[blockX][dwtLevel][component][type].getBitFast();
//			return bufWorking[blockX][dwtLevel][component][type].getBit();
		}
		/* (non-Javadoc)
		 * @see y0.wavelet.bcwt.linebased.BCWTInput#readBits(int, int, int)
		 */
		public int readBits(int qLower, int qUpper, int type) {
			int bits = 0;
			for (int q = qLower, offset = 1 << qLower; q <= qUpper; ++q, offset <<= 1) {
				if (readBit(type))
					bits |= offset;
			}
			return bits;
		}
		public void ackData(int tag) {
			// This method won't be called.
		}
		public void setSendTo(DataReceiver receiver) {
			// This method won't be called.
		}
	}
	
	private DataInput input;
    private DataHandler[][] dataHandler;
	
	private DWTProp dwtParam;
	private BCWTProp bcwtParam;
	private boolean isCompleted;

	public boolean isCompleted() {
		return isCompleted;
	}

	public void initialize(ImageOp caller) {
		dwtProp = new DWTProp(props);
    	dwtParam = new DWTProp(param);
    	bcwtParam = new BCWTProp(param);

		blockY = 0;
    	blockXNum = bcwtParam.blockROI.width;
		
    	// Allocate dataHandler.
    	dataHandler = new DataHandler[dwtProp.levelNum + 1][componentNum];
		for (int comp = 0; comp < componentNum; comp++) {
			for (int level = dwtParam.levelNum + 1; level <= dwtProp.levelNum; level++) {
				dataHandler[level][comp] = new DataHandler(comp, level);
			}
			dataHandler[Const.BCWT_LEVEL_LL][comp] = new DataHandler(comp, Const.BCWT_LEVEL_LL);
		}

		blockParts = new BlockParts(dwtProp.levelNum, dwtParam.levelNum + 1);
		bufWorking = createBuf(blockXNum, dwtProp.levelNum, dwtParam.levelNum + 1, componentNum);
		bufCompleted = createBuf(blockXNum, dwtProp.levelNum, dwtParam.levelNum + 1, componentNum);
		sizes = new int[dwtProp.levelNum + 1][componentNum][BCWTIO.TYPE_NUM];

		readBlocks(bufWorking);
		readBlocks(bufCompleted);
		
		super.initialize(caller);
	}
	
	private void readBlocks(BitStream[][][][] buf) {
		if (blockY < bcwtParam.blockROI.height) {
			try {
				for (int blockX = 0; blockX < blockXNum; blockX++) {
					readBlock(blockX, blockY, buf);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			isCompleted = true;
		}
		blockY++;
	}
	
	/**
	 * Read a block (X,Y) from input, skipping block data of DWT level lower than target level.<p>
	 * Note: This method will skip any block in-between until it finds a match. Therefore, blocks should be in
	 * proper order or some useful blocks may be skipped. 
	 * @param blockX X coordinate of the block.
	 * @param blockY Y coordinate of the block.
	 * @throws IOException If can not read from the input.
	 */
	private synchronized void readBlock(int blockX, int blockY, BitStream[][][][] buf)
			throws IOException {
		while (true) {
			int blockDataSize = 0;
			int bufSize = 0;

			// Read the (X,Y) of this block.
			int thisX = input.readShort();
			int thisY = input.readShort();

			// Read LL header
			for (int comp = 0; comp < componentNum; comp++) {
				bufSize = input.readInt();
				blockDataSize += bufSize;
				sizes[Const.BCWT_LEVEL_LL][comp][TYPE_COEFF] = bufSize;
			}
			// Read top level header
			for (int comp = 0; comp < componentNum; comp++) {
				bufSize = input.readInt();
				blockDataSize += bufSize;
				sizes[dwtProp.levelNum][comp][TYPE_COEFF] = bufSize;
			}
			// Read other levels' header
			for (int level = dwtProp.levelNum - 1; level > 0; level--) {
				for (int comp = 0; comp < componentNum; comp++) {
					bufSize = input.readInt();
					blockDataSize += bufSize;
					sizes[level][comp][TYPE_MQD] = bufSize;

					bufSize = input.readInt();
					blockDataSize += bufSize;
					sizes[level][comp][TYPE_COEFF] = bufSize;
				}
			}

			if (thisX == (blockX + bcwtParam.blockROI.x) && thisY == (blockY + bcwtParam.blockROI.y)) {
				// This block is what requested, read its data.
				
				// For progressive-of-resolution decoding, some levels of data may be skipped.
				int dataToSkip = 0;
				
				// Read LL data. Shall not skip these in any case.
				for (int comp = 0; comp < componentNum; comp++) {
					buf[blockX][Const.BCWT_LEVEL_LL][comp][TYPE_COEFF]
							.load(input, 0, sizes[Const.BCWT_LEVEL_LL][comp][TYPE_COEFF]);
				}
				
				// Read top level data or skip them.
				for (int comp = 0; comp < componentNum; comp++) {
					if (dwtParam.levelNum < dwtProp.levelNum) {
						buf[blockX][dwtProp.levelNum][comp][TYPE_COEFF]
								.load(input, 0, sizes[dwtProp.levelNum][comp][TYPE_COEFF]);
					} else {
						dataToSkip += sizes[dwtProp.levelNum][comp][TYPE_COEFF];
					}
				}
				
				// Read other levels' data or skip them if required.
				for (int level = dwtProp.levelNum - 1; level > 0; level--) {
					for (int comp = 0; comp < componentNum; comp++) {
						if (dwtParam.levelNum < level) {
							buf[blockX][level][comp][TYPE_MQD]
									.load(input, 0, sizes[level][comp][TYPE_MQD]);
							buf[blockX][level][comp][TYPE_COEFF]
									.load(input, 0, sizes[level][comp][TYPE_COEFF]);
						} else {
							dataToSkip += sizes[level][comp][TYPE_MQD];
							dataToSkip += sizes[level][comp][TYPE_COEFF];
						}
					}
				}
				
				if (dataToSkip > 0)
					input.skipBytes(dataToSkip);

				// We are done reading the requested block, break out.
				break;
				
			} else {
				// This block is not what we need, skip it and continue to scan next block.
				input.skipBytes(blockDataSize);
			}
		}
	}
	
	public DataInput getInput() {
		return input;
	}

	public void setInput(DataInput input) {
		this.input = input;
	}

	public DataReceiver getInputPins(int tag) {
		return null;
	}

	public DataSender getOutputPins(int tag) {
		int component = DWTTag.getComponent(tag);
		int dwtLevel = DWTTag.getDwtLevel(tag);
		int band = DWTTag.getDwtBand(tag);
		return dataHandler[band == y0.wavelet.Const.BAND_LL ? Const.BCWT_LEVEL_LL : dwtLevel][component];
	}

}
