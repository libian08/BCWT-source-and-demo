/*
 * Daub4.java
 *
 * Created on September 8, 2004, 4:15 PM
 */

package edu.ttu.cvial.wavelet.lifting;

/**
 * <P>
 * Daub4 DWT using lifting scheme.
 * </P>
 * 
 * @author Jiangling Guo
 * @version 1.0.0
 */
public class Daub4 extends DWTLiftingFloat {

    /** Constants used in formulas */
    private final static float sqrt3 = (float) Math.sqrt(3.0);

    private final static float sqrt2 = (float) Math.sqrt(2.0);

    private final static float a1 = (float) ((sqrt3 - 1.0) / sqrt2);

    private final static float a2 = (float) ((sqrt3 + 1.0) / sqrt2);

    private final static float a3 = (float) (sqrt3 / 4.0);

    private final static float a4 = (float) ((sqrt3 - 2.0) / 4.0);

    /** Creates a new instance of Daub4 */
    public Daub4() {
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
            X[n + halfN] -= sqrt3 * s(n);

        // step 2
        for (n = 0; n < halfN; n++)
            X[n] += a3 * d(n) + a4 * d(n + 1);

        // step 3
        for (n = 0; n < halfN; n++)
            X[n + halfN] += s(n - 1);

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
        for (n = 0; n < halfN; n++)
            X[n + halfN] -= s(n - 1);

        // step 2
        for (n = 0; n < halfN; n++)
            X[n] -= a3 * d(n) + a4 * d(n + 1);

        // step 1
        for (n = 0; n < halfN; n++)
            X[n + halfN] += sqrt3 * s(n);
    }
}
