package edu.ttu.cvial.imageio.plugins.ttc.v40;

import edu.ttu.cvial.codec.sq.bcwt.BCWTEncoder.Param;
import edu.ttu.cvial.imageio.plugins.ttc.TTCImageWriteParam;

/**
 * A class describing how encode an image in TTC 4.0 format. It is recommended
 * to use
 * {@link edu.ttu.cvial.imageio.plugins.ttc.TTCImageWriter#getDefaultWriteParam()}
 * to get a default instance of this class.
 */
public class TTCImageWriteParamV40 extends TTCImageWriteParam {
    protected Param param = new Param();

    /**
     * {@inheritDoc}
     */
    public float getBitrateTarget() {
        return param.getBitrateTarget();
    }

    /**
     * {@inheritDoc}
     */
    public int getBitTarget() {
        return param.getBitTarget();
    }

    /**
     * {@inheritDoc}
     */
    public float getCompRatioTarget() {
        return param.getCompRatioTarget();
    }

    /**
     * {@inheritDoc}
     */
    public int getDwtLevel() {
        return param.getDwtLevel();
    }

    /**
     * {@inheritDoc}
     */
    public float getQualityIndex() {
        return param.getQualityIndex();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBitrateTarget(float bitrateTarget) {
        param.setBitrateTarget(bitrateTarget);
    }

    /**
     * {@inheritDoc}
     */
    public void setBitTarget(int bitTarget) {
        param.setBitTarget(bitTarget);
    }

    /**
     * {@inheritDoc}
     */
    public void setCompRatioTarget(float compRatioTarget) {
        param.setCompRatioTarget(compRatioTarget);
    }

    /**
     * {@inheritDoc}
     */
    public void setDwtLevel(int dwtLevel) {
        param.setDwtLevel(dwtLevel);
    }

    /**
     * {@inheritDoc}
     */
    public void setQualityIndex(float qualityIndex) {
        param.setQualityIndex(qualityIndex);
    }

    protected Param getParam() {
        return param;
    }
}
