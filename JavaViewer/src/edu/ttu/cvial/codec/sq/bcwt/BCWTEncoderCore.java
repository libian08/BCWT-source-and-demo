package edu.ttu.cvial.codec.sq.bcwt;

import java.text.DecimalFormat;
import java.util.Properties;

import edu.ttu.cvial.arrayop.ArrayOp;
import edu.ttu.cvial.imageio.ImageCoeff;
import edu.ttu.cvial.util.Stopwatch;
import edu.ttu.cvial.util.storage.BitStream;

public class BCWTEncoderCore extends BCWTCoreBase {
    private abstract class Output {
        public static final int SIZE_BITPLANE = 32;
        public static final int QMAX_MAX = 16;
        
        public abstract void writeBits(int bits, int qMinThis, int qMax);
        public abstract void writeBit(boolean bit, int q);
        public abstract int getNumOfDiscardedBitsForQMin();
        protected abstract void init(int streamSize, int[] qHist);
        public abstract BitStream getOutput();
        public abstract int[] getQHist();
        
        protected void writeQHist(BitStream stream) {
            int[] qHist = getQHist();
            for (int q = qMax; q >= 0; --q) {
                stream.appendIntFast(qHist[q], SIZE_BITPLANE);
            }

        }
        
        public void showQHist() {
            int[] qHist = getQHist();
            int bitsTotal = 0;
            for (int q = qMax; q >= 0; q--) {
                logger.info("qHist[" + (new DecimalFormat("00").format(q)) + "]:" + qHist[q]);
                bitsTotal += qHist[q];
            }
            logger.info("bitsTotal = " + bitsTotal);
        }

    }
    
    private class QHistCounter extends Output {
                
        private int[] qHist;
        
        QHistCounter() {
            init(0, null);
        }
        
        QHistCounter(int[] qHist) {
            init(0, qHist);
        }

        public void writeBits(int bits, int qMinThis, int qMax) {
            for (int q = qMinThis; q <= qMax; q++) {
                qHist[q]++;
            }
        }

        public void writeBit(boolean bit, int q) {
            qHist[q]++;
        }

        public int[] getQHist() {
            return qHist;
        }
        
        protected void init(int streamSize, int[] qHist) {
            if (qHist == null) {
                this.qHist = new int[QMAX_MAX + 1];
            } else {
                this.qHist = qHist;
            }
        }
        
        public int getNumOfDiscardedBitsForQMin() {
            return 0;
        }

        public BitStream getOutput() {
            return null;
        }

    }
    
    private class OutputBitStream extends Output {
        private boolean doCalcQHist;
        
        private BitStream stream;
        private int numOfDiscardedBitsForQMin = 0;
        private int[] qHist;
        
        public void writeBits(int bits, int qMinThis, int qMax) {
            if (needToDiscardBitsForQMin) {
                if (qMinThis == qMin) {
                    numOfDiscardedBitsForQMin++;
                    qMinThis++;
                    if (qMinThis > qMax)
                        return;
                }
            }
            stream.appendIntFast(bits >> qMinThis, qMax - qMinThis + 1);
            if (doCalcQHist) {
                for (int q = qMinThis; q <= qMax; q++) {
                    qHist[q]++;
                }
            }
        }

        public void writeBit(boolean bit, int q) {
            if (needToDiscardBitsForQMin) {
                if (q == qMin) {
                    numOfDiscardedBitsForQMin++;
                    return;
                }
            }
            stream.appendBitFast(bit);
            if (doCalcQHist) {
                qHist[q]++;
            }
        }

        public int getNumOfDiscardedBitsForQMin() {
            return numOfDiscardedBitsForQMin;
        }
        
        OutputBitStream(int streamSize, int[] qHist) {
            init(streamSize, qHist);
        }

        protected void init(int streamSize, int[] qHist) {
            // The first byte is for bitOffset
            stream = new BitStream(streamSize + 1);
            stream.appendIntFast(0, 8);
            
            if (qHist == null) {
                doCalcQHist = true;
                this.qHist = new int[QMAX_MAX + 1];
            } else {
                doCalcQHist = false;
                this.qHist = qHist;
            }
        }

        public BitStream getOutput() {
            BitStream header = new BitStream();
            writeQHist(header);
            
            byte[] buf = stream.getBuf();
            buf[0] = (byte) stream.getBitCountOffsetComplement();
            stream.reverseBits(1, stream.getCountInByte() - 1);
            
            BitStream[] streamArray = new BitStream[] {header, stream};
            return BitStream.combine(streamArray);
        }
        
