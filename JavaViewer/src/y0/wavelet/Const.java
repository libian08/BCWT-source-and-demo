package y0.wavelet;

public class Const {

    public final static int BAND_HL = 0;
    public final static int BAND_LH = 1;
    public final static int BAND_HH = 2;
    public final static int BAND_LL = 3;
    public final static String[] BAND_NAMES = {"HL", "LH", "HH", "LL"};
    public final static int BAND_NUM = BAND_NAMES.length;

    // BCWT/SPIHT constants
    public final static float DWT97_ALPHA = -1.586134342f;
    public final static float DWT97_BETA = -0.05298011854f;
    public final static float DWT97_GAMMA = 0.8829110762f;
    public final static float DWT97_DELTA = 0.4435068522f;
    public final static float DWT97_K = 1.0f / 1.149604398f;
//  public final static float DWT97_K = 1.149604398f;
//    public final static float DWT97_K = 1f;

    // JPEG2000's constants
//    public final static float DWT97_ALPHA = -1.586134342f;
//    public final static float DWT97_BETA = -0.052980118f;
//    public final static float DWT97_GAMMA = 0.882911075f;
//    public final static float DWT97_DELTA = 0.443506852f;
//    public final static float DWT97_K = 1.230174105f;

    // Other DWT-related constants
    public final static float DWT97_ALPHA2 = DWT97_ALPHA * 2.0f;
    public final static float DWT97_BETA2 = DWT97_BETA * 2.0f;
    public final static float DWT97_GAMMA2 = DWT97_GAMMA * 2.0f;
    public final static float DWT97_DELTA2 = DWT97_DELTA * 2.0f;
    public final static float DWT97_K_INVERSE = 1.0f / DWT97_K;
}
