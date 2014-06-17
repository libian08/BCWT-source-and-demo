/*
 * Created on Nov 20, 2004
 *
 */
package edu.ttu.cvial.imageio.plugins.ttc.v40;

import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;

/**
 * The service provider interface (SPI) for TTCImageWriterV40.
 * <p>
 * This class handles registration of TTC image codec to JVM, and will be
 * automatically called by JVM when certain tasks are required. Users should
 * have no need to work with this class or it's instances directly.
 */
public class TTCImageWriterSpiV40 extends ImageWriterSpi {

    static final String vendorName = "CVIAL.TTU";

    static final String version = "4.0";

    static final String writerClassName = "edu.ttu.cvial.imageio.plugins.ttc.v40.TTCImageWriterV40";

    static final String[] names = { "TTC 4.0" };

    static final String[] suffixes = { "ttc" };

    static final String[] MIMETypes = { "image/x-ttc" };

    static final String[] readerSpiNames = { "edu.ttu.cvial.imageio.plugins.ttc.v40.TTCImageReaderSpiV40" };

    static final boolean supportsStandardStreamMetadataFormat = false;

    static final String nativeStreamMetadataFormatName = null;

    static final String nativeStreamMetadataFormatClassName = null;

    static final String[] extraStreamMetadataFormatNames = null;

    static final String[] extraStreamMetadataFormatClassNames = null;

    static final boolean supportsStandardImageMetadataFormat = false;

    static final String nativeImageMetadataFormatName = null;

    static final String nativeImageMetadataFormatClassName = null;

    static final String[] extraImageMetadataFormatNames = null;

    static final String[] extraImageMetadataFormatClassNames = null;

    public TTCImageWriterSpiV40() {
        super(vendorName, version, names, suffixes, MIMETypes, writerClassName,
                STANDARD_OUTPUT_TYPE, // Write to ImageOutputStreams
                readerSpiNames, supportsStandardStreamMetadataFormat,
                nativeStreamMetadataFormatName,
                nativeStreamMetadataFormatClassName,
                extraStreamMetadataFormatNames,
                extraStreamMetadataFormatClassNames,
                supportsStandardImageMetadataFormat,
                nativeImageMetadataFormatName,
                nativeImageMetadataFormatClassName,
                extraImageMetadataFormatNames,
                extraImageMetadataFormatClassNames);
    }

    public boolean canEncodeImage(ImageTypeSpecifier imageType) {
        int bands = imageType.getNumBands();
        return bands == 1 || bands == 3;
    }

    public ImageWriter createWriterInstance(Object extension)
            throws IOException {
        return new TTCImageWriterV40(this);
    }

    public String getDescription(Locale locale) {
        return "Texas Tech CVIAL (TTC) image writer";
    }
}
