package edu.ttu.cvial.wavelet.lifting;

import java.awt.Rectangle;

import edu.ttu.cvial.wavelet.DWT;

public abstract class DWTLiftingShort extends DWT {

    private short[] X;

    /** Length of input data being processed */
    private int N;

    /**
     * Length minus 1 of the data being processed (N - 1). This field is
     * initialized by {@link #initStep initStep} and should not be changed
     * manually.
     */
    protected int N_minus_1;

    /**
     * Half the length of the data being processed (N/2). This field is
     * initialized by {@link #initStep initStep} and should not be changed
     * manually.
     */
    protected int halfN;

    /**
     * Half the length minus 1 of the data being processed (N/2 - 1). This field
     * is initialized by {@link #initStep initStep} and should not be changed
     * manually.
     */
    protected int halfN_minus_1;
    
    protected short[] hx;
    protected short[] lx;
    protected short[] temp;

    protected abstract void forwardTransStep(short[] X, int N);

    protected abstract void inverseTransStep(short[] X, int N);

    public DWTLiftingShort() {
    }

    protected final void initStep(short[] X, int N) {
        this.X = X;
        this.N = N;
        N_minus_1 = N - 1;
        halfN = N >> 1;
        halfN_minus_1 = halfN - 1;
    }

    public short[][] forwardTrans(short[][] data, int maxLevel) {
        return forwardTransFast(data, maxLevel, 1, data.length, data[0].length);
    }
    
    public short[][] forwardTransFast(short[][] data, int maxLevel,
            int minLevel, int rows, int cols) {
        rows = rows >> (minLevel - 1);
        cols = cols >> (minLevel - 1);
        int maxLen = rows > cols ? rows : cols;
        short[] data1D0 = new short[maxLen];
        short[] data1D1 = new short[maxLen];
        short[] data1D2 = new short[maxLen];
        short[] data1D3 = new short[maxLen];
        int r, c, o, e;
        short[] thisRow;
        hx = new short[maxLen >> 1];
        lx = new short[maxLen >> 1];

        for (int currentLevel = minLevel; currentLevel <= maxLevel; rows >>= 1, cols >>= 1, currentLevel++) {
            int rowsHalf = rows >> 1;
            int colsHalf = cols >> 1;

            // Transforms row-wise.
            for (r = 0; r < rows; ++r)
                forwardTransStep(data[r], cols);


            // Transform column-wise.
            for (int c0 = cols - 1, c1 = cols - 2, c2 = cols - 3, c3 = cols - 4; c0 > 2; c0 -= 4, c1 -= 4, c2 -= 4, c3 -= 4) {
                for (r = 0; r < rows; ++r) {
                    thisRow = data[r];
                    data1D0[r] = thisRow[c0];
                    data1D1[r] = thisRow[c1];
                    data1D2[r] = thisRow[c2];
                    data1D3[r] = thisRow[c3];
                }

                forwardTransStep(data1D0, rows);
                forwardTransStep(data1D1, rows);
                forwardTransStep(data1D2, rows);
                forwardTransStep(data1D3, rows);

                for (r = 0; r < rows; ++r) {
                    thisRow = data[r];
                    thisRow[c0] = data1D0[r];
                    thisRow[c1] = data1D1[r];
                    thisRow[c2] = data1D2[r];
                    thisRow[c3] = data1D3[r];
                }
            }
        }

        return data;
    }


    public short[][] forwardTrans(short[][] data, int maxLevel, int minLevel,
            int rows, int cols) {
        int r, c;
        int maxLen = rows > cols ? rows : cols;
        short[] data1D = new short[maxLen];
        rows = rows >> (minLevel - 1);
        cols = cols >> (minLevel - 1);
        
        hx = new short[maxLen >> 1];
        lx = new short[maxLen >> 1];

        for (int currentLevel = minLevel; currentLevel <= maxLevel; rows >>= 1, cols >>= 1, currentLevel++) {

            for (r = 0; r < rows; r++) {
                forwardTransStep(data[r], cols);
            }

            for (c = 0; c < cols; c++) {
                for (r = 0; r < rows; r++) {
                    data1D[r] = data[r][c];
                }
                forwardTransStep(data1D, rows);
                for (r = 0; r < rows; r++)
                    data[r][c] = data1D[r];
            }

        }
        return data;
    }

