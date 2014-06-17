package y0.imageio.linebased;

public interface DataSender {

	public abstract void ackData(int tag);
	public abstract void setSendTo(DataReceiver receiver);

}