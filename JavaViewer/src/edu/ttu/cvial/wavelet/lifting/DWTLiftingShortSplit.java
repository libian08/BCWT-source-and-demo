/*
 * LiftingBase.java
 *
 * Created on September 8, 2004, 4:25 PM
 */

package edu.ttu.cvial.wavelet.lifting;

import edu.ttu.cvial.wavelet.DWT;

/**
 * <P>
 * This class provide a framework for Discrete Wavelet Transform (DWT) using
 * lifting scheme (1-D and 2-D). Each subclasses shall implement one of the DWTs
 * with a certain filter, and is only required to implement two methods:
 * {@link #forwardTransStep forwardTransStep} and
 * {@link #inverseTransStep inverseTransStep} to handle a single step of 1-D
 * transform. All spliting, merging, 2-D handling, and multi-level handling are
 * already implemented in this class. In addition, easy access to even and odd
 * elements, with proper odd boundary mirroring, is also provided in
 * {@link #s s} and {@link #d d} methods if speed is not the primary concern.
 * </P>
 * <P>
 * <B>References:</B>
 * </P>
 * <P>
 * "Factoring Wavelet Transforms into Lifting Steps" by Ingrid Daubchies and Wim
 * Sweldens, 1996.
 * </P>
 * <P>
 * This source code is influenced by the Java pacakage of "Basic Lifting Scheme
 * Wavelets in Java" by Ian Kaplan.
 * </P>
 * <P>
 * <B>Copyright and Use:</B>
 * </P>
 * <P>
 * You may use this source code without limitation and without fee as long as
 * you include:
 * </P>
 * <P>
 * <I>This software was written and is copyrighted by Jiangling Guo, CVIAL,
 * 2004.</I>
 * </P>
 * <P>
 * This software is provided "as is", without any warranty or claim as to its
 * usefulness. Anyone who uses this source code uses it at their own risk. Nor
 * is any support provided by the author.
 * </P>
 * 
 * @author Jiangling Guo
 * @version 1.0.0
 */
public abstract class DWTLiftingShortSplit extends DWT {

    /** Reference to input data being processed */
    private short[] X;

    /** Length of input data being processed */
    private int N;

    /**
     * Length minus 1 of the data being processed (N - 1). This field is
     * initialized by {@link #initStep initStep} and should not be changed
     * manually.
     */
    protected int N_minus_1;

    /**
     * Half the length of the data being processed (N/2). This field is
     * initialized by {@link #initStep initStep} and should not be changed
     * manually.
     */
    protected int halfN;

    /**
     * Half the length minus 1 of the data being processed (N/2 - 1). This field
     * is initialized by {@link #initStep initStep} and should not be changed
     * manually.
     */
    protected int halfN_minus_1;

    /**
     * Forward transform step, to be implemented by subclasses
     * 
     * @param X
     *            Input array
     * @param N
     *            Length of input to process (only [0]~[N-1] elements will be
     *            processed.)
     */
    protected abstract void forwardTransStep(short[] X, int N);

    /**
     * Inverse transform step, to be implemented by subclasses
     * 
     * @param X
     *            Input array
     * @param N
     *            Length of input to process (only [0]~[N-1] elements will be
     *            processed.)
     */
    protected abstract void inverseTransStep(short[] X, int N);

    /** Creates a new instance of LiftingBase */
    public DWTLiftingShortSplit() {
    }

    /**
     * Initializes some fields for quick access. Only should be called at the
     * beginning of {@link #forwardTransStep forwardTransStep} and
     * {@link #inverseTransStep inverseTransStep} if {@link #s s} and
     * {@link #d d} methods are used.
     * 
     * @param X
     *            Input array
     * @param N
     *            Length of input to process (only [0]~[N-1] elements will be
     *            processed.)
     */
    protected final void initStep(short[] X, int N) {
        this.X = X;
        this.N = N;
        N_minus_1 = N - 1;
        halfN = N >> 1;
        halfN_minus_1 = halfN - 1;
    }

    /**
     * Returns the value of an even element with boundary mirroring. This method
     * should only be used inside {@link #forwardTransStep forwardTransStep} and
     * {@link #inverseTransStep inverseTransStep}.
     * 
     * @param n
     *            Index of the even element (first half).
     * @return The value of <I>n</I>th even element, odd mirrored is necessary.
     */
    protected final short s(int n) {
        // odd mirroring for borders
        if (n > halfN_minus_1)
            n = N - n - 2;
        else if (n < 0)
            n = -n;

        return X[n];
    }

