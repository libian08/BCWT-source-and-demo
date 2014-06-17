package edu.ttu.cvial.codec.sq.bcwt;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Properties;
import java.util.logging.Logger;

import javax.imageio.stream.ImageOutputStream;

import edu.ttu.cvial.arrayop.ArrayOp;
import edu.ttu.cvial.colorspace.ColorSpaceYCbCrICT;
import edu.ttu.cvial.colorspace.ColorSpaceYCoCg;
import edu.ttu.cvial.imageio.ImageCoeff;
import edu.ttu.cvial.imageio.ImageCoeffFloat;
import edu.ttu.cvial.imageio.ImageCoeffShort;
import edu.ttu.cvial.imageio.ImageIO;
import edu.ttu.cvial.util.Stopwatch;
import edu.ttu.cvial.wavelet.lifting.DWT53FastShort;
import edu.ttu.cvial.wavelet.lifting.DWT97FastFloat;

public class BCWTEncoder extends BCWTCodecBase {

    public static Logger logger = Logger.getLogger("edu.ttu.cvial.codec.sq.bcwt.BCWTEncoder");
    
    public static class Param {
        private int logLevel;
        private float roundingFactor;
        private float bitrateTarget;
        private int bitTarget;
        private float compRatioTarget;
        private int qMinTarget;
        private int numOfCoeffToDiscardQMin;
        private float qualityIndex;
        private int dwtLevel;
        private boolean isReversible;
        private String progressiveType;
                
        public Param() {
            // Defaults
            logLevel = 2;
            roundingFactor = 0.9f;
            bitrateTarget = -1.0f;
            bitTarget = -1;
            compRatioTarget = -1;
            numOfCoeffToDiscardQMin = -1;
            qualityIndex = -1;
            qMinTarget = 0;
            dwtLevel = 7;
            isReversible = false;
//            progressiveType = "qua";
            progressiveType = "res";
        }
        
        public float getBitrateTarget() {
            return bitrateTarget;
        }
        public void setBitrateTarget(float bitrate) {
            this.bitrateTarget = bitrate;
        }
        public int getLogLevel() {
            return logLevel;
        }
        public void setLogLevel(int logLevel) {
            this.logLevel = logLevel;
        }
        public int getNumOfCoeffToDiscardQMin() {
            return numOfCoeffToDiscardQMin;
        }
        public void setNumOfCoeffToDiscardQMin(int numOfCoeffToDiscardQMin) {
            this.numOfCoeffToDiscardQMin = numOfCoeffToDiscardQMin;
        }
        public float getRoundingFactor() {
            return roundingFactor;
        }
        public void setRoundingFactor(float roundingFactor) {
            this.roundingFactor = roundingFactor;
        }

        public int getBitTarget() {
            return bitTarget;
        }

        public void setBitTarget(int bitsTarget) {
            this.bitTarget = bitsTarget;
        }

        public float getCompRatioTarget() {
            return compRatioTarget;
        }

        public void setCompRatioTarget(float compRatioTarget) {
            this.compRatioTarget = compRatioTarget;
        }

        public float getQualityIndex() {
            return qualityIndex;
        }

        public void setQualityIndex(float qualityIndex) {
            this.qualityIndex = qualityIndex;
        }

        public int getDwtLevel() {
            return dwtLevel;
        }

        public void setDwtLevel(int dwtLevel) {
            this.dwtLevel = dwtLevel;
        }

        public boolean isReversible() {
            return isReversible;
        }

        public void setReversible(boolean isLossless) {
            this.isReversible = isLossless;
        }

        public int getQMinTarget() {
            return qMinTarget;
        }

        public void setQMinTarget(int min) {
            qMinTarget = min;
        }

        public String getProgressiveType() {
            return progressiveType;
        }

        public void setProgressiveType(String progressiveType) {
            this.progressiveType = progressiveType;
        }
    }

    // Input parameters
    private RenderedImage input;
    private ImageOutputStream output;
    private Param param;
    
    // Output parameters
    private float bitrateAchieved;
    private int bitAchieved;
    private float compRatioAchieved;
    private float qualityIndexAchieved;

    // Internal fields
    private BCWTEncoderCore bcwtCore;
    private ImageCoeff imageCoeffs;
    private EncodedProperties outputProperties;
    
