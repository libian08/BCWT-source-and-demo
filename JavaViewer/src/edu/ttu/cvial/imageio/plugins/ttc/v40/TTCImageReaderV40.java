package edu.ttu.cvial.imageio.plugins.ttc.v40;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import edu.ttu.cvial.codec.Codec;
import edu.ttu.cvial.codec.Codec.CodecEventListener;
import edu.ttu.cvial.codec.sq.bcwt.BCWTDecoder;
import edu.ttu.cvial.codec.sq.bcwt.BCWTCodecBase.EncodedProperties;
import edu.ttu.cvial.codec.sq.bcwt.BCWTDecoder.Param;
import edu.ttu.cvial.imageio.plugins.ttc.TTCImageReader;

/**
 * A class for reading and decoding images in TTC 4.0 format.
 * <p>
 * Generally, users should control instances of this class via the superclass,
 * {@link edu.ttu.cvial.imageio.plugins.ttc.TTCImageReader}. In this way,
 * different versions of TTC readers can be controlled via the same interface.
 * 
 * @see edu.ttu.cvial.imageio.plugins.ttc.TTCImageReader
 */
public class TTCImageReaderV40 extends TTCImageReader {
    private BCWTDecoder coder = new BCWTDecoder();
    private EncodedProperties inputProperties;

    private int imageIndexCurrent;

    public TTCImageReaderV40(ImageReaderSpi originatingProvider) {
        super(originatingProvider);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getBitAchieved() {
        return coder.getBitAchieved();
    }

    /**
     * {@inheritDoc}
     */
    public float getBitrateAchieved() {
        return coder.getBitrateAchieved();
    }

    /**
     * {@inheritDoc}
     */
    public float getCompRatioAchieved() {
        return coder.getCompRatioAchieved();
    }

    /**
     * {@inheritDoc}
     */
    public ImageReadParam getDefaultReadParam() {
        return new TTCImageReadParamV40();
    }

    /**
     * {@inheritDoc}
     */
    public int getHeight(int imageIndex) throws IOException {
        readHeader();
        return inputProperties.getRowsReal();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator getImageTypes(int imageIndex)
            throws IOException {
        ImageTypeSpecifier imageType = null;
        int datatype = DataBuffer.TYPE_BYTE;
        java.util.List typeList = new ArrayList();

        readHeader();
        switch (inputProperties.getComponents()) {
        case 1:
            imageType = ImageTypeSpecifier.createGrayscale(8, datatype, false);
            break;
        case 3:
            imageType = ImageTypeSpecifier.createBanded(ColorSpace
                    .getInstance(ColorSpace.CS_sRGB), new int[] { 0, 1, 2 },
                    new int[] { 0, 0, 0 }, datatype, false, false);
            break;
        }

        typeList.add(imageType);
        return typeList.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public int getNumThumbnails(int imageIndex) throws IOException {
        readHeader();
        return inputProperties.getDwtLevel();
    }

    /**
     * {@inheritDoc}
     */
    public float getQualityIndexAchieved() {
        return coder.getQualityIndexAchieved();
    }

    /**
     * {@inheritDoc}
     */
    public int getThumbnailHeight(int imageIndex, int thumbnailIndex)
            throws IOException {
        checkThumbnailIndex(imageIndex, thumbnailIndex);
        int numOfThumbs = getNumThumbnails(imageIndex);
        return (getHeight(imageIndex) >> (numOfThumbs - thumbnailIndex));
    }

    /**
     * {@inheritDoc}
     */
    public int getThumbnailWidth(int imageIndex, int thumbnailIndex)
            throws IOException {
        checkThumbnailIndex(imageIndex, thumbnailIndex);
        int numOfThumbs = getNumThumbnails(imageIndex);
        return (getWidth(imageIndex) >> (numOfThumbs - thumbnailIndex));
    }

    /**
     * {@inheritDoc}
     */
    public int getWidth(int imageIndex) throws IOException {
        readHeader();
        return inputProperties.getColsReal();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasThumbnails(int imageIndex) throws IOException {
        return (getNumThumbnails(imageIndex) > 0);
    }

    /**
     * {@inheritDoc}
     */
    public BufferedImage read(int imageIndex, ImageReadParam param)
            throws IOException {
        if (param instanceof TTCImageReadParamV40) {
            coder.setParam(((TTCImageReadParamV40)param).getParam());
        } else {
            coder.setParam(new Param());
        }

        imageIndexCurrent = imageIndex;
        coder.setEventListener(new CodecEventListener() {

            public void codingComplete(Codec source) {
                processImageComplete();
            }

            public void codingProgress(Codec source, float percentageDone) {
                processImageProgress(percentageDone);
            }

            public void codingStarted(Codec source) {
                processImageStarted(imageIndexCurrent);
            }
        });
        
        coder.start();
        
        coder.setEventListener(null);
        BufferedImage image = coder.getOutput();

        return image;
    }

    /**
     * {@inheritDoc}
     */
    public BufferedImage readThumbnail(int imageIndex, int thumbnailIndex)
            throws IOException {
        checkThumbnailIndex(imageIndex, thumbnailIndex);

        Param param = new Param();
        param.setResolutionIndex(thumbnailIndex);
        coder.setParam(param);
        coder.start();
        BufferedImage image = coder.getOutput();
        
        return image;
    }

    /**
     * Restores the reader object to its initial state.
     * 
     * @see javax.imageio.ImageReader#reset()
     */
    public void reset() {
        super.reset();
        coder = new BCWTDecoder();
        inputProperties = null;
    }

    /**
     * {@inheritDoc}
     */
    public void setInput(Object input, boolean seekForwardOnly,
            boolean ignoreMetadata) {
        logger.info("TTC 4.0 used.");
        super.setInput(input, seekForwardOnly, ignoreMetadata);
        if (input == null) {
            coder.setInput(null);
        } else if (input instanceof ImageInputStream) {
            coder.setInput((ImageInputStream) input);
        } else {
            throw new IllegalArgumentException("bad input");
        }
    }

    private void checkInput() {
        if (getInput() == null)
            throw new IllegalStateException("Input has not been set.");
    }

    private void checkThumbnailIndex(int imageIndex, int thumbnailIndex)
            throws IOException {
        if (thumbnailIndex >= getNumThumbnails(imageIndex)
                || thumbnailIndex < 0)
            throw new IndexOutOfBoundsException();
    }

    private void readHeader() throws IOException {
        checkInput();
        coder.readHeader();
        inputProperties = coder.getInputProperties();
    }

}
