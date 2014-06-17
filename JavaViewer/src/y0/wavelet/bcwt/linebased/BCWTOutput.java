package y0.wavelet.bcwt.linebased;

public interface BCWTOutput extends BCWTIO {

	public abstract void writeBit(boolean bit, int type);

	public abstract void writeBits(int bits, int qMin, int qMax, int type);

}