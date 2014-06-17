/**
 * 
 */
package y0.wavelet.bcwt.linebased;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

class EncodedProperties {
        public static final String fileType = "BCWT";

        public static byte[] fileTypeInBytes = fileType.getBytes();

        public byte versionMajor;

        public byte versionMinor;

        public byte components;

        public int rows;

        public int cols;

        public int rowsReal;

        public int colsReal;

        public byte bitsPerElement;
        
        public int blockWidth;
        
        public int blockHeight;

        public byte qMin;
        
        public int transitionOfQMin;

        public byte qMax;

        public byte[][] qMaxOfSubbands;

        public byte dwtLevel;

        public float[] meanOfLLBands;

        public int sizeInByte;

        public boolean isReversible;
        
        public byte progressiveType;

		public static final byte PROGRESSIVE_RESOLUTION = 1;

		public static final byte PROGRESSIVE_QUALITY = 2;

        public EncodedProperties() {
            versionMajor = 1;
            versionMinor = 3;
            qMin = 0;
            transitionOfQMin = 0;
            dwtLevel = 5;
            blockWidth = 4;
            blockHeight = 4;

            qMaxOfSubbands = null;
            meanOfLLBands = null;
            
            isReversible = false;
            
            progressiveType = EncodedProperties.PROGRESSIVE_RESOLUTION;
        }

        public void write(DataOutput file) throws IOException {
            file.writeBytes(fileType + '\n');
            file.writeBytes(Byte.toString(versionMajor) + '\n');
            file.writeBytes(Byte.toString(versionMinor) + '\n');

            file.writeBytes(Byte.toString(components) + '\n');
            file.writeBytes(Integer.toString(rowsReal) + '\n');
            file.writeBytes(Integer.toString(colsReal) + '\n');
            file.writeBytes(Byte.toString(bitsPerElement) + '\n');
            file.writeBytes(Integer.toString(rows) + '\n');
            file.writeBytes(Integer.toString(cols) + '\n');
            file.writeBytes(Integer.toString(blockWidth) + '\n');
            file.writeBytes(Integer.toString(blockHeight) + '\n');

            file.writeByte(0x1a); // EOF byte for stopping the TYPE command.

            file.writeByte(isReversible ? 1 : 0);
            file.writeByte(dwtLevel);
            file.writeInt(transitionOfQMin);
            file.writeByte(qMin);
            file.writeByte(qMax);
            file.writeByte(progressiveType);
            file.writeInt(sizeInByte);

            file.writeBytes("-EOH-\n");
            
//            for (int cp = 0; cp < components; ++cp) {
//                file.writeFloat(meanOfLLBands[cp]);
//
//                byte qMax01 = (byte) ((qMax - qMaxOfSubbands[cp][0]) | ((qMax - qMaxOfSubbands[cp][1]) << 4));
//                byte qMax23 = (byte) ((qMax - qMaxOfSubbands[cp][2]) | ((qMax - qMaxOfSubbands[cp][3]) << 4));
//                file.writeByte(qMax01);
//                file.writeByte(qMax23);
//            }
        }

        public void read(DataInput file) throws IOException {
        	if (!file.readLine().equals(fileType))
        		throw new IOException("Not a " + fileType + " file.");
        	
            versionMajor = Byte.parseByte(file.readLine());
            versionMinor = Byte.parseByte(file.readLine());

            components = Byte.parseByte(file.readLine());
            rowsReal = Integer.parseInt(file.readLine());
            colsReal = Integer.parseInt(file.readLine());
            bitsPerElement = Byte.parseByte(file.readLine());
            rows = Integer.parseInt(file.readLine());
            cols = Integer.parseInt(file.readLine());
            blockWidth = Integer.parseInt(file.readLine());
            blockHeight = Integer.parseInt(file.readLine());

            file.readByte(); // Skip the EOF byte (0x1a)

            isReversible = file.readByte() > 0;
            dwtLevel = file.readByte();
            transitionOfQMin = file.readInt();
            qMin = file.readByte();
            qMax = file.readByte();
            progressiveType = file.readByte();
            sizeInByte = file.readInt();
            
            file.readLine(); // Skip the EOH

//            meanOfLLBands = new float[components];
//            qMaxOfSubbands = new byte[components][4];
//            for (int cp = 0; cp < components; ++cp) {
//                meanOfLLBands[cp] = file.readFloat();
//
//                byte qMax01;
//                byte qMax23;
//                qMax01 = file.readByte();
//                qMax23 = file.readByte();
//                qMaxOfSubbands[cp][0] = (byte) (qMax - (qMax01 & 0x0F));
//                qMaxOfSubbands[cp][1] = (byte) (qMax - ((qMax01 & 0xF0) >> 4));
//                qMaxOfSubbands[cp][2] = (byte) (qMax - (qMax23 & 0x0F));
//                qMaxOfSubbands[cp][3] = (byte) (qMax - ((qMax23 & 0xF0) >> 4));
//            }
        }

        public int getBlockWidth() {
			return blockWidth;
		}

		public void setBlockWidth(int blockWidth) {
			this.blockWidth = blockWidth;
		}

		public byte getBitsPerElement() {
            return bitsPerElement;
        }

        public void setBitsPerElement(int bitsPerElement) {
            this.bitsPerElement = (byte) bitsPerElement;
        }

        public int getCols() {
            return cols;
        }

        public void setCols(int cols) {
            this.cols = cols;
        }

        public int getColsReal() {
            return colsReal;
        }

        public void setColsReal(int colsReal) {
            this.colsReal = colsReal;
        }

        public int getComponents() {
            return components;
        }

        public void setComponents(int components) {
            this.components = (byte) components;
        }

        public int getDwtLevel() {
            return dwtLevel;
        }

        public void setDwtLevel(int dwtLevel) {
            this.dwtLevel = (byte) dwtLevel;
        }

        public boolean isReversible() {
            return isReversible;
        }

        public void setReversible(boolean isReversibe) {
            this.isReversible = isReversibe;
        }

        public int getTransitionOfQMin() {
            return transitionOfQMin;
        }

        public void setTransitionOfQMin(int numOfCoeffBeforeQMinChanged) {
            this.transitionOfQMin = numOfCoeffBeforeQMinChanged;
        }

        public int getQMin() {
            return qMin;
        }

        public void setQMin(int min) {
            qMin = (byte) min;
        }

        public int getRows() {
            return rows;
        }

        public void setRows(int rows) {
            this.rows = rows;
        }

        public int getRowsReal() {
            return rowsReal;
        }

        public void setRowsReal(int rowsReal) {
            this.rowsReal = rowsReal;
        }
        
        public static byte[] getSignatureBytes() {
            return fileTypeInBytes;
        }

        public byte getProgressiveType() {
            return progressiveType;
        }

        public void setProgressiveType(byte progressiveType) {
            this.progressiveType = progressiveType;
        }

		public int getBlockHeight() {
			return blockHeight;
		}

		public void setBlockHeight(int blockHeight) {
			this.blockHeight = blockHeight;
		}
        
    }