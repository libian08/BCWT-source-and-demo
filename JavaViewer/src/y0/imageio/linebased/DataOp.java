package y0.imageio.linebased;

public interface DataOp {
	
	public abstract DataSender getOutputPins(int tag);
	public abstract DataReceiver getInputPins(int tag);
	
//	public abstract void setSink(DataOp sink);
//	public abstract void setSource(DataOp source);
//	
//	public abstract void connectToSink();
//	public abstract void connectToSource();
}