    /**
     * Returns the value of an odd element with boundary mirroring. This method
     * should only be used inside {@link #forwardTransStep forwardTransStep} and
     * {@link #inverseTransStep inverseTransStep}.
     * 
     * @param n
     *            Index of the odd element (last half).
     * @return The value of <I>n</I>th odd element, odd mirrored is necessary.
     */
    protected final short d(int n) {
        // odd mirroring for borders
        if (n > halfN_minus_1)
            n = N - n - 2;
        else if (n < 0)
            n = -n;

        return X[n + halfN];
    }

    /**
     * Splits the odd and even parts of input data. Example: [e,o,e,o,e,o,...] ->
     * [e,e,e,...,o,o,o,...]
     * 
     * @param X
     *            Input array
     * @param N
     *            Length of input to process (only [0]~[N-1] elements will be
     *            processed.)
     */
    private final void split(final short[] X, final int N) {
        short tempX[] = new short[N];
        final int oneHalf = N >> 1;
        int i;
        for (i = 0; i < oneHalf; i++) {
            tempX[i] = X[i << 1];
            tempX[oneHalf + i] = X[(i << 1) + 1];
        }
        for (i = 0; i < N; i++) {
            X[i] = tempX[i];
        }
        tempX = null;
    }

    /**
     * Merges the even and odd parts. Example: [e,e,e,...,o,o,o,...] ->
     * [e,o,e,o,e,o,...]
     * 
     * @param X
     *            Input array
     * @param N
     *            Length of input to process (only [0]~[N-1] elements will be
     *            processed.)
     */
    private final void merge(final short[] X, final int N) {
        short tempX[] = new short[N];
        final int oneHalf = N >> 1;
        int i;
        for (i = 0; i < oneHalf; i++) {
            tempX[i << 1] = X[i];
            tempX[(i << 1) + 1] = X[oneHalf + i];
        }
        for (i = 0; i < N; i++) {
            X[i] = tempX[i];
        }
        tempX = null;
    }

    /**
     * Forward transforms (1D) from 1 to maxLevel. Transform is in-place, which
     * means input data are overwritten by transformed data.
     * 
     * @param data
     *            Input array
     * @param maxLevel
     *            Maximum level of transform (shall be >= 1)
     * @return Reference to the input/transformed data
     */
    public short[] forwardTrans(short[] data, int maxLevel) {
        return forwardTrans(data, maxLevel, 1, data.length);
    }

    /**
     * Forward transforms (1D) from minLevel to maxLevel, skipping transforms
     * from 1 to (minLevel - 1). Transform is in-place, which means input data
     * are overwritten by transformed data.
     * 
     * @param data
     *            Input array
     * @param maxLevel
     *            Maximum level of transform (shall be >= minLevel)
     * @param minLevel
     *            Minimum level of transform (shall be >= 1)
     * @return Reference to the input/transformed data
     */
    public short[] forwardTrans(short[] data, int maxLevel, int minLevel) {
        return forwardTrans(data, maxLevel, minLevel, data.length);
    }

    /**
     * Forward transforms (1D) from minLevel to maxLevel, skipping transforms
     * from 1 to (minLevel - 1). Transform is in-place, which means input data
     * are overwritten by transformed data.
     * 
     * @param data
     *            Input array
     * @param maxLevel
     *            Maximum level of transform (shall be >= minLevel)
     * @param minLevel
     *            Minimum level of transform (shall be >= 1)
     * @param N
     *            Length of input to process (only [0]~[N-1] elements will be
     *            processed.)
     * @return Reference to the input/transformed data
     */
    public short[] forwardTrans(short[] data, int maxLevel, int minLevel, int N) {
        for (int currentLevel = minLevel; currentLevel <= maxLevel; N >>= 1, currentLevel++) {
            split(data, N);
            forwardTransStep(data, N);
        }
        return data;
    }

    /**
     * Forward transforms (2D) from 1 to maxLevel. Transform is in-place, which
     * means input data are overwritten by transformed data.
     * 
     * @param data
     *            Input array
     * @param maxLevel
     *            Maximum level of transform (shall be >= 1)
     * @return Reference to the input/transformed data
     */
    public short[][] forwardTrans(short[][] data, int maxLevel) {
        return forwardTransFast(data, maxLevel, 1, data.length, data[0].length);
    }

