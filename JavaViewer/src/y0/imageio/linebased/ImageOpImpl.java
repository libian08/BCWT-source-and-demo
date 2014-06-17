package y0.imageio.linebased;

import java.util.Hashtable;

public abstract class ImageOpImpl implements ImageOp {
	protected int height;
    protected int width;
    protected int componentNum;
    
    protected Hashtable props;
    protected Hashtable param;
    protected Hashtable result;

    protected ImageOp source;
    protected ImageOp sink;
    
    protected boolean hasInitialized = false;
    protected boolean isOnline = false;
    
    protected boolean doPullPropsFromSource = true;
    protected boolean doPushPropsToSink = false;
    
    protected boolean doPullParamFromSource = true;
    protected boolean doPushParamToSink = false;

    protected boolean doPullResultFromSource = true;
    protected boolean doPushResultToSink = false;

    protected boolean doInitChainInvoke = true;
    protected boolean doFinishChainInvoke = true;
    
    protected boolean doSetBidirectionLink = true;
    
	public Hashtable getParam() {
		return param;
	}
	
	public Hashtable getProperties() {
		return props;
	}

	public Hashtable getResult() {
		return result;
	}

    public Object getProperty(String name) {
		return props.get(name);
	}

	public ImageOp getSink() {
		return sink;
	}
	
	public ImageOp getSource() {
		return source;
	}
	
	public void initialize(ImageOp caller) {
		hasInitialized = true;
		
		if (doInitChainInvoke) {
			if (source != null && caller != source)
				source.initialize(this);
			if (sink != null && caller != sink)
				sink.initialize(this);
		}
	}

	public void online(ImageOp caller) {
		isOnline = true;
		
		if (doInitChainInvoke) {
			if (source != null && caller != source)
				source.online(this);
			if (sink != null && caller != sink)
				sink.online(this);
		}
	}

	public void setParam(Hashtable param) {
		this.param = param;
	}
	
	public void setResult(Hashtable result) {
		this.result = result;
	}
	
	public void setProperties(Hashtable props) {
		this.width = ImageProp.getWidth(props);
		this.height = ImageProp.getHeight(props);
		this.componentNum = ImageProp.getComponentNum(props);
		this.props = props;
	}
	
	public void setSink(ImageOp sink) {
    	this.sink = sink;
    	
    	if (sink != null) {
    		if (doPushPropsToSink)
    			sink.setProperties(props);
    		
    		if (doPushParamToSink)
    			sink.setParam(param);

    		if (doPushResultToSink)
    			sink.setResult(result);
    		
    		if (doSetBidirectionLink) {
	    		if (sink.getSource() != this)
	    			sink.setSource(this);
    		}
    	}
    }

	public void setSource(ImageOp source) {
		this.source = source;
		
		if (source != null) {
			if (doPullPropsFromSource)
				setProperties(source.getProperties());
			
			if (doPullParamFromSource)
				setParam(source.getParam());

			if (doPullResultFromSource)
				setResult(source.getResult());

			if (doSetBidirectionLink) {
				if (source.getSink() != this)
					source.setSink(this);
			}
		}
    }

	public void finish(ImageOp caller) {
		isOnline = false;
		
		if (doFinishChainInvoke) {
			if (source != null && caller != source)
				source.finish(this);
			if (sink != null && caller != sink)
				sink.finish(this);
		}
		
	}

	public boolean hasInitialized() {
		return hasInitialized;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public float getProgress() {
		return 0;
	}
}
