package edu.ttu.cvial.codec.sq.bcwt;

import java.text.DecimalFormat;
import java.util.Properties;

import edu.ttu.cvial.arrayop.ArrayOp;
import edu.ttu.cvial.imageio.ImageCoeff;
import edu.ttu.cvial.util.storage.BitStream;

public class BCWTDecoderCore extends BCWTCoreBase {
    
    private abstract class Input {
        public static final int SIZE_BITPLANE = 32;

        public abstract void setInput(BitStream stream);
        public abstract boolean readBit(int q);
        public abstract int getCountTotalReadBits();
        public abstract int[] getQHist();
        public abstract int getNumOfBitsReadAtQMinTarget();
        public abstract void init();
        
        protected int[] readQHist(BitStream stream) {
            int[] qHist = new int[qMax + 1];
            for (int q = qMax; q >= 0; q--) {
                qHist[q] = stream.getIntFast(SIZE_BITPLANE);
            }
            return qHist;
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

    private class InputBitStream extends Input {
        private BitStream stream;
        private int countTotalReadBits;
        private int[] qHist;
        private int numOfBitsReadAtQMinTarget;

        public void setInput(BitStream stream) {
            qHist = readQHist(stream);
            this.stream = stream;
        }
        
        public void init() {
            int offsetBits = stream.getIntFast(8);
            stream.setPosInBit(stream.getPosInBit() + offsetBits);
        }
        
        public boolean readBit(int q) {
            if (q >= qMinTarget) {
                countTotalReadBits++;
                if (q == qMinTarget) {
                    numOfBitsReadAtQMinTarget++;
                }
            }
            return stream.getBitFast();
        }
        
        public int getCountTotalReadBits() {
            return countTotalReadBits;
        }

        public int[] getQHist() {
            return qHist;
        }

        public int getNumOfBitsReadAtQMinTarget() {
            return numOfBitsReadAtQMinTarget;
        }
    }
    
    private class InputBitplaneStream extends Input {
        
        private BitStream[] streamBitplane;
        private int countTotalReadBits;
        private int[] qHist;
        private int numOfBitsReadAtQMinTarget;
        private BitStream stream;
        
        public boolean readBit(int q) {
            boolean bit = false;
            if (q >= qMinTarget) {
                countTotalReadBits++;
                if (q == qMinTarget) {
                    numOfBitsReadAtQMinTarget++;
                }
                bit = streamBitplane[q].getBitFast();
            }
            return bit;
        }

        public void setInput(BitStream stream) {
            countTotalReadBits = 0;
            qHist = readQHist(stream);
            this.stream = stream;
        }
        
        public void init() {
            if (qMinTarget > qMin) {
                setQMin(qMinTarget);
                setQMinTransitionInCoeff(0);
            }
            
            streamBitplane = new BitStream[qMax + 1];
            int dataPos = stream.getPosInByte();
            for (int q = qMax; q >= qMin; --q) {
                if (qHist[q] == 0) {
                    streamBitplane[q] = null;
                } else {
                    int sizeInByte = qHist[q] / 8;
                    int offsetBits = 8 - (qHist[q] % 8);
                    if (offsetBits == 8) {
                        offsetBits = 0;
                    } else {
                        sizeInByte++;
                    }

                    streamBitplane[q] = BitStream.slice(stream, dataPos, sizeInByte);
                    streamBitplane[q].setPosInBit(offsetBits);
                    dataPos += sizeInByte;
                }
            }
        }

        public int getCountTotalReadBits() {
            return countTotalReadBits;
        }

        public int[] getQHist() {
            return qHist;
        }
        
        public int getNumOfBitsReadAtQMinTarget() {
            return numOfBitsReadAtQMinTarget;
        }

    }

    private ImageCoeff coeff;
    private int numOfComponents;
    private int numOfRows;
    private int numOfCols;
    
    private Input input;
    
    private byte[][][] mqdMap;
    private int mqdMapComponents;
    private int mqdMapRows;
    private int mqdMapCols;

    private WaveletDomainPosition coeffUnit;
    private WaveletDomainPosition mqdUnit;
    
    private byte[][] qMaxOfSubbands;
    private int qMax;
    private int qMin;
    private int qMinMask;
    private int dwtLevel;
    private int dwtLevelTarget;
    private float refinementFactor;
    private float refinement;
    
    private boolean hasInit;
    private boolean hasReset;
    
    private boolean needToChangeQMin;
    private int qMinTransitionInCoeff;
    private int coeffProcessed;
    
    private int qMinTarget;
    private int qMinTargetMask;
    private boolean needToChangeQMinTarget;
    private int qMinTargetTransitionInCoeff;

    private int bitsTarget;
    private int qMinTargetTransitionInBits;

    BCWTDecoderCore() {
        hasInit = false;
        hasReset = false;
        refinementFactor = 0.375f;
        qMinTarget = -1;
        
        needToChangeQMin = false;
        needToChangeQMinTarget = false;
        qMinTargetTransitionInBits = Integer.MAX_VALUE;
        qMinTargetTransitionInCoeff = Integer.MAX_VALUE;
    }
    
    public int getCountInputBits() {
        return input.getCountTotalReadBits();
    }
    
    public void setQMaxOfSubbands(byte[][] qMaxOfSubbands) {
        this.qMaxOfSubbands = qMaxOfSubbands;
    }
    
    public void setLogLevel(int logLevel) {
    }
    
    public void setRefinementFactor(float factor) {
        refinementFactor = factor;
    }
    
    public void setImage(ImageCoeff coeff) {
        this.coeff = coeff;
        
        Properties prop = coeff.getPropertySet(ImageCoeff.PROPSET_PADDED);
        numOfComponents = Integer.parseInt(prop.getProperty(ImageCoeff.PROP_BANDS));
        numOfRows = Integer.parseInt(prop.getProperty(ImageCoeff.PROP_HEIGHT));
        numOfCols = Integer.parseInt(prop.getProperty(ImageCoeff.PROP_WIDTH));
    }
    
    public void setInputStream(BitStream stream, int progressiveType) {
        switch (progressiveType) {
        case PROGRESSIVE_RESOLUTION:
            input = new InputBitStream();
            break;
        case PROGRESSIVE_QUALITY:
            input = new InputBitplaneStream();
            break;
        default:
            String msg = "Unknown progressive type: " + progressiveType;
            logger.severe(msg);
            throw new IllegalArgumentException(msg);
        }
        input.setInput(stream);
        
        logger.info("Progressive type: " + progressiveType);
    }
    
    private BitStream inputStream;
    private int progressiveType;
    
    public ImageCoeff getImage() {
        return coeff;
    }
    
    public void setQMin(int qMin) {
        this.qMin = qMin;
        qMinMask = (-1 << qMin);
        calcRefinement();
    }

    public void setQMinTarget(int qMinTarget) {
        this.qMinTarget = qMinTarget;
        qMinTargetMask = (-1 << qMinTarget);
        calcRefinement();
    }

    public void setQMax(final int qMax) {
        this.qMax = qMax;
    }
    
    public void setDwtLevel(final int dwtLevel) {
        this.dwtLevel = dwtLevel;
    }

    public void setDwtLevelTarget(final int dwtLevelTarget) {
        this.dwtLevelTarget = dwtLevelTarget;
    }
    
    public void start() {
        init();
        
        processLLBand();
        logger.info("coeffProcessed = " + coeffProcessed);

        if (dwtLevelTarget <= dwtLevel) {
            processSubbandRoots();
        
            if (dwtLevelTarget < dwtLevel) {
                processSubbands();
            }
        }
        logger.info("coeffProcessed = " + coeffProcessed);
    }

    private void processLLBand() {
        int cpStart = 0;
        int cpEnd = mqdMapComponents;
        int rStart = 0;
        int rEnd = numOfRows >> dwtLevel;
        int cStart = 0;
        int cEnd = numOfCols >> dwtLevel;
        for (int cp = cpStart; cp < cpEnd; ++cp) {
            for (int r = rStart; r < rEnd; ++r) {
                for (int c = cStart; c < cEnd; ++c) {
                    int absValueInt = readBits(qMin, qMaxOfSubbands[cp][0]);
                    if (absValueInt > 0) {
                        boolean sign = input.readBit(getQMSB(absValueInt));
                        absValueInt &= qMinTargetMask;
                        if (absValueInt > 0) {
                            coeff.setCoeff(c, r, cp, sign ? (absValueInt + refinement) 
                                    : -(absValueInt + refinement));
                        }
                    }
                    
                    coeffProcessed++;
                    checkTransition();
                }
            }
        }
    }
    
    private void processSubbandRoots() {
        WaveletDomainPosition cTop = coeffUnit;

        cTop.dwtLevel = dwtLevel + 1;

        int cpStart = 0;
        int cpEnd = numOfComponents;
        int rStart = 0;
        int rEnd = numOfRows >> dwtLevel;
        int cStart = 0;
        int cEnd = numOfCols >> dwtLevel;
        int rEndHalf = rEnd >> 1;
        int cEndHalf = cEnd >> 1;
        for (int cp = cpStart; cp < cpEnd; ++cp) {
            cTop.component = cp;
            for (int r = rStart; r < rEnd; ++r) {
                cTop.row = r;
                cStart = r < rEndHalf ? cEndHalf : 0;
                for (int c = cStart; c < cEnd; ++c) {
                    cTop.col = c;
                    int qMaxThis = r < rEndHalf ? qMaxOfSubbands[cp][2] 
                        : (c < cEndHalf ? qMaxOfSubbands[cp][1] 
                            : qMaxOfSubbands[cp][3]);
                    int qMQD = readZeros(qMin, qMaxThis);
                    mqdMap[cp][r][c] = (byte) qMQD;
                    if (qMQD >= qMin) {
                        retrieveCoeff(cTop, false);
                        readCoeffOffspring(cTop, qMin, qMQD, qMinTargetMask);
                    }
                    
                    coeffProcessed += 4;
                    checkTransition();
                }
            }
        }
    }
    
    private int readZeros(final int qMin, final int qMax) {
        int q;
        for (q = qMax; q >= qMin; q--) {
            if (input.readBit(q))
                break;
        }
        if (q < qMin)
            q = -1;
        return q;
    }

    private void processSubbands() {
        WaveletDomainPosition cTop = coeffUnit;
        WaveletDomainPosition mTop = mqdUnit;

        int cpStart = 0;
        int cpEnd = numOfComponents;
        int rStart = 0;
        int rEnd = numOfRows >> dwtLevel;
        int cStart = 0;
        int cEnd = numOfCols >> dwtLevel;
        for (int n = dwtLevel + 1; n >= dwtLevelTarget + 2; --n) {
            cTop.dwtLevel = n;
            mTop.dwtLevel = n;
            
            int rEndHalf = rEnd >> 1;
            int cEndHalf = cEnd >> 1;
            for (int cp = cpStart; cp < cpEnd; ++cp) {
                cTop.component = cp;
                mTop.component = cp;
    
                for (int r = rStart; r < rEnd; ++r) {
                    cTop.row = r;
                    mTop.row = r;
                    
                    cStart = r < rEndHalf ? cEndHalf : 0;
                    for (int c = cStart; c < cEnd; ++c) {
                        cTop.col = c;
                        mTop.col = c;
                        
                        cTop.qMin = qMin;
                        
                        processUnit(cTop, mTop);
                        coeffProcessed += 16;
                        checkTransition();
                    }
                }
            }
            rEnd <<= 1;
            cEnd <<= 1;
            
        }
    }

    private void checkTransition() {
        if (needToChangeQMin) {
            if (coeffProcessed >= qMinTransitionInCoeff) {
                setQMin(qMin + 1);
                needToChangeQMin = false;
                logger.info("qmin changed to " + qMin + 
                        " after coeff " + coeffProcessed);
            }
        }
        if (needToChangeQMinTarget) {
            if (coeffProcessed >= qMinTargetTransitionInCoeff) {
                setQMinTarget(qMinTarget + 1);
                needToChangeQMinTarget = false;
                qMinTargetTransitionInCoeff = coeffProcessed;
                logger.info("qMinTarget changed to " + qMinTarget + 
                        " after coeff " + coeffProcessed);
            } else if (input.getNumOfBitsReadAtQMinTarget() >= qMinTargetTransitionInBits) {
                setQMinTarget(qMinTarget + 1);
                needToChangeQMinTarget = false;
                qMinTargetTransitionInCoeff = coeffProcessed;
                logger.info("qMinTarget changed to " + qMinTarget + 
                        " after bits " + input.getNumOfBitsReadAtQMinTarget());
            }
        }
    }

    private void calcRefinement() {
        refinement = refinementFactor * (1 << Math.max(qMin, qMinTarget));
    }
    
    private void init() {
        if (bitsTarget > 0) {
            findQMinTargetForGivenBitsTarget();
        }
        if (qMinTarget < 0) {
            setQMinTarget(qMin);
        } else if (qMinTarget < qMin) {
            logger.warning("qMinTarget < qMin");
        }
        logger.info("qMinTarget is " + qMinTarget);

        if (!hasInit) {
            // Note that MQD map will be larger than 1/16 of the coeff size if
            // there are lesser than 2 levels. 
            mqdMapRows = dwtLevel > 1 ? numOfRows >> 2 : numOfRows;
            mqdMapCols = dwtLevel > 1 ? numOfCols >> 2 : numOfCols;
            mqdMapComponents = numOfComponents;
            mqdMap = ArrayOp.newByte3D(mqdMapComponents, mqdMapRows, mqdMapCols, -1);
            
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
            
            input.init();
            
            hasInit = true;
        }
    }
    
    private void retrieveCoeff(final WaveletDomainPosition cTop, boolean isNotRoot) {
        WaveletDomainPosition.getOffspring(cTop, cTop.offspring);
        for (int i = 0; i < 4; ++i) {
            WaveletDomainPosition cOffs = cTop.offspring[i];
            
            if (isNotRoot) {
                WaveletDomainPosition.getOffspring(cOffs, cOffs.offspring);
            }
        }
    }
    
    private void processUnit(WaveletDomainPosition cTop, WaveletDomainPosition mTop) {

        int qLeaves = -1;
        mTop.absValueInt = mqdMap[mTop.component][mTop.row][mTop.col];

        if (mTop.absValueInt >= cTop.qMin) {
            qLeaves = readZeros(cTop.qMin, mTop.absValueInt);
            if (qLeaves >= cTop.qMin) {
                retrieveCoeff(cTop, true);

                WaveletDomainPosition.getOffspring(mTop, mTop.offspring);
                for (int i = 0; i < 4; ++i) {
                    WaveletDomainPosition mOffs = mTop.offspring[i];
                    mOffs.absValueInt = readZeros(cTop.qMin, qLeaves);
                    if (cTop.dwtLevel > 3)
                        mqdMap[cTop.component][mOffs.row][mOffs.col] = (byte) mOffs.absValueInt;
                    if (mOffs.absValueInt >= cTop.qMin) {
                        readCoeffOffspring(cTop.offspring[i], cTop.qMin,
                                mOffs.absValueInt, qMinTargetMask);
                    }
                }
            }
        }
        
    }
    
    private void readCoeffOffspring(final WaveletDomainPosition pos, int qMin, int qMax, int qMinTargetMask) {
        for (int i = 0; i < 4; ++i) {
            WaveletDomainPosition cOffs = pos.offspring[i];
            cOffs.absValueInt = readBits(qMin, qMax);
            if (cOffs.absValueInt > 0) {
                cOffs.sign = input.readBit(getQMSB(cOffs.absValueInt));
                cOffs.absValueInt &= qMinTargetMask;
                if (cOffs.absValueInt > 0) {
                  coeff.setCoeff(cOffs.col, cOffs.row, coeffUnit.component,
                            cOffs.sign ? (cOffs.absValueInt + refinement)
                                    : -(cOffs.absValueInt + refinement));
                }
            }
        }
    }
    
    private int readBits(final int qMin, final int qMax) {
        int bits = 0;
        for (int q = qMax, offset = 1 << qMax; q >= qMin; --q, offset >>= 1) {
            if (input.readBit(q))
                bits |= offset;
        }
        return bits;
    }
    
    public final void setQMinTransitionInCoeff(int transitionOfQMin) {
        this.qMinTransitionInCoeff = transitionOfQMin;
        needToChangeQMin = (transitionOfQMin > 0);
    }

    public final void setQMinTargetTransitionInCoeff(int transitionOfQMinTarget) {
        this.qMinTargetTransitionInCoeff = transitionOfQMinTarget;
        needToChangeQMinTarget |= (transitionOfQMinTarget > 0);
    }

    public int getQMin() {
        return qMin;
    }

    public int getQMinTarget() {
        return qMinTarget;
    }

    public int getQMinTransitionInCoeff() {
        return qMinTransitionInCoeff;
    }

    public int getQMinTargetTransitionInCoeff() {
        return qMinTargetTransitionInCoeff;
    }
    
    private int getQMSB(int value) {
        int qMSB = 0;
        while ((value >>= 1) > 0)
            ++qMSB;
        return qMSB;
    }
    

    private void findQMinTargetForGivenBitsTarget() {
        int[] qHist = input.getQHist();
        input.showQHist();
        
        int qMinTargetTransitionInBits = bitsTarget;
        int qMinTarget = qMax;
        boolean needToChangeQMinTarget = false;
        
        logger.info("Finding decoding parameters for given bitrate.");
        while (true) {
            if (qMinTargetTransitionInBits > qHist[qMinTarget]) {
                qMinTargetTransitionInBits -= qHist[qMinTarget];
                if (--qMinTarget < qMin) {
                    needToChangeQMinTarget = false;
                    qMinTargetTransitionInBits = Integer.MAX_VALUE;
                    logger.warning("Target bitrate cannot be reached when qMin >= " + qMin);
                    break;
                }
            }
            else {
                logger.info("qMinTarget for target bitrate is : " + qMinTarget);
    
                if (qMinTargetTransitionInBits > 0) {
                    needToChangeQMinTarget = true;
                    logger.info("bits to process at qMin: " + qMinTargetTransitionInBits);
                }
                else {
                    needToChangeQMinTarget = false;
                    qMinTargetTransitionInBits = Integer.MAX_VALUE;
                    logger.info("All bits need to be processed at qMin.");
                }
                break;
            }
        }
        
        // Apply the found parameters.
        if (needToChangeQMinTarget && ((this.needToChangeQMinTarget && this.qMinTarget <= qMinTarget)
                || !(this.needToChangeQMinTarget))) {
            this.needToChangeQMinTarget = true;
            this.setQMinTarget(qMinTarget);
            this.qMinTargetTransitionInBits = qMinTargetTransitionInBits;
            logger.info("Parameters applied.");
        } else {
            logger.info("Parameters discarded.");
        }
    }

    public int getBitsTarget() {
        return bitsTarget;
    }

    public void setBitsTarget(int bitsTarget) {
        this.bitsTarget = bitsTarget;
    }

}
