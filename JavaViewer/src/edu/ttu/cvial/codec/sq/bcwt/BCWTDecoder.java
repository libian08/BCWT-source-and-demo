package edu.ttu.cvial.codec.sq.bcwt;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Properties;
import java.util.logging.Logger;

import javax.imageio.stream.ImageInputStream;

import edu.ttu.cvial.arrayop.ArrayOp;
import edu.ttu.cvial.colorspace.ColorSpaceYCbCrICT;
import edu.ttu.cvial.colorspace.ColorSpaceYCoCg;
import edu.ttu.cvial.imageio.ImageCoeff;
import edu.ttu.cvial.imageio.ImageCoeffFloat;
import edu.ttu.cvial.imageio.ImageCoeffShort;
import edu.ttu.cvial.imageio.ImageIO;
import edu.ttu.cvial.util.Stopwatch;
import edu.ttu.cvial.util.storage.BitStream;
import edu.ttu.cvial.wavelet.lifting.DWT53FastShort;
import edu.ttu.cvial.wavelet.lifting.DWT97FastFloat;

public class BCWTDecoder extends BCWTCodecBase {

    public static Logger logger = Logger
            .getLogger("edu.ttu.cvial.codec.sq.bcwt.BCWTDecoder");
    
    public static class Param {
        private int compTarget;
        private int dwtLevelTarget;
        private int resolutionIndex;
        private int rowsRealTarget;
        private int colsRealTarget;
        private int rowsTarget;
        private int colsTarget;
        private int dwtLevelEffective;
        
        private Rectangle sourceRegion;
        private Rectangle sourceRegionTarget;
        
        private float refinementFactor;
        private int logLevel;
        private float qualityIndexTarget;
        private float bitrateTarget;
        private int bitTarget;
        private float compRatioTarget;
        
        public Param() {
            compTarget = ImageIO.ALL_COMPONENTS;
            dwtLevelTarget = 1;
            resolutionIndex = -1;
            refinementFactor = 0.375f;
            logLevel = 2;
            qualityIndexTarget = -1;
            bitrateTarget = -1;
            bitTarget = -1;
            compRatioTarget = -1;

            sourceRegion = null;
            sourceRegionTarget = null;
        }

        public int getCompTarget() {
            return compTarget;
        }

        public void setCompTarget(int compTarget) {
            this.compTarget = compTarget;
        }

        public int getDwtLevelTarget() {
            return dwtLevelTarget;
        }

        public void setDwtLevelTarget(int dwtLevelTarget) {
            this.dwtLevelTarget = dwtLevelTarget;
        }

        public int getLogLevel() {
            return logLevel;
        }

        public void setLogLevel(int logLevel) {
            this.logLevel = logLevel;
        }

        public float getQualityIndexTarget() {
            return qualityIndexTarget;
        }

        public void setQualityIndexTarget(float qualityIndexTarget) {
            this.qualityIndexTarget = qualityIndexTarget;
        }

        public float getRefinementFactor() {
            return refinementFactor;
        }

        public void setRefinementFactor(float refinementFactor) {
            this.refinementFactor = refinementFactor;
        }

        public int getResolutionIndex() {
            return resolutionIndex;
        }

        public void setResolutionIndex(int resolutionFactor) {
            this.resolutionIndex = resolutionFactor;
        }

        public int getColsRealTarget() {
            return colsRealTarget;
        }

        public int getRowsRealTarget() {
            return rowsRealTarget;
        }

        public float getBitrateTarget() {
            return bitrateTarget;
        }

        public void setBitrateTarget(float bitrateTarget) {
            this.bitrateTarget = bitrateTarget;
        }

        public int getBitTarget() {
            return bitTarget;
        }

        public void setBitTarget(int bitTarget) {
            this.bitTarget = bitTarget;
        }

        public float getCompRatioTarget() {
            return compRatioTarget;
        }

        public void setCompRatioTarget(float compRatioTarget) {
            this.compRatioTarget = compRatioTarget;
        }

        public Rectangle getSourceRegion() {
            return sourceRegion;
        }

        public void setSourceRegion(Rectangle sourceRegion) {
            this.sourceRegion = sourceRegion;
        }
    }
    
    // Input parameters
    ImageInputStream input;
    private Param param;
    
