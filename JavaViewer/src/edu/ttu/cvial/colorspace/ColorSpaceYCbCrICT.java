package edu.ttu.cvial.colorspace;

public class ColorSpaceYCbCrICT implements ColorTransform {

    public final void fromRGB(float[][] band0, float[][] band1, float[][] band2) {
        fromRGB(band0, band1, band2, 0, 0, band0[0].length, band0.length);
    }

    public final void fromRGB(float[][] band0, float[][] band1,
            float[][] band2, int x, int y, int w, int h) {
        int xiEnd = x + w;
        int yiEnd = y + h;
        double r, g, b;

        for (int yi = y; yi < yiEnd; yi++) {
            float[] band0Row = band0[yi];
            float[] band1Row = band1[yi];
            float[] band2Row = band2[yi];
            for (int xi = x; xi < xiEnd; xi++) {
                r = band0Row[xi];
                g = band1Row[xi];
                b = band2Row[xi];
                band0Row[xi] = (float) (0.2989 * r + 0.5866 * g + 0.1145 * b);
                band1Row[xi] = (float) (-0.1687 * r - 0.3312 * g + 0.5000 * b + 127.5);
                band2Row[xi] = (float) (0.5000 * r - 0.4183 * g - 0.0816 * b + 127.5);
            }
        }
    }

    public final void fromRGB(float[][][] bands) {
        fromRGB(bands[0], bands[1], bands[2]);
    }

    public final void fromRGB(float[][][] bands, int x, int y, int w, int h) {
        fromRGB(bands[0], bands[1], bands[2], x, y, w, h);
    }

    public final void toRGB(float[][] band0, float[][] band1, float[][] band2) {

        toRGB(band0, band1, band2, 0, 0, band0[0].length, band0.length);
    }

    public final void toRGB(float[][] band0, float[][] band1, float[][] band2,
            int x, int y, int w, int h) {
        int yi, xi;
        double Y, Cb, Cr;

        int xiEnd = x + w;
        int yiEnd = y + h;

        for (yi = y; yi < yiEnd; yi++) {
            float[] band0Row = band0[yi];
            float[] band1Row = band1[yi];
            float[] band2Row = band2[yi];
            for (xi = x; xi < xiEnd; xi++) {
                Y = band0Row[xi];
                Cb = band1Row[xi] - 127.5;
                Cr = band2Row[xi] - 127.5;
                band0Row[xi] = (float) (Y + 1.4022 * Cr);
                band1Row[xi] = (float) (Y - 0.3456 * Cb - 0.7145 * Cr);
                band2Row[xi] = (float) (Y + 1.7710 * Cb);
            }
        }
    }

    public final void toRGB(float[][][] bands) {
        toRGB(bands[0], bands[1], bands[2]);
    }

    public final void toRGB(float[][][] bands, int x, int y, int w, int h) {
        toRGB(bands[0], bands[1], bands[2], x, y, w, h);
    }

}
