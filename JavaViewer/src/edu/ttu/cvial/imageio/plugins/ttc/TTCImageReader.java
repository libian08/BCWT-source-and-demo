package edu.ttu.cvial.imageio.plugins.ttc;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;

/**
 * An abstract class for decoding images in TTC format. This class is designed
 * to be a unified interface for all versions of TTC readers.
 * <p>
 * All TTC readers are subclasses of this class. For a specific version of TTC
 * reader, if certain methods are not supported, calls to those methods will be
 * ignored with a warning record in the log.
 * <p>
 * Because all versions of TTC images have the same ".ttc" extension, it is
 * recommended to use {@link javax.imageio.ImageIO#getImageReaders(Object)} to
 * automatically get an appropriate reader object based on the content of the
 * input source.
 * <p>
 */
public abstract class TTCImageReader extends ImageReader {

    public static Logger logger = Logger
            .getLogger("edu.ttu.cvial.imageio.plugins.ttc");

    protected TTCImageReader(ImageReaderSpi originatingProvider) {
        super(originatingProvider);
    }

    /**
     * Returns the number of bits actually decoded. Return value is undetermined
     * if read before decoding.
     * 
     * @return the number of bits decoded.
     * @since 4.0
     */
    public int getBitAchieved() {
        logger.warning("Unsupported operation.");
        return 0;
    }

    /**
     * Returns bitrate actually achieved. Return value is undetermined if read
     * before decoding.
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
     * if read before decoding.
     * 
     * @return the compression-ratio achieved.
     * @since 4.0
     */
    public float getCompRatioAchieved() {
        logger.warning("Unsupported operation.");
        return 0;
    }

    /**
     * Returns the height in pixels of the given image within the input source.
     * 
     * @param imageIndex
     *            the index of the image to be retrieved.
     * @return the height of the image, as an int.
     * @throws IOException
     *             if an error occurs reading the information from the input
     *             source.
     * @throws IllegalStateException
     *             if the input source has not been set.
     * @see javax.imageio.ImageReader#getHeight(int)
     * @since 4.0
     */
    public abstract int getHeight(int imageIndex) throws IOException;

