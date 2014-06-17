package edu.ttu.cvial.imageio.plugins.ttc.v50;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import y0.imageio.linebased.BufferedImageSink;
import y0.imageio.linebased.ImageOp;
import y0.imageio.linebased.ImageOpGraph;
import y0.imageio.linebased.ImageProp;
import y0.utils.Task;
import y0.wavelet.DWTProp;
import y0.wavelet.bcwt.linebased.ILBCWT;
import edu.ttu.cvial.imageio.plugins.ttc.TTCImageReadParam;
import edu.ttu.cvial.imageio.plugins.ttc.TTCImageReader;

/**
 * A class for reading and decoding images in TTC 5.0 format.
 * <p>
 * Generally, users should control instances of this class via the superclass,
 * {@link edu.ttu.cvial.imageio.plugins.ttc.TTCImageReader}. In this way,
 * different versions of TTC readers can be controlled via the same interface.
 * 
 * @see edu.ttu.cvial.imageio.plugins.ttc.TTCImageReader
 */
/**
 * @author gjl
 *
 */
public class TTCImageReaderV50 extends TTCImageReader implements Task {
	private ImageOpGraph graph;
    private ILBCWT coder;
    private Hashtable props;
    private Hashtable result;

	private ImageOp progressReporter;
    private long progressLastBroadcastTime;
    private long progressBroadcastInterval = 10;
    private boolean isCompleted;

    TTCImageV50Metadata metadata = null;

    public TTCImageReaderV50(ImageReaderSpi originatingProvider) {
        super(originatingProvider);
        reset();
    }

    private void checkInput() {
        if (getInput() == null)
            throw new IllegalStateException("Input has not been set.");
    }

    private void checkThumbnailIndex(int imageIndex, int thumbnailIndex)
            throws IOException {
        if (thumbnailIndex > getNumThumbnails(imageIndex)
                || thumbnailIndex < 0)
            throw new IndexOutOfBoundsException();
    }

    private Dimension getBestFitSourceRenderSize(int imageIndex, ImageReadParam param) throws IOException {
    	Dimension size = param.getSourceRenderSize();
    	Rectangle sourceRegion = getSourceRegion(imageIndex, param);
    	float renderAspect = (float) size.width / size.height;
    	float regionAspect = (float) sourceRegion.width / sourceRegion.height;
    	
    	Dimension bestFit = null;
    	if (renderAspect > regionAspect) {
    		bestFit = new Dimension((int) (size.height * regionAspect), size.height);
    	} else {
    		bestFit = new Dimension(size.width, (int) (size.width / regionAspect));
    	}
    	
    	return bestFit;
    }

//    /**
//     * {@inheritDoc}
//     */
//    public float getQualityIndexAchieved() {
//        return coder.getQualityIndexAchieved();
//    }

    /**
     * Find the best resolution index based on sourceRenderSize and sourceRegion.
     * @param imageIndex
     * @param param
     * @return
     * @throws IOException
     */
    private int getBestResolutionIndex(int imageIndex, ImageReadParam param)
			throws IOException {
		int numOfLevel = getNumThumbnails(imageIndex);
		int bestRI = numOfLevel;
		Dimension sourceRenderSize = param.getSourceRenderSize();
		Rectangle sourceRegion = getSourceRegion(imageIndex, param);
		
		if (sourceRenderSize != null) {
			for (int level = numOfLevel; level >= 0; level--) {
				if ((sourceRegion.width >> level) >= sourceRenderSize.width
						|| (sourceRegion.height >> level) >= sourceRenderSize.height) {
					bestRI = numOfLevel - level;
					break;
				}
			}
		}

		return bestRI;
	}

    private Rectangle getBestSourceRegion(int imageIndex, TTCImageReadParamV50 param, Point origin) throws IOException {
    	Rectangle sourceRegion = new Rectangle();
    	
    	Dimension sourceRenderSize = param.getSourceRenderSize();
    	int resolutionIndex = param.getResolutionIndex();
    	int numOfLevel = getNumThumbnails(imageIndex);
    	int imageWidth = getWidth(imageIndex);
    	int imageHeight = getHeight(imageIndex);
    	
    	sourceRegion.x = origin.x;
    	sourceRegion.y = origin.y;
    	sourceRegion.width = sourceRenderSize.width << (numOfLevel - resolutionIndex);
    	sourceRegion.height = sourceRenderSize.height << (numOfLevel - resolutionIndex);

    	if (sourceRegion.x + sourceRegion.width > imageWidth) {
    		sourceRegion.width = imageWidth - sourceRegion.x;
    	}
    	if (sourceRegion.y + sourceRegion.height > imageHeight) {
    		sourceRegion.height = imageHeight - sourceRegion.y;
    	}
    	
    	return sourceRegion;
    }