    public BCWTEncoder() {
        input = null;
        output = null;
        param = null;
        
        bcwtCore = null;
        imageCoeffs = null;
    }

    public void start() throws IOException {
        if (input == null) {
            logger.severe("No input set!");
            return;
        }
        if (output == null) {
            logger.severe("No output set!");
            return;
        }
        if (param == null) {
            logger.warning("No param set, using defauls");
            setParam(new Param());
        }
        
        logger.info("BCWTEncoder started....");
        Stopwatch watch = new Stopwatch();
        watch.start();

        init();

        readInput();

        startCT();

        startDWT();

        startBCWTCore();
        
        writeOutput();
        
        watch.stop();
        logger.info(watch.getElapsedTime() + "ms: End-to-end encoding completed");
    }

    private void init() {
        bcwtCore = new BCWTEncoderCore();
    }

    private void startBCWTCore() {
        Stopwatch watch = new Stopwatch();
        
        // Find the lower bound of bitsTarget for encoding.
        int bitsTarget = -1;
        if (param.bitrateTarget > 0 || param.bitTarget > 0
                || param.compRatioTarget > 0) {
            bitsTarget = Integer.MAX_VALUE;
            if (param.bitrateTarget > 0) {
                bitsTarget = Math.min(bitsTarget, (int) (param.bitrateTarget * outputProperties.rowsReal
                        * outputProperties.colsReal));
            }
            if (param.compRatioTarget > 0) {
                bitsTarget = Math
                        .min(bitsTarget, (int) (outputProperties.bitsPerElement
                                * outputProperties.rowsReal * outputProperties.colsReal
                                * outputProperties.components / param.compRatioTarget));
            }
            if (param.bitTarget > 0) {
                bitsTarget = Math.min(bitsTarget, param.bitTarget);
            }
            logger.info("bits target is " + bitsTarget);
        }
        
        if (param.qualityIndex > 0) {
            int thisQMin = (int) param.qualityIndex;
            float fractionQI = param.qualityIndex - (float) thisQMin;
            int thisNumOfCoeffToDiscardQMin = (int) (fractionQI
                    * outputProperties.rows * outputProperties.cols * outputProperties.components);
            logger.info("frationQI = " + fractionQI);
            param.setQMinTarget(thisQMin);
            param.setNumOfCoeffToDiscardQMin(thisNumOfCoeffToDiscardQMin);
        }
        
        if (param.progressiveType.equalsIgnoreCase("res")) {
            bcwtCore.setProgressiveType(BCWTCoreBase.PROGRESSIVE_RESOLUTION);
        } else if (param.progressiveType.equalsIgnoreCase("qua")) {
            bcwtCore.setProgressiveType(BCWTCoreBase.PROGRESSIVE_QUALITY);
        }
        outputProperties.setProgressiveType((byte) bcwtCore.getProgressiveType());
        
        watch.start();
        bcwtCore.setImage(imageCoeffs);
        bcwtCore.setDwtLevel(param.dwtLevel);
        bcwtCore.setQMin(param.qMinTarget);
        bcwtCore.setRoundingFactor(param.roundingFactor);
        bcwtCore.setLogLevel(param.logLevel);
        if (bitsTarget > 0)
            bcwtCore.setBitsTarget(bitsTarget);
        if (param.numOfCoeffToDiscardQMin > 0)
            bcwtCore.setNumOfCoeffToDiscardQMin(param.numOfCoeffToDiscardQMin);
        
        bcwtCore.start();
        watch.stop();
        
        outputProperties.qMax = (byte) bcwtCore.getQMax();
        outputProperties.qMaxOfSubbands = bcwtCore.getQMaxOfSubbands();
        logger.info(watch.getElapsedTime() + "ms: Encoding completed");
        logger.info("\tqmin = " + bcwtCore.getQMin() + ", qmax = " + outputProperties.qMax);
        for (int cp = 0; cp < outputProperties.components; ++cp) {
            logger.info("\tcomponent " + cp + ":");
            String msg = "";
            for (int i = 0; i < 4; i++) {
                msg += "\t" + outputProperties.qMaxOfSubbands[cp][i];
            }
            logger.info(msg);
        }
    }

