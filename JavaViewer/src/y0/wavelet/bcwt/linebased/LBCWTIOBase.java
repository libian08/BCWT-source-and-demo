package y0.wavelet.bcwt.linebased;

import java.util.Vector;

import y0.imageio.linebased.ImageOpImpl;
import y0.utils.BitStream;
import y0.wavelet.DWTProp;

public abstract class LBCWTIOBase extends ImageOpImpl implements BCWTIO {

	protected class BlockParts {
		private int[][] completedBlockParts;
		private int dwtLevelLower;
		private int dwtLevelUpper;
		
		public BlockParts(int dwtLevelUpper, int dwtLevelLower) {
			this.dwtLevelUpper = dwtLevelUpper;
			this.dwtLevelLower = dwtLevelLower;
			completedBlockParts = new int[dwtLevelUpper + 1][componentNum];
		}
		
		public synchronized boolean addAndCheck(int dwtLevel,
				int component) {
			completedBlockParts[dwtLevel][component]++;
	
			boolean isBlocksCompleted = true;
			for (int comp = 0; comp < componentNum && isBlocksCompleted; comp++) {
				isBlocksCompleted = (completedBlockParts[Const.BCWT_LEVEL_LL][comp] > 0);
				for (int level = dwtLevelLower; level <= dwtLevelUpper && isBlocksCompleted; level++) {
					isBlocksCompleted = (completedBlockParts[level][comp] > 0);
				}
			}
			return isBlocksCompleted;
		}
	
		public synchronized void reduceOne() {
			synchronized (completedBlockParts) {
				for (int comp = 0; comp < componentNum; comp++) {
					completedBlockParts[Const.BCWT_LEVEL_LL][comp]--;
					for (int level = dwtLevelLower; level <= dwtLevelUpper; level++) {
						completedBlockParts[level][comp]--;
					}
				}
			}
		}
	}

	protected DWTProp dwtProp;
	
    // The 3 dimensions are (in order): block, dwtLevel, component, type
	protected BitStream[][][][] bufWorking;
	protected BitStream[][][][] bufCompleted;
	
	protected int[][][] sizes;
	protected int blockXNum;
	protected int blockY;
	protected static final int BLOCK_CAPACITY_EST = 100 * 1024 * 8;
    protected BlockParts blockParts;


	public LBCWTIOBase() {
		super();
	}

	protected BitStream[][][][] createBuf(int blockXNum, int dwtLevelUpper, int dwtLevelLower, int componentNum) {
		BitStream[][][][] buf = new BitStream[blockXNum][dwtLevelUpper + 1][componentNum][TYPE_NUM];
		for (int blockX = 0; blockX < blockXNum; blockX++) {
			for (int comp = 0; comp < componentNum; comp++) {
				buf[blockX][Const.BCWT_LEVEL_LL][comp][TYPE_MQD] = new BitStream(BLOCK_CAPACITY_EST >> dwtLevelUpper);
				buf[blockX][Const.BCWT_LEVEL_LL][comp][TYPE_COEFF] = new BitStream(BLOCK_CAPACITY_EST >> dwtLevelUpper);
				for (int level = dwtLevelUpper; level >= dwtLevelLower ; level--) {
					buf[blockX][level][comp][TYPE_MQD] = new BitStream(BLOCK_CAPACITY_EST >> level);
					buf[blockX][level][comp][TYPE_COEFF] = new BitStream(BLOCK_CAPACITY_EST >> level);
				}
			}
		}
		return buf;
	}

	public Vector getTasks() {
		return null;
	}
	
}