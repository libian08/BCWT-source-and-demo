package y0.imageio.linebased;

import java.util.Vector;

public class DataInbox extends Vector implements DataReceiver {

	private static final long serialVersionUID = -6086381696618699779L;
	
	public class Item {
		public DataSender sender;
		public Object data;
		public int dataType;
		public int offset;
		public int len;
		public int tag;
		
		public Item(DataSender sender, Object data, int offset, int len, int tag, int dataType) {
			super();
			this.data = data;
			this.dataType = dataType;
			this.len = len;
			this.offset = offset;
			this.sender = sender;
			this.tag = tag;
		}
		
		public boolean isEOF() {
			return data == null;
		}
	}
	
	public synchronized Item poll() {
		Item item;
		
		// In J2ME MIDP 2.0, no "remove()" method.
		item = (Item) this.firstElement();
		this.removeElementAt(0);
		
		return item;
	}

	public synchronized void receiveData(DataSender sender, Object data, int offset,
			int len, int tag, int dataType) {
		Item item = new Item(sender, data, offset, len, tag, dataType);
		add(item);
	}
}
