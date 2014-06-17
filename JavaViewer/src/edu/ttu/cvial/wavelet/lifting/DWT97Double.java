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
final public class DWT97Double extends DWTLiftingDouble {

    /** Constants used in formulas */
    private final static double alpha = -1.586134342;

    private final static double beta = -0.05298011854;

    private final static double gamma = 0.8829110762;

    private final static double delta = 0.4435068522;

    private final static double K = 1.149604398;

    private final static double K_inverse = 1.0 / K;

    /** Creates a new instance of DWT97 */
    public DWT97Double() {
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
    protected void inverseTransStep(double[] X, int N) {
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