        public int[] getQHist() {
            return qHist;
        }
        
    }
    
    private class OutputBitplaneStream extends Output {
        
        private BitStream[] streamBitplane;
        private int numOfDiscardedBitsForQMin = 0;
        private int[] qHist;
        
        public void writeBits(int bits, int qMinThis, int qMax) {
            if (needToDiscardBitsForQMin) {
                if (qMinThis == qMin) {
                    numOfDiscardedBitsForQMin++;
                    qMinThis++;
                    if (qMinThis > qMax)
                        return;
                }
            }
            bits >>= qMinThis;
            for (int q = qMinThis; q <= qMax; ++q) {
                streamBitplane[q].appendBitFast((bits & 0x1) > 0);
                bits >>= 1;
            }
        }
    
        public void writeBit(boolean bit, int q) {
            if (needToDiscardBitsForQMin) {
                if (q == qMin) {
                    numOfDiscardedBitsForQMin++;
                    return;
                }
            }
            streamBitplane[q].appendBitFast(bit);
        }
    
        public int getNumOfDiscardedBitsForQMin() {
            return numOfDiscardedBitsForQMin;
        }
        
        OutputBitplaneStream(int streamSize, int[] qHist) {
            init(streamSize, qHist);
        }
    
        protected void init(int streamSize, int[] qHist) {
            streamBitplane = new BitStream[QMAX_MAX + 1];
            if (qHist == null) {
                this.qHist = new int[QMAX_MAX + 1];
                
                int thisStreamSize = streamSize >> 1;
                int totalStreamSize = 0;
                for (int q = qMin; q < QMAX_MAX; ++q) {
                    streamBitplane[q] = new BitStream(thisStreamSize);
                    totalStreamSize += thisStreamSize;
                    thisStreamSize >>= 1;
                }
                streamBitplane[QMAX_MAX] = new BitStream(streamSize
                        - totalStreamSize);
            } else {
                this.qHist = qHist;
                for (int q = qMin; q <= qMax; ++q) {
                    streamBitplane[q] = new BitStream(qHist[q]);
                }
            }
        }
    
        public BitStream getOutput() {
            BitStream header = new BitStream();
            writeQHist(header);
            
            BitStream[] streamArray = new BitStream[qMax + 2];
            int streamArrayI = 0;
            streamArray[streamArrayI++] = header;

            for (int q = qMax; q >= 0; --q) {
                if (streamBitplane[q] != null) {
                    streamBitplane[q].reverseBits();
                }
                streamArray[streamArrayI++] = streamBitplane[q];
            }
            
            return BitStream.combine(streamArray);
        }
        
        public int[] getQHist() {
            for (int q = 0; q < qHist.length; ++q) {
                if (streamBitplane[q] != null) {
                    qHist[q] = streamBitplane[q].getCountInBit();
                } else {
                    qHist[q] = 0;
                }
            }
            return qHist;
        }
        
    }

    private Output output;

    private ImageCoeff coeff;

    private int numOfComponents;

    private int numOfRows;

    private int numOfCols;

    private BitStream outputStream;

    private byte[][][] mqdMap;

    private int mqdMapComponents;

    private int mqdMapRows;

    private int mqdMapCols;

    private WaveletDomainPosition coeffUnit;

    private WaveletDomainPosition mqdUnit;

    private int qMax;

    private int qMin;

    private int qMinMask;

    private float roundingFactor;

    private float roundingThreshold;

    private int roundingValue;

    private int dwtLevel;

    private byte[][] qMaxOfSubbands;

    private boolean hasInit;

    private boolean hasReset;
    
    private int coeffProcessed;
    
    private int bitsToDiscardForQMin;
    
    private boolean needToDiscardBitsForQMin;
    
    private int numOfCoeffToDiscardQMin;
    
    private int bitsTarget;
    
    private boolean hasMQDMapComputed;
    
    private int progressiveType;

    public final int getBitsTarget() {
        return bitsTarget;
    }

    public final void setBitsTarget(int bitsTarget) {
        this.bitsTarget = bitsTarget;
    }

    public BCWTEncoderCore() {
        hasInit = false;
        hasReset = false;
        outputStream = null;
        roundingFactor = 0.9f;
        bitsTarget = -1;    // default: not specified, determined by qmin
        qMin = 0;   // default: the best quality (lossless or near-lossless)
        numOfCoeffToDiscardQMin = -1;
        needToDiscardBitsForQMin = false;
        progressiveType = PROGRESSIVE_RESOLUTION;
    }

