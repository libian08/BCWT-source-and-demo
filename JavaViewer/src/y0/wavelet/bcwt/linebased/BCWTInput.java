package y0.wavelet.bcwt.linebased;

public interface BCWTInput extends BCWTIO {

	public abstract boolean readBit(int type);

	public abstract int readBits(int qLower, int qUpper, int type);

}