    public short[][] inverseTransROI(short[][] data, int maxLevel, int minLevel,
            int rows, int cols, Rectangle sourceRegion) {
        short[] data1D = new short[(rows > cols ? rows : cols) >> (minLevel - 1)];
        int oneHalf;
        int maxLen = (rows > cols ? rows : cols) >> (minLevel - 1);
        temp = new short[maxLen];
        
        int negSupport = 4;
        int posSupport = 4;
        int roiLevel = 1;
        int[] maskLeftX = new int[roiLevel + 1];
        maskLeftX[0] = sourceRegion.x;
        for (int i = 1; i <= roiLevel; i++) {
            maskLeftX[i] = (maskLeftX[i-1] >> 1) - negSupport;
            if (maskLeftX[i] < 0)
                maskLeftX[i] = 0;
        }
        
        int[] maskRightX = new int[roiLevel + 1];
        maskRightX[0] = sourceRegion.x + sourceRegion.width;
        for (int i = 1; i <= roiLevel; i++) {
            maskRightX[i]= (maskRightX[i-1] >> 1) + posSupport;
            if (maskRightX[1] > (cols >> i))
                maskRightX[1] = (cols >> i);
        }
        
        int[] maskTopY = new int[roiLevel + 1];
        for (int i = 1; i <= roiLevel; i++) {
            maskTopY[i] = sourceRegion.y >> (i - 1);
        }
        
        int[] maskBottomY = new int[roiLevel + 1];
        for (int i = 1; i <= roiLevel; i++) {
            maskBottomY[i] = (sourceRegion.y + sourceRegion.height) >> (i - 1);
        }

        rows >>= maxLevel - 1;
        cols >>= maxLevel - 1;
        for (int currentLevel = maxLevel; currentLevel >= minLevel; rows <<= 1, cols <<= 1, currentLevel--) {

            if (currentLevel > roiLevel) {
                // Normal i-DWT
                // Inverse transform column-wise.
                for (int j = 0; j < cols; j++) {
                    inverseTransOneColumn(data, rows, data1D, j);
                }
    
                // Inverse transform row-wise
                for (int i = 0; i < rows; i++) {
                    inverseTransOneRow(data, cols, data1D, i);
                }
            } else {
                // ROI i-DWT
                // Inverse transform column-wise.
                for (int j = maskLeftX[currentLevel]; j < maskRightX[currentLevel]; j++) {
                    inverseTransOneColumn(data, rows, data1D, j);
                }
                int startCol = maskLeftX[currentLevel] + (cols >> 1);
                int endCol = maskRightX[currentLevel] + (cols >> 1);
                for (int j = startCol; j < endCol; j++) {
                    inverseTransOneColumn(data, rows, data1D, j);
                }

                // Inverse transform row-wise
                for (int i = maskTopY[currentLevel]; i < maskBottomY[currentLevel]; i++) {
                    inverseTransOneRow(data, cols, data1D, i);
                }
            }
            
            
        }

        return data;
    }

    private void inverseTransOneRow(short[][] data, int cols, short[] data1D, int i) {
//        int oneHalf;
        inverseTransStep(data[i], cols);
//        short[] thisRow = data[i];
//        oneHalf = cols >> 1;
//        for (int j = 0, a = 0; j < oneHalf; j++) {
//            data1D[a++] = thisRow[j];
//            data1D[a++] = thisRow[oneHalf + j];
//        }
//        for (int j = 0; j < cols; j++) {
//            thisRow[j] = data1D[j];
//        }
    }

    private void inverseTransOneColumn(short[][] data, int rows, short[] data1D, int j) {
        int oneHalf;
        for (int i = 0; i < rows; i++)
            data1D[i] = data[i][j];
        inverseTransStep(data1D, rows);
        for (int i = 0; i < rows; i++)
            data[i][j] = data1D[i]; 
    }

    public short[][] inverseTrans(short[][] data, int maxLevel) {
        return inverseTransFast(data, maxLevel, 1, data.length, data[0].length);
    }

    public short[][] inverseTrans(short[][] data, int maxLevel, int minLevel,
            int rows, int cols) {
        int maxLen = (rows > cols ? rows : cols) >> (minLevel - 1);
        short[] data1D = new short[maxLen];
        temp = new short[maxLen];
        int r, c;

        rows >>= maxLevel - 1;
        cols >>= maxLevel - 1;

        for (int currentLevel = maxLevel; currentLevel >= minLevel; rows <<= 1, cols <<= 1, currentLevel--) {
            for (c = 0; c < cols; c++) {
                for (r = 0; r < rows; r++) {
                    data1D[r] = data[r][c];
                }
                inverseTransStep(data1D, rows);
                for (r = 0; r < rows; r++)
                    data[r][c] = data1D[r];
            }

            for (r = 0; r < rows; r++) {
                inverseTransStep(data[r], cols);
            }
        }
        return data;
    }
    
    public short[][] inverseTransFast(short[][] data, int maxLevel, int minLevel
            , int rows, int cols) {
        int maxLen = (rows > cols ? rows : cols) >> (minLevel - 1);
        short[] data1D0 = new short[maxLen];
        short[] data1D1 = new short[maxLen];
        short[] data1D2 = new short[maxLen];
        short[] data1D3 = new short[maxLen];
        short[] thisRow;
        temp = new short[maxLen];
        int r, c, o, e;

        rows >>= maxLevel - 1;
        cols >>= maxLevel - 1;
        for (int currentLevel = maxLevel; currentLevel >= minLevel
            ; rows <<= 1, cols <<= 1, currentLevel--) {
            int rowsHalf = rows >> 1;
            int colsHalf = cols >> 1;
            
            // Inverse transform column-wise.
            for (int c0 = cols - 1, c1 = cols - 2, c2 = cols - 3, c3 = cols - 4
                ; c0 > 2; c0 -= 4, c1 -= 4, c2 -= 4, c3 -= 4) {
                for (r = 0; r < rows; r++) {
                    thisRow = data[r];
                    data1D0[r] = thisRow[c0];
                    data1D1[r] = thisRow[c1];
                    data1D2[r] = thisRow[c2];
                    data1D3[r] = thisRow[c3];
                }
                inverseTransStep(data1D0, rows);
                inverseTransStep(data1D1, rows);
                inverseTransStep(data1D2, rows);
                inverseTransStep(data1D3, rows);

                for (r = 0; r < rows; r++) {
                    thisRow = data[r];
                    thisRow[c0] = data1D0[r];
                    thisRow[c1] = data1D1[r];
                    thisRow[c2] = data1D2[r];
                    thisRow[c3] = data1D3[r];
                }
            }

            // Inverse transform row-wise
            for (r = 0; r < rows; r++) {
                inverseTransStep(data[r], cols);
            }
        }
        return data;
    }
}