    private void writeOutput() throws IOException {
        Stopwatch watch = new Stopwatch();
        watch.start();
        byte[] outputBuf = bcwtCore.getOutput();
        outputProperties.qMin = (byte) bcwtCore.getQMin();
        logger.info("Achieved qmin = " + outputProperties.qMin);
        
        outputProperties.sizeInByte = bcwtCore.getOutputSizeInByte();
        bitAchieved = outputProperties.sizeInByte * 8;
        bitrateAchieved = (float) bitAchieved / (float) (outputProperties.rowsReal * outputProperties.colsReal);
        compRatioAchieved = (float) (outputProperties.components * outputProperties.bitsPerElement / bitrateAchieved);
        logger.info("\tbitrate = " + bitrateAchieved +  
                " bpp, comp-ratio = " + compRatioAchieved + 
                ":1"
        );

        qualityIndexAchieved = outputProperties.qMin
        + ((float) bcwtCore.getNumOfCoeffToDiscardQMin() / (float) (outputProperties.rows
                * outputProperties.cols * outputProperties.components));
        logger.info("Quality-Index achieved: " + qualityIndexAchieved);


        outputProperties.transitionOfQMin = bcwtCore.getNumOfCoeffToDiscardQMin(); 
        if (outputProperties.transitionOfQMin > 0) {
            logger.info("After encoded coeff " + outputProperties.transitionOfQMin + ", no more bits are discarded");
            outputProperties.transitionOfQMin = outputProperties.rows * outputProperties.cols
                    * outputProperties.components - outputProperties.transitionOfQMin;
            logger.info("Decoder will change qmin to " + (param.qMinTarget + 1) + 
                    " after coeff " + outputProperties.transitionOfQMin + 
                    " is decoded");
        }

        outputProperties.dwtLevel = (byte) param.dwtLevel;
        outputProperties.isReversible = param.isReversible;
        
        // To ensure compatibility with C++ version, use little endian.        
        ByteOrder orgOrder = output.getByteOrder();
        output.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        
        outputProperties.write(output);
        output.write(outputBuf, 0 , outputProperties.sizeInByte);
        
        output.setByteOrder(orgOrder);
        
        watch.stop();
        logger.info(watch.getElapsedTime() + "ms: Encoded bitstream outputted");
        logger.info("\tsize = " + outputProperties.sizeInByte + " bytes");
    }

    private void startDWT() {
        Stopwatch watch = new Stopwatch();
        switch (imageCoeffs.getType()) {
        case ImageCoeff.TYPE_FLOAT: {
            float[][][] imageData = (float[][][]) imageCoeffs.getCoeffs();
            DWT97FastFloat dwt = new DWT97FastFloat();
            int rowsLL = outputProperties.rows >> param.dwtLevel;
            int colsLL = outputProperties.cols >> param.dwtLevel;
            outputProperties.meanOfLLBands = new float[outputProperties.components];
            watch.start();
            for (int cp = 0; cp < outputProperties.components; ++cp) {
                dwt.forwardTrans(imageData[cp], param.dwtLevel);
                outputProperties.meanOfLLBands[cp] = ArrayOp.getMean(imageData[cp], 0, 0,
                        colsLL, rowsLL);
                ArrayOp.subtract(imageData[cp], outputProperties.meanOfLLBands[cp], 0, 0,
                        colsLL, rowsLL);
            }
            watch.stop();
            for (int cp = 0; cp < outputProperties.components; ++cp)
                logger.info("\tLL-band(" + cp + ") mean = "
                        + outputProperties.meanOfLLBands[cp]);
            break;
        }
        case ImageCoeff.TYPE_SHORT: {
            short[][][] imageData = (short[][][]) imageCoeffs.getCoeffs();
            DWT53FastShort dwt = new DWT53FastShort();
//            DWT42FastShort dwt = new DWT42FastShort();
//            DWT222FastShort dwt = new DWT222FastShort();
//            DWT97FastShort dwt = new DWT97FastShort();
            int rowsLL = outputProperties.rows >> param.dwtLevel;
            int colsLL = outputProperties.cols >> param.dwtLevel;
            outputProperties.meanOfLLBands = new float[outputProperties.components];
            watch.start();
            for (int cp = 0; cp < outputProperties.components; ++cp) {
                dwt.forwardTrans(imageData[cp], param.dwtLevel);
            }
            watch.stop();
            break;
        }
        default:
            logger.severe("Unknow image data type: " + imageCoeffs.getType());
            return;
        }
        logger.info(watch.getElapsedTime() + "ms: DWT completed, levels = " + param.dwtLevel);

        // Debug: To output a DWT'ed image.
//        BufferedImage dwtImage = ImageIO.toBufferedImage(imageCoeffs, 0, 0, outputProperties.cols, outputProperties.rows);
//        try {
//            javax.imageio.ImageIO.write(dwtImage, "pnm", new File("C:/home/gjl/doc/Research/images/dwtImage.pnm"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        
    }

