/*
 * DWT97.java
 *
 * Created on September 8, 2004, 4:15 PM
 */

package edu.ttu.cvial.wavelet.lifting;

/**
 * <P>
 * 9-7 DWT using lifting scheme.
 * </P>
 * 
 * @author Jiangling Guo
 * @version 1.0.0
 */
final public class DWT97Float extends DWTLiftingFloat {

    /** Constants used in formulas */
    private final static float alpha = -1.586134342f;

    private final static float beta = -0.05298011854f;

    private final static float gamma = 0.8829110762f;

    private final static float delta = 0.4435068522f;

    private final static float K = 1.149604398f;

    private final static float K_inverse = 1.0f / K;

    /** Creates a new instance of DWT97 */
    public DWT97Float() {
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

        initStep(X, N);

        // step 1
        for (n = 0; n < halfN; n++)
            X[n + halfN] += alpha * (s(n) + s(n + 1));
        // step 2
        for (n = 0; n < halfN; n++)
            X[n] += beta * (d(n) + d(n - 1));
        // step 3
        for (n = 0; n < halfN; n++)
            X[n + halfN] += gamma * (s(n) + s(n + 1));
        // step 4
        for (n = 0; n < halfN; n++)
            X[n] += delta * (d(n) + d(n - 1));
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
    protected void inverseTransStep(float[] X, int N) {
        int n;

        initStep(X, N);

        // step 5
        for (n = 0; n < halfN; n++) {
            X[n + halfN] *= K;
            X[n] *= K_inverse;
        }
        // step 4
        for (n = 0; n < halfN; n++)
            X[n] -= delta * (d(n) + d(n - 1));
        // step 3
        for (n = 0; n < halfN; n++)
            X[n + halfN] -= gamma * (s(n) + s(n + 1));
        // step 2
        for (n = 0; n < halfN; n++)
            X[n] -= beta * (d(n) + d(n - 1));
        // step 1
        for (n = 0; n < halfN; n++)
            X[n + halfN] -= alpha * (s(n) + s(n + 1));
    }

}