    /**
     * Forward transforms (2D) from minLevel to maxLevel, skipping transforms
     * from 1 to (minLevel - 1). Transform is in-place, which means input data
     * are overwritten by transformed data.
     * 
     * @param data
     *            Input array
     * @param maxLevel
     *            Maximum level of transform (shall be >= minLevel)
     * @param minLevel
     *            Minimum level of transform (shall be >= 1)
     * @return Reference to the input/transformed data
     */
    public short[][] forwardTrans(short[][] data, int maxLevel, int minLevel) {
        return forwardTransFast(data, maxLevel, minLevel, data.length,
                data[0].length);
    }

    /**
     * Forward transforms (2D) from minLevel to maxLevel, skipping transforms
     * from 1 to (minLevel - 1). Transform is in-place, which means input data
     * are overwritten by transformed data.
     * 
     * @param data
     *            Input array
     * @param maxLevel
     *            Maximum level of transform (shall be >= minLevel)
     * @param minLevel
     *            Minimum level of transform (shall be >= 1)
     * @param rows
     *            Number of rows to process
     * @param cols
     *            Number of cols to process
     * @return Reference to the input/transformed data
     */
    public short[][] forwardTrans(short[][] data, int maxLevel, int minLevel,
            int rows, int cols) {
        rows = rows >> (minLevel - 1);
        cols = cols >> (minLevel - 1);
        short[] data1D = new short[rows > cols ? rows : cols];
        int i, j, a;
        int oneHalf;
        short[] thisRow;

        for (int currentLevel = minLevel; currentLevel <= maxLevel; rows >>= 1, cols >>= 1, currentLevel++) {
            // Transforms row-wise.
            for (i = 0; i < rows; i++) {
                // Splits a row and put it into data1D.
                thisRow = data[i];
                oneHalf = cols >> 1;
                for (j = 0, a = 0; j < oneHalf; j++) {
                    data1D[j] = thisRow[a++];
                    data1D[oneHalf + j] = thisRow[a++];
                }
                forwardTransStep(data1D, cols);
                
                for (j = 0; j < cols; j++)
                    thisRow[j] = data1D[j];
            }

            // Transform column-wise.
            oneHalf = rows >> 1;
            for (j = 0; j < cols; j++) {
                // Splits a column and put it into data1D.
                for (i = 0, a = 0; i < oneHalf; i++) {
                    data1D[i] = data[a++][j];
                    data1D[oneHalf + i] = data[a++][j];
                }

                forwardTransStep(data1D, rows);

                for (i = 0; i < rows; i++)
                    data[i][j] = data1D[i];
            }
        }

        return data;
    }
    
    public short[][] forwardTransFast(short[][] data, int maxLevel,
            int minLevel, int rows, int cols) {
        rows = rows >> (minLevel - 1);
        cols = cols >> (minLevel - 1);
        int maxLen = rows > cols ? rows : cols;
        short[] data1D0 = new short[maxLen];
        short[] data1D1 = new short[maxLen];
        short[] data1D2 = new short[maxLen];
        short[] data1D3 = new short[maxLen];
        int r, c, o, e;
        short[] thisRow;

        for (int currentLevel = minLevel; currentLevel <= maxLevel; rows >>= 1, cols >>= 1, currentLevel++) {
            int rowsHalf = rows >> 1;
            int colsHalf = cols >> 1;

            // Transforms row-wise.
            for (r = 0; r < rows; ++r) {
                // Splits a row.
                thisRow = data[r];
                for (e = 0, o = colsHalf, c = 0; e < colsHalf; ++e, ++o) {
                    data1D0[e] = thisRow[c++];
                    data1D0[o] = thisRow[c++];
                }
                forwardTransStep(data1D0, cols);

                for (c = 0; c < cols; c++)
                    thisRow[c] = data1D0[c];
            }

            // Transform column-wise.
            for (int c0 = cols - 1, c1 = cols - 2, c2 = cols - 3, c3 = cols - 4; c0 > 2; c0 -= 4, c1 -= 4, c2 -= 4, c3 -= 4) {
                // Splits columns.
                for (e = 0, o = rowsHalf, r = 0; e < rowsHalf; ++e, ++o) {
                    thisRow = data[r];
                    data1D0[e] = thisRow[c0];
                    data1D1[e] = thisRow[c1];
                    data1D2[e] = thisRow[c2];
                    data1D3[e] = thisRow[c3];
                    thisRow = data[++r];
                    data1D0[o] = thisRow[c0];
                    data1D1[o] = thisRow[c1];
                    data1D2[o] = thisRow[c2];
                    data1D3[o] = thisRow[c3];
                    ++r;
                }

                forwardTransStep(data1D0, rows);
                forwardTransStep(data1D1, rows);
                forwardTransStep(data1D2, rows);
                forwardTransStep(data1D3, rows);

                for (r = 0; r < rows; ++r) {
                    thisRow = data[r];
                    thisRow[c0] = data1D0[r];
                    thisRow[c1] = data1D1[r];
                    thisRow[c2] = data1D2[r];
                    thisRow[c3] = data1D3[r];
                }
            }
        }

        return data;
    }

