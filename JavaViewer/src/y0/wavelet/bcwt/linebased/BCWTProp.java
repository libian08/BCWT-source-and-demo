package y0.wavelet.bcwt.linebased;

import java.util.Hashtable;

import y0.imageio.ROI;

public class BCWTProp {
	public static final String Q_MIN = "bcwt.qi";
	public static final String DATA_BIT_NUM = "bcwt.dataBits";
	public static final String BLOCK_WIDTH = "bcwt.block.width";
	public static final String BLOCK_HEIGHT = "bcwt.block.height";
	public static final String BLOCK_ROI = "bcwt.block.roi";
	public static final String PROGRESSION_TYPE = "bcwt.progressionType";
	
	public int qMin;
	public int blockWidth;
	public int blockHeight;
	public int progressionType;
	public ROI blockROI;
	
	public BCWTProp(Hashtable props) {
		qMin = getQMin(props);
		blockWidth = getBlockWidth(props);
		blockHeight = getBlockHeight(props);
		progressionType = getProgressionType(props);
		blockROI = getBlockROI(props);
	}
	
	public void setProps(Hashtable props) {
		setQMin(props, qMin);
		setBlockWidth(props, blockWidth);
		setBlockHeight(props, blockHeight);
		setProgressionType(props, progressionType);
		setBlockROI(props, blockROI);
	}
	
	public static final int PTYPE_RESOLUTION = 1;
	
	public static int getQMin(Hashtable props) {
		Object value = props.get(Q_MIN);
		if (value == null)
			return 0;
		else
			return ((Integer) value).intValue();
	}
	
	public static void setQMin(Hashtable props, int value) {
		props.put(Q_MIN, new Integer(value));
	}

	public static int getBlockWidth(Hashtable props) {
		Object value = props.get(BLOCK_WIDTH);
		if (value == null)
			return 0;
		else
			return ((Integer) value).intValue();
	}
	
	public static void setBlockWidth(Hashtable props, int value) {
		props.put(BLOCK_WIDTH, new Integer(value));
	}

	public static int getBlockHeight(Hashtable props) {
		Object value = props.get(BLOCK_HEIGHT);
		if (value == null)
			return 0;
		else
			return ((Integer) value).intValue();
	}
	
	public static void setBlockHeight(Hashtable props, int value) {
		props.put(BLOCK_HEIGHT, new Integer(value));
	}

	public static int getProgressionType(Hashtable props) {
		Object value = props.get(PROGRESSION_TYPE);
		if (value == null)
			return 0;
		else
			return ((Integer) value).intValue();
	}
	
	public static void setProgressionType(Hashtable props, int value) {
		props.put(PROGRESSION_TYPE, new Integer(value));
	}

	public static void setBlockROI(Hashtable props, ROI blockROI) {
		if (blockROI != null)
			props.put(BLOCK_ROI, blockROI);
	}

	public static ROI getBlockROI(Hashtable props) {
		return (ROI) props.get(BLOCK_ROI);
	}

	public static int getDataBitNum(Hashtable props) {
		Object value = props.get(DATA_BIT_NUM);
		if (value == null)
			return 0;
		else
			return ((Integer) value).intValue();
	}
	
	public static void setDataBitNum(Hashtable props, int value) {
		props.put(DATA_BIT_NUM, new Integer(value));
	}

}
