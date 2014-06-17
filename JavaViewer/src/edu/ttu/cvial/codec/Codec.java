package edu.ttu.cvial.codec;

import java.util.EventListener;

public interface Codec {
    
    public interface CodecEventListener extends EventListener {
        public abstract void codingStarted(Codec source);
        public abstract void codingProgress(Codec source, float percentageDone);
        public abstract void codingComplete(Codec source);
    }

    public abstract CodecEventListener getEventListener();

	public abstract void setEventListener(CodecEventListener eventListener);

	public abstract void processCodingStarted();

	public abstract void processCodingProgress(float percentageDone);

	public abstract void processCodingComplete();
}