    /**
     * Image metadata is not supported yet. This method always returns
     * <code>null</code>.
     * 
     * @return null
     * @since 4.0
     */
    public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
        return null;
    }

    /**
     * Stream metadata is not supported yet. This method always returns
     * <code>null</code>.
     * 
     * @return null
     * @since 4.0
     */
    public IIOMetadata getStreamMetadata() throws IOException {
        return null;
    }

    /**
     * Returns the height of the thumbnail of the given image.
     * 
     * @param imageIndex
     *            the index of the image to be retrieved.
     * @param thumbnailIndex
     *            the index of the thumbnail to be retrieved.
     * @return the height of the desired thumbnail as an int.
     * @throws IOException
     *             if an error occurs reading the information from the input
     *             source.
     * @throws IllegalStateException
     *             if the input source has not been set.
     * @throws IndexOutOfBoundsException
     *             if the thumbnail index is out of bounds.
     * @see javax.imageio.ImageReader#getThumbnailHeight(int, int)
     * @since 4.0
     */
    public abstract int getThumbnailHeight(int imageIndex, int thumbnailIndex)
            throws IOException;

    /**
     * Returns the width of the thumbnail of the given image.
     * 
     * @param imageIndex
     *            the index of the image to be retrieved.
     * @param thumbnailIndex
     *            the index of the thumbnail to be retrieved.
     * @return the width of the desired thumbnail as an int.
     * @throws IOException
     *             if an error occurs reading the information from the input
     *             source.
     * @throws IllegalStateException
     *             if the input source has not been set.
     * @throws IndexOutOfBoundsException
     *             if the thumbnail index is out of bounds.
     * @see javax.imageio.ImageReader#getThumbnailHeight(int, int)
     * @since 4.0
     */
    public abstract int getThumbnailWidth(int imageIndex, int thumbnailIndex)
            throws IOException;

    /**
     * Returns the width in pixels of the given image within the input source.
     * 
     * @param imageIndex
     *            the index of the image to be retrieved.
     * @return the width of the image, as an int.
     * @throws IOException
     *             if an error occurs reading the information from the input
     *             source.
     * @throws IllegalStateException
     *             if the input source has not been set.
     * @see javax.imageio.ImageReader#getWidth(int)
     * @since 4.0
     */
    public abstract int getWidth(int imageIndex) throws IOException;

    /**
     * Returns <code>true</code> if the given images has thumbnails.
     * 
     * @param imageIndex
     *            the index of the image to be retrieved.
     * @return Always <code>true</code>.
     * @throws IOException
     *             if an error occurs reading the information from the input
     *             source.
     * @throws IllegalStateException
     *             if the input source has not been set.
     * @see javax.imageio.ImageReader#hasThumbnails(int)
     * @since 4.0
     */
    public abstract boolean hasThumbnails(int imageIndex) throws IOException;

    /**
     * Reads the image indexed by <code>imageIndex</code> and returns it as a
     * complete <code>BufferedImage</code>, using a supplied
     * <code>ImageReadParam</code>.
     * 
     * @param imageIndex
     *            the index of the image to be retrieved.
     * @param param
     *            <code>null</code> or must be an
     *            <code>TTCImageReadParam</code> object, otherwise it will be
     *            ignored.
     * @return the desired image.
     * @throws IOException
     *             if an error occurs reading the information from the input
     *             source.
     * @throws IllegalStateException
     *             if the input source has not been set.
     * @see javax.imageio.ImageReader#read(int, javax.imageio.ImageReadParam)
     * @since 4.0
     */
    public abstract BufferedImage read(int imageIndex, ImageReadParam param)
            throws IOException;

    /**
     * Returns if the reader supports thumbnails. Always <code>true</code>.
     * 
     * @return <code>true</code>
     * @since 4.0
     */
    public boolean readerSupportsThumbnails() {
        return true;
    }

    /**
     * Reads the thumbnail of the given image and returns as a
     * <code>BufferedImage</code>.
     * <p>
     * To use this method is equivalent to read the image with resolution-index
     * set to <code>thumbnailIndex</code>.
     * 
     * @param imageIndex
     *            the index of the image to be retrieved.
     * @param thumbnailIndex
     *            the index of the thumbnail to be retrieved.
     * @return the desired thumbnail.
     * @throws IOException
     *             if an error occurs reading the information from the input
     *             source.
     * @throws IllegalStateException
     *             if the input source has not been set.
     * @see javax.imageio.ImageReader#readThumbnail(int, int)
     * @since 4.0
     */
    public abstract BufferedImage readThumbnail(int imageIndex,
            int thumbnailIndex) throws IOException;

    /**
     * Sets the input source to use to the given <code>ImageInputStream</code>.
     * The input source must be set before any of the query or read methods are
     * used. If input is <code>null</code>, any currently set input source
     * will be removed.
     * 
     * @param input
     *            the <code>ImageInputStream</code> for decoding.
     * @param seekForwardOnly
     *            ignored. Always assumes <code>true</code>.
     * @param ignoreMetadata
     *            ignored. Always assumes <code>true</code>.
     * @throws IllegalArgumentException
     *             if <code>input</code> is not <code>null</code> or an
     *             <code>ImageInputStream</code> object.
     * @see javax.imageio.ImageReader#setInput(java.lang.Object, boolean,
     *      boolean)
     * @since 4.0
     */
    public void setInput(Object input, boolean seekForwardOnly,
            boolean ignoreMetadata) {
        super.setInput(input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Returns an Iterator containing possible image types to which the given
     * image may be decoded, in the form of ImageTypeSpecifierss. At least one
     * legal image type will be returned.
     * <p>
     * Currently, two possible image types are supported: 8-bit grayscale, and
     * 24-bit RGB image.
     * 
     * @param imageIndex
     *            the index of the image to be retrieved.
     * @return an Iterator containing at least one ImageTypeSpecifier
     *         representing suggested image types for decoding the current given
     *         image.
     * @throws IOException
     *             if an error occurs reading the information from the input
     *             source.
     * @throws IllegalStateException
     *             if the input source has not been set.
     * @see javax.imageio.ImageReader#getImageTypes(int)
     * @since 4.0
     */
    public abstract Iterator getImageTypes(int imageIndex) throws IOException;

    /**
     * Returns the number of images within the input source. Currently, TTC
     * format only support one image per source. So this will always return 1.
     * 
     * @param allowSearch
     *            this will be ignored.
     * @return number of images within the input source.
     * @throws IOException
     *             if an error occurs reading the information from the input
     *             source.
     * @throws IllegalStateException
     *             if the input source has not been set.
     * @see javax.imageio.ImageReader#getNumImages(boolean)
     * @since 4.0
     */
    public int getNumImages(boolean allowSearch) throws IOException {
        return 1;
    }

    /**
     * Returns the number of thumbnails the given image has. The value will be
     * equal to the DWT-level the image was encoded into.
     * 
     * @param imageIndex
     *            the index of the image to be retrieved.
     * @return number of thumbnails the given image has.
     * @throws IOException
     *             if an error occurs reading the information from the input
     *             source.
     * @throws IllegalStateException
     *             if the input source has not been set.
     * @see javax.imageio.ImageReader#getNumThumbnails(int)
     * @since 4.0
     */
    public abstract int getNumThumbnails(int imageIndex) throws IOException;

    /**
     * Returns a default <code>ImageReadParam</code> object appropriate for
     * TTC format. To gain access to TTC-specific parameters, cast the returned
     * object to <code>TTCImageReadParam</code>. This method may be called
     * before the input source is set.
     * 
     * @return an <code>TTCImageReadParam</code> object be used to control the
     *         decoding process with a set of default settings.
     * @see javax.imageio.ImageReader#getDefaultReadParam()
     * @since 4.0
     */
    public abstract ImageReadParam getDefaultReadParam();

    /**
     * Returns quality-index actually achieved. Return value is undetermined if
     * read before decoding.
     * 
     * @return the quality-index achieved.
     * @since 4.0
     */
    public float getQualityIndexAchieved() {
        logger.warning("Unsupported operation.");
        return 0;
    }

}
