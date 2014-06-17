package edu.ttu.cvial.imageio.plugins.ttc.v40;

import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

import edu.ttu.cvial.codec.Codec;
import edu.ttu.cvial.codec.Codec.CodecEventListener;
import edu.ttu.cvial.codec.sq.bcwt.BCWTEncoder;
import edu.ttu.cvial.imageio.plugins.ttc.TTCImageWriter;

/**
 * A class for encoding images in TTC 4.0 format.
 * <p>
 * Generally, users should control instances of this class via the superclass,
 * {@link edu.ttu.cvial.imageio.plugins.ttc.TTCImageWriter}. In this way,
 * different versions of TTC readers can be controlled via the same interface.
 * 
 * @see edu.ttu.cvial.imageio.plugins.ttc.TTCImageWriter
 */
public class TTCImageWriterV40 extends TTCImageWriter {

    private BCWTEncoder coder = new BCWTEncoder();

    public TTCImageWriterV40(ImageWriterSpi originatingProvider) {
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
    public float getQualityIndexAchieved() {
        return coder.getQualityIndexAchieved();
    }

    /**
     * {@inheritDoc}
     */
    public void setOutput(Object output) {
        logger.info("TTC 4.0 used.");
        super.setOutput(output);
        if (output != null) {
            if (!(output instanceof ImageOutputStream)) {
                throw new IllegalArgumentException(
                        "output is not an ImageOutputStream!");
            }
            coder.setOutput((ImageOutputStream) output);
        } else {
            coder.setOutput(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void write(IIOMetadata streamMetadata, IIOImage image,
            ImageWriteParam param) throws IOException {
        if (image == null)
            throw new IllegalArgumentException("image cannot be null");

        checkOutput();
        if (param instanceof TTCImageWriteParamV40) {
            coder.setParam(((TTCImageWriteParamV40) param).getParam());
        }

        coder.setEventListener(new CodecEventListener() {

            public void codingComplete(Codec source) {
                processImageComplete();
            }

            public void codingProgress(Codec source, float percentageDone) {
                processImageProgress(percentageDone);
            }

            public void codingStarted(Codec source) {
                processImageStarted(0);
            }
        });
        coder.setInput(image.getRenderedImage());
        coder.start();
        coder.setEventListener(null);
    }

    private void checkOutput() {
        if (getOutput() == null)
            throw new IllegalStateException("Output has not been set.");
    }

    public ImageWriteParam getDefaultWriteParam() {
        return new TTCImageWriteParamV40();
    }
}
