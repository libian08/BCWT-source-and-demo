package y0.imageio.linebased;

public interface DataReceiver {

	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_FLOAT = 1;
	public static final int TYPE_SHORT = 2;
	public static final int TYPE_BYTE = 3;
	
	public abstract void receiveData(DataSender source, Object data,
			int offset, int len, int tag, int dataType);

}
