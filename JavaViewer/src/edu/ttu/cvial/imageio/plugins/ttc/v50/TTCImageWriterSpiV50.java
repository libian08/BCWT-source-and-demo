package edu.ttu.cvial.imageio.plugins.ttc.v50;

import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;

/**
 * The service provider interface (SPI) for TTCImageWriterV50.
 * <p>
 * This class handles registration of TTC image codec to JVM, and will be
 * automatically called by JVM when certain tasks are required. Users should
 * have no need to work with this class or it's instances directly.
 */
public class TTCImageWriterSpiV50 extends ImageWriterSpi {

    public TTCImageWriterSpiV50() {
        super(
                TTCImageInfoV50.vendorName, 
                TTCImageInfoV50.version, 
                TTCImageInfoV50.names, 
                TTCImageInfoV50.suffixes, 
                TTCImageInfoV50.MIMETypes, 
                TTCImageInfoV50.writerClassName,
                STANDARD_OUTPUT_TYPE, // Write to ImageOutputStreams
                TTCImageInfoV50.readerSpiNames, 
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

    public boolean canEncodeImage(ImageTypeSpecifier imageType) {
        int bands = imageType.getNumBands();
        return bands == 1 || bands == 3;
    }

    public ImageWriter createWriterInstance(Object extension)
            throws IOException {
        return new TTCImageWriterV50(this);
    }

    public String getDescription(Locale locale) {
        return "Texas Tech CVIAL (TTC) image writer";
    }
}
