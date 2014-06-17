package edu.ttu.cvial.codec;

public abstract class CodecImpl implements Codec {
    
	private CodecEventListener eventListener = null;

	public CodecEventListener getEventListener() {
		return eventListener;
	}

	public void setEventListener(CodecEventListener eventListener) {
		this.eventListener = eventListener;
	}

	public void processCodingStarted() {
		if (this.eventListener != null)
			eventListener.codingStarted(this);
	}

	public void processCodingProgress(float percentageDone) {
		if (this.eventListener != null)
			eventListener.codingProgress(this, percentageDone);
	}

	public void processCodingComplete() {
		if (this.eventListener != null)
			eventListener.codingComplete(this);
	}
}