    public short[][][][] forwardTransSpecial(short[][] data, int maxLevel,
            int minLevel, int rows, int cols) {
        
        // Initialize subbands.
        short[][][][] subbands = new short[maxLevel + 1][][][];
        for (int l = minLevel; l <= maxLevel; l++) {
            subbands[l] = new short[4][][];
        }
        
        rows = rows >> (minLevel - 1);
        cols = cols >> (minLevel - 1);
        int maxLen = rows > cols ? rows : cols;
        short[] data1D0 = new short[maxLen];
        short[] data1D1 = new short[maxLen];
        short[] data1D2 = new short[maxLen];
        short[] data1D3 = new short[maxLen];
        int r, c, o, e;
        short[] thisRow;
        short[] coefs = new short[4];
        
        final int SUBBAND_LL = 0;
        final int SUBBAND_LH = 1;
        final int SUBBAND_HL = 2;
        final int SUBBAND_HH = 3;

        for (int currentLevel = minLevel; currentLevel <= maxLevel; rows >>= 1, cols >>= 1, currentLevel++) {
            int rowsHalf = rows >> 1;
            int colsHalf = cols >> 1;
            
            boolean isMaxLevel = currentLevel == maxLevel;
            short[][][] thisSubbandLevel = subbands[currentLevel];
            for (int s = (isMaxLevel ? 0 : 1); s < 4; s++)
                thisSubbandLevel[s] = new short[rowsHalf][colsHalf];

            // Transforms row-wise.
            for (r = 0; r < rows; ++r) {
                // Splits a row.
                thisRow = data[r];
                for (e = 0, o = colsHalf, c = 0; e < colsHalf; ++e, ++o) {
                    data1D0[e] = thisRow[c++];
                    data1D0[o] = thisRow[c++];
                }
                forwardTransStep(data1D0, cols);

                for (c = 0; c < cols; c++)
                    thisRow[c] = data1D0[c];
            }

            // Transform column-wise.
            for (int c0 = cols - 1, c1 = cols - 2, c2 = cols - 3, c3 = cols - 4
                    ; c0 > 2; c0 -= 4, c1 -= 4, c2 -= 4, c3 -= 4) {
                boolean isCLower = c0 < colsHalf;
                // Splits columns.
                for (e = 0, o = rowsHalf, r = 0; e < rowsHalf; ++e, ++o) {
                    thisRow = data[r];
                    data1D0[e] = thisRow[c0];
                    data1D1[e] = thisRow[c1];
                    data1D2[e] = thisRow[c2];
                    data1D3[e] = thisRow[c3];
                    thisRow = data[++r];
                    data1D0[o] = thisRow[c0];
                    data1D1[o] = thisRow[c1];
                    data1D2[o] = thisRow[c2];
                    data1D3[o] = thisRow[c3];
                    ++r;
                }

                forwardTransStep(data1D0, rows);
                forwardTransStep(data1D1, rows);
                forwardTransStep(data1D2, rows);
                forwardTransStep(data1D3, rows);

                for (r = 0; r < rows; ++r) {
                    boolean isRLower = r < rowsHalf;
                    
                    if (isCLower && isRLower) {
                        if (isMaxLevel) {
                            setSubbandCoeff(thisSubbandLevel[SUBBAND_LL][r],
                                    c0, data1D0[r], data1D1[r], data1D2[r],
                                    data1D3[r]);
                        } else {
                            thisRow = data[r];
                            thisRow[c0] = data1D0[r];
                            thisRow[c1] = data1D1[r];
                            thisRow[c2] = data1D2[r];
                            thisRow[c3] = data1D3[r];
                        }
                    } else if (isCLower && !isRLower) {
                        setSubbandCoeff(thisSubbandLevel[SUBBAND_LH][r
                                - rowsHalf], c0, data1D0[r], data1D1[r],
                                data1D2[r], data1D3[r]);
                    } else if (!isCLower && isRLower) {
                        setSubbandCoeff(thisSubbandLevel[SUBBAND_HL][r], c0
                                - colsHalf, data1D0[r], data1D1[r], data1D2[r],
                                data1D3[r]);
                    } else {
                        setSubbandCoeff(thisSubbandLevel[SUBBAND_HH][r
                                - rowsHalf], c0 - colsHalf, data1D0[r],
                                data1D1[r], data1D2[r], data1D3[r]);
                    }
                }
            }
        }

        return subbands;
    }
    
