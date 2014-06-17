/*
 * Daub4Fast.java
 *
 * Created on September 8, 2004, 4:15 PM
 */

package edu.ttu.cvial.wavelet.lifting;

/**
 * <P>
 * Daub4 DWT using lifting scheme (a faster implementation). This class takes
 * care of boundary mirroring directly without using {@link #s s} and
 * {@link #d d}, thus performs faster than {@link Daub4}.
 * </P>
 * 
 * @author Jiangling Guo
 * @version 1.0.0
 */
public class Daub4Fast extends DWTLiftingFloat {

    /** Constants used in formulas */
    private final static float sqrt3 = (float) Math.sqrt(3.0);

    private final static float sqrt2 = (float) Math.sqrt(2.0);

    private final static float a1 = (float) ((sqrt3 - 1.0) / sqrt2);

    private final static float a2 = (float) ((sqrt3 + 1.0) / sqrt2);

    private final static float a3 = (float) (sqrt3 / 4.0);

    private final static float a4 = (float) ((sqrt3 - 2.0) / 4.0);

    /** Creates a new instance of Daub4Fast */
    public Daub4Fast() {
    }

    /**
     * Forward transform step
     * 
     * @param X
     *            Input array
     * @param N
     *            Length of input to process (only [0]~[N-1] elements will be
     *            processed.)
     */
    protected void forwardTransStep(float[] X, int N) {
        int n;

        // step 1
        for (n = 0; n < halfN; n++)
            X[n + halfN] -= sqrt3 * X[n];

        // step 2
        for (n = 0; n < halfN_minus_1; n++)
            X[n] += a3 * X[n + halfN] + a4 * X[n + halfN + 1];
        X[halfN_minus_1] += a3 * X[N_minus_1] + a4 * X[N - 2];

        // step 3
        X[halfN] += X[1];
        for (n = 1; n < halfN; n++)
            X[n + halfN] += X[n - 1];

        // step 4
        for (n = 0; n < halfN; n++) {
            X[n] *= a2;
            X[n + halfN] *= a1;
        }
    }

    /**
     * Inverse transform step
     * 
     * @param X
     *            Input array
     * @param N
     *            Length of input to process (only [0]~[N-1] elements will be
     *            processed.)
     */
    protected void inverseTransStep(float[] X, int N) {
        int n;

        // step 4
        for (n = 0; n < halfN; n++) {
            X[n + halfN] *= a2;
            X[n] *= a1;
        }

        // step 3
        X[halfN] -= X[1];
        for (n = 1; n < halfN; n++)
            X[n + halfN] -= X[n - 1];

        // step 2
        for (n = 0; n < halfN_minus_1; n++)
            X[n] -= a3 * X[n + halfN] + a4 * X[n + halfN + 1];
        X[halfN_minus_1] -= a3 * X[N_minus_1] + a4 * X[N - 2];

        // step 1
        for (n = 0; n < halfN; n++)
            X[n + halfN] += sqrt3 * X[n];
    }
}