    // Output parameters
    private BufferedImage output;
    private float bitrateAchieved;
    private int bitAchieved;
    private float compRatioAchieved;
    private float qualityIndexAchieved;

    // Internal fields
    private BCWTDecoderCore bcwtCore;
    private ImageCoeff imageCoeff;
    private BitStream bitStream;
    ByteOrder orgOrder;
    private boolean isHeaderRead;
    private boolean isInputRead;
    private EncodedProperties inputProperties;
    
    public BCWTDecoder() {
        bcwtCore = null;
        imageCoeff = null;
        bitStream = null;
        orgOrder = null;
        isHeaderRead = false;
        isInputRead = false;
        inputProperties = null;
    }

    public void start() throws IOException {
        if (input == null) {
            logger.severe("No input set!");
            return;
        }
        if (param == null) {
            logger.warning("No param set, using defauls");
            setParam(new Param());
        }

        logger.info("BCWTDecoder started....");
        Stopwatch watch = new Stopwatch();
        watch.start();

        readInput();
        
        init();

        startBCWT();

        startDWT();
        
        startCT();
        
        writeOutput();
        
        watch.stop();
        logger.info(watch.getElapsedTime() + "ms: Total time");
        
        cleanup();
    }

    private void writeOutput() {
        Stopwatch watch = new Stopwatch();
        watch.start();
        if (param.sourceRegion == null) {
            output = ImageIO.toBufferedImage(imageCoeff, 0, 0, param.colsRealTarget, param.rowsRealTarget);
        } else {
            output = ImageIO.toBufferedImage(imageCoeff, 
                    param.sourceRegionTarget.x, 
                    param.sourceRegionTarget.y,
                    param.sourceRegionTarget.width, 
                    param.sourceRegionTarget.height);
        }
        watch.stop();
        
        logger.info(watch.getElapsedTime() + "ms: Decoded image outputted");
        logger.info("\tsize = " + (inputProperties.components * param.rowsRealTarget * param.colsRealTarget) + "bytes");
        logger.info("\tcols x rows = " + param.colsRealTarget + " x " + param.rowsRealTarget);
    }
    
    private void cleanup() {
        // Clean up some fields that are not needed in this session, but make 
        // sure to keep those required for next session.
        bcwtCore = null;
        imageCoeff = null;
        orgOrder = null;
    }

    private void startCT() {
        if (inputProperties.components != 3)
            return;
        
        Stopwatch watch = new Stopwatch();
        switch (imageCoeff.getType()) {
        case ImageCoeff.TYPE_FLOAT: {
            float[][][] imageData = (float[][][]) imageCoeff.getCoeffs();
            ColorSpaceYCbCrICT ct = new ColorSpaceYCbCrICT();
            watch.start();
            ct.toRGB(imageData, 0, 0, param.colsRealTarget, param.rowsRealTarget);
            watch.stop();
            logger.info(watch.getElapsedTime()
                    + "ms: YCbCr(ICT)->RGB completed.");
            break;
        }
        case ImageCoeff.TYPE_SHORT: {
            short[][][] imageData = (short[][][]) imageCoeff.getCoeffs();
            ColorSpaceYCoCg ct = new ColorSpaceYCoCg();
            watch.start();
            ct.toRGB(imageData, 0, 0, param.colsRealTarget, param.rowsRealTarget);
            watch.stop();
            logger.info(watch.getElapsedTime() + "ms: YCoCg->RGB completed");
            break;
        }
        default:
            logger.severe("Unknow image data type: " + imageCoeff.getType());
            return;
        }
    }

