package edu.ttu.cvial.imageio;

import java.util.Hashtable;
import java.util.Properties;

public abstract class ImageCoeff {

    protected Hashtable propertySets;

    protected int type;
    
    public float roundingThreshold;
    public int roundingValue;
    public int qMinMask;

    public static final int TYPE_INT = 1;

    public static final int TYPE_SHORT = 2;

    public static final int TYPE_FLOAT = 3;

    public static final int TYPE_DOUBLE = 4;
    
    public static final String PROPSET_ORG = "Original";
    public static final String PROPSET_PADDED = "Padded";
    public static final String PROPSET_DWT = "DWT";
    public static final String PROPSET_COLOR = "Color";

    public static final String PROP_HEIGHT = "Height"; 
    public static final String PROP_WIDTH = "Width";
    public static final String PROP_BANDS = "Bands";
    public static final String PROP_COLORSPACE = "Colorspace";
    public static final String PROP_BITSPERELEMENT = "BitsPerElement";
    
    public static final String PROP_DWTLEVEL = "DWTLevel";
    public static final String PROP_DWTMETHOD = "DWTMethod"; 

    public static final String PROP_CTMethod = "CTMethod"; 

    public abstract int getCoeffInt(int x, int y, int b);

    public abstract short getCoeffShort(int x, int y, int b);

    public abstract float getCoeffFloat(int x, int y, int b);

    public abstract double getCoeffDouble(int x, int y, int b);

    public abstract void setCoeff(int x, int y, int b, int c);

    public abstract void setCoeff(int x, int y, int b, short c);

    public abstract void setCoeff(int x, int y, int b, float c);

    public abstract void setCoeff(int x, int y, int b, double c);
    
    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getNumBands();

    public abstract Object getCoeffs();

    public abstract void newCoeffs(int width, int height, int bands);

    public abstract void setCoeffs(Object c);
    
    public abstract int getAbsMaxInt(int b, int x, int y, int w, int h);
    
    public ImageCoeff() {
        propertySets = new Hashtable();
    }
    
    public int getNumPropertySets() {
        return propertySets.size();
    }

    public Properties addPropertySet(String setName) {
        Properties newProp = new Properties();
        propertySets.put(setName, newProp);
        return newProp;
    }

    public void removePropertySet(String setName) {
        propertySets.remove(setName);
    }

    public Properties getPropertySet(String setName) {
        return (Properties) propertySets.get(setName);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
