package y0.imageio.linebased;

public class DataOutbox implements DataSender {
	private DataReceiver receiver;
	private int waitingForACK;
	private int tag;
	
	public boolean isEmpty() {
		return (getWaitingForACK() == 0);
	}
	public void send(Object data, int offset, int len, int dataType) {
		receiver.receiveData(this, data, offset, len, tag, dataType);
		waitingForACK++;
	}
//	public void sendAs(DataSender sender, Object data, int offset, int len, int dataType) {
//		receiver.receiveData(sender, data, offset, len, tag, dataType);
////		waitingForACK++;
//	}
	public void forward(DataInbox.Item incoming) {
		receiver.receiveData(incoming.sender, incoming.data, incoming.offset, incoming.len, incoming.tag, incoming.dataType);
	}
	public void ackData(int tag) {
		waitingForACK--;
	}
	public void sendEOF() {
		receiver.receiveData(this, null, 0, 0, tag, DataReceiver.TYPE_UNKNOWN);
	}

	public void setSendTo(DataReceiver receiver) {
		this.receiver = receiver;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getTag() {
		return tag;
	}
	
	public int getWaitingForACK() {
		return waitingForACK;
	}

}
