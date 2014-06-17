package edu.ttu.cvial.imageio.plugins.ttc.v50;

import java.awt.Rectangle;
import java.util.Hashtable;

import y0.imageio.ROI;
import y0.imageio.linebased.ImageProp;
import y0.wavelet.DWTProp;

import edu.ttu.cvial.imageio.plugins.ttc.TTCImageReadParam;

/**
 * A class describing how a TTC 5.0 image is to be decoded. It is recommended to
 * use
 * {@link edu.ttu.cvial.imageio.plugins.ttc.TTCImageReader#getDefaultReadParam()}
 * to get a default instance of this class.
 */
public class TTCImageReadParamV50 extends TTCImageReadParam {
	protected Hashtable param = new Hashtable();
	
	protected boolean isBestFitSourceRenderSize;
	protected boolean isBestFitSourceRegion;

    protected Hashtable getParam() {
        return param;
    }

    /**
     * {@inheritDoc}
     */
    public int getResolutionIndex() {
        return DWTProp.getResolutionIndex(param);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setResolutionIndex(int index) {
    	DWTProp.setResolutionIndex(param, index);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setSourceRegion(Rectangle sourceRegion) {
        super.setSourceRegion(sourceRegion);
        ROI imgROI = new ROI();
        imgROI.x = sourceRegion.x;
        imgROI.y = sourceRegion.y;
        imgROI.width = sourceRegion.width;
        imgROI.height = sourceRegion.height;
        ImageProp.setROI(param, imgROI);
    }

    /**
     * {@inheritDoc}
     */
	public boolean canSetSourceRenderSize() {
		return true;
	}

    /**
     * {@inheritDoc}
     */
	public boolean isBestFitSourceRenderSize() {
		return isBestFitSourceRenderSize;
	}

    /**
     * {@inheritDoc}
     */
	public void setBestFitSourceRenderSize(boolean isBestFitSourceRenderSize) {
		this.isBestFitSourceRenderSize = isBestFitSourceRenderSize;
	}
}
