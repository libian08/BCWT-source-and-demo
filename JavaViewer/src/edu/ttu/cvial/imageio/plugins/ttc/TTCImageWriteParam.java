package edu.ttu.cvial.imageio.plugins.ttc;

import java.util.logging.Logger;

import javax.imageio.ImageWriteParam;

/**
 * An abstract class for paramters of encoding images in TTC format. This class
 * is designed to be a unified interface for all versions of TTC write params.
 * <p>
 * All TTC write params are subclasses of this class. For a specific version of
 * TTC write params, if certain methods are not supported, calls to those
 * methods will be ignored with a warning record in the log.
 * <p>
 * The following parameters have direct effect on the output file size and image
 * quality:
 * <ul>
 * <li><b>bit</b> : {@link #setBitTarget(int)}, {@link #getBitTarget()}
 * <li><b>bitrate</b> : {@link #setBitrateTarget(float)},
 * {@link #getBitrateTarget()}
 * <li><b>compression-ratio</b> : {@link #setCompRatioTarget(float)},
 * {@link #getCompRatioTarget()}
 * <li><b>quality-index</b> : {@link #setQualityIndex(float)},
 * {@link #getQualityIndex()}
 * </ul>
 * <p>
 * Since they are all related, if more than one of them are set, the
 * encoder will only achieve the one corresponding to the lowest bitrate (the
 * highest compression-ratio).
 * <p>
 */
public abstract class TTCImageWriteParam extends ImageWriteParam {
    
    public static Logger logger = Logger.getLogger("edu.ttu.cvial.imageio.plugins.ttc");

    /**
     * Returns the target bitrate, in bit-per-pixel, or bpp. The default value
     * is -1, which means the highest possible.
     * 
     * @return target bitrate.
     * @see #setBitrateTarget(float)
     * @since 4.0
     */
    public float getBitrateTarget() {
        logger.warning("Unsupported operation.");
        return 0;
    }

    /**
     * Returns the target number of bits. The default value is -1, which means
     * the highest possible.
     * 
     * @return number of bits targeting.
     * @see #setBitTarget(int)
     * @since 4.0
     */
    public int getBitTarget() {
        logger.warning("Unsupported operation.");
        return 0;
    }

    /**
     * Returns the target compression ratio. The default value is -1, which
     * means the lowest possible.
     * 
     * @return target compression ratio.
     * @see #setCompRatioTarget(float)
     * @since 4.0
     */
    public float getCompRatioTarget() {
        logger.warning("Unsupported operation.");
        return 0;
    }

    /**
     * Returns the number of Discrete Wavelet Tranform level. The default value
     * is 7.
     * 
     * @return number of DWT level.
     * @see #setDwtLevel(int)
     * @since 4.0
     */
    public int getDwtLevel() {
        logger.warning("Unsupported operation.");
        return 0;
    }

    /**
     * Returns the target quality-index.
     *  
     * @return the quality-index value.
     * @see #setQualityIndex(float)
     * @since 4.0
     */
    public float getQualityIndex() {
        logger.warning("Unsupported operation.");
        return 0;
    }

    
    /**
     * Returns if coding in reversible mode.
     * <p>
     * This method always return <code>false</code> by TTC codecs ealier than 4.1.
     * 
     * @return 
     *  <code>true</code> coding in reversible mode. <p>
     *  <code>false</code> coding in irreversible mode.
     *  
     * @see #setReversible(boolean)
     * 
     * @since 4.1
     */
    public boolean isReversible() {
        logger.warning("Unsupported operation.");
        return false;
    }

    /**
     * Sets the target bitrate, in bit-per-pixel, or bpp.
     * 
     * @param bitrateTarget
     *            target bitrate.
     * @see #getBitrateTarget() 
     * @since 4.0
     */
    public void setBitrateTarget(float bitrateTarget) {
        logger.warning("Unsupported operation.");
    }

    /**
     * Sets the number of bits targeting.
     * 
     * @param bitTarget
     *            number of bits targeting.
     * @see #getBitTarget()
     * @since 4.0
     */
    public void setBitTarget(int bitTarget) {
        logger.warning("Unsupported operation.");
    }

    /**
     * Sets the target compression ratio.
     * 
     * @param compRatioTarget
     *            target compression ratio.
     * @see #setCompRatioTarget(float)
     * @since 4.0
     */
    public void setCompRatioTarget(float compRatioTarget) {
        logger.warning("Unsupported operation.");
    }

    /**
     * Sets the number of Discrete Wavelet Tranform level. This number is also
     * exactly the number of resolutions (thumbnails) can be chose from in the
     * decoder.
     * 
     * @param dwtLevel
     *            the number of DWT level.
     * @see #getDwtLevel()
     * @since 4.0
     */
    public void setDwtLevel(int dwtLevel) {
        logger.warning("Unsupported operation.");
    }

    /**
     * Sets the quality-index (>=0). The smaller the value, the higher the image
     * quality, as well as the larger the output file size. A value of 0.0f has
     * the quality of near lossless. Estimated PSNRs for some value are listed
     * as below. The actual PSNRs are image dependent.
     * <ul>
     * <li><b>0.0</b> ~ 50dB 
     * <li><b>1.0</b> ~ 45dB
     * <li><b>2.0</b> ~ 38dB
     * <li><b>3.0</b> ~ 32dB
     * </ul>
     * Note: Since the rate-distortion characteristic, i.e. compression ratio 
     * vs. quality, is highly image-dependent, using a fix bitrate or
     * compression-ratio for different images may results in significantly 
     * different quality. When a certian level of quality is desired, it is 
     * recommended to use quality-index instead.
     * 
     * @param qualityIndex
     *            the quality-index.
     * @see #getQualityIndex()
     * @since 4.0
     */
    public void setQualityIndex(float qualityIndex) {
        logger.warning("Unsupported operation.");
    }
    
    /**
     * Set whether to use reversible mode or not. When in reversible mode, if
     * quality-index is set to 0.0f, the image is loseless-encoded; otherwise
     * the image is lossy-encoded.
     * <p>
     * @param isReversible
     *            <code>true</code> for using reversible mode;
     *            <code>false</code> (default) for using irreversible mode.
     *            
     * @since 4.1
     */
    public void setReversible(boolean isReversible) {
        logger.warning("Unsupported operation.");
    }
    
}