    public void setRoundingFactor(float roundingFactor) {
        this.roundingFactor = roundingFactor;
    }

    public void setLogLevel(int logLevel) {
    }

    public void setImage(ImageCoeff coeff) {
        this.coeff = coeff;

        Properties prop = coeff.getPropertySet(ImageCoeff.PROPSET_PADDED);
        numOfComponents = Integer.parseInt(prop.getProperty(ImageCoeff.PROP_BANDS));
        numOfRows = Integer.parseInt(prop.getProperty(ImageCoeff.PROP_HEIGHT));
        numOfCols = Integer.parseInt(prop.getProperty(ImageCoeff.PROP_WIDTH));
    }

    public ImageCoeff getImage() {
        return coeff;
    }

    public void setQMin(final int qMin) {
        this.qMin = qMin;
        qMinMask = (-1 << qMin);
    }
    
    public int getQMin() {
        return qMin;
    }
    
    public int getQMax() {
        return qMax;
    }

    public byte[][] getQMaxOfSubbands() {
        return qMaxOfSubbands;
    }

    public void setDwtLevel(final int dwtLevel) {
        this.dwtLevel = dwtLevel;
    }

    public void start() {
        init();
        processAll();
        logger.info("Coeff processed: " + coeffProcessed);
        outputStream = output.getOutput();
//        output.showQHist();
//        logger.info("roundingCount = " + roundingCount);
        
//        for (int i = 0; i < tHist.length; ++i) {
//            logger.info("tHist[" + i + "] = " + tHist[i]);
//        }
    }
    
    private int[] findQMinForGivenBitsTarget() {
        
        roundingValue = 1 << qMin;
        roundingThreshold = roundingFactor * roundingValue;
        
        coeff.qMinMask = qMinMask;
        coeff.roundingThreshold = roundingThreshold;
        coeff.roundingValue = roundingValue;

        Stopwatch watch = new Stopwatch();
        watch.start();
        output = new QHistCounter();
        needToDiscardBitsForQMin = false;
        processAll();
        int[] qHist = output.getQHist();
        output.showQHist();
        
        bitsToDiscardForQMin = bitsTarget;
        int qMinTarget = qMax;
        while (true) {
            if (bitsToDiscardForQMin > qHist[qMinTarget]) {
                bitsToDiscardForQMin -= qHist[qMinTarget];
                if (--qMinTarget < qMin) {
                    needToDiscardBitsForQMin = false;
                    logger.warning("Target bitrate cannot be reached when qMin >= " + qMin);
                    break;
                }
            }
            else {
                logger.info("qMin for target bitrate is : " + qMinTarget);
                bitsToDiscardForQMin = qHist[qMinTarget] - bitsToDiscardForQMin; 
                this.setQMin(qMinTarget);
   
                if (bitsToDiscardForQMin > 0) {
                    needToDiscardBitsForQMin = true;
                    logger.info("bits to discard at qMin: " + bitsToDiscardForQMin);
                }
                else {
                    needToDiscardBitsForQMin = false;
                    logger.info("No bits need to be discarded during coding.");
                }
                break;
            }
        }
        coeffProcessed = 0;
        numOfCoeffToDiscardQMin = Integer.MAX_VALUE;
        watch.stop();
        logger.info(watch.getElapsedTime() + "ms : Auto-detected parameters for target bitrate");
        
        return qHist;
    }

    private void processAll() {
        processSubbands();
        processSubbandRoots();
        processLLBand();
        hasMQDMapComputed = true;
    }

    private void processLLBand() {
        WaveletDomainPosition cTop = coeffUnit;

        int cpStart = mqdMapComponents;
        int cpEnd = 0;
        int rStart = numOfRows >> dwtLevel;
        int rEnd = 0;
        int cStart = numOfCols >> dwtLevel;
        int cEnd = 0;
        for (int cp = cpStart - 1; cp >= cpEnd; --cp) {
            cTop.component = cp;
            for (int r = rStart - 1; r >= rEnd; --r) {
                cTop.row = r;
                for (int c = cStart - 1; c >= cEnd; --c) {
                    cTop.col = c;
                    getCoeff(cTop, cTop);
                    if (cTop.absValueInt > 0) {
                        output.writeBit(cTop.sign, getQMSB(cTop.absValueInt));
                    }

                    output.writeBits(cTop.absValueInt, qMin, qMaxOfSubbands[cp][0]);
                    
                    coeffProcessed++;
                    if (needToDiscardBitsForQMin) {
                        checkBitsDiscard();
                    }
                }
            }
        }
    }

