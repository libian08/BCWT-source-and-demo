package edu.ttu.cvial.imageio.plugins.ttc.v41;

import java.util.Locale;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

/**
 * The service provider interface (SPI) for TTCImageReaderV41.
 * <p>
 * This class handles registration of TTC image codec to JVM, and will be
 * automatically called by JVM when certain tasks are required. Users should
 * have no need to work with this class or it's instances directly.
 */
public class TTCImageReaderSpiV41 extends ImageReaderSpi {

    public TTCImageReaderSpiV41() {
        super(
                TTCImageInfoV41.vendorName, 
                TTCImageInfoV41.version, 
                TTCImageInfoV41.names, 
                TTCImageInfoV41.suffixes, 
                TTCImageInfoV41.MIMETypes, 
                TTCImageInfoV41.readerClassName,
                STANDARD_INPUT_TYPE, // Accept ImageInputStreams
                TTCImageInfoV41.writerSpiNames, 
                TTCImageInfoV41.supportsStandardStreamMetadataFormat,
                TTCImageInfoV41.nativeStreamMetadataFormatName,
                TTCImageInfoV41.nativeStreamMetadataFormatClassName,
                TTCImageInfoV41.extraStreamMetadataFormatNames,
                TTCImageInfoV41.extraStreamMetadataFormatClassNames,
                TTCImageInfoV41.supportsStandardImageMetadataFormat,
                TTCImageInfoV41.nativeImageMetadataFormatName,
                TTCImageInfoV41.nativeImageMetadataFormatClassName,
                TTCImageInfoV41.extraImageMetadataFormatNames,
                TTCImageInfoV41.extraImageMetadataFormatClassNames
        );
    }

    public String getDescription(Locale locale) {
        return "Texas Tech CVIAL (TTC) image reader";
    }

    public ImageReader createReaderInstance(Object parm1)
            throws java.io.IOException {
        return new TTCImageReaderV41(this);
    }

    public boolean canDecodeInput(Object input) throws java.io.IOException {
        if (!(input instanceof ImageInputStream)) {
            return false;
        }
        
        ImageInputStream stream = (ImageInputStream) input;
        stream.mark();
        boolean value = TTCImageInfoV41.foundSignature(stream);
        stream.reset();
        
        return value;
    }
}
