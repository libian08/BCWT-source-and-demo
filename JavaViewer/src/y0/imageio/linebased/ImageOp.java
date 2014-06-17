package y0.imageio.linebased;

import java.util.Hashtable;

import y0.utils.TaskGroup;

public interface ImageOp extends DataOp, TaskGroup {

	public abstract ImageOp getSink();

	public abstract ImageOp getSource();

	public abstract void setSink(ImageOp sink);

	public abstract void setSource(ImageOp source);

	public abstract Hashtable getProperties();
	
	public abstract Hashtable getResult();

	public abstract Object getProperty(String name);

	public abstract void setProperties(Hashtable props);

	public abstract void setParam(Hashtable param);
	
	public abstract void setResult(Hashtable result);

	public abstract Hashtable getParam();

	public abstract void initialize(ImageOp caller);
	
	public abstract void online(ImageOp caller);
	
	public abstract void finish(ImageOp caller);
	
	public abstract boolean hasInitialized();
	
	public abstract boolean isOnline();
	
	public abstract float getProgress();

}