    private void findQMax() {
        int cp;
        int rowsLL = numOfRows >> dwtLevel;
        int colsLL = numOfCols >> dwtLevel;

        qMax = -1;
        for (cp = numOfComponents - 1; cp >= 0; --cp) {
            int absValueIntMax  = coeff.getAbsMaxInt(cp, 0, 0, colsLL, rowsLL);
            int qH = -1;
            for (; absValueIntMax > 0; ++qH, absValueIntMax >>= 1)
                ;
            qMaxOfSubbands[cp][0] = (byte) qH;
            qMax = Math.max(qMax, qH);
        }

        int rowsLLHalf = rowsLL >> 1;
        int colsLLHalf = colsLL >> 1;
        for (cp = numOfComponents - 1; cp >= 0; --cp) {
            qMaxOfSubbands[cp][1] = ArrayOp.getMax(mqdMap[cp], 0, rowsLLHalf,
                    colsLLHalf, rowsLLHalf);
            qMax = Math.max(qMax, qMaxOfSubbands[cp][1]);

            qMaxOfSubbands[cp][2] = ArrayOp.getMax(mqdMap[cp], colsLLHalf, 0,
                    colsLLHalf, rowsLLHalf);
            qMax = Math.max(qMax, qMaxOfSubbands[cp][2]);

            qMaxOfSubbands[cp][3] = ArrayOp.getMax(mqdMap[cp], colsLLHalf,
                    rowsLLHalf, colsLLHalf, rowsLLHalf);
            qMax = Math.max(qMax, qMaxOfSubbands[cp][3]);
        }
    }

    private void processSubbandRoots() {
        int cp, r, c;

        WaveletDomainPosition cTop = coeffUnit;

        // Find qMax.
        int cpStart = mqdMapComponents;
        int cpEnd = 0;
        int rStart = numOfRows >> dwtLevel;
        int rEnd = 0;
        int cStart = numOfCols >> dwtLevel;
        int cEnd = 0;
        findQMax();

        // Encode level N subbands.
        cTop.dwtLevel = dwtLevel + 1;
        int rStartHalf = rStart >> 1;
        int cStartHalf = cStart >> 1;
        for (cp = cpStart - 1; cp >= cpEnd; --cp) {
            cTop.component = cp;
            for (r = rStart - 1; r >= rEnd; --r) {
                cTop.row = r;
                cEnd = r < rStartHalf ? cStartHalf : 0;
                for (c = cStart - 1; c >= cEnd; --c) {
                    cTop.col = c;
                    int qMQD = mqdMap[cp][r][c];
                    if (qMQD >= qMin) {
                        retrieveCoeff(cTop, false);
                        writeCoeffOffspring(cTop, qMQD);
                    }
                    int qMinThis = Math.max(qMQD, qMin);
                    int qMaxThis = r < rStartHalf ? qMaxOfSubbands[cp][2]
                            : (c < cStartHalf ? qMaxOfSubbands[cp][1]
                                    : qMaxOfSubbands[cp][3]);
                    output.writeBits(1 << qMQD, qMinThis, qMaxThis);
                    
                    coeffProcessed += 4;
                    if (needToDiscardBitsForQMin) {
                        checkBitsDiscard();
                    }

                }
            }
        }

    }

    private void processSubbands() {
        WaveletDomainPosition cTop = coeffUnit;
        WaveletDomainPosition mTop = mqdUnit;

        // Encode level 1 to (N-1) subbands.
        int cpStart = mqdMapComponents;
        int cpEnd = 0;
        int rStart = mqdMapRows;
        int rEnd = 0;
        int cStart = mqdMapCols;
        int cEnd = 0;
        for (int n = 3; n <= dwtLevel + 1; ++n) {
            cTop.dwtLevel = n;
            mTop.dwtLevel = n;

            int rStartHalf = rStart >> 1;
            int cStartHalf = cStart >> 1;
            for (int cp = cpStart - 1; cp >= cpEnd; --cp) {
                cTop.component = cp;
                mTop.component = cp;

                for (int r = rStart - 1; r >= rEnd; --r) {
                    cTop.row = r;
                    mTop.row = r;

                    cEnd = r < rStartHalf ? cStartHalf : 0;
                    for (int c = cStart - 1; c >= cEnd; --c) {
                        cTop.col = c;
                        mTop.col = c;
                        processUnit();

                        if (needToDiscardBitsForQMin) {
                            checkBitsDiscard();
                        }
                    }
                }
            }
            rStart >>= 1;
            cStart >>= 1;
        }
    }