    private void startDWT() {
        int cp;
        Stopwatch watch = new Stopwatch();
        
        switch (imageCoeff.getType()) {
        case ImageCoeff.TYPE_FLOAT: {
            float[][][] imageData = (float[][][]) imageCoeff.getCoeffs();
            DWT97FastFloat dwt = new DWT97FastFloat();
            watch.start();
            int rowsLL = param.rowsTarget >> param.dwtLevelEffective;
            int colsLL = param.colsTarget >> param.dwtLevelEffective;
            for (cp = 0; cp < inputProperties.components; ++cp) {
                
                ArrayOp.add(imageData[cp], inputProperties.meanOfLLBands[cp], 0, 0,
                        colsLL, rowsLL);
                if (param.sourceRegion == null) {
                    dwt.inverseTrans(imageData[cp], param.dwtLevelEffective,
                            1, param.rowsTarget, param.colsTarget);
                } else {
                    dwt.inverseTransROI(imageData[cp], param.dwtLevelEffective,
                            1, param.rowsTarget, param.colsTarget, param.sourceRegionTarget);
                }
            }
            watch.stop();
            for (cp = 0; cp < inputProperties.components; ++cp)
                logger.info("\tLL-band(" + cp + ") mean = "
                        + inputProperties.meanOfLLBands[cp] + "");

            if (param.dwtLevelTarget > 1) {
                int scalingFactor = (1 << (param.dwtLevelTarget - 1));
                for (cp = 0; cp < inputProperties.components; ++cp) {
                    ArrayOp.divide(imageData[cp], (float) scalingFactor, 0, 0,
                            param.colsRealTarget, param.rowsRealTarget);
                }
            }
            break;
        }
        case ImageCoeff.TYPE_SHORT: {
            short[][][] imageData = (short[][][]) imageCoeff.getCoeffs();
            DWT53FastShort dwt = new DWT53FastShort();
            watch.start();
            for (cp = 0; cp < inputProperties.components; ++cp) {
                if (param.sourceRegion == null) {
                    dwt.inverseTrans(imageData[cp], param.dwtLevelEffective,
                            1, param.rowsTarget, param.colsTarget);
                } else {
                    dwt.inverseTransROI(imageData[cp], param.dwtLevelEffective,
                            1, param.rowsTarget, param.colsTarget, param.sourceRegionTarget);
                }
            }
            watch.stop();
            break;
        }
        default:
            logger.severe("Unknow image data type: " + imageCoeff.getType());
            return;
        }
        logger.info(watch.getElapsedTime() + "ms: DWT completed");
        logger.info("\tlevel = " + inputProperties.dwtLevel + " to " + param.dwtLevelTarget
                + "");
    }

