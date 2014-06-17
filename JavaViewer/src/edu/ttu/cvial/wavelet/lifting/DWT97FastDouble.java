/*
 * DWT97FastDouble.java
 *
 * Created on September 8, 2004, 4:15 PM
 */

package edu.ttu.cvial.wavelet.lifting;

/**
 * <P>
 * 9-7 DWT using lifting scheme (a faster implementation). This class takes care
 * of boundary mirroring directly without using {@link #s s} and {@link #d d},
 * thus performs faster than {@link DWT97}.
 * </P>
 * 
 * @author Jiangling Guo
 * @version 1.0.0
 */
final public class DWT97FastDouble extends DWTLiftingDouble {

    /** Constants used in formulas */
    private final static double alpha = -1.586134342;

    private final static double beta = -0.05298011854;

    private final static double gamma = 0.8829110762;

    private final static double delta = 0.4435068522;

    private final static double K = 1.149604398;

    private final static double K_inverse = 1.0 / K;

    /** Creates a new instance of DWT97Fast */
    public DWT97FastDouble() {
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
    protected void forwardTransStep(double[] X, int N) {
        int n;

        int N_minus_1 = N - 1;
        int halfN = N >> 1;
        int halfN_minus_1 = halfN - 1;

        // step 1
        for (n = 0; n < halfN_minus_1; n++)
            X[n + halfN] += alpha * (X[n] + X[n + 1]);
        X[N_minus_1] += alpha * (X[halfN_minus_1] + X[halfN - 2]);

        // step 2
        X[0] += beta * (X[halfN] + X[halfN + 1]);
        for (n = 1; n < halfN; n++)
            X[n] += beta * (X[halfN + n] + X[halfN_minus_1 + n]);

        // step 3
        for (n = 0; n < halfN_minus_1; n++)
            X[n + halfN] += gamma * (X[n] + X[n + 1]);
        X[N_minus_1] += gamma * (X[halfN_minus_1] + X[halfN - 2]);

        // step 4
        X[0] += delta * (X[halfN] + X[halfN + 1]);
        for (n = 1; n < halfN; n++)
            X[n] += delta * (X[halfN + n] + X[halfN_minus_1 + n]);

        // step 5
        for (n = 0; n < halfN; n++) {
            X[n] *= K;
            X[n + halfN] *= K_inverse;
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
    protected void inverseTransStep(double[] X, int N) {
        int n;

        int N_minus_1 = N - 1;
        int halfN = N >> 1;
        int halfN_minus_1 = halfN - 1;

        // step 5
        for (n = 0; n < halfN; n++) {
            X[n + halfN] *= K;
            X[n] *= K_inverse;
        }

        // step 4
        X[0] -= delta * (X[halfN] + X[halfN + 1]);
        for (n = 1; n < halfN; n++)
            X[n] -= delta * (X[halfN + n] + X[halfN_minus_1 + n]);

        // step 3
        for (n = 0; n < halfN_minus_1; n++)
            X[n + halfN] -= gamma * (X[n] + X[n + 1]);
        X[N_minus_1] -= gamma * (X[halfN_minus_1] + X[halfN - 2]);

        // step 2
        X[0] -= beta * (X[halfN] + X[halfN + 1]);
        for (n = 1; n < halfN; n++)
            X[n] -= beta * (X[halfN + n] + X[halfN_minus_1 + n]);

        // step 1
        for (n = 0; n < halfN_minus_1; n++)
            X[n + halfN] -= alpha * (X[n] + X[n + 1]);
        X[N_minus_1] -= alpha * (X[halfN_minus_1] + X[halfN - 2]);
    }

}
