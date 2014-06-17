package edu.ttu.cvial.imageio.plugins.ttc.v41;

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
 * A class for encoding and writing images in TTC format.
 * <p>
 * Instances of this class (and of all <code>ImageWriter</code> classes) are
 * normally created by service provider class.
 */
public class TTCImageWriterV41 extends TTCImageWriter {

    private BCWTEncoder coder = new BCWTEncoder();

    public TTCImageWriterV41(ImageWriterSpi originatingProvider) {
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
        logger.info(TTCImageInfoV41.fileType + " " + TTCImageInfoV41.version + " used.");
        if (output != null) {
            if (!(output instanceof ImageOutputStream)) {
                throw new IllegalArgumentException(
                        "output is not an ImageOutputStream!");
            }
            coder.setOutput((ImageOutputStream) output);
        } else {
            coder.setOutput(null);
        }
        super.setOutput(output);
    }

    /**
     * {@inheritDoc}
     */
    public void write(IIOMetadata streamMetadata, IIOImage image,
            ImageWriteParam param) throws IOException {
        if (image == null)
            throw new IllegalArgumentException("image cannot be null");

        checkOutput();
        
        // Write the TTC signature to the output-stream.
        ImageOutputStream output = (ImageOutputStream) getOutput();
        output.write(TTCImageInfoV41.getSignatureBytes());
        
        if (param instanceof TTCImageWriteParamV41) {
            coder.setParam(((TTCImageWriteParamV41) param).getParam());
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
        return new TTCImageWriteParamV41();
    }

}
