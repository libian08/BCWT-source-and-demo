package y0.wavelet.bcwt.linebased;

import y0.wavelet.WTNode;

class BCWTUnit {
    	
		public static void encodeUnitBottom(WTNode coeffNode, WTNode mqdNode, int qMin, BCWTOutput sink) {
        	mqdNode.absValueInt = getQMaxOfCoeffOffspring(coeffNode);
        	if (mqdNode.absValueInt >= qMin) {
            	// Encode coeffNode.offspring[i] with (mqdNode ~ qMin)
            	writeCoeffOffspring(coeffNode, mqdNode.absValueInt, qMin, sink);
        	}
        }
    	
        public static void encodeUnitLL(WTNode coeffNode, int qMin, int qMax, BCWTOutput sink) {
			int mqd = getQMaxOfCoeffOffspring(coeffNode);
			writeDistance(qMax, mqd, qMin, sink, LBCWTOutput.TYPE_COEFF);
			
			// If this unit is significant, encode it.
			if (mqd >= qMin) {
				writeCoeffOffspring(coeffNode, mqd, qMin, sink);
			}
		}
        
        public static void encodeUnitMiddle(WTNode coeffNode, WTNode mqdNode, int qMin, BCWTOutput sink) {
           	int qLeaves = getQMaxOfMQDOffspring(mqdNode);
            mqdNode.absValueInt = Math.max(getQMaxOfCoeffOffspring(coeffNode), qLeaves);

            if (mqdNode.absValueInt >= qMin) {
            	// Encode coeffNode.offspring[i] with (mqdNode ~ qMin)
                writeCoeffOffspring(coeffNode, mqdNode.absValueInt, qMin, sink);
                
                // Encode the distances between mqdNode and its offsprings.
                {
	                // Encode (mqdNode - qLeaves)
	                writeDistance(mqdNode.absValueInt, qLeaves, qMin, sink, LBCWTOutput.TYPE_MQD);

	                if (qLeaves >= qMin) {
	                	// Encode (qLeaves - mqdNode.offspring[i])
	                	for (int i = 0; i < 4; i++) {
	                		writeDistance(qLeaves, mqdNode.offspring[i].absValueInt, qMin, sink, LBCWTOutput.TYPE_MQD);
	                	}
	                }
                }
            }
        }
        
        public static void encodeUnitTop(WTNode coeffNode, WTNode mqdNode, int qMin, int qMax, BCWTOutput sink) {
           	int qLeaves = getQMaxOfMQDOffspring(mqdNode);
            mqdNode.absValueInt = Math.max(getQMaxOfCoeffOffspring(coeffNode), qLeaves);
            
            // Encode (qMax - mqdNode)
            writeDistance(qMax, mqdNode.absValueInt, qMin, sink, LBCWTOutput.TYPE_COEFF);

            if (mqdNode.absValueInt >= qMin) {
            	// Encode coeffNode.offspring[i] with (mqdNode ~ qMin)
                writeCoeffOffspring(coeffNode, mqdNode.absValueInt, qMin, sink);
                
                // Encode the distances between mqdNode and its offsprings.
                {
                	
	                // Encode (mqdNode - qLeaves)
	                writeDistance(mqdNode.absValueInt, qLeaves, qMin, sink, LBCWTOutput.TYPE_MQD);

	                if (qLeaves >= qMin) {
	                	// Encode (qLeaves - mqdNode.offspring[i])
	                	for (int i = 0; i < 4; i++) {
	                		writeDistance(qLeaves, mqdNode.offspring[i].absValueInt, qMin, sink, LBCWTOutput.TYPE_MQD);
	                	}
	                }
                }
            }
        }
        
        static int getQMax(int value) {
            int qMSB = 0;
            while ((value >>= 1) > 0)
                ++qMSB;
            return qMSB;
        }

        private static int getQMaxOfCoeffOffspring(final WTNode coeffNode) {
            int absValueIntMerged = 0;
            for (int i = 0; i < 4; ++i)
                absValueIntMerged |= coeffNode.offspring[i].absValueInt;
            return getQMax(absValueIntMerged);
        }

        private static int getQMaxOfMQDOffspring(final WTNode mqdNode) {
            int qMax = mqdNode.offspring[0].absValueInt;
            for (int i = 1; i < 4; ++i) {
                int value = mqdNode.offspring[i].absValueInt;
                if (qMax < value)
                    qMax = value;
            }
            return qMax;
        }
        
        private static void writeCoeffOffspring(final WTNode coeffNode, final int qMax, final int qMin, BCWTOutput sink) {
            for (int i = 0; i < 4; ++i) {
                WTNode cOffs = coeffNode.offspring[i];
//                sink.writeIntMSB2LSB(cOffs.absValueInt >> qMin, qMax - qMin + 1);
                sink.writeBits(cOffs.absValueInt, qMin, qMax, LBCWTOutput.TYPE_COEFF);
                if (cOffs.absValueInt > 0) {
                    sink.writeBit(cOffs.sign, LBCWTOutput.TYPE_COEFF);
                }
            }
        }
        
        private static void writeDistance(final int upper, final int lower, final int qMin, BCWTOutput sink, int type) {
//            sink.writeIntLSB2MSB(1 << (upper - lower), upper - Math.max(lower, qMin) + 1);
//        	sink.writeBits(1 << upper, Math.max(lower, qMin), upper, type);
//        	sink.writeBits(1 << lower, Math.max(lower, qMin), upper, type);
        	if (lower < qMin) {
        		sink.writeBits(0, qMin, upper, type);
        	} else {
        		sink.writeBits(1 << upper, lower, upper, type);
        	}
        }

    }