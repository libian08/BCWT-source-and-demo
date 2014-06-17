/*
 * Created on Nov 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.ttu.cvial.util;

/**
 * @author eg-guo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BitOp {

	public static boolean getBit(int value, int bitIndex) {
		return ((value & (1 << bitIndex)) > 0);
	}

	public static int getBitAsInt(int value, int bitIndex) {
		return ((value & (1 << bitIndex)) > 0 ? 1 : 0);
	}
	
	public static int getNumOfOnes(int value) {
		int count = 0;
		while (value > 0) {
			count += value & 0x1;
			value >>= 1;
		}
		return count;
	}
}
