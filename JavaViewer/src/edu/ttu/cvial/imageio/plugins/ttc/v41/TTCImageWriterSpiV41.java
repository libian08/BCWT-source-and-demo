package edu.ttu.cvial.imageio.plugins.ttc.v41;

import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;

/**
 * The service provider interface (SPI) for TTCImageWriterV41.
 * <p>
 * This class handles registration of TTC image codec to JVM, and will be
 * automatically called by JVM when certain tasks are required. Users should
 * have no need to work with this class or it's instances directly.
 */
public class TTCImageWriterSpiV41 extends ImageWriterSpi {

    public TTCImageWriterSpiV41() {
        super(
                TTCImageInfoV41.vendorName, 
                TTCImageInfoV41.version, 
                TTCImageInfoV41.names, 
                TTCImageInfoV41.suffixes, 
                TTCImageInfoV41.MIMETypes, 
                TTCImageInfoV41.writerClassName,
                STANDARD_OUTPUT_TYPE, // Write to ImageOutputStreams
                TTCImageInfoV41.readerSpiNames, 
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

    public boolean canEncodeImage(ImageTypeSpecifier imageType) {
        int bands = imageType.getNumBands();
        return bands == 1 || bands == 3;
    }

    public ImageWriter createWriterInstance(Object extension)
            throws IOException {
        return new TTCImageWriterV41(this);
    }

    public String getDescription(Locale locale) {
        return "Texas Tech CVIAL (TTC) image writer";
    }
}
