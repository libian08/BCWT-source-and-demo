package edu.ttu.cvial.colorspace;

public interface ColorTransform {

    public abstract void fromRGB(float[][] band0, float[][] band1,
            float[][] band2);

    public abstract void fromRGB(float[][] band0, float[][] band1,
            float[][] band2, int x, int y, int w, int h);

    public abstract void fromRGB(float[][][] bands);

    public abstract void fromRGB(float[][][] bands, int x, int y, int w, int h);

    public abstract void toRGB(float[][] band0, float[][] band1, float[][] band2);

    public abstract void toRGB(float[][] band0, float[][] band1,
            float[][] band2, int x, int y, int w, int h);

    public abstract void toRGB(float[][][] bands);

    public abstract void toRGB(float[][][] bands, int x, int y, int w, int h);

}