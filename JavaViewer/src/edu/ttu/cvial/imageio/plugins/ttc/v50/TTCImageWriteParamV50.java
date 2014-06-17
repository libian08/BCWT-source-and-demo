package edu.ttu.cvial.imageio.plugins.ttc.v50;

import java.util.Hashtable;

import y0.wavelet.DWTProp;
import y0.wavelet.bcwt.linebased.BCWTProp;

import edu.ttu.cvial.imageio.plugins.ttc.TTCImageWriteParam;

/**
 * A class describing how to encode an image in TTC 5.0 format. It is recommended
 * to use
 * {@link edu.ttu.cvial.imageio.plugins.ttc.TTCImageWriter#getDefaultWriteParam()}
 * to get a default instance of this class.
 */
public class TTCImageWriteParamV50 extends TTCImageWriteParam {
    protected Hashtable param = new Hashtable();

    public TTCImageWriteParamV50() {
    	setQualityIndex(2);
    	setDwtLevel(5);
    	
    	BCWTProp.setBlockWidth(param, 8);
    	BCWTProp.setBlockHeight(param, 8);
    }
    
//    /**
//     * {@inheritDoc}
//     */
//    public float getBitrateTarget() {
//        return param.getBitrateTarget();
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    public int getBitTarget() {
//        return param.getBitTarget();
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    public float getCompRatioTarget() {
//        return param.getCompRatioTarget();
//    }

    /**
     * {@inheritDoc}
     */
    public int getDwtLevel() {
        return DWTProp.getDwtLevelNum(param);
    }

    /**
      * {@inheritDoc}
    */
    public float getQualityIndex() {
        return BCWTProp.getQMin(param);
    }
    
//    public boolean isReversible() {
//        return param.isReversible();
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    public void setBitrateTarget(float bitrateTarget) {
//        param.setBitrateTarget(bitrateTarget);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    public void setBitTarget(int bitTarget) {
//        param.setBitTarget(bitTarget);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    public void setCompRatioTarget(float compRatioTarget) {
//        param.setCompRatioTarget(compRatioTarget);
//    }

    /**
     * {@inheritDoc}
     */
    public void setDwtLevel(int dwtLevel) {
        DWTProp.setDwtLevelNum(param, dwtLevel);
    }

    /**
     * {@inheritDoc}
     */
    public void setQualityIndex(float qualityIndex) {
        BCWTProp.setQMin(param, (int)qualityIndex);
    }
    
//    /**
//     * {@inheritDoc}
//     */
//    public void setReversible(boolean isReversible) {
//        param.setReversible(isReversible);
//    }
    
    protected Hashtable getParam() {
        return param;
    }

}
