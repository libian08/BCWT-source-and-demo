package y0.wavelet;

import java.util.Hashtable;

import y0.imageio.ROI;

public class DWTProp {
	public static final int NAN = -1;
	
	public int levelNum;
	public String type;
	public ROI[] roi;
	
	public static ROI[] getROI(Hashtable props) {
		return (ROI[]) props.get(DWT_ROI);
	}

	public static void setROI(Hashtable props, ROI[] roi) {
		if (roi != null) {
			props.put(DWT_ROI, roi);
		}
	}

	public DWTProp() {
		roi = null;
	}
	
	public DWTProp(Hashtable props) {
		levelNum = getDwtLevelNum(props);
		type = getDwtType(props);
		roi = getROI(props);
	}
	
	public void setProp(Hashtable props) {
		setDwtLevelNum(props, levelNum);
		setDwtType(props, type);
		setROI(props, roi);
	}
	
	public static final String DWT_LEVEL_NUM = "dwt.level";
	public static final String DWT_RESOLUTION_INDEX = "dwt.resolutionIndex";
	public static final String DWT_TYPE = "dwt.type";
	public static final String DWT_ROI = "dwt.roi";
	
	public static final String TYPE_97_FLOAT = "DWT-97-Float";
	
	public static int getDwtLevelNum(Hashtable props) {
		Object value = props.get(DWT_LEVEL_NUM);
		if (value == null)
			return NAN;
		else
			return ((Integer) value).intValue();
	}
	
	public static void setDwtLevelNum(Hashtable props, int value) {
		props.put(DWT_LEVEL_NUM, new Integer(value));
	}

	public static String getDwtType(Hashtable props) {
		return (String) props.get(DWT_TYPE);
	}
	
	public static void setDwtType(Hashtable props, String value) {
		if (value != null)
			props.put(DWT_TYPE, value);
	}

	public static void setResolutionIndex(Hashtable props, int value) {
		props.put(DWT_RESOLUTION_INDEX, new Integer(value));
	}

	public static int getResolutionIndex(Hashtable props) {
		Object value = props.get(DWT_RESOLUTION_INDEX);
		if (value == null)
			return NAN;
		else
			return ((Integer) value).intValue();
	}
}