    private void startCT() {
        if (outputProperties.components != 3)
            return;
        
        Stopwatch watch = new Stopwatch();
        switch (imageCoeffs.getType()) {
        case ImageCoeff.TYPE_FLOAT: {
            float[][][] imageData = (float[][][]) imageCoeffs.getCoeffs();
            ColorSpaceYCbCrICT ct = new ColorSpaceYCbCrICT();
            watch.start();
            ct.fromRGB(imageData, 0, 0, outputProperties.cols, outputProperties.rows);
            watch.stop();
            logger.info(watch.getElapsedTime() + "ms: RGB->YCbCr(ICT) completed");
            break;
        }
        case ImageCoeff.TYPE_SHORT: {
            short[][][] imageData = (short[][][]) imageCoeffs.getCoeffs();
            ColorSpaceYCoCg ct = new ColorSpaceYCoCg();
            watch.start();
            ct.fromRGB(imageData, 0, 0, outputProperties.cols, outputProperties.rows);
            watch.stop();
            logger.info(watch.getElapsedTime() + "ms: RGB->YCoCg completed");
            break;
        }
        default:
            logger.severe("Unknow image data type: " + imageCoeffs.getType());
            return;
        }
    }

    private void readInput() throws IOException {
        Stopwatch watch = new Stopwatch();
        if (param.isReversible) {
            imageCoeffs = new ImageCoeffShort();
            logger.info("Encoding in reversible mode");
        } else {
            imageCoeffs = new ImageCoeffFloat();
            logger.info("Encoding in irreversible mode");
        }
        
        watch.start();
        ImageIO.readWithPadding(input, imageCoeffs, param.dwtLevel);
        watch.stop();
        input = null;
        
        outputProperties = new EncodedProperties();
        Properties prop = imageCoeffs.getPropertySet(ImageCoeff.PROPSET_ORG);
        outputProperties.components = Byte.parseByte(prop.getProperty(ImageCoeff.PROP_BANDS));
        outputProperties.rowsReal = Integer.parseInt(prop.getProperty(ImageCoeff.PROP_HEIGHT));
        outputProperties.colsReal = Integer.parseInt(prop.getProperty(ImageCoeff.PROP_WIDTH));
        outputProperties.bitsPerElement = Byte.parseByte(prop.getProperty(ImageCoeff.PROP_BITSPERELEMENT));
        
        prop = imageCoeffs.getPropertySet(ImageCoeff.PROPSET_PADDED);
        outputProperties.rows = Integer.parseInt(prop.getProperty(ImageCoeff.PROP_HEIGHT));
        outputProperties.cols = Integer.parseInt(prop.getProperty(ImageCoeff.PROP_WIDTH));

        logger.info(watch.getElapsedTime() + "ms: Image read ");
        logger.info(
                "\tcomponents = " + outputProperties.components +
                ", cols x rows = " + outputProperties.colsReal + " x " + outputProperties.rowsReal +
                " (padded " + outputProperties.cols + " x " + outputProperties.rows + 
                "), " + outputProperties.bitsPerElement + " bpe"
        );
    }

    public RenderedImage getInput() {
        return input;
    }

    public void setInput(RenderedImage inputImage) {
        this.input = inputImage;
    }

    public ImageOutputStream getOutput() {
        return output;
    }

    public void setOutput(ImageOutputStream outputStream) {
        this.output = outputStream;
    }

    public float getBitrateAchieved() {
        return bitrateAchieved;
    }

    public float getCompRatioAchieved() {
        return compRatioAchieved;
    }
    
    public void setParam(Param param) {
        this.param = param;
    }
    
    public Param getParam() {
        return param;
    }

    public float getQualityIndexAchieved() {
        return qualityIndexAchieved;
    }

    public int getBitAchieved() {
        return bitAchieved;
    }
}
