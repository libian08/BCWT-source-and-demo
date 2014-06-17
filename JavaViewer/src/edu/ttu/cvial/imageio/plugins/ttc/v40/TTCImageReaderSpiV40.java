package edu.ttu.cvial.imageio.plugins.ttc.v40;

import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import edu.ttu.cvial.codec.sq.bcwt.BCWTCodecBase.EncodedProperties;

/**
 * The service provider interface (SPI) for TTCImageReaderV40.
 * <p>
 * This class handles registration of TTC image codec to JVM, and will be
 * automatically called by JVM when certain tasks are required. Users should
 * have no need to work with this class or it's instances directly.
 */
public class TTCImageReaderSpiV40 extends ImageReaderSpi {
    static final String vendorName = "CVIAL.TTU";

    static final String version = "4.0";

    static final String readerClassName = "edu.ttu.cvial.imageio.plugins.ttc.v40.TTCImageReaderV40";

    static final String[] names = { "TTC 4.0" };

    static final String[] suffixes = { "ttc" };

    static final String[] MIMETypes = { "image/x-ttc" };

    static final String[] writerSpiNames = { "edu.ttu.cvial.imageio.plugins.ttc.v40.TTCImageReaderSpiV40" };

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

    public TTCImageReaderSpiV40() {
        super(vendorName, version, names, suffixes, MIMETypes, readerClassName,
                STANDARD_INPUT_TYPE, // Accept ImageInputStreams
                writerSpiNames, supportsStandardStreamMetadataFormat,
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

    public String getDescription(Locale locale) {
        return "Texas Tech CVIAL (TTC) image reader";
    }

    public ImageReader createReaderInstance(Object parm1)
            throws java.io.IOException {
        return new TTCImageReaderV40(this);
    }

    public boolean canDecodeInput(Object input) throws java.io.IOException {
        if (!(input instanceof ImageInputStream)) {
            return false;
        }
        ImageInputStream stream = (ImageInputStream) input;

        byte signature[] = EncodedProperties.getSignatureBytes();
        byte b[] = new byte[signature.length];
        try {
            stream.mark();
            stream.readFully(b);
            stream.reset();
        } catch (IOException e) {
            return false;
        }

        boolean value = true;
        for (int i = 0; i < b.length; i++) {
            if (b[i] != signature[i]) {
                value = false;
                break;
            }
        }
        return value;
    }
}
