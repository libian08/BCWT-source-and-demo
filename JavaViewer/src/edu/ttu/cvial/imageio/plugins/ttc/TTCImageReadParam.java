package edu.ttu.cvial.imageio.plugins.ttc;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.logging.Logger;

import javax.imageio.ImageReadParam;

/**
 * An abstract class for paramters of decoding images in TTC format. This class
 * is designed to be a unified interface for all versions of TTC read params.
 * <p>
 * All TTC read params are subclasses of this class. For a specific version of
 * TTC read params, if certain methods are not supported, calls to those
 * methods will be ignored with a warning record in the log.
 * <p>
 */
public abstract class TTCImageReadParam extends ImageReadParam {

    public static Logger logger = Logger.getLogger("edu.ttu.cvial.imageio.plugins.ttc");

    /**
     * Returns the target resolution index.
     * 
     * @return the target resulution index.
     * @see #setResolutionIndex(int)
     * @since 4.0
     */
    public int getResolutionIndex() {
        logger.warning("Unsupported operation.");
        return 0;
    }

    /**
     * Sets the target resolution index. The possible index value is from 0 to 
     * the DWT level.
     * 
     * @param index
     *            target resolution index. 0 for the lowest resolution; each 
     *            level up doubles the height and width until the full 
     *            resolution is reached. 
     * @see #getResolutionIndex()
     * @since 4.0
     */
    public void setResolutionIndex(int index) {
        logger.warning("Unsupported operation.");
    }

    /**
     * Returns the source region to be used.  The returned value is
     * that set by the most recent call to
     * <code>setSourceRegion</code>, and will be <code>null</code> if
     * there is no region set.
     * 
     * @return the source region of interest as a
     * <code>Rectangle</code>, or <code>null</code>.
     *
     * @see #setSourceRegion
     * @since 4.1
     */
    public Rectangle getSourceRegion() {
        return super.getSourceRegion();
    }

    
    /**
     * Sets the source region of interest. The region of interest is described
     * as a rectangle, with the upper-left corner of the source image as pixel
     * (0, 0) and increasing values down and to the right.
     * <p>
     * The source region of interest specified by this method will be clipped as
     * needed to fit within the source bounds at the time of actual I/O.
     * <p>
     * A value of <code>null</code> for <code>sourceRegion</code> will
     * remove any region specification, causing the entire image to be used.
     * 
     * @param sourceRegion
     *            a <code>Rectangle</code> specifying the source region of
     *            interest, or <code>null</code>.
     * @exception IllegalArgumentException
     *                if <code>sourceRegion</code> is non-<code>null</code>
     *                and either <code>sourceRegion.x</code> or
     *                <code>sourceRegion.y</code> is negative.
     * @exception IllegalArgumentException
     *                if <code>sourceRegion</code> is non-<code>null</code>
     *                and either <code>sourceRegion.width</code> or
     *                <code>sourceRegion.height</code> is negative or 0.
     * @exception IllegalStateException
     *                if subsampling is such that this region will have a
     *                subsampled width or height of zero.
     * @see #getSourceRegion
     * @since 4.1
     */
    public void setSourceRegion(Rectangle sourceRegion) {
        super.setSourceRegion(sourceRegion);
    }

	/**
	 * Return if BestFitSourceRenderSize is true.
	 * @return <code>true</code> or <code>false</code>
	 * @see TTCImageReadParam#setBestFitSourceRenderSize(boolean)
	 * @since 5.0
	 */
	public boolean isBestFitSourceRenderSize() {
		logger.warning("Unsupported operation.");
		return false;
	}
	
	/**
	 * If set to true, reader will scale the sourceRegion to fit the sourceRenderSize
	 * while maintaining aspect ratio.
	 * @param isBestFitSourceRenderSize
	 * @since 5.0
	 */
	public void setBestFitSourceRenderSize(boolean isBestFitSourceRenderSize) {
		logger.warning("Unsupported operation.");
	}
	
	
	/**
	 * Returns true if this reader allows the source image to be rendered at an
	 * arbitrary size as part of the decoding process, by means of the
	 * setSourceRenderSize method. If this method returns false, calls to
	 * setSourceRenderSize will throw an UnsupportedOperationException.
	 * 
	 * Returns: true if setting source rendering size is supported.
	 * 
	 * @see javax.imageio.ImageReadParam#canSetSourceRenderSize()
	 * 
	 * @since 5.0
	 */
	public boolean canSetSourceRenderSize() {
		return false;
	}

	/**
	 * If the image is able to be rendered at an arbitrary size, sets the source
	 * width and height to the supplied values. Note that the values returned
	 * from the getWidth and getHeight methods on ImageReader are not affected
	 * by this method; they will continue to return the default size for the
	 * image. Similarly, if the image is also tiled the tile width and height
	 * are given in terms of the default size. Typically, the width and height
	 * should be chosen such that the ratio of width to height closely
	 * approximates the aspect ratio of the image, as returned from
	 * ImageReader.getAspectRatio.<p>
	 * 
	 * If this plug-in does not allow the rendering size to be set, an
	 * UnsupportedOperationException will be thrown.<p>
	 * 
	 * To remove the render size setting, pass in a value of null for size.<p>
	 * 
	 * @param size - a Dimension indicating the desired width and height.
	 * @throws IllegalArgumentException if either the width or the height is
	 * negative or 0. 
	 * @throws UnsupportedOperationException if image resizing is not
	 * supported by this plug-in.
	 * 
	 * @see javax.imageio.ImageReadParam#setSourceRenderSize(java.awt.Dimension)
	 * @since 5.0
	 */
	public void setSourceRenderSize(Dimension size)
			throws UnsupportedOperationException {
		super.setSourceRenderSize(size);
	}
	
	

}