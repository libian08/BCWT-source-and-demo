package edu.ttu.cvial.imageio;

import edu.ttu.cvial.arrayop.ArrayOp;

public class ImageCoeffShort extends ImageCoeff {
    
    private short[][][] coeffs;
    
    public int getCoeffInt(int x, int y, int b) {
        return (int) coeffs[b][y][x];
    }

    public short getCoeffShort(int x, int y, int b) {
        return coeffs[b][y][x];
    }

    public float getCoeffFloat(int x, int y, int b) {
        return (float) coeffs[b][y][x];
    }

    public double getCoeffDouble(int x, int y, int b) {
        return (double) coeffs[b][y][x];
    }

    public void setCoeff(int x, int y, int b, int c) {
        coeffs[b][y][x] = (short) c;
    }

    public void setCoeff(int x, int y, int b, short c) {
        coeffs[b][y][x] = c;
    }

    public void setCoeff(int x, int y, int b, float c) {
        coeffs[b][y][x] = (short) c;
    }

    public void setCoeff(int x, int y, int b, double c) {
        coeffs[b][y][x] = (short) c;
    }

    public int getWidth() {
        return coeffs[0][0].length;
    }

    public int getHeight() {
        return coeffs[0].length;
    }

    public int getNumBands() {
        return coeffs.length;
    }

    public Object getCoeffs() {
        return coeffs;
    }
    
    public ImageCoeffShort() {
        type = ImageCoeff.TYPE_SHORT;
    }

    public void newCoeffs(int width, int height, int bands) {
        coeffs = new short[bands][height][width];
    }

    public void setCoeffs(Object c) {
        coeffs = (short[][][]) c;
    }

    public int getAbsMaxInt(int b, int x, int y, int w, int h) {
        return (int) ArrayOp.getAbsMax(coeffs[b], x, y, w, h);
    }
}
