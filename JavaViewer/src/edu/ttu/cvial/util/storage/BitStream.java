package edu.ttu.cvial.util.storage;

public class BitStream {
    public static final int RV_FAILED = -1;
    
    private static final int bufElementSizeInBit = 8;
    
    private static final int bufElementMask = 0xFF; //~(-1 << bufElementSizeInBit);

    public static int calcMinBytesForBits(int numOfBits) {
        return (numOfBits / 8 + (((numOfBits % 8) > 0) ? 1 : 0));
    }

    protected int bitCountOffset;

    protected int bitPosOffset;

    protected byte[] buf;

    protected int capacityInBit;

    protected int capacityInByte;
    
    protected int countInBit;

    protected int countInByte;

    protected boolean isEndOfStream = false;

    protected int posInBit;

    protected int posInByte;
    
    private int capacityGrowthStep;
    
    public static BitStream combine(BitStream[] streams) {
        int newSizeInByte = 0;
        for (int i = 0; i < streams.length; ++i) {
            if (streams[i] != null) {
                newSizeInByte += streams[i].getCountInByte();
            }
        }
        BitStream newStream = new BitStream(newSizeInByte * 8);
        byte[] newBuf = newStream.getBuf();
        int newBufPos = 0;
        for (int i = 0; i < streams.length; ++i) {
            if (streams[i] != null) {
                int streamSizeInByte = streams[i].getCountInByte();
                System.arraycopy(streams[i].getBuf(), 0, newBuf, newBufPos, streamSizeInByte);
                newBufPos += streamSizeInByte;
            }
        }
        newStream.setCountInByte(newSizeInByte);
        return newStream;
    }
    
    public static BitStream slice(BitStream stream, int fromByte, int length) {
        BitStream newStream = new BitStream(length * 8);
        byte[] buf = stream.getBuf();
        byte[] newBuf = newStream.getBuf();
        System.arraycopy(buf, fromByte, newBuf, 0, length);
        newStream.setCountInByte(length);
        return newStream;
    }

    public BitStream() {
        allocate(1024);
        setCountInBit(0);
        setPosInBit(0);
    }

    public BitStream(int capacityInBit) {
        allocate(capacityInBit);
        setCountInBit(0);
        setPosInBit(0);
    }

    public void allocate(int capacityInBit) {
        this.capacityInBit = capacityInBit;
        capacityInByte = calcMinBytesForBits(capacityInBit);
        setCountInBit(0);
        setPosInBit(0);
        buf = new byte[capacityInByte];
        capacityGrowthStep = capacityInByte / 2;
    }
    
    private void growCapacity() {
        int newCapacity = capacityInByte + capacityGrowthStep;
        byte[] newBuf = new byte[newCapacity];
        System.arraycopy(buf, 0, newBuf, 0, capacityInByte);
        capacityInByte = newCapacity;
        capacityInBit = capacityInByte * 8;
        buf = newBuf;
    }

    public void appendBit(boolean bit) {
        if (countInBit >= capacityInBit) {
            throw new IndexOutOfBoundsException();
        } else {
            if (bitCountOffset == 0)
                buf[countInByte] |= (bit ? 1 : 0);
            else
                buf[countInByte - 1] |= ((bit ? 1 : 0) << bitCountOffset);

            increaseCountInBit();
        }
    }

    /**
     * A fast version of {@link #appendBit(boolean)}, without boundary check. 
     * @param bit
     */
    public void appendBitFast(final boolean bit) {
        if (bitCountOffset == 0) {
            try {
                buf[countInByte] |= (bit ? 1 : 0);
            } catch (ArrayIndexOutOfBoundsException e) {
                growCapacity();
                buf[countInByte] |= (bit ? 1 : 0);
            }
            countInByte++;
            bitCountOffset++;
        } else {
            buf[countInByte - 1] |= ((bit ? 1 : 0) << bitCountOffset);
            if (++bitCountOffset == 8)
                bitCountOffset = 0;
        }
        countInBit++;
    }
    
    public void appendByte(byte value) {
        //C++    appendInt( (int) value, 8);
        appendInt(value & 0xff, 8);
    }

    public void appendFloat(float value) {
        appendInt(Float.floatToIntBits(value), 32);
    }

    public void appendInt(int value, int SizeInBit) {
        for (int i = 0; i < SizeInBit; i++)
            appendBit(((value >> i) & 0x01) != 0);
    }

