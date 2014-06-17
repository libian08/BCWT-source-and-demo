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
public abstract class DWTLiftingDouble extends DWT {

    /** Reference to input data being processed */
    private double[] X;

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
    protected abstract void forwardTransStep(double[] X, int N);

    /**
     * Inverse transform step, to be implemented by subclasses
     * 
     * @param X
     *            Input array
     * @param N
     *            Length of input to process (only [0]~[N-1] elements will be
     *            processed.)
     */
    protected abstract void inverseTransStep(double[] X, int N);

    /** Creates a new instance of LiftingBase */
    public DWTLiftingDouble() {
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
    protected void initStep(double[] X, int N) {
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
    protected double s(int n) {
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
    protected double d(int n) {
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
    private void split(double[] X, int N) {
        double tempX[] = new double[N];
        int oneHalf = N >> 1;
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
    private void merge(double[] X, int N) {
        double tempX[] = new double[N];
        int oneHalf = N >> 1;
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
    public double[] forwardTrans(double[] data, int maxLevel) {
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
    public double[] forwardTrans(double[] data, int maxLevel, int minLevel) {
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
    public double[] forwardTrans(double[] data, int maxLevel, int minLevel,
            int N) {
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
    public double[][] forwardTrans(double[][] data, int maxLevel) {
        return forwardTrans(data, maxLevel, 1, data.length, data[0].length);
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
    public double[][] forwardTrans(double[][] data, int maxLevel, int minLevel) {
        return forwardTrans(data, maxLevel, minLevel, data.length,
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
    public double[][] forwardTrans(double[][] data, int maxLevel, int minLevel,
            int rows, int cols) {

        long startTime = System.currentTimeMillis();
        rows = rows >> (minLevel - 1);
        cols = cols >> (minLevel - 1);
        double[] tempX = new double[rows];
        int i, j;
        int oneHalf;

        for (int currentLevel = minLevel; currentLevel <= maxLevel; rows >>= 1, cols >>= 1, currentLevel++) {
            // Transforms row-wise.
            for (i = 0; i < rows; i++) {
                split(data[i], cols);
                forwardTransStep(data[i], cols);
            }

            // Transform column-wise.
            oneHalf = rows >> 1;
            for (j = 0; j < cols; j++) {
                // Splits a column and put it into tempX.
                for (i = 0; i < oneHalf; i++) {
                    tempX[i] = data[i << 1][j];
                    tempX[oneHalf + i] = data[(i << 1) + 1][j];
                }

                forwardTransStep(tempX, rows);
                for (i = 0; i < rows; i++)
                    data[i][j] = tempX[i];
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time in Java: " + (endTime - startTime));
        return data;
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
    public double[] inverseTrans(double[] data, int maxLevel) {
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
    public double[] inverseTrans(double[] data, int maxLevel, int minLevel) {
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
    public double[] inverseTrans(double[] data, int maxLevel, int minLevel,
            int N) {
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
    public double[][] inverseTrans(double[][] data, int maxLevel) {
        return inverseTrans(data, maxLevel, 1, data.length, data[0].length);
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
    public double[][] inverseTrans(double[][] data, int maxLevel, int minLevel) {
        return inverseTrans(data, maxLevel, minLevel, data.length,
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
    public double[][] inverseTrans(double[][] data, int maxLevel, int minLevel,
            int rows, int cols) {
        double[] tempX = new double[rows >> (minLevel - 1)];
        int i, j;
        int oneHalf;
        rows >>= maxLevel - 1;
        cols >>= maxLevel - 1;

        for (int currentLevel = maxLevel; currentLevel >= minLevel; rows <<= 1, cols <<= 1, currentLevel--) {
            // Inverse transform column-wise.
            for (j = 0; j < cols; j++) {
                for (i = 0; i < rows; i++)
                    tempX[i] = data[i][j];
                inverseTransStep(tempX, rows);

                // Merge the column and copy it back to data.
                oneHalf = rows >> 1;
                for (i = 0; i < oneHalf; i++) {
                    data[i << 1][j] = tempX[i];
                    data[(i << 1) + 1][j] = tempX[oneHalf + i];
                }
            }

            // Inverse transform row-wise
            for (i = 0; i < rows; i++) {
                inverseTransStep(data[i], cols);
                merge(data[i], cols);
            }
        }

        return data;
    }

}
