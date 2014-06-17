package edu.ttu.cvial.imageio.plugins.ttc.v50;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

import y0.imageio.linebased.ImageOp;
import y0.imageio.linebased.ImageOpGraph;
import y0.imageio.linebased.ImageProp;
import y0.imageio.linebased.RenderedImageSource;
import y0.utils.Task;
import y0.wavelet.bcwt.linebased.LBCWT;
import edu.ttu.cvial.imageio.plugins.ttc.TTCImageWriter;

/**
 * A class for encoding and writing images in TTC format.
 * <p>
 * Instances of this class (and of all <code>ImageWriter</code> classes) are
 * normally created by service provider class.
 */
public class TTCImageWriterV50 extends TTCImageWriter implements Task {

    private LBCWT coder;
    private Hashtable result;
	private long outputPosStart;
	private long outputPosEnd;
	private ImageProp imgProp;
	ImageOpGraph graph;
	
	private ImageOp progressReporter;
    private long progressLastBroadcastTime;
    private long progressBroadcastInterval = 50;
    private boolean isCompleted;

    public TTCImageWriterV50(ImageWriterSpi originatingProvider) {
        super(originatingProvider);
    }

    private void checkOutput() {
        if (getOutput() == null)
            throw new IllegalStateException("Output has not been set.");
    }

    /**
     * {@inheritDoc}
     */
    public int getBitAchieved() {
    	return (int)((outputPosEnd - outputPosStart) * 8);
    }

    /**
     * {@inheritDoc}
     */
    public float getBitrateAchieved() {
    	return (float)getBitAchieved() / (float)(imgProp.width * imgProp.height);
    }
    
    /**
     * {@inheritDoc}
     */
    public float getCompRatioAchieved() {
    	return (float)(imgProp.width * imgProp.height * imgProp.componentNum * 8) / (float)getBitAchieved();
    }

    public ImageWriteParam getDefaultWriteParam() {
        return new TTCImageWriteParamV50();
    }

    /**
     * {@inheritDoc}
     */
    public float getQualityIndexAchieved() {
        return coder.getQualityIndexAchieved();
    }

	public boolean isCompleted() {
		return isCompleted;
	}

	public void run() {
	}
	public void runOnce() {
		if (abortRequested()) {
			graph.abort();
			isCompleted = true;
		}
		if (System.currentTimeMillis() - progressLastBroadcastTime > progressBroadcastInterval) {
			float progress = progressReporter.getProgress();
			processImageProgress(progress);
			progressLastBroadcastTime = System.currentTimeMillis();
			if (progress > 0.98f) {
				isCompleted = true;
			}
		}
	}
	/**
     * {@inheritDoc}
     */
    public void setOutput(Object output) {
		super.setOutput(output);
		
		logger.info(TTCImageInfoV50.fileType + " " + TTCImageInfoV50.version
				+ " used.");
		
	}
	
	/**
     * {@inheritDoc}
     */
    public void write(IIOMetadata streamMetadata, IIOImage image,
            ImageWriteParam param) throws IOException {
        if (image == null)
            throw new IllegalArgumentException("image cannot be null");
        
        RenderedImage img = image.getRenderedImage();
        RenderedImageSource imageSource = new RenderedImageSource(img);
        
        write(streamMetadata, imageSource, param);
    }

	public void write(IIOMetadata streamMetadata, ImageOp imageSource,
            ImageWriteParam param) throws IOException {
        checkOutput();
        
        imgProp = new ImageProp(imageSource.getProperties());
        
        // Write the TTC signature to the output-stream.
        ImageOutputStream output = (ImageOutputStream) getOutput();
        outputPosStart = output.getStreamPosition();
        output.write(TTCImageInfoV50.getSignatureBytes());

        graph = new ImageOpGraph();

        if (param instanceof TTCImageWriteParamV50) {
        	imageSource.setParam(((TTCImageWriteParamV50) param).getParam());
        }
        imageSource.setResult(result);
        graph.add(imageSource);
        
        coder = new LBCWT();
        graph.add(coder);
		coder.setOutput(output);
		
		progressReporter = imageSource;
		isCompleted = false;
		graph.addTask(this);

		clearAbortRequest();
		processImageStarted(0);
		progressLastBroadcastTime = System.currentTimeMillis();

		graph.run();
		
        outputPosEnd = output.getStreamPosition();

        if (abortRequested()) {
			processWriteAborted();
        } else {
        	processImageComplete();
        }

    }

}
