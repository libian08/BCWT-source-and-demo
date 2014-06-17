package edu.ttu.cvial.wavelet;

import edu.ttu.cvial.arrayop.ArrayOp;
import edu.ttu.cvial.wavelet.lifting.DWTLiftingFloat;

public class DWT {
    public static final int SB_LL = 0;
    public static final int SB_LH = 1;
    public static final int SB_HL = 2;
    public static final int SB_HH = 4;

//    public static int getSubBandIndex(int value, int sizeLLBand) {
//        int temp = value / sizeLLBand;
//        int sbIndex = Integer.SIZE - Integer.numberOfLeadingZeros(temp);
//        return sbIndex;
//    }
    
    public static int getSubBandType(int rowSBIndex, int colSBIndex) {
        return (rowSBIndex < colSBIndex) ? SB_LH
                : ((rowSBIndex > colSBIndex) ? SB_HL
                        : ((rowSBIndex == 0 && colSBIndex == 0) ? SB_LL : SB_HH));
    }
    
//    public static int getSubBandType(int row, int col, int rowsLLBand,
//            int colsLLBand) {
//        return getSubBandType(getSubBandIndex(row, rowsLLBand),
//                getSubBandIndex(col, colsLLBand));
//    }
    
    public static float[][][] scale(float[][][] imageOrg, int scalingFactor,
            DWTLiftingFloat dwt) {
        scalingFactor--;
        int rows = imageOrg[0].length >> scalingFactor;
        int cols = imageOrg[0][0].length >> scalingFactor;
        
        for (int b = 0; b < imageOrg.length; b++) {
            dwt.forwardTrans(imageOrg[b], scalingFactor);
            ArrayOp.divide(imageOrg[b], 1 << scalingFactor, 0, 0, cols, rows);
        }

        return imageOrg;
    }
    
    public static int getPaddedSize(int size, int dwtLevel) {
        int resolutionBase = 1 << (dwtLevel + 1);
        int sizeExtra = resolutionBase - size % resolutionBase;
        int paddedSize = (sizeExtra == resolutionBase) ? size :
            (size + sizeExtra);
        return paddedSize;
    }
}