    /**
     * A fast version of {@link #appendInt(int, int)}.
     * It ueses an efficient way to split the value into parts and put them
     * directly into the buffer without doing in bit-by-bit fasion. 
     * @param value the integer to be appended at the end of the stream.
     * @param sizeInBit number of bits in the value to be appended.
     */
    public final void appendIntFast(int value, int sizeInBit) {
        if (bitCountOffset == 0) {
            // Fill this byte with the bits of value.
            int mask = (sizeInBit < bufElementSizeInBit ? ((~(-1 << sizeInBit)) & bufElementMask)
                    : bufElementMask);
            try {
                buf[countInByte] |= (value & mask);
            } catch (ArrayIndexOutOfBoundsException e) {
                growCapacity();
                buf[countInByte] |= (value & mask);
            }
            countInByte++;
            
            // Are we done with all the bits of value?
            if (sizeInBit < bufElementSizeInBit) {
                countInBit += sizeInBit;
                bitCountOffset = sizeInBit;
            } else if (sizeInBit == bufElementSizeInBit) {
                countInBit += sizeInBit;
            } else {
                // Nope, there are some more bits in value.
                countInBit += bufElementSizeInBit;
                appendIntFast(value >> bufElementSizeInBit, sizeInBit - bufElementSizeInBit);
            }
            
        } else {
            // Fill the remaining bits of current byte in buf[] with
            // the part of the value.
            int firstPartSize = bufElementSizeInBit - bitCountOffset;
            int mask = (~(-1 << firstPartSize)) & (~(-1 << sizeInBit));
            int firstPart = (value & mask) << bitCountOffset;
            buf[countInByte - 1] |= firstPart;
            
            // Are we done with all the bits of value?
            if (sizeInBit < firstPartSize) {
                bitCountOffset += sizeInBit;
                countInBit += sizeInBit;
            } else if (sizeInBit == firstPartSize) {
                bitCountOffset = 0;
                countInBit += sizeInBit;
            } else {
                // Nope, there are some more bits in value.
                bitCountOffset = 0;
                countInBit += firstPartSize;
                appendIntFast(value >> firstPartSize, sizeInBit - firstPartSize);
            }
            
        }
    }
    
    public final void appendString(String string) {
        byte[] bytes = string.getBytes();
        for (int i = 0; i < bytes.length; ++i) {
            appendIntFast(bytes[i], 8);
        }
    }

    /**
     * A fast version of {@link #getInt(int)}.
     * It ueses an efficient way to get parts of the value
     * directly from the buffer without doing in bit-by-bit fasion. 
     * @param sizeInBit number of bits to get.
     */
    public final int getIntFast(int sizeInBit) {
        int value = 0;
        if (bitCountOffset == 0) {
            int mask = (sizeInBit < bufElementSizeInBit ? ((~(-1 << sizeInBit)) & bufElementMask)
                    : bufElementMask);
            value = buf[posInByte] & mask;
            
            // Got all the bits?
            if (sizeInBit < bufElementSizeInBit) {
                posInBit += sizeInBit;
                bitPosOffset = sizeInBit;
            } else if (sizeInBit == bufElementSizeInBit) {
                posInBit += sizeInBit;
                posInByte++;
            } else {
                // Nope, there are some more bits to get.
                posInBit += bufElementSizeInBit;
                posInByte++;
                value |= getIntFast(sizeInBit - bufElementSizeInBit) << bufElementSizeInBit;
            }
            
        } else {
            int firstPartSize = bufElementSizeInBit - bitPosOffset;
            int mask = ((~(-1 << firstPartSize)) & (~(-1 << sizeInBit))) << bitPosOffset;
            value = (buf[posInByte] & mask) >> bitPosOffset;
            
            // Got all the bits?
            if (sizeInBit < firstPartSize) {
                bitPosOffset += sizeInBit;
                posInBit += sizeInBit;
            } else if (sizeInBit == firstPartSize) {
                bitPosOffset = 0;
                posInBit += sizeInBit;
                posInByte++;
            } else {
                // Nope, there are some more bits to get.
                bitPosOffset = 0;
                posInBit += firstPartSize;
                posInByte++;
                value |= getIntFast(sizeInBit - firstPartSize) << firstPartSize;
            }
            
        }
        
        return value;
    }

    public void appendStream(BitStream stream) {
        appendStream(stream, -1);
    }

    public void appendStream(BitStream stream, int countInBit) {
        countInBit = countInBit < 0 || countInBit > stream.getCountInBit() ? stream
                .getCountInBit()
                : countInBit;
        int i;
        for (i = 0; i < countInBit; i++) {
            appendBit(stream.getBit());
        }
    }

    public boolean getBit() {
        boolean result = false;
        if (posInBit < countInBit) {
            result = (((0x01 << bitPosOffset) & buf[posInByte]) != 0);
            increasePosInBit();
        }
        
        return result;
    }

    public boolean getBitFast() {
        boolean result = false;
        result = (((0x01 << bitPosOffset) & buf[posInByte]) != 0);
        
        ++posInBit;
        if (++bitPosOffset == 8) {
            ++posInByte;
            bitPosOffset = 0;
        }

        return result;
    }

    public byte[] getBuf() {
        return buf;
    }

    public byte getByte() {
        return ((byte) getInt(8));
    }

    public int getCapacityInBit() {
        return capacityInBit;
    }
    
    public int getCapacityInByte() {
        return capacityInByte;
    }

    public int getCountInBit() {
        return countInBit;
    }

    public int getCountInByte() {
        return countInByte;
    }

