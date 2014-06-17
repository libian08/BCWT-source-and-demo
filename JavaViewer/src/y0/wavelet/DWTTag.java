package y0.wavelet;

public class DWTTag {
	public static final int BITS_DWT_BAND = 2;
	public static final int BITS_COMPONENT = 8;
	public static final int BITS_DWT_LEVEL = 6;
	
	public static final int OFFSET_DWT_BAND = 0;
	public static final int OFFSET_COMPONENT = BITS_DWT_BAND;
	public static final int OFFSET_DWT_LEVEL = BITS_DWT_BAND + BITS_COMPONENT;
	
	public static final int MASK_DWT_BAND = ~(~0 << BITS_DWT_BAND);
	public static final int MASK_COMPONENT = ~(~0 << BITS_COMPONENT) << OFFSET_COMPONENT;
	public static final int MASK_DWT_LEVEL = ~(~0 << BITS_DWT_LEVEL) << OFFSET_DWT_LEVEL;
	
	public int dwtLevel;
	public int component;
	public int dwtBand;
	
	public DWTTag(int tag) {
		dwtLevel = getDwtLevel(tag);
		component = getComponent(tag);
		dwtBand = getDwtBand(tag);
	}
	
	public static int getDwtBand(int tag) {
		return (tag & MASK_DWT_BAND);
	}
	
	public static int getComponent(int tag) {
		return (tag & MASK_COMPONENT) >> OFFSET_COMPONENT;
	}

	public static int getDwtLevel(int tag) {
		return (tag & MASK_DWT_LEVEL) >> OFFSET_DWT_LEVEL;
	}

	public static int getTag(int dwtLevel, int component, int dwtBand) {
		return (dwtLevel << OFFSET_DWT_LEVEL) | (component << OFFSET_COMPONENT) | dwtBand;
	}
	
	public static int getTag(DWTTag dwtTag) {
		return getTag(dwtTag.dwtLevel, dwtTag.component, dwtTag.dwtBand);
	}

}
