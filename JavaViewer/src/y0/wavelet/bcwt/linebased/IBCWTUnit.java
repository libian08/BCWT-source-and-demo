package y0.wavelet.bcwt.linebased;

import y0.wavelet.WTNode;

class IBCWTUnit {

	private int qMin;
	private float refinementFactor;
	private int refinement;
	
	public WTNode mqdNode;
	public WTNode coeffNode;

	private BCWTInput input;

	IBCWTUnit() {
		setQMin(0);
		setRefinementFactor(0.375f);
		
        // Initialize coding unit.
        coeffNode = new WTNode();
        mqdNode = new WTNode();
        WTNode cTop = coeffNode;
        WTNode mTop = mqdNode;
        cTop.offspring = new WTNode[4];
        mTop.offspring = new WTNode[4];
        for (int i = 0; i < 4; ++i) {
            cTop.offspring[i] = new WTNode();
            mTop.offspring[i] = new WTNode();
        }
	}

	public void decodeUnitTopOrLL(int qMax) {
		mqdNode.absValueInt = readDistance(qMax, BCWTInput.TYPE_COEFF);
		if (mqdNode.absValueInt >= qMin) {
			decodeCoeffOffspring();
		}
	}

	public void decodeMQD() {
		WTNode.initOffspring(mqdNode, mqdNode.offspring);
		if (mqdNode.absValueInt >= qMin) {
			// Decode (mTop - qLeaves)
			int qLeaves = readDistance(mqdNode.absValueInt, BCWTInput.TYPE_MQD);
			if (qLeaves >= qMin) {

				// Decode (qLeaves - mTop.offspring[i])
				for (int i = 0; i < 4; i++) {
					mqdNode.offspring[i].absValueInt = readDistance(qLeaves, BCWTInput.TYPE_MQD);
				}
			}
		}
	}
	
	public void decodeCoeffOffspring() {
		WTNode.initOffspring(coeffNode, coeffNode.offspring);
		for (int i = 0; i < 4; ++i) {
			WTNode cOffs = coeffNode.offspring[i];
			cOffs.absValueInt = input.readBits(qMin, mqdNode.absValueInt, BCWTInput.TYPE_COEFF);
			if (cOffs.absValueInt > 0) {
				cOffs.sign = input.readBit(BCWTInput.TYPE_COEFF);
				cOffs.absValueInt |= refinement;
			}
		}
	}

	private int readDistance(final int qUpper, int type) {
		int q;
		for (q = qUpper; q >= qMin; q--) {
			if (input.readBit(type))
				break;
		}
		if (q < qMin)
			q = -1;
		return q;
	}

	public void setQMin(int qMin) {
		this.qMin = qMin;
		setRefinementFactor(refinementFactor);
	}

	public void setRefinementFactor(float factor) {
		refinementFactor = factor;
		refinement = (int) (refinementFactor * (1 << qMin) + 0.5f);
	}

	public void setInput(BCWTInput input) {
		this.input = input;
	}

}