    private void checkBitsDiscard() {
        if (bitsToDiscardForQMin <= output
                .getNumOfDiscardedBitsForQMin()
                || numOfCoeffToDiscardQMin <= coeffProcessed
        ) {
            needToDiscardBitsForQMin = false;
            numOfCoeffToDiscardQMin = coeffProcessed;
        }
    }

    private void init() {

        // The followings should be initiliaze only once.
        if (!hasInit) {
            // Init MQD map.
            mqdMapComponents = numOfComponents;
            mqdMapRows = numOfRows >> 2;
            mqdMapCols = numOfCols >> 2;
            mqdMap = ArrayOp.newByte3D(mqdMapComponents, mqdMapRows,
                    mqdMapCols, -1);

            // Init coding unit.
            coeffUnit = new WaveletDomainPosition();
            mqdUnit = new WaveletDomainPosition();
            WaveletDomainPosition cTop = coeffUnit;
            WaveletDomainPosition mTop = mqdUnit;
            cTop.offspring = new WaveletDomainPosition[4];
            mTop.offspring = new WaveletDomainPosition[4];
            for (int i = 0; i < 4; ++i) {
                cTop.offspring[i] = new WaveletDomainPosition();
                mTop.offspring[i] = new WaveletDomainPosition();
                cTop.offspring[i].offspring = new WaveletDomainPosition[4];
                for (int j = 0; j < 4; ++j) {
                    cTop.offspring[i].offspring[j] = new WaveletDomainPosition();
                }
            }

            qMaxOfSubbands = new byte[numOfComponents][4];

            hasInit = true;
            hasMQDMapComputed = false;
        }
        
        // Find the bitsTarget and initialize the output
        {
            int[] qHist = null;
            if (getBitsTarget() > 0) {
                qHist = findQMinForGivenBitsTarget();
            } else {
                // rough estimate of the bitsTarget based on qMin
                bitsTarget = (numOfComponents * numOfRows * numOfCols * 8) >> (qMin + 1);
            }
            bitsTarget += 1024 * 8;
            
            switch (progressiveType) {
            case PROGRESSIVE_RESOLUTION:
                output = new OutputBitStream(bitsTarget, qHist);
                break;
            case PROGRESSIVE_QUALITY:
                output = new OutputBitplaneStream(bitsTarget, qHist);
                break;
            default:
                String msg = "Unknown progressive type: " + progressiveType;
                logger.severe(msg);
                throw new IllegalArgumentException(msg);
            }
            
            logger.info("Progressive type: " + progressiveType);
        }

        // Rounding is not used yet, but let's keep it for the momemnt.
        roundingValue = 1 << qMin;
        roundingThreshold = roundingFactor * roundingValue;
        logger.info("roundingThreshold = " + roundingThreshold);
        
        coeff.qMinMask = qMinMask;
        coeff.roundingThreshold = roundingThreshold;
        coeff.roundingValue = roundingValue;
        
        coeffProcessed = 0;
    }

    private void retrieveCoeff(final WaveletDomainPosition cTop, final boolean isNotRoot) {
        WaveletDomainPosition.getOffspring(cTop, cTop.offspring);
        for (int i = 0; i < 4; ++i) {
            WaveletDomainPosition cOffs = cTop.offspring[i];
            getCoeff(cTop, cOffs);

            if (isNotRoot) {
                WaveletDomainPosition.getOffspring(cOffs, cOffs.offspring);
                for (int j = 0; j < 4; ++j) {
                    getCoeff(cTop, cOffs.offspring[j]);
                }
            }
        }
    }
    
    private int roundingCount = 0;

    private void getCoeff(final WaveletDomainPosition cTop, WaveletDomainPosition cPos) {
        int value = coeff.getCoeffInt(cPos.col, cPos.row, cTop.component);
        cPos.absValueInt = Math.abs(value) & qMinMask;
        cPos.sign = value >= 0;
        
//        int value = coeff.getCoeffInt(cPos.col, cPos.row, cTop.component);
//        int absValue = Math.abs(value);
//        cPos.absValueInt = absValue & qMinMask;
//        cPos.sign = value >= 0;
//        if (absValue - cPos.absValueInt > roundingThreshold) {
//            cPos.absValueInt += roundingValue;
//            roundingCount++;
//        }

    }