    private void startBCWT() {
        
        Stopwatch watch = new Stopwatch();
        bcwtCore = new BCWTDecoderCore();
        bcwtCore.setImage(imageCoeff);
        bcwtCore.setDwtLevel(param.dwtLevelEffective);
        bcwtCore.setDwtLevelTarget(1);
        bcwtCore.setQMin(inputProperties.qMin);
        bcwtCore.setQMax(inputProperties.qMax);
        bcwtCore.setQMaxOfSubbands(inputProperties.qMaxOfSubbands);
        bcwtCore.setLogLevel(param.logLevel);
        bcwtCore.setRefinementFactor(param.refinementFactor);
        bcwtCore.setQMinTransitionInCoeff(inputProperties.transitionOfQMin);

        // Find the lower bound of bitsTarget for encoding.
        if (param.bitrateTarget > 0 || param.bitTarget > 0
                || param.compRatioTarget > 0) {
            int bitsTarget = Integer.MAX_VALUE;
            if (param.bitrateTarget > 0) {
                bitsTarget = Math.min(bitsTarget, (int) (param.bitrateTarget * inputProperties.rowsReal
                        * inputProperties.colsReal));
            }
            if (param.compRatioTarget > 0) {
                bitsTarget = Math
                        .min(bitsTarget, (int) (inputProperties.bitsPerElement
                                * inputProperties.rowsReal * inputProperties.colsReal
                                * inputProperties.components / param.compRatioTarget));
            }
            if (param.bitTarget > 0) {
                bitsTarget = Math.min(bitsTarget, param.bitTarget);
            }
            logger.info("bits target is " + bitsTarget);
            bcwtCore.setBitsTarget(bitsTarget);
        }

        if (param.qualityIndexTarget > 0) {
            int qMinTarget = (int) param.qualityIndexTarget;
            float qiTargetFraction = param.qualityIndexTarget - qMinTarget;
            int qMinTargetTransitionInCoeff = 0;
            if (qiTargetFraction > 0) {
                qMinTargetTransitionInCoeff = (int) ((1 - qiTargetFraction)
                        * inputProperties.rows * inputProperties.cols * inputProperties.components);
            }
            bcwtCore.setQMinTarget(qMinTarget);
            bcwtCore.setQMinTargetTransitionInCoeff(qMinTargetTransitionInCoeff);
        }
        
        watch.start();
        bcwtCore.setInputStream(bitStream, inputProperties.getProgressiveType());
        bcwtCore.start();
        watch.stop();

        {
            // Note the difference in compuation of the fraction of QualityIndex
            // in here from in BCWTEncoder. The redundant 1's are to clearly
            // show the meaning of each part of the formula.
            int coeffTotal = param.rowsTarget * param.colsTarget
                    * inputProperties.components;

            int coeffOfQMin = bcwtCore.getQMinTransitionInCoeff();
            int qMinAchieved = bcwtCore.getQMin();
            float qiAchievedByQMin = -1;
            if (coeffOfQMin > 0 && coeffTotal > coeffOfQMin) {
                qiAchievedByQMin = (qMinAchieved - 1)
                        + (1 - ((float) coeffOfQMin / (float) coeffTotal));
            } else {
                qiAchievedByQMin = qMinAchieved;
            }

            int coeffOfQMinTarget = bcwtCore.getQMinTargetTransitionInCoeff();
            int qMinTargetAchieved = bcwtCore.getQMinTarget();
            float qiAchievedByQMinTarget = -1;
            if (coeffOfQMinTarget > 0 && coeffTotal > coeffOfQMinTarget) {
                qiAchievedByQMinTarget = (qMinTargetAchieved - 1)
                        + (1 - ((float) coeffOfQMinTarget / (float) coeffTotal));
            } else {
                qiAchievedByQMinTarget = qMinTargetAchieved;
            }
            
            if (qiAchievedByQMin > qiAchievedByQMinTarget) {
                qualityIndexAchieved = qiAchievedByQMin;
                logger.warning("QualityIndex achieved is greater than target");
            } else {
                qualityIndexAchieved = qiAchievedByQMinTarget;
            }
            logger.info("QualityIndex achieved: " + qualityIndexAchieved);
        }
        
        bitAchieved = bcwtCore.getCountInputBits();
//        bitrateAchieved = (float) bitAchieved / (float) (param.rowsRealTarget * param.colsRealTarget);
        bitrateAchieved = (float) bitAchieved / (float) (inputProperties.rowsReal * inputProperties.colsReal);
        compRatioAchieved = (float) (inputProperties.components * inputProperties.bitsPerElement)
                / (float) bitrateAchieved;
        logger.info(watch.getElapsedTime() + "ms: Decoding completed.");
        logger.info("\trefinement factor = " + param.refinementFactor +"");
        logger.info("\tsize = " + (bitAchieved / 8) + " bytes");
        logger.info("bitrate = " + bitrateAchieved + ", comp-ratio = " + compRatioAchieved + ":1");
        bitStream = null;
    }
    
    public void readHeader() throws IOException {
        if (isHeaderRead) {
            return;
        }
        // To ensure compatibility with C++ version, use little endian.
        orgOrder = input.getByteOrder();
        input.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        
        inputProperties = new EncodedProperties();
        inputProperties.read(input);
        
        isHeaderRead = true;
    }

    private void readInput() throws IOException {
        if (isInputRead) {
            // xxx: True progressive decoding has not been implemented yet.
            // We will decode from the beginning for a second session.
            bitStream.setPosInBit(0);
            return;
        }
        
        Stopwatch watch = new Stopwatch();

        {
            watch.start();
            readHeader();
            bitStream = new BitStream(inputProperties.sizeInByte * 8);
            byte[] inputBuf = bitStream.getBuf();
            input.read(inputBuf);
            bitStream.setCountInByte(inputProperties.sizeInByte);

            input.setByteOrder(orgOrder);
            watch.stop();
        }
        
        logger.info(watch.getElapsedTime() + "ms: Read input stream");
        logger.info("\tsize = " + inputProperties.sizeInByte + " bytes");
        logger.info(
                "\tcomponents = " + inputProperties.components + 
                ", cols x rows = " + inputProperties.colsReal + " x " + inputProperties.rowsReal + 
                "(padded " + inputProperties.cols + " x " + inputProperties.rows + "), " + 
                inputProperties.bitsPerElement + "bpe "
        );
        logger.info(
                "\tdwt = " + inputProperties.dwtLevel + 
                ", qmin = " + inputProperties.qMin + 
                ", qmax = " + inputProperties.qMax + ""
        );
        for (int cp = 0; cp < inputProperties.components; ++cp) {
            logger.info("\tcomponent " + cp + ":");
            String msg = "";
            for (int i = 0; i < 4; i++) {
                msg += "\t" + inputProperties.qMaxOfSubbands[cp][i];
            }
            logger.info(msg);
        }
        
        isInputRead = true;
    }

