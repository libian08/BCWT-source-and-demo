/*
 * DWT97Fast.java
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
final public class DWT97FastShort extends DWTLiftingShortSplit {

    /** Constants used in formulas */
    private final static float alpha = -1.586134342f;

    private final static float beta = -0.05298011854f;

    private final static float gamma = 0.8829110762f;

    private final static float delta = 0.4435068522f;

    private final static float K = 1.149604398f;

    private final static float K_inverse = 1.0f / K;

    /** Creates a new instance of DWT97Fast */
    public DWT97FastShort() {
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
    protected final void forwardTransStep(final short[] X, final int N) {
        int n;

        final int N_minus_1 = N - 1;
        final int halfN = N >> 1;
        final int halfN_minus_1 = halfN - 1;

        // step 1
        for (n = 0; n < halfN_minus_1; n++)
            X[n + halfN] += Math.round(alpha * (X[n] + X[n + 1]));
        X[N_minus_1] += Math.round(alpha * (X[halfN_minus_1] + X[halfN - 2]));

        // step 2
        X[0] += Math.round(beta * (X[halfN] + X[halfN + 1]));
        for (n = 1; n < halfN; n++)
            X[n] += Math.round(beta * (X[halfN + n] + X[halfN_minus_1 + n]));

        // step 3
        for (n = 0; n < halfN_minus_1; n++)
            X[n + halfN] += Math.round(gamma * (X[n] + X[n + 1]));
        X[N_minus_1] += Math.round(gamma * (X[halfN_minus_1] + X[halfN - 2]));

        // step 4
        X[0] += Math.round(delta * (X[halfN] + X[halfN + 1]));
        for (n = 1; n < halfN; n++)
            X[n] += Math.round(delta * (X[halfN + n] + X[halfN_minus_1 + n]));

        // step 5
//        for (n = 0; n < halfN; n++) {
//            X[n] *= K;
//            X[n + halfN] *= K_inverse;
//        }
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
    protected final void inverseTransStep(final short[] X, final int N) {
        int n;

        final int N_minus_1 = N - 1;
        final int halfN = N >> 1;
        final int halfN_minus_1 = halfN - 1;

//        // step 5
//        for (n = 0; n < halfN; n++) {
//            X[n + halfN] *= K;
//            X[n] *= K_inverse;
//        }

        // step 4
        X[0] -= Math.round(delta * (X[halfN] + X[halfN + 1]));
        for (n = 1; n < halfN; n++)
            X[n] -= Math.round(delta * (X[halfN + n] + X[halfN_minus_1 + n]));

        // step 3
        for (n = 0; n < halfN_minus_1; n++)
            X[n + halfN] -= Math.round(gamma * (X[n] + X[n + 1]));
        X[N_minus_1] -= Math.round(gamma * (X[halfN_minus_1] + X[halfN - 2]));

        // step 2
        X[0] -= Math.round(beta * (X[halfN] + X[halfN + 1]));
        for (n = 1; n < halfN; n++)
            X[n] -= Math.round(beta * (X[halfN + n] + X[halfN_minus_1 + n]));

        // step 1
        for (n = 0; n < halfN_minus_1; n++)
            X[n + halfN] -= Math.round(alpha * (X[n] + X[n + 1]));
        X[N_minus_1] -= Math.round(alpha * (X[halfN_minus_1] + X[halfN - 2]));
    }

}
