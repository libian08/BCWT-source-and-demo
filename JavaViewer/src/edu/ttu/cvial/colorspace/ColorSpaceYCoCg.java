package edu.ttu.cvial.colorspace;

public class ColorSpaceYCoCg {

    public final void fromRGB(short[][] band0, short[][] band1, short[][] band2) {
        fromRGB(band0, band1, band2, 0, 0, band0[0].length, band0.length);
    }

    public final void fromRGB(short[][] band0, short[][] band1,
            short[][] band2, int x, int y, int w, int h) {
        int xiEnd = x + w;
        int yiEnd = y + h;
        int R, G, B;
        int Y, Co, Cg, t;

        for (int yi = y; yi < yiEnd; yi++) {
            short[] band0Row = band0[yi];
            short[] band1Row = band1[yi];
            short[] band2Row = band2[yi];
            for (int xi = x; xi < xiEnd; xi++) {
                R = band0Row[xi];
                G = band1Row[xi];
                B = band2Row[xi];
                
                Co = R - B;
                t = B + (Co >> 1);
                Cg = G - t;
                Y = t + (Cg >> 1);
                
                band0Row[xi] = (short) Y;
                band1Row[xi] = (short) Co;
                band2Row[xi] = (short) Cg;
            }
        }
    }

    public final void fromRGB(short[][][] bands) {
        fromRGB(bands[0], bands[1], bands[2]);
    }

    public final void fromRGB(short[][][] bands, int x, int y, int w, int h) {
        fromRGB(bands[0], bands[1], bands[2], x, y, w, h);
    }

    public final void toRGB(short[][] band0, short[][] band1, short[][] band2) {

        toRGB(band0, band1, band2, 0, 0, band0[0].length, band0.length);
    }

    public final void toRGB(short[][] band0, short[][] band1, short[][] band2,
            int x, int y, int w, int h) {
        int yi, xi;
        int Y, Co, Cg, t;
        int R, G, B;

        int xiEnd = x + w;
        int yiEnd = y + h;

        for (yi = y; yi < yiEnd; yi++) {
            short[] band0Row = band0[yi];
            short[] band1Row = band1[yi];
            short[] band2Row = band2[yi];
            for (xi = x; xi < xiEnd; xi++) {
                Y = band0Row[xi];
                Co = band1Row[xi];
                Cg = band2Row[xi];
                
                t = Y - (Cg >> 1);
                G = Cg + t;
                B = t - (Co >> 1);
                R = B + Co;
                
                band0Row[xi] = (short) R;
                band1Row[xi] = (short) G;
                band2Row[xi] = (short) B;
            }
        }
    }

    public final void toRGB(short[][][] bands) {
        toRGB(bands[0], bands[1], bands[2]);
    }

    public final void toRGB(short[][][] bands, int x, int y, int w, int h) {
        toRGB(bands[0], bands[1], bands[2], x, y, w, h);
    }

}
