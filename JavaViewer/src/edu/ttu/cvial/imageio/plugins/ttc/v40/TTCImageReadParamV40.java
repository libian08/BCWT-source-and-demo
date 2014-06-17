package edu.ttu.cvial.imageio.plugins.ttc.v40;

import edu.ttu.cvial.codec.sq.bcwt.BCWTDecoder.Param;
import edu.ttu.cvial.imageio.plugins.ttc.TTCImageReadParam;

/**
 * A class describing how a TTC 4.0 image is to be decoded. It is recommended to
 * use
 * {@link edu.ttu.cvial.imageio.plugins.ttc.TTCImageReader#getDefaultReadParam()}
 * to get a default instance of this class.
 */
public class TTCImageReadParamV40 extends TTCImageReadParam {
    protected Param param = new Param();

    /**
     * {@inheritDoc}
     */
    public int getResolutionIndex() {
        return param.getResolutionIndex();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setResolutionIndex(int index) {
        param.setResolutionIndex(index);
    }
    
    protected Param getParam() {
        return param;
    }

}