    /**
     * {@inheritDoc}
     */
    public ImageReadParam getDefaultReadParam() {
        return new TTCImageReadParamV50();
    }

    /**
     * {@inheritDoc}
     */
    public int getHeight(int imageIndex) throws IOException {
        readHeader();
        return ImageProp.getHeightReal(props);
    }

    /**
     * Returns an IIOMetadata object containing TTC v5.0 metadata associated with the given image
     * 
     * @return an IIOMetadata object
     * @since 5.0
     */
    public IIOMetadata getImageMetadata(int imageIndex)
		throws IIOException {
		if (imageIndex != 0) {
			throw new IndexOutOfBoundsException("imageIndex != 0!");
		}
		readMetadata();
		return metadata;
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
        switch (ImageProp.getComponentNum(props)) {
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
    public int getNumImages(boolean allowSearch) throws IOException {
        readHeader();
        return 1;
    }
    
    /**
     * {@inheritDoc}
     */
    public int getNumThumbnails(int imageIndex) throws IOException {
        readHeader();
        return DWTProp.getDwtLevelNum(props);
    }
    
    private Rectangle getSourceRegion(int imageIndex, ImageReadParam param) throws IOException {
		Rectangle sourceRegion = param.getSourceRegion();
		if (sourceRegion == null) {
			sourceRegion = new Rectangle(0, 0, getWidth(imageIndex),
					getHeight(imageIndex));
		}
		return sourceRegion;
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
        return ImageProp.getWidthReal(props);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasThumbnails(int imageIndex) throws IOException {
        return (getNumThumbnails(imageIndex) > 0);
    }

    
    /**
     * Internal use only.
     */
    public boolean isCompleted() {
		return isCompleted;
	}

    /**
     * {@inheritDoc}
     */
    public BufferedImage read(int imageIndex) throws IOException {
        return read(imageIndex, null);
    }

    /**
     * {@inheritDoc}
     */
    public BufferedImage read(int imageIndex, ImageReadParam param)
            throws IOException {
   	
    	TTCImageReadParamV50 ttcParam = null;
        if (param instanceof TTCImageReadParam) {
        	ttcParam = (TTCImageReadParamV50) param;
        } else {
            ttcParam = new TTCImageReadParamV50();
        }
        
        Dimension sourceRenderSize = param.getSourceRenderSize();
		if (sourceRenderSize != null) {
			if (ttcParam.isBestFitSourceRegion) {
				// resolutionIndex, sourceRenderSize -> sourceRegion
				if (ttcParam.getResolutionIndex() < 0)
					ttcParam.setResolutionIndex(getNumThumbnails(imageIndex));
				Rectangle sourceRegion = getBestSourceRegion(imageIndex,
						ttcParam, getSourceRegion(imageIndex, ttcParam)
								.getLocation());
				param.setSourceRegion(sourceRegion);
				logger.info("Automatically setting sourceRegion to " + sourceRegion.toString());
				
				// sourceRegion may be clipped at the image boundary, thus change the sourceRenderSize to accordingly.
				int scalingFactor = getNumThumbnails(imageIndex) - ttcParam.getResolutionIndex();
				sourceRenderSize.width = sourceRegion.width >> scalingFactor;
				sourceRenderSize.height = sourceRegion.height >> scalingFactor;
			} else {
				if (ttcParam.isBestFitSourceRenderSize()) {
					// Find the best sourceRenderSize that keep the aspect
					// ratio.
					// sourceRegion, sourceRenderSize -> sourceRenderSize
					sourceRenderSize = getBestFitSourceRenderSize(imageIndex,
							param);
					param.setSourceRenderSize(sourceRenderSize);
				}

				// Find the best resolution index so that decoded image has
				// slightly higher or equal resolution to the sourceRenderSize.
				// sourceRenderSize, sourceRegion -> resolutionIndex
				int bestResolutionIndex = getBestResolutionIndex(imageIndex,
						param);
				ttcParam.setResolutionIndex(bestResolutionIndex);
				logger.info("Automatically setting resolution index to "
						+ bestResolutionIndex
						+ " to fit in the sourceRenderSize.");
			}

		}
		
        coder.setParam(ttcParam.getParam());
        result = new Hashtable();
        coder.setResult(result);

    	BufferedImage image = param.getDestination();
    	// If the sourceRenderSize is not supplied, directly decode into the supplied image object.
    	// Otherwise, BufferedImageSink(null) will automatically create a new image object.
    	BufferedImage decodedImage = sourceRenderSize == null ? image : null;
    	BufferedImageSink imageSink = new BufferedImageSink(decodedImage);
    	graph.add(imageSink);
    	
		progressReporter = imageSink;
		isCompleted = false;
		graph.addTask(this);

		processImageStarted(0);
		progressLastBroadcastTime = System.currentTimeMillis();

    	graph.run();
    	
    	decodedImage = imageSink.getImage();
    	if (sourceRenderSize != null) {
    		decodedImage = scaleImage(decodedImage, image, sourceRenderSize);
    	}
    	
        processImageComplete();

        return decodedImage;
    }

    /**
     * {@inheritDoc}
     */
    public boolean readerSupportsThumbnails() {
        return true;
    }

	private void readHeader() throws IIOException {
        checkInput();
        try {
			coder.readHeader();
		} catch (IOException e) {
			throw new IIOException("Error reading header", e);
		}
    }

	private void readMetadata() throws IIOException {
		if (metadata != null) {
			return;
		}
		this.metadata = new TTCImageV50Metadata();
		readHeader();
		
		Enumeration keys = props.keys();
		while (keys.hasMoreElements()) {
			String keyword = (String) keys.nextElement();
			String value = props.get(keyword).toString();

			metadata.keywords.add(keyword);
			metadata.values.add(value);
		}
	}

	/**
     * {@inheritDoc}
     */
    public BufferedImage readThumbnail(int imageIndex, int thumbnailIndex)
            throws IOException {
        checkThumbnailIndex(imageIndex, thumbnailIndex);

        TTCImageReadParamV50 ttcParam = new TTCImageReadParamV50();
        ttcParam.setResolutionIndex(thumbnailIndex);
        coder.setParam(ttcParam.getParam());
        result = new Hashtable();
        coder.setResult(result);

    	BufferedImageSink imageSink = new BufferedImageSink();
    	graph.add(imageSink);
    	
    	graph.run();

        return imageSink.getImage();
    }

	/**
     * Restores the <code>TTCImageReader</code> to its initial state.
     * 
     * @see javax.imageio.ImageReader#reset()
     */
    public void reset() {
        
        props = new Hashtable();
        result = null;
        
        graph = new ImageOpGraph(props, null, null);
        graph.setActionStartPoint(ImageOpGraph.ACTION_FROM_LAST);
        
        coder = new ILBCWT();
        graph.add(coder);
        
        // super.reset() should be called last.
        super.reset();
    }
	
    /**
     * Internal use only.
     */
	public void run() {
	}

    /**
     * Internal use only.
     */
	public void runOnce() {
		if (System.currentTimeMillis() - progressLastBroadcastTime > progressBroadcastInterval) {
			float progress = progressReporter.getProgress();
			processImageProgress(progress);
			progressLastBroadcastTime = System.currentTimeMillis();
			if (progress > 0.98f) {
				isCompleted = true;
			}
		}
	}

	private BufferedImage scaleImage(BufferedImage src, BufferedImage dest, Dimension sourceRenderSize) {
		// Scale the image if sourceRenderSize is set.
		if (dest == null)
			dest = new BufferedImage(sourceRenderSize.width, sourceRenderSize.height, src.getType());
		Graphics2D destGraphics2D = dest.createGraphics();
		destGraphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			      RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		destGraphics2D.drawImage(src, 0, 0, sourceRenderSize.width, sourceRenderSize.height, null);
		return dest;
	}
	
	/**
     * {@inheritDoc}
     */
    public void setInput(Object input, boolean seekForwardOnly,
            boolean ignoreMetadata) {
        super.setInput(input, true, true);
        logger.info(TTCImageInfoV50.fileType + " " + TTCImageInfoV50.version + " used.");
        
        try {
        if (input == null) {
        	coder.setInput(null);
        } else if (input instanceof ImageInputStream) {
            ImageInputStream stream = (ImageInputStream) input;
            stream.mark();
            if (!TTCImageInfoV50.foundSignature(input)) {
                stream.reset();
                throw new IllegalArgumentException("Can not find format signature in the input.");
            }
            coder.setInput((ImageInputStream) input);
        } else {
          throw new IllegalArgumentException("Input must be null or an instance of ImageInputStream.");
        }
        } catch (IOException e) {
        	throw new IllegalArgumentException("Set input error", e);
        }
    }


}
