package edu.ttu.cvial.imageio.plugins.ttc.v50;
import java.io.DataInput;
import java.io.IOException;

import javax.swing.JOptionPane;

/**
 * This class provides some information on the TTC Image codec. It also acts as 
 * the default main class for displaying the About box.
 */
public class TTCImageInfoV50 {
    
    public static String fileType = "TTC";

    public static String version = "5.0";

    public static final String classSuffix = "V50";

    public static final String vendorName = "CVIAL.TTU";
    
    public static final String[] suffixes = { "ttc" };

    public static final String[] MIMETypes = { "image/x-ttc" };

    public static final String packageName = TTCImageInfoV50.class.getPackage().getName();
    
    public static final String writerClassName = packageName + ".TTCImageWriter" + classSuffix;
    
    public static final String readerClassName = packageName + ".TTCImageReader" + classSuffix;

    public static final String[] readerSpiNames = { packageName + ".TTCImageReaderSpi" + classSuffix };

    public static final String[] writerSpiNames = { packageName + ".TTCImageWriterSpi" + classSuffix};
    
    public static final String[] names = { fileType + " " + version };

    public static final boolean supportsStandardStreamMetadataFormat = false;

    public static final String nativeStreamMetadataFormatName = null;

    public static final String nativeStreamMetadataFormatClassName = null;

    public static final String[] extraStreamMetadataFormatNames = null;

    public static final String[] extraStreamMetadataFormatClassNames = null;

    public static final boolean supportsStandardImageMetadataFormat = false;

    public static final String nativeImageMetadataFormatName = null;

    public static final String nativeImageMetadataFormatClassName = null;

    public static final String[] extraImageMetadataFormatNames = null;

    public static final String[] extraImageMetadataFormatClassNames = null;

    public static byte[] getSignatureBytes() {
        String signature = fileType + "\n" + version + "\n";
        return signature.getBytes();
    }

    /**
     * Tries to find the TTC signature in the input. </p>
     * Caution: The file pointer is moved forward to the length of the expected
     * signature. It is the caller's duty to mark and reset the file pointer if
     * needed.
     * 
     * @param input the input stream to find the signature.
     * @return <code>true</code> if the signature has been found. </p>
     * <code>false</code> if no signature or signature mismatched.
     */
    public static boolean foundSignature(Object input) {
        if (!(input instanceof DataInput)) {
            return false;
        }
        DataInput stream = (DataInput) input;

        byte signature[] = TTCImageInfoV50.getSignatureBytes();
        byte b[] = new byte[signature.length];
        try {
            stream.readFully(b);
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

    /**
     * Displays the About box.
     */
    public static void main(String[] args) {
        JOptionPane.showMessageDialog(
                null,
                new String[] {
                        fileType + " Image Codec " + version,
                        " ",
                        "Developed by",
                        "Jiangling Guo",
                        "Computer Vision and Image Analysis Lab",
                        "Texas Tech University",
                        " ",
                        "Quick usage :", 
                        "1. Include this JAR file in CLASSPATH",
                        "2. Use ImageI/O classes (in javax.imageio.*) to read/write .ttc images",
                        "Examples :",
                        "BufferedImage myImage = javax.imageio.ImageIO.read(\"myImage.ttc\");",
                        "javax.imageio.ImageIO.write(myImage,\"ttc\", new File(\"myImage.ttc\"));"
                }, 
                "About",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

}