    private final void setSubbandCoeff(short[] subbandRow, int colBase
            , short coef0, short coef1, short coef2, short coef3) {
        subbandRow[colBase--] = (short) coef0;
        subbandRow[colBase--] = (short) coef1;
        subbandRow[colBase--] = (short) coef2;
        subbandRow[colBase--] = (short) coef3;
    }


    /**
     * Inverse transforms (1D) from maxLevel to 1. Transform is in-place, which
     * means input data are overwritten by transformed data.
     * 
     * @param data
     *            Input array
     * @param maxLevel
     *            Maximum level of transform (shall be >= 1)
     * @return Reference to the input/transformed data
     */
    public short[] inverseTrans(short[] data, int maxLevel) {
        return inverseTrans(data, maxLevel, 1, data.length);
    }

    /**
     * Inverse transforms (1D) from maxLevel to 1. Transform is in-place, which
     * means input data are overwritten by transformed data.
     * 
     * @param data
     *            Input array
     * @param maxLevel
     *            Maximum level of transform (shall be >= minLevel)
     * @param minLevel
     *            Minimum level of transform (shall be >= 1)
     * @return Reference to the input/transformed data
     */
    public short[] inverseTrans(short[] data, int maxLevel, int minLevel) {
        return inverseTrans(data, maxLevel, minLevel, data.length);
    }

    /**
     * Inverse transforms (1D) from maxLevel to minLevel, skipping transforms
     * from (minLevel - 1) to 1. Transform is in-place, which means input data
     * are overwritten by transformed data.
     * 
     * @param data
     *            Input array
     * @param maxLevel
     *            Maximum level of transform (shall be >= minLevel)
     * @param minLevel
     *            Minimum level of transform (shall be >= 1)
     * @param N
     *            Length of input to process (only [0]~[N-1] elements will be
     *            processed.)
     * @return Reference to the input/transformed data X
     */
    public short[] inverseTrans(short[] data, int maxLevel, int minLevel, int N) {
        N = N >> (maxLevel - 1);

        for (int currentLevel = maxLevel; currentLevel >= minLevel; N <<= 1, currentLevel--) {
            inverseTransStep(data, N);
            merge(data, N);
        }
        return data;
    }

    /**
     * Inverse transforms (2D) from maxLevel to 1. Transform is in-place, which
     * means input data are overwritten by transformed data.
     * 
     * @param data
     *            Input array
     * @param maxLevel
     *            Maximum level of transform (shall be >= 1)
     * @return Reference to the input/transformed data
     */
    public short[][] inverseTrans(short[][] data, int maxLevel) {
        return inverseTransFast(data, maxLevel, 1, data.length, data[0].length);
    }

    /**
     * Inverse transforms (2D) from maxLevel to minLevel, skipping transforms
     * from (minLevel - 1) to 1. Transform is in-place, which means input X are
     * overwritten by transformed data.
     * 
     * @param data
     *            Input array
     * @param maxLevel
     *            Maximum level of transform (shall be >= minLevel)
     * @param minLevel
     *            Minimum level of transform (shall be >= 1)
     * @return Reference to the input/transformed data
     */
    public short[][] inverseTrans(short[][] data, int maxLevel, int minLevel) {
        return inverseTransFast(data, maxLevel, minLevel, data.length,
                data[0].length);
    }