    private void retrieveMQDNodes(final WaveletDomainPosition mTop) {
        WaveletDomainPosition.getOffspring(mTop, mTop.offspring);
        for (int i = 0; i < 4; ++i) {
            WaveletDomainPosition mOffs = mTop.offspring[i];
            mOffs.absValueInt = mqdMap[mTop.component][mOffs.row][mOffs.col];
        }
    }
    
//    private int[] tHist = new int[16];

    private void processUnit() {
        WaveletDomainPosition cTop = coeffUnit;
        WaveletDomainPosition mTop = mqdUnit;

        retrieveCoeff(cTop, true);

        if (cTop.dwtLevel == 3) {
            for (int i = 0; i < 4; ++i) {
                mTop.offspring[i].absValueInt = getMaxQOfCoeffOffspring(cTop.offspring[i]);
            }
        } else {
            retrieveMQDNodes(mTop);
        }

        int qLeaves = getMaxQOfMQDOffspring(mTop);

        if (qLeaves >= qMin) {
            for (int i = 3; i >= 0; --i) {
                WaveletDomainPosition mOffs = mTop.offspring[i];
                if (mOffs.absValueInt >= qMin) {
                    writeCoeffOffspring(cTop.offspring[i], mOffs.absValueInt);
                }
                int qMinThis = Math.max(mOffs.absValueInt, qMin);
                output.writeBits(1 << mOffs.absValueInt, qMinThis, qLeaves);
//                tHist[qLeaves - qMinThis + 1]++;
            }
        }

        if (hasMQDMapComputed) {
            mTop.absValueInt = mqdMap[mTop.component][mTop.row][mTop.col];
        } else {
            int qOffs = getMaxQOfCoeffOffspring(cTop);
            mTop.absValueInt = Math.max(qOffs, qLeaves);
            mqdMap[mTop.component][mTop.row][mTop.col] = (byte) mTop.absValueInt;
        }

        if (mTop.absValueInt >= qMin) {
            int qMinThis = Math.max(qLeaves, qMin);
            output.writeBits(1 << qLeaves, qMinThis, mTop.absValueInt);
//            tHist[mTop.absValueInt - qMinThis + 1]++;
        }
        
        coeffProcessed += 16;
    }
    
    private void writeCoeffOffspring(final WaveletDomainPosition pos, final int qMax) {
        for (int i = 3; i >= 0; --i) {
            WaveletDomainPosition cOffs = pos.offspring[i];
            if (cOffs.absValueInt > 0) {
                output.writeBit(cOffs.sign, getQMSB(cOffs.absValueInt));
            }

            output.writeBits(cOffs.absValueInt, qMin, qMax);
        }
    }
    
    private int getQMSB(int value) {
        int qMSB = 0;
        while ((value >>= 1) > 0)
            ++qMSB;
        return qMSB;
    }

    private int getMaxQOfCoeffOffspring(final WaveletDomainPosition pos) {
        int absValueIntMerged = 0;
        for (int i = 0; i < 4; ++i)
            absValueIntMerged |= pos.offspring[i].absValueInt;

        int qMax = -1;
        for (; absValueIntMerged > 0; ++qMax, absValueIntMerged >>= 1)
            ;

        return qMax;
    }

    private int getMaxQOfMQDOffspring(final WaveletDomainPosition pos) {
        int qMax = pos.offspring[0].absValueInt;
        for (int i = 1; i < 4; ++i) {
            int value = pos.offspring[i].absValueInt;
            if (qMax < value)
                qMax = value;
        }
        return qMax;
    }

    public int getOutputSizeInByte() {
        return outputStream.getCountInByte();
    }

    public byte[] getOutput() {
        return outputStream.getBuf();
    }

    public final int getNumOfCoeffToDiscardQMin() {
        return numOfCoeffToDiscardQMin;
    }

    public final void setNumOfCoeffToDiscardQMin(int numOfCoeffToDiscardQMin) {
        this.numOfCoeffToDiscardQMin = numOfCoeffToDiscardQMin;
        needToDiscardBitsForQMin = (numOfCoeffToDiscardQMin > 0);
        bitsToDiscardForQMin = Integer.MAX_VALUE;
    }

    public int getProgressiveType() {
        return progressiveType;
    }

    public void setProgressiveType(int progressiveType) {
        this.progressiveType = progressiveType;
    }

}
