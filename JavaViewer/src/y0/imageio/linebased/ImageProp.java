package y0.imageio.linebased;

import java.util.Hashtable;

import y0.imageio.ROI;

public class ImageProp {
	public static final int NAN = -1;
	
	public static final String HEIGHT = "image.height.padded";
	public static final String HEIGHT_REAL = "image.height.real";
	public static final String WIDTH = "image.width.padded";
	public static final String WIDTH_REAL = "image.width.real";
	public static final String COMPONENT_NUM = "image.components";
	public static final String BITS_PER_PIXEL = "image.bitsPerPixel";
	public static final String IMAGE_ROI = "image.roi";
	public static final String COLOR_SPACE = "image.colorSpace";
	
	public static final int COLORSPACE_RGB = 1;
	public static final int COLORSPACE_GRAY = 10;
	public static final int COLORSPACE_YCBCRICT = 64;
	
	public static int getBitsPerPixel(Hashtable props) {
		Object value = props.get(BITS_PER_PIXEL);
		if (value == null)
			return NAN;
		else
			return ((Integer) value).intValue();
	}
	public static int getComponentNum(Hashtable props) {
		Object value = props.get(COMPONENT_NUM);
		if (value == null)
			return NAN;
		else
			return ((Integer) value).intValue();
	}
	public static int getHeight(Hashtable props) {
		Object value = props.get(HEIGHT);
		if (value == null)
			return NAN;
		else
			return ((Integer) value).intValue();
	}
	public static int getHeightReal(Hashtable props) {
		Object value = props.get(HEIGHT_REAL);
		if (value == null)
			return NAN;
		else
			return ((Integer) value).intValue();
	}
	public static ROI getROI(Hashtable props) {
		return (ROI) props.get(IMAGE_ROI);
	}
	public static int getWidth(Hashtable props) {
		Object value = props.get(WIDTH);
		if (value == null)
			return NAN;
		else
			return ((Integer) value).intValue();
	}
	public static int getWidthReal(Hashtable props) {
		Object value = props.get(WIDTH_REAL);
		if (value == null)
			return NAN;
		else
			return ((Integer) value).intValue();
	}
	public static int getColorSpace(Hashtable props) {
		Object value = props.get(COLOR_SPACE);
		if (value == null)
			return NAN;
		else
			return ((Integer) value).intValue();
	}
	
	public static void setBitsPerPixel(Hashtable props, int value) {
		props.put(BITS_PER_PIXEL, new Integer(value));
	}
	
	public static void setComponentNum(Hashtable props, int value) {
		props.put(COMPONENT_NUM, new Integer(value));
	}
	
	public static void setHeight(Hashtable props, int value) {
		props.put(HEIGHT, new Integer(value));
	}
	
	public static void setHeightReal(Hashtable props, int value) {
		props.put(HEIGHT_REAL, new Integer(value));
	}
	
	public static void setROI(Hashtable props, ROI roi) {
		if (roi != null)
			props.put(IMAGE_ROI, roi);
	}

	public static void setWidth(Hashtable props, int value) {
		props.put(WIDTH, new Integer(value));
	}
	
	public static void setWidthReal(Hashtable props, int value) {
		props.put(WIDTH_REAL, new Integer(value));
	}
	public static void setColorSpace(Hashtable props, int value) {
		props.put(COLOR_SPACE, new Integer(value));
	}

	public int height;

	public int heightReal;

	public int width;

	public int widthReal;

	public int componentNum;
	
	public int bitsPerPixel;

	public ROI roi;
	
	public int colorSpace;
	
	public ImageProp() {
		
	}

	public ImageProp(Hashtable props) {
		height = getHeight(props);
		heightReal = getHeightReal(props);
		width = getWidth(props);
		widthReal = getWidthReal(props);
		componentNum = getComponentNum(props);
		bitsPerPixel = getBitsPerPixel(props);
		roi = getROI(props);
		colorSpace = getColorSpace(props);
	}

	public void setProps(Hashtable props) {
		setHeight(props, height);
		setHeightReal(props, heightReal);
		setWidth(props, width);
		setWidthReal(props, widthReal);
		setComponentNum(props, componentNum);
		setBitsPerPixel(props, bitsPerPixel);
		setROI(props, roi);
		setColorSpace(props, colorSpace);
	}

}
