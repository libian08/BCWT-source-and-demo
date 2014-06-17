import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JOptionPane;

//import y0.imageio.linebased.LSVSReader;
import edu.ttu.cvial.imageio.plugins.ttc.TTCImageReadParam;
import edu.ttu.cvial.imageio.plugins.ttc.TTCImageReader;
import edu.ttu.cvial.imageio.plugins.ttc.TTCImageWriteParam;
import edu.ttu.cvial.imageio.plugins.ttc.v50.TTCImageWriterV50;
import edu.ttu.cvial.util.Stopwatch;


public class TTCImageViewerCore {
    
	private Dimension previewDimension = null;
	private Dimension DisplayDimension = null;
	
	private String decodeType = "";
	private  static String DECODE_TYPE_THUMBNAIL = "thumb";
	private  static String DECODE_TYPE_ROI = "roi";
	
	private Rectangle roiRectangle = null;
	private BufferedImage decodedThumbImage = null;
	private BufferedImage decodedImage = null;
	
	private int fullSizeImageWidth = 0;
	private int fullSizeImageHeight = 0;
	private URL ttcImageURL;
	private String ttcImage;
	
	public static final String SRC_TYPE_SVS = "svs";
	
	public TTCImageViewerCore (String ttcImagePath){
		this.ttcImageURL = toURL(ttcImagePath);
		this.ttcImage = ttcImagePath;
	}
	
	public long decodeThumbnail(Dimension previewSize){
		if (!this.ttcImageURL.equals("")){
			decodeType = DECODE_TYPE_THUMBNAIL ;
			this.previewDimension = previewSize ;
			decodedThumbImage = decodeTTCImage(); 
			return decodeTime;
		}else
			return -1;
	}
	
	public long decodeROIImage(Rectangle ROIRange){
		if (!this.ttcImageURL.equals("")){
			// if ROIRange set as null, the full size image will be decoded.
			decodeType = DECODE_TYPE_ROI ;
			roiRectangle = ROIRange;
			decodedImage = decodeTTCImage();
			return decodeTime;
		}else
			return -1;
	}
	
	private long decodeTime;

    private BufferedImage decodeTTCImage() {

    	ImageInputStream inputStream = null;
    	BufferedImage outputImage = null;
    	
        // Decode the TTC image.
        try {
            // Open the input stream.
            inputStream = ImageIO
                    .createImageInputStream(ttcImageURL.openStream());

            // Get a reader that can decode the input.
            Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(inputStream);
            if (imageReaders.hasNext()) {
            	reader = imageReaders.next();
            } else {
            	throw new UnsupportedOperationException("Java does not recognize the selected image: \n" + ttcImageURL);
            }
            
            // Set the input and get some basic image info.
            reader.setInput(inputStream);
            fullSizeImageWidth = reader.getWidth(0);
            fullSizeImageHeight = reader.getHeight(0);

            ImageReadParam param = reader.getDefaultReadParam();
            
    		if (decodeType.equals(DECODE_TYPE_ROI)) {
				if (roiRectangle != null) {
					if (roiRectangle.width <= 0 && roiRectangle.height <= 0) {
						throw new IllegalStateException("roiRange error");
					}
					param.setSourceRegion(roiRectangle);
	    			if (param.canSetSourceRenderSize()) {
	    				param.setSourceRenderSize(DisplayDimension);
	    			}
				}
				reader.addIIOReadProgressListener(readImageProgressListener);
			} else if (decodeType.equals(DECODE_TYPE_THUMBNAIL)) {
    			if (!param.canSetSourceRenderSize())
    				throw new IllegalStateException("Reader cannot set sourceRenderSize.");
				
    			param.setSourceRenderSize(previewDimension);
    			reader.addIIOReadProgressListener(readThumbProgressListener);
			}

            if (reader instanceof TTCImageReader) {
            	// Set TTC-specific parameters.
                TTCImageReadParam ttcParam = (TTCImageReadParam) param;
				ttcParam.setBestFitSourceRenderSize(true);
            }

            // Decode!
			Stopwatch watchSection = new Stopwatch();
			watchSection.start();
			outputImage = reader.read(0, param);
			watchSection.stop();
			decodeTime = watchSection.getElapsedTime();
			
        } catch (UnsupportedOperationException e) {
        	JOptionPane.showMessageDialog(null, e.getMessage(), "Decoding Error!", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
	        try {
	        	if (inputStream != null)
	        		inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        reader = null;
        }
        
        return outputImage;
    }
    
    public IIOWriteProgressListener writeProgressListener;
	public IIOReadProgressListener readThumbProgressListener;
	public IIOReadProgressListener readImageProgressListener;
	
    private TTCImageWriterV50 writer;
    private ImageReader reader;
    
    public void abortCoding() {
    	if (writer != null)
    		writer.abort();
    	if (reader != null)
    		reader.abort();
    }
    
    public void encode(String encodeName, TTCImageWriteParam ttcParam, String srcType) throws Exception {
    	Hashtable<String, Object> props = new Hashtable<String, Object>();
    	Hashtable<String, Object> param = new Hashtable<String, Object>();
    	Hashtable<String, Object> result = new Hashtable<String, Object>();
    	
		File ttcImageFile = new File(encodeName);
		if (ttcImageFile.exists()) {
			ttcImageFile.delete();
		}

		// Get a proper writer.
		Iterator<ImageWriter> writers = null;
		writers = ImageIO.getImageWritersByFormatName("TTC 5.0");
		writer = (TTCImageWriterV50) writers.next();

		try {
			// Set the output.
			ImageOutputStream ttcOutput = javax.imageio.ImageIO
					.createImageOutputStream(ttcImageFile);
			writer.setOutput(ttcOutput);
			writer.addIIOWriteProgressListener(writeProgressListener);
			
			// Encode!
			Stopwatch watchSection = new Stopwatch();
			watchSection.start();
			
			if (srcType.equalsIgnoreCase(SRC_TYPE_SVS)){
//				LSVSReader srcReader = new LSVSReader(props, param, result);
//				srcReader.setInput(ttcImage);
//				writer.write(null, srcReader, ttcParam);
				
			}else
				writer.write(null, new IIOImage(decodedImage, null, null),
								ttcParam);
			watchSection.stop();

			writer = null;

			ttcOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    private URL toURL(String string) {
        URL url = null;
        try {
            URI uri = new URI(string);
            url = uri.toURL();
        } catch (URISyntaxException e) {
        } catch (IllegalArgumentException e) {
        } catch (MalformedURLException e) {
        }
        if (url == null) {
            try {
                url = new File(string).toURI().toURL(); //.toURL();
            } catch (MalformedURLException e) {
            }
        }

        return url;
    }
    

	public BufferedImage  getDecodedThumbImage() {
		return this.decodedThumbImage;
	}

	public BufferedImage  getDecodedImage() {
		return this.decodedImage;
	}

	public int getFullSizeImageWidth() {
		return fullSizeImageWidth;
	}

	public int getFullSizeImageHeight() {
		return fullSizeImageHeight;
	}

	public Dimension getDisplayDimension() {
		return DisplayDimension;
	}

	public void setDisplayDimension(Dimension displayDimension) {
		DisplayDimension = displayDimension;
	}
}