    private void init() {
        if (param.resolutionIndex >= 0) {
            param.dwtLevelTarget = Math.max(param.dwtLevelTarget,
                    inputProperties.dwtLevel - param.resolutionIndex + 1);
        }
        param.dwtLevelEffective = inputProperties.dwtLevel - param.dwtLevelTarget + 1;
        param.rowsTarget = inputProperties.rows >> (param.dwtLevelTarget - 1);
        param.colsTarget = inputProperties.cols >> (param.dwtLevelTarget - 1);
        param.rowsRealTarget = inputProperties.rowsReal >> (param.dwtLevelTarget - 1);
        param.colsRealTarget = inputProperties.colsReal >> (param.dwtLevelTarget - 1);
        
        if (param.sourceRegion != null) {
            int x = param.sourceRegion.x >> (param.dwtLevelTarget - 1);
            int y = param.sourceRegion.y >> (param.dwtLevelTarget - 1);
            int width = param.sourceRegion.width >> (param.dwtLevelTarget - 1);
            int height = param.sourceRegion.height >> (param.dwtLevelTarget - 1);
            
            Rectangle sourceRegionTarget = new Rectangle(x, y, width, height);
            Rectangle sourceBoundsTarget = new Rectangle(0, 0, param.colsRealTarget, param.rowsRealTarget);
            param.sourceRegionTarget = sourceRegionTarget.intersection(sourceBoundsTarget);
            logger.info("Source ROI (x,y,w,h): " + param.sourceRegion.x + ", "
                    + param.sourceRegion.y + ", " + param.sourceRegion.width
                    + ", " + param.sourceRegion.height);
            logger.info("Target ROI (x,y,w,h): " + param.sourceRegionTarget.x + ", "
                    + param.sourceRegionTarget.y + ", "
                    + param.sourceRegionTarget.width + ", "
                    + param.sourceRegionTarget.height);
            if (param.sourceRegionTarget.isEmpty()) {
                logger.warning("ROI is not inside the image. Performing full decoding instead.");
                param.sourceRegion = null;
                param.sourceRegionTarget = null;
            }
        }
        
        if (inputProperties.isReversible) {
            imageCoeff = new ImageCoeffShort();
//            param.refinementFactor = 0;
            logger.info("Decoding in reversible mode");
        } else {
            imageCoeff = new ImageCoeffFloat();
            logger.info("Decoding in irreversible mode");
        }
        imageCoeff.newCoeffs(param.colsTarget, param.rowsTarget, inputProperties.components);
        Properties prop = imageCoeff.addPropertySet(ImageCoeff.PROPSET_ORG);
        prop.setProperty(ImageCoeff.PROP_HEIGHT, String.valueOf(param.rowsRealTarget));
        prop.setProperty(ImageCoeff.PROP_WIDTH, String.valueOf(param.colsRealTarget));
        prop.setProperty(ImageCoeff.PROP_BANDS, String.valueOf(inputProperties.components));
        prop.setProperty(ImageCoeff.PROP_COLORSPACE, inputProperties.components == 1 ? "Grayscale" : "RGB");
        prop.setProperty(ImageCoeff.PROP_BITSPERELEMENT, String.valueOf(8));

        prop = imageCoeff.addPropertySet(ImageCoeff.PROPSET_PADDED);
        prop.setProperty(ImageCoeff.PROP_HEIGHT, String.valueOf(param.rowsTarget));
        prop.setProperty(ImageCoeff.PROP_WIDTH, String.valueOf(param.colsTarget));
        prop.setProperty(ImageCoeff.PROP_BANDS, String.valueOf(inputProperties.components));
    }

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

    public BufferedImage getOutput() {
        return output;
    }

    public ImageInputStream getInput() {
        return input;
    }

    public void setInput(ImageInputStream input) {
        this.input = input;
    }

    public int getBitAchieved() {
        return bitAchieved;
    }

    public float getBitrateAchieved() {
        return bitrateAchieved;
    }

    public float getCompRatioAchieved() {
        return compRatioAchieved;
    }

    public float getQualityIndexAchieved() {
        return qualityIndexAchieved;
    }

    public EncodedProperties getInputProperties() {
        return inputProperties;
    }

}
