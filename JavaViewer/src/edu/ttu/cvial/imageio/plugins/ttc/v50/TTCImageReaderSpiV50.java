package edu.ttu.cvial.imageio.plugins.ttc.v50;

import java.util.Locale;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

/**
 * The service provider interface (SPI) for TTCImageReaderV50.
 * <p>
 * This class handles registration of TTC image codec to JVM, and will be
 * automatically called by JVM when certain tasks are required. Users should
 * have no need to work with this class or it's instances directly.
 */
public class TTCImageReaderSpiV50 extends ImageReaderSpi {

    public TTCImageReaderSpiV50() {
        super(
                TTCImageInfoV50.vendorName, 
                TTCImageInfoV50.version, 
                TTCImageInfoV50.names, 
                TTCImageInfoV50.suffixes, 
                TTCImageInfoV50.MIMETypes, 
                TTCImageInfoV50.readerClassName,
                STANDARD_INPUT_TYPE, // Accept ImageInputStreams
                TTCImageInfoV50.writerSpiNames, 
                TTCImageInfoV50.supportsStandardStreamMetadataFormat,
                TTCImageInfoV50.nativeStreamMetadataFormatName,
                TTCImageInfoV50.nativeStreamMetadataFormatClassName,
                TTCImageInfoV50.extraStreamMetadataFormatNames,
                TTCImageInfoV50.extraStreamMetadataFormatClassNames,
                TTCImageInfoV50.supportsStandardImageMetadataFormat,
                TTCImageInfoV50.nativeImageMetadataFormatName,
                TTCImageInfoV50.nativeImageMetadataFormatClassName,
                TTCImageInfoV50.extraImageMetadataFormatNames,
                TTCImageInfoV50.extraImageMetadataFormatClassNames
        );
    }

    public String getDescription(Locale locale) {
        return "Texas Tech CVIAL (TTC) image reader";
    }

    public ImageReader createReaderInstance(Object parm1)
            throws java.io.IOException {
        return new TTCImageReaderV50(this);
    }

    public boolean canDecodeInput(Object input) throws java.io.IOException {
        if (!(input instanceof ImageInputStream)) {
            return false;
        }
        
        ImageInputStream stream = (ImageInputStream) input;
        stream.mark();
        boolean value = TTCImageInfoV50.foundSignature(stream);
        stream.reset();
        
        return value;
    }
}
