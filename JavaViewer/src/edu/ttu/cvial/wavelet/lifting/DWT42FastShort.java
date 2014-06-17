package edu.ttu.cvial.wavelet.lifting;

public class DWT42FastShort extends DWTLiftingShort{
    private static final double CONST9_16 = 9.0/16.0;
    private static final double CONST1_16 = 1.0/16.0;
    private static final double CONST1_4 = 0.25;
    private static final double CONST1_2 = 0.5;
    
	protected  void forwardTransStep(short[] X, int N){
        int n;
        int ik=1;
        int hk=0;
        int lk=0;

        final int halfN = N >> 1;
        final int halfN_minus_2 = halfN - 2;
        
        hx[hk] = (short) (X[ik] - (int) Math
                .floor((CONST9_16 * (X[ik - 1] + X[ik + 1]) - CONST1_16
                        * (X[ik - 1] + X[ik + 3]) + CONST1_2)));
        ik += 2;
        hk++;
        
        for (n = 1; n < halfN_minus_2; n++){
            hx[hk] = (short) (X[ik] - (int) Math.floor(CONST9_16
                    * (X[ik - 1] + X[ik + 1]) - CONST1_16
                    * (X[ik - 3] + X[ik + 3]) + CONST1_2));
        	ik += 2;
        	hk++;
        }
        
        hx[hk] = (short) (X[ik] - (int) Math.floor(CONST9_16
                * (X[ik - 1] + X[ik + 1]) - CONST1_16 * (X[ik - 3] + X[ik + 1])
                + CONST1_2));
        ik += 2;
        hk++;
        
        hx[hk] = (short) (X[ik] - (int) Math.floor(CONST9_16 * 2 * X[ik - 1]
                - CONST1_16 * 2 * X[ik - 3] + CONST1_2));
        
        ik=0;hk=0;lk=0;
        
        lx[lk] = (short) (X[ik] + (int) Math.floor(
                CONST1_4 * 2 * hx[hk] + CONST1_2));
        ik += 2;
        lk++;
        hk++;
        
        for(n = 2; n < N-1; n +=2){
            lx[lk] = (short) (X[ik] + (int) Math.floor(CONST1_4
                    * (hx[hk - 1] + hx[hk]) + CONST1_2));
            ik += 2;
            lk++;
            hk++;
        }
        
        System.arraycopy(lx,0, X, 0, halfN);
        System.arraycopy(hx,0, X, halfN, halfN);       
        		
	}
	protected  void inverseTransStep(short[] X, int N){
        int n;
        final int N_minus_1 = N - 1;
        final int halfN = N >> 1;
        final int halfN_minus_2 = halfN - 2;
        
        int ik = 0;
        int lk = 0;
        int hk = 0;
        
        temp[ik] = (short) (X[lk] - (int) Math.floor(CONST1_4 * 2
                * X[halfN + hk] + CONST1_2));
        lk++;
        hk++;
        ik += 2;
        
        for ( n = 2; n < N_minus_1; n += 2) {
            temp[ik] = (short) (X[lk] - (int) Math.floor(CONST1_4
                    * (X[halfN + hk - 1] + X[halfN + hk]) + CONST1_2));
            lk++;
            hk++;
            ik +=2;
        }
        if (N % 2 ==1) {
            temp[ik] = (short) (X[lk] - (int) Math.floor(CONST1_4 * 2
                    * X[halfN + hk - 1] + CONST1_2));
        }
        
        hk = 0;
        ik = 1;
        
        temp[ik] = (short) (X[halfN + hk] + (int) Math.floor(CONST9_16
                * (temp[ik - 1] + temp[ik + 1]) - CONST1_16
                * (temp[ik - 1] + temp[ik + 3]) + CONST1_2));
        hk++;
        ik += 2;
        
        for ( n = 1; n < halfN_minus_2; n ++ ) {
            temp[ik] = (short) (X[halfN + hk] + (int) Math.floor(CONST9_16
                    * (temp[ik - 1] + temp[ik + 1]) - CONST1_16
                    * (temp[ik - 3] + temp[ik + 3]) + CONST1_2));
            hk++;
            ik += 2;
        }
        temp[ik] = (short) (X[halfN + hk] + (int) Math.floor(CONST9_16
                * (temp[ik - 1] + temp[ik + 1]) - CONST1_16
                * (temp[ik - 3] + temp[ik + 1]) + CONST1_2));
        hk++;
        ik += 2;
        temp[ik] = (short) (X[halfN + hk] + (int) Math.floor(CONST9_16
                * (temp[ik - 1] + temp[ik - 1]) - CONST1_16
                * (temp[ik - 3] + temp[ik - 3]) + CONST1_2));
        System.arraycopy(temp, 0, X, 0, N); 
	}
}

