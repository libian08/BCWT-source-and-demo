package edu.ttu.cvial.imageio.plugins.ttc.v40;
import javax.swing.JOptionPane;

/**
 * This class provides some information on the TTC Image codec. It also acts as 
 * the default main class for displaying the About box.
 */
public class TTCImageInfoV40 {

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(
                null,
                new String[] {
                        "TTC Image Codec v4.0",
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
