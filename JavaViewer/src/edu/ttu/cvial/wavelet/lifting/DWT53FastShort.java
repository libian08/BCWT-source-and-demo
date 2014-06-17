package edu.ttu.cvial.wavelet.lifting;

public class DWT53FastShort extends DWTLiftingShort {
    
    private static final float CONST1_2 = 0.5f;
    private static final float CONST1_4 = 0.25f;

    protected void forwardTransStep(short[] X, int N) {

        int n;
        int ik = 1;
        int hk = 0;
        int lk = 0;
        final int N_minus_1 = N - 1;
        final int halfN = N >> 1;
        final int halfN_minus_1 = halfN - 1;

        for (n = 1; n < N_minus_1; n += 2) {
            hx[hk] = (short) (X[ik] - ((X[ik - 1] + X[ik + 1]) >> 1));
            ik += 2;
            ++hk;
        }
        if (N % 2 == 0) {
            hx[hk] = (short) (X[ik] - ((2 * X[ik - 1]) >> 1));
        }

        ik = 0;
        hk = 0;
        lk = 0;

        lx[lk] = (short) (X[ik] + ((hx[hk] + 1) >> 1));
        ik += 2;
        ++lk;
        ++hk;

        for (n = 2; n < N - 1; n += 2) {
            lx[lk] = (short) (X[ik] + ((hx[hk - 1] + hx[hk] + 2) >> 2));
            ik += 2;
            ++lk;
            ++hk;
        }
        if (N % 2 == 1) {
            lx[lk] = (short) (X[ik] + ((2 * hx[hk - 1] + 2) >> 2));
        }

        System.arraycopy(lx, 0, X, 0, halfN);
        System.arraycopy(hx, 0, X, halfN, halfN);

    }

    protected void inverseTransStep(short[] X, int N) {

        int n;
        final int N_minus_1 = N - 1;
        final int halfN = N >> 1;
        final int halfN_minus_1 = halfN - 1;

        int ik = 0;
        int lk = 0;
        int hk = 0;

        temp[ik] = (short) (X[lk] - ((X[halfN + hk] + 1) >> 1));
        lk += 1;
        hk += 1;
        ik += 2;

        for (n = 2; n < N_minus_1; n += 2) {
            temp[ik] = (short) (X[lk] - ((X[halfN + hk - 1] + X[halfN + hk] + 2) >> 2));
            lk += 1;
            hk += 1;
            ik += 2;
        }
        if (N % 2 == 1) {
            temp[ik] = (short) (X[lk] - ((2 * X[halfN + hk - 1] + 2) >> 2));
        }
        hk = 0;
        ik = 1;
        for (n = 1; n < N_minus_1; n += 2) {
            temp[ik] = (short) (X[halfN + hk] + ((temp[ik - 1] + temp[ik + 1]) >> 1));
            hk += 1;
            ik += 2;
        }
        if (N % 2 == 0) {
            temp[ik] = (short) (X[halfN + hk] + temp[ik - 1]);
        }
        System.arraycopy(temp, 0, X, 0, N);
    }

}
