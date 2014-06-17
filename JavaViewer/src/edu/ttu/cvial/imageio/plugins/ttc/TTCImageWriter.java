package edu.ttu.cvial.imageio.plugins.ttc;

import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;

/**
 * An abstract class for encoding images in TTC format. This class is designed
 * to be a unified interface for all versions of TTC writers.
 * <p>
 * All TTC writers are subclasses of this class. For a specific version of TTC
 * writer, if certain methods are not supported, calls to those methods will be
 * ignored with a warning record in the log.
 * <p>
 * Because all versions of TTC images have the same ".ttc" extension, it is
 * recommended to use
 * {@link javax.imageio.ImageIO#getImageWritersByFormatName(String)} to get an
 * appropriate writer object, where the <code>String</code> is "TTC [version]" (
 * e.g. "TTC 4.0" or "TTC 4.1").
 * <p>
 */
public abstract class TTCImageWriter extends ImageWriter {

    public static Logger logger = Logger.getLogger("edu.ttu.cvial.imageio.plugins.ttc");

    protected TTCImageWriter(ImageWriterSpi arg0) {
        super(arg0);
    }

    /**
     * Returns the number of bits actually encoded. Return value is undetermined
     * if read before encoding.
     * 
     * @return the number of bits encoded.
     * @since 4.0
     */
    public int getBitAchieved() {
        logger.warning("Unsupported operation.");
        return 0;
    }

    /**
     * Returns bitrate actually achieved. Return value is undetermined if read
     * before encoding.
     * 
     * @return the bitrate achieved.
     * @since 4.0
     */
    public float getBitrateAchieved() {
        logger.warning("Unsupported operation.");
        return 0;
    }

    /**
     * Returns compression-ratio actually achieved. Return value is undetermined
     * if read before encoding.
     * 
     * @return the compression-ratio achieved.
     * @since 4.0
     */
    public float getCompRatioAchieved() {
        logger.warning("Unsupported operation.");
        return 0;
    }
    
    /**
     * Returns quality-index actually achieved. Return value is undetermined if
     * read before encoding.
     * 
     * @return the quality-index achieved.
     * @since 4.0
     */
    public float getQualityIndexAchieved() {
        logger.warning("Unsupported operation.");
        return 0;
    }

    /**
     * Returns a default <code>ImageWriteParam</code> object appropriate for
     * TTC format. To gain access to TTC-specific parameters, cast the returned
     * object to <code>TTCImageWriteParam</code>. This method may be called
     * before the output is set.
     * 
     * @return an <code>TTCImageWriteParam</code> object be used to control the
     *         encoding process with a set of default settings.
     * @see javax.imageio.ImageWriter#getDefaultWriteParam()
     * @since 4.0
     */
    public abstract ImageWriteParam getDefaultWriteParam();
    
    /**
     * Image metadata is not supported yet. This method always returns
     * <code>null</code>.
     * 
     * @return null
     * @since 4.0
     */
    public IIOMetadata convertImageMetadata(IIOMetadata arg0,
            ImageTypeSpecifier arg1, ImageWriteParam arg2) {
        return null;
    }

    /**
     * Stream metadata is not supported yet. This method always returns
     * <code>null</code>.
     * 
     * @return null
     * @since 4.0
     */
    public IIOMetadata convertStreamMetadata(IIOMetadata arg0,
            ImageWriteParam arg1) {
        return null;
    }
    
    /**
     * Image metadata is not supported yet. This method always returns
     * <code>null</code>.
     * 
     * @return null
     * @since 4.0
     */
    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType,
            ImageWriteParam param) {
        return null;
    }

    /**
     * Stream metadata is not supported yet. This method always returns
     * <code>null</code>.
     * 
     * @return null
     * @since 4.0
     */
    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
        return null;
    }
    
    /**
     * Sets the destination to the given <code>ImageOutputStream</code>. If
     * <code>output</code> is <code>null</code>, any currently set output
     * will be removed. A non-<code>null</code> output must be set before
     * encoding starts.
     * 
     * @param output
     *            the destination of the encoded data.
     * @throws IllegalArgumentException
     *             if <code>output</code> is not an instance of
     *             <code>ImageOutputStream</code>.
     * @see javax.imageio.ImageWriter#setOutput(java.lang.Object)
     * @since 4.0
     */
    public void setOutput(Object output) {
        super.setOutput(output);
    }

    /**
     * Encodes the <code>image</code>, and writes all encoded data to the
     * output specified by {@link #setOutput(Object)}. <code>image</code> can
     * be either a gray-scale or RGB-color image.
     * <p>
     * Since metadata is not supported yet, <code>streamMetadata</code> will
     * be ignored. A
     * <code>TTCImageWriteParam</code> may optionally be supplied to control
     * the encoding process. If <code>param</code> is <code>null</code>, a
     * default set of parameters will be used.
     * 
     * @param streamMetadata
     *            <code>null</code>, or be ignored.
     * @param image
     *            the image to be encoded.
     * @param param
     *            <code>null</code> or must be an
     *            <code>TTCImageWriteParam</code> object, otherwise it will be
     *            ignored.
     * @throws IllegalStateException
     *             if the output has not been set.
     * @throws IllegalArgumentException
     *             if <code>image</code> is <code>null</code>.
     * @throws IOException
     *             if an error occurs during writing.
     * @see edu.ttu.cvial.imageio.plugins.ttc.v40.TTCImageWriteParamV40
     * @see javax.imageio.ImageWriter#write(IIOMetadata, IIOImage,
     *      ImageWriteParam)
     * @since 4.0
     */
    public abstract void write(IIOMetadata streamMetadata, IIOImage image,
            ImageWriteParam param) throws IOException;
}