    /**
     * Inverse transforms (2D) from maxLevel to minLevel, skipping transforms
     * from (minLevel - 1) to 1. Transform is in-place, which means input X are
     * overwritten by transformed data.
     * 
     * @param data
     *            Input array
     * @param rows
     *            Number of rows to process
     * @param cols
     *            Number of cols to process
     * @param maxLevel
     *            Maximum level of transform (shall be >= minLevel)
     * @param minLevel
     *            Minimum level of transform (shall be >= 1)
     * @return Reference to the input/transformed data
     */
    public short[][] inverseTrans(short[][] data, int maxLevel, int minLevel,
            int rows, int cols) {
        short[] data1D = new short[(rows > cols ? rows : cols) >> (minLevel - 1)];
        int i, j, a;
        int oneHalf;
        rows >>= maxLevel - 1;
        cols >>= maxLevel - 1;

        for (int currentLevel = maxLevel; currentLevel >= minLevel; rows <<= 1, cols <<= 1, currentLevel--) {
            // Inverse transform column-wise.
            for (j = 0; j < cols; j++) {
                for (i = 0; i < rows; i++)
                    data1D[i] = data[i][j];
                inverseTransStep(data1D, rows);

                // Merge the column and copy it back to data.
                oneHalf = rows >> 1;
                for (i = 0, a = 0; i < oneHalf; i++) {
                    data[a++][j] = data1D[i];
                    data[a++][j] = data1D[oneHalf + i];
                }
            }

            // Inverse transform row-wise
            for (i = 0; i < rows; i++) {
                inverseTransStep(data[i], cols);
                short[] thisRow = data[i];
                oneHalf = cols >> 1;
                for (j = 0, a = 0; j < oneHalf; j++) {
                    data1D[a++] = thisRow[j];
                    data1D[a++] = thisRow[oneHalf + j];
                }
                for (j = 0; j < cols; j++) {
                    thisRow[j] = data1D[j];
                }
            }
        }

        return data;
    }
    
    public short[][] inverseTransFast(short[][] data, int maxLevel, int minLevel
            , int rows, int cols) {
        int maxLen = (rows > cols ? rows : cols) >> (minLevel - 1);
        short[] data1D0 = new short[maxLen];
        short[] data1D1 = new short[maxLen];
        short[] data1D2 = new short[maxLen];
        short[] data1D3 = new short[maxLen];
        short[] thisRow;
        int r, c, o, e;

        rows >>= maxLevel - 1;
        cols >>= maxLevel - 1;
        for (int currentLevel = maxLevel; currentLevel >= minLevel
            ; rows <<= 1, cols <<= 1, currentLevel--) {
            int rowsHalf = rows >> 1;
            int colsHalf = cols >> 1;
            
            // Inverse transform column-wise.
            for (int c0 = cols - 1, c1 = cols - 2, c2 = cols - 3, c3 = cols - 4
                ; c0 > 2; c0 -= 4, c1 -= 4, c2 -= 4, c3 -= 4) {
                for (r = 0; r < rows; r++) {
                    thisRow = data[r];
                    data1D0[r] = thisRow[c0];
                    data1D1[r] = thisRow[c1];
                    data1D2[r] = thisRow[c2];
                    data1D3[r] = thisRow[c3];
                }
                inverseTransStep(data1D0, rows);
                inverseTransStep(data1D1, rows);
                inverseTransStep(data1D2, rows);
                inverseTransStep(data1D3, rows);

                // Merge the column and copy it back to data.
                for (e = 0, o = rowsHalf, r = 0; e < rowsHalf; e++, o++) {
                    thisRow = data[r];
                    thisRow[c0] = data1D0[e];
                    thisRow[c1] = data1D1[e];
                    thisRow[c2] = data1D2[e];
                    thisRow[c3] = data1D3[e];
                    thisRow = data[++r];
                    thisRow[c0] = data1D0[o];
                    thisRow[c1] = data1D1[o];
                    thisRow[c2] = data1D2[o];
                    thisRow[c3] = data1D3[o];
                    ++r;
                }
            }

            // Inverse transform row-wise
            for (r = 0; r < rows; r++) {
                inverseTransStep(data[r], cols);
                thisRow = data[r];
                for (e = 0, o = colsHalf, c = 0; e < colsHalf; e++, o++) {
                    data1D0[c++] = thisRow[e];
                    data1D0[c++] = thisRow[o];
                }
                for (c = 0; c < cols; c++) {
                    thisRow[c] = data1D0[c];
                }
            }
        }
        return data;
    }


}