    public float getFloat() {
        return Float.intBitsToFloat(getInt(32));
    }

    public int getInt(int sizeInBit) {
        int result = 0;
        // try {
        for (int i = 0; i < sizeInBit; i++)
            result |= (getBit() ? 1 : 0) << i;
        // }
        // catch (IndexOutOfBoundsException ex) {
        // // Do nothing. This allows returning a value with remaining bits set
        // to zero.
        // }
        return result;
    }

    public int getPosInBit() {
        return posInBit;
    }

    public int getPosInByte() {
        return posInByte;
    }

    public int increaseCountInBit() {
        int result = RV_FAILED;
        if (++countInBit > capacityInBit)
            throw new IndexOutOfBoundsException();
        else {
            if (bitCountOffset++ == 0)
                countInByte++;
            else if (bitCountOffset == 8) // Be careful. bitCountOffset has
                                            // increased in the above "if(...)".
                bitCountOffset = 0;
            result = countInBit;
        }
        return result;
    }

    public int increasePosInBit() {
        if (++posInBit >= countInBit) {
            posInBit = countInBit;
            isEndOfStream = true;
        }
        else {
            if (++bitPosOffset == 8) {
                ++posInByte;
                bitPosOffset = 0;
            }
        }
        return posInBit;
    }

    public boolean isEndOfStream() {
        return isEndOfStream;
    }
    
    public void putBit(boolean bit) {
        if (posInBit > countInBit)
            throw new IndexOutOfBoundsException();
        else {
            if (bit) {
                // Setting a bit to 1 is simple.
                if (bitPosOffset == 0)
                    buf[posInByte] |= bit ? 1 : 0;
                else
                    buf[posInByte - 1] |= ((bit ? 1 : 0) << bitPosOffset);
            } else {
                // Setting a bit to 0 is a bit tricky.
                byte tempc = (byte) (~(0x01 << bitPosOffset));
                if (bitPosOffset == 0)
                    buf[posInByte] &= tempc;
                else
                    buf[posInByte - 1] &= tempc;
            }
            increasePosInBit();
        }
    }
    public void putByte(byte value) {
        putInt((int) value, 8);
    }

    public void putInt(int value, int SizeInBit) {
        if ((posInBit + SizeInBit - 1) > countInBit)
            throw new IndexOutOfBoundsException();
        else {
            for (int i = 0; i < SizeInBit; i++)
                putBit(((value >> i) & 0x01) != 0);
        }
    }

    public int setCountInBit(int bit_count) {
        int result = RV_FAILED;
        if (bit_count > capacityInBit)
            throw new IndexOutOfBoundsException();
        else {
            countInBit = bit_count;
            bitCountOffset = countInBit % 8;
            countInByte = calcMinBytesForBits(countInBit);
            result = countInBit;
        }
        return result;
    }

    public int setCountInByte(int byte_count) {
        int result = RV_FAILED;
        if (byte_count > capacityInByte)
            throw new IndexOutOfBoundsException();
        else {
            countInBit = byte_count * 8;
            countInByte = byte_count;
            bitCountOffset = 0;
            result = countInByte;
        }
        return result;
    }

    public int setPosInBit(int bit_pos) {
        int result = RV_FAILED;
        if ((bit_pos >= countInBit) && (!((bit_pos == 0) && (countInBit == 0)))) {
            throw new IndexOutOfBoundsException();
        } else {
            posInBit = bit_pos;
            bitPosOffset = posInBit % 8;
            posInByte = posInBit / 8;
            result = posInBit;
            isEndOfStream = false;
        }
        return result;
    }

    public int setPosInByte(int byte_pos) {
        int result = RV_FAILED;
        if (byte_pos > countInByte)
            throw new IndexOutOfBoundsException();
        else {
            posInBit = byte_pos * 8;
            posInByte = byte_pos;
            bitPosOffset = 0;
            result = posInBit;
            isEndOfStream = false;
        }
        return result;
    }

    public final int getBitCountOffset() {
        return bitCountOffset;
    }
    
    public final int getBitCountOffsetComplement() {
        int bitOffset = bitCountOffset;
        if (bitOffset > 0)
            bitOffset = 8 - bitOffset;
        return bitOffset;
    }
    
    public void reverseBits(int fromByte, int toByte) {
        int pHead = fromByte;
        int pTail = toByte;
        int b;
        for (; pHead <= pTail; ++pHead, --pTail) {
            b = 0xFF & buf[pHead];
            byte headReversed = (byte) ((((b) * 0x0802L & 0x22110L) | ((b) * 0x8020L & 0x88440L)) * 0x10101L >> 16);
            b = 0xFF & buf[pTail];
            byte tailReversed = (byte) ((((b) * 0x0802L & 0x22110L) | ((b) * 0x8020L & 0x88440L)) * 0x10101L >> 16);
            buf[pHead] = tailReversed;
            buf[pTail] = headReversed;
        }
    }
    
    public void reverseBits() {
        reverseBits(0, countInByte - 1);
    }
}
