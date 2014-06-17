package y0.utils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ArrayOp {

    public static float[] add(float[] data, float num) {
        return add(data, num, 0, data.length);
    }

    public static float[] add(float[] data, float num, int x, int w) {
        int i;
        int iStart = x;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            data[i] += num;
        }
        return data;
    }

    public static float[] add(float[] data0, float data1[]) {
        return add(data0, data1, 0, data0.length);
    }

    public static float[] add(float[] data0, float[] data1, int x, int w) {
        int i;
        int iStart = x;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            data0[i] += data1[i];
        }
        return data0;
    }

    public static float[][] add(float[][] data, float num) {
        return add(data, num, 0, 0, data[0].length, data.length);
    }

    public static float[][] add(float[][] data, float num, int x, int y, int w,
            int h) {
        int i;
        int iStart = y;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            add(data[i], num, x, w);
        }

        return data;
    }

    public static float[][] add(float[][] data0, float[][] data1) {
        return add(data0, data1, 0, 0, data0[0].length, data0.length);
    }

    public static float[][] add(float[][] data0, float[][] data1, int x, int y,
            int w, int h) {
        int i;
        int iStart = y;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            add(data0[i], data1[i], x, w);
        }

        return data0;
    }

    public static float[][][] add(float[][][] data, float num) {
        return add(data, num, 0, 0, 0, data[0][0].length, data[0].length,
                data.length);
    }

    public static float[][][] add(float[][][] data, float num, int x, int y,
            int z, int w, int h, int d) {
        int i;
        int iStart = z;
        int iEnd = z + d;
        for (i = iStart; i < iEnd; i++) {
            add(data[i], num, x, y, w, h);
        }

        return data;
    }

    public static float[][][] add(float[][][] data0, float[][][] data1) {
        return add(data0, data1, 0, 0, 0, data0[0][0].length, data0[0].length,
                data0.length);
    }

    public static float[][][] add(float[][][] data0, float[][][] data1, int x,
            int y, int z, int w, int h, int d) {
        int i;
        int iStart = z;
        int iEnd = z + d;
        for (i = iStart; i < iEnd; i++) {
            add(data0[i], data1[i], x, y, w, h);
        }

        return data0;
    }

    public static float[] copy(float[] src, float dest[]) {
        return copy(src, dest, 0, src.length, 0);
    }

    public static float[] copy(float[] src, float[] dest, int src_x, int w,
            int dest_x) {
        int i;
        int iStart = src_x;
        int iEnd = src_x + w;
        int dest_i = dest_x;
        for (i = iStart; i < iEnd; i++) {
            dest[dest_i++] = src[i];
        }
        return dest;
    }

    public static float[][] copy(float[][] src, float[][] dest) {
        return copy(src, dest, 0, 0, src[0].length, src.length, 0, 0);
    }

    public static float[][] copy(float[][] src, float[][] dest, int src_x,
            int src_y, int w, int h, int dest_x, int dest_y) {
        int i;
        int iStart = src_y;
        int iEnd = src_y + h;
        int dest_i = dest_y;
        for (i = iStart; i < iEnd; i++) {
            copy(src[i], dest[dest_i++], src_x, w, dest_x);
        }

        return dest;
    }

    public static float[][][] copy(float[][][] src, float[][][] dest) {
        return copy(src, dest, 0, 0, 0, src[0][0].length, src[0].length,
                src.length, 0, 0, 0);
    }

    public static float[][][] copy(float[][][] src, float[][][] dest,
            int src_x, int src_y, int src_z, int w, int h, int d, int dest_x,
            int dest_y, int dest_z) {
        int i;
        int iStart = src_z;
        int iEnd = src_z + d;
        int dest_i = dest_z;
        for (i = iStart; i < iEnd; i++) {
            copy(src[i], dest[dest_i++], src_x, src_y, w, h, dest_x, dest_y);
        }

        return dest;
    }

    public static float[] divide(float[] data, float num) {
        return divide(data, num, 0, data.length);
    }

    public static float[] divide(float[] data, float num, int x, int w) {
        int i;
        int iStart = x;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            data[i] /= num;
        }
        return data;
    }

    public static float[][] divide(float[][] data, float num) {
        return divide(data, num, 0, 0, data[0].length, data.length);
    }

    public static float[][] divide(float[][] data, float num, int x, int y,
            int w, int h) {
        int i;
        int iStart = y;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            divide(data[i], num, x, w);
        }

        return data;
    }

    public static void exportRaw(float[] data, DataOutputStream stream)
            throws IOException {
        exportRaw(data, stream, 0, data.length);
    }

    public static void exportRaw(float[] data, DataOutputStream stream, int x,
            int w) throws IOException {
        int i;
        int iStart = x;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            stream.writeFloat(data[i]);
        }
    }

    public static void exportRaw(float[][] data, DataOutputStream stream)
            throws IOException {
        exportRaw(data, stream, 0, 0, data[0].length, data.length);
    }

    public static void exportRaw(float[][] data, DataOutputStream stream,
            int x, int y, int w, int h) throws IOException {
        int i;
        int iStart = y;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            exportRaw(data[i], stream, x, w);
        }
    }

    public static void exportRaw(float[][] data, String fileName)
            throws IOException {
        DataOutputStream stream = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(fileName)));
        ArrayOp.exportRaw(data, stream);
        stream.close();
    }

    public static void exportRaw(int[] data, DataOutputStream stream)
            throws IOException {
        exportRaw(data, stream, 0, data.length);
    }

    public static void exportRaw(int[] data, DataOutputStream stream, int x,
            int w) throws IOException {
        int i;
        int iStart = x;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            stream.writeInt(data[i]);
        }
    }

    public static void exportRaw(int[][] data, DataOutputStream stream)
            throws IOException {
        exportRaw(data, stream, 0, 0, data[0].length, data.length);
    }

    public static void exportRaw(int[][] data, DataOutputStream stream, int x,
            int y, int w, int h) throws IOException {
        int i;
        int iStart = y;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            exportRaw(data[i], stream, x, w);
        }

    }

    public static void exportRaw(int[][] data, String fileName)
            throws IOException {
        DataOutputStream stream = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(fileName)));
        ArrayOp.exportRaw(data, stream);
        stream.close();
    }

    public static float getAbsMax(float[] data) {
        return getAbsMax(data, 0, data.length);
    }

    public static float getAbsMax(float[] data, int x, int w) {
        int i;
        float temp;
        float valueAbsMax = Math.abs(data[x]);

        int iStart = x + 1;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            temp = Math.abs(data[i]);
            if (valueAbsMax < temp)
                valueAbsMax = temp;
        }

        return valueAbsMax;
    }

    public static float getAbsMax(float[][] data) {
        return getAbsMax(data, 0, 0, data[0].length, data.length);
    }

    public static float getAbsMax(float[][] data, int x, int y, int w, int h) {
        int i;
        float temp;
        float valueAbsMax = getAbsMax(data[y], x, w);

        int iStart = y + 1;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            temp = getAbsMax(data[i], x, w);
            if (valueAbsMax < temp)
                valueAbsMax = temp;
        }

        return valueAbsMax;
    }

    public static byte getMax(byte[] data) {
        return getMax(data, 0, data.length);
    }

    public static byte getMax(byte[] data, int x, int w) {
        int i;
        byte temp;
        byte valueMax = data[x];

        int iStart = x + 1;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            temp = data[i];
            if (valueMax < temp)
                valueMax = temp;
        }

        return valueMax;
    }

    public static byte getMax(byte[][] data) {
        return getMax(data, 0, 0, data[0].length, data.length);
    }

    public static byte getMax(byte[][] data, int x, int y, int w, int h) {
        int i;
        byte temp;
        byte valueMax = getMax(data[y], x, w);

        int iStart = y + 1;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            temp = getMax(data[i], x, w);
            if (valueMax < temp)
                valueMax = temp;
        }

        return valueMax;
    }

    public static float getAbsMin(float[] data) {
        return getAbsMin(data, 0, data.length);
    }

    public static float getAbsMin(float[] data, int x, int w) {
        int i;
        float temp;
        float ValueAbsMin = Math.abs(data[x]);

        int iStart = x + 1;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            temp = Math.abs(data[i]);
            if (ValueAbsMin > temp)
                ValueAbsMin = temp;
        }

        return ValueAbsMin;
    }

    public static float getAbsMin(float[][] data) {
        return getAbsMin(data, 0, 0, data[0].length, data.length);
    }

    public static float getAbsMin(float[][] data, int x, int y, int w, int h) {
        int i;
        float temp;
        float ValueAbsMin = getAbsMin(data[y], x, w);

        int iStart = y + 1;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            temp = getAbsMin(data[i], x, w);
            if (ValueAbsMin > temp)
                ValueAbsMin = temp;
        }

        return ValueAbsMin;
    }

    public static float getMean(float[] data) {
        return getMean(data, 0, data.length);
    }

    public static float getMean(float[] data, int x, int w) {
        return (getSum(data, x, w) / w);
    }

    public static float getMean(float[][] data) {
        return getMean(data, 0, 0, data[0].length, data.length);
    }

    public static float getMean(float[][] data, int x, int y, int w, int h) {
        return (getSum(data, x, y, w, h) / (w * h));
    }

    public static float getSquareSum(float[] data) {
        return getSquareSum(data, 0, data.length);
    }

    public static float getSquareSum(float[] data, int x, int w) {
        int i;
        float valueSquareSum = 0;

        int iStart = x;
        int iEnd = x + w;
        float value;
        for (i = iStart; i < iEnd; i++) {
            value = data[i];
            valueSquareSum += value * value;
        }

        return valueSquareSum;
    }

    public static float getSquareSum(float[][] data) {
        return getSquareSum(data, 0, 0, data[0].length, data.length);
    }

    public static float getSquareSum(float[][] data, int x, int y, int w, int h) {
        int i;
        float valueSquareSum = 0;

        int iStart = y;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            valueSquareSum += getSquareSum(data[i], x, w);
        }

        return valueSquareSum;
    }

    public static float getSum(float[] data) {
        return getSum(data, 0, data.length);
    }

    public static float getSum(float[] data, int x, int w) {
        int i;
        float valueSum = 0;

        int iStart = x;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            valueSum += data[i];
        }

        return valueSum;
    }

    public static float getSum(float[][] data) {
        return getSum(data, 0, 0, data[0].length, data.length);
    }

    public static float getSum(float[][] data, int x, int y, int w, int h) {
        int i;
        float valueSum = 0;

        int iStart = y;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            valueSum += getSum(data[i], x, w);
        }

        return valueSum;
    }

    public static float[] set(float[] data, float num) {
        return set(data, num, 0, data.length);
    }

    public static float[] set(float[] data, float num, int x, int w) {
        int i;
        int iStart = x;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            data[i] = num;
        }
        return data;
    }

    public static float[][] set(float[][] data, float num) {
        return set(data, num, 0, 0, data[0].length, data.length);
    }

    public static float[][] set(float[][] data, float num, int x, int y, int w,
            int h) {
        int i;
        int iStart = y;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            set(data[i], num, x, w);
        }

        return data;
    }

    public static int[] set(int[] data, int num) {
        return set(data, num, 0, data.length);
    }

    public static int[] set(int[] data, int num, int x, int w) {
        int i;
        int iStart = x;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            data[i] = num;
        }
        return data;
    }

    public static int[][] set(int[][] data, int num) {
        return set(data, num, 0, 0, data[0].length, data.length);
    }

    public static int[][] set(int[][] data, int num, int x, int y, int w, int h) {
        int i;
        int iStart = y;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            set(data[i], num, x, w);
        }

        return data;
    }

    public static float[] subtract(float[] data, float num) {
        return subtract(data, num, 0, data.length);
    }

    public static float[] subtract(float[] data, float num, int x, int w) {
        int i;
        int iStart = x;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            data[i] -= num;
        }
        return data;
    }

    public static float[] subtract(float[] data0, float data1[]) {
        return subtract(data0, data1, 0, data0.length);
    }

    public static float[] subtract(float[] data0, float[] data1, int x, int w) {
        int i;
        int iStart = x;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            data0[i] -= data1[i];
        }
        return data0;
    }

    public static float[][] subtract(float[][] data, float num) {
        return subtract(data, num, 0, 0, data[0].length, data.length);
    }

    public static float[][] subtract(float[][] data, float num, int x, int y,
            int w, int h) {
        int i;
        int iStart = y;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            subtract(data[i], num, x, w);
        }

        return data;
    }

    public static float[][] subtract(float[][] data0, float[][] data1) {
        return subtract(data0, data1, 0, 0, data0[0].length, data0.length);
    }

    public static float[][] subtract(float[][] data0, float[][] data1, int x,
            int y, int w, int h) {
        int i;
        int iStart = y;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            subtract(data0[i], data1[i], x, w);
        }

        return data0;
    }
    
    public static int getMinIndex(float[] data) {
        return getMinIndex(data, 0, data.length);
    }

    public static int getMinIndex(float[] data, int fromIndex, int length) {
        float temp;
        float minValue = data[fromIndex];
        int minIndex = fromIndex;

        int iStart = fromIndex + 1;
        int iEnd = fromIndex + length;
        for (int i = iStart; i < iEnd; i++) {
            temp = data[i];
            if (minValue > temp) {
                minValue = temp;
                minIndex = i;
            }
        }

        return minIndex;
    }

    public static double[][][] toMatlab3DArray(float[][][] array) {
        int i, j, k;
    
        int size1D = array[0].length;
        int size2D = array[0][0].length;
        int size3D = array.length;
        double[][][] output = new double[size1D][size2D][size3D];
    
        for (i = 0; i < size1D; i++) {
          for (j = 0; j < size2D; j++) {
            for (k = 0; k < size3D; k++) {
              output[i][j][k] = array[k][i][j];
            }
          }
        }
    
        return output;
      }
    
    public static byte[][][] newByte3D(int dim0, int dim1, int dim2, int value) {
        byte byteValue = (byte) value;
        byte[][][] output = new byte[dim0][dim1][dim2];
        for (int d0 = 0; d0 < dim0; ++d0) {
            for (int d1 = 0; d1 < dim1; ++d1) {
                for (int d2 = 0; d2 < dim2; ++d2) {
                    output[d0][d1][d2] = byteValue;
                }
            }
        }
        return output;
    }
    
    public static short getAbsMax(short[] data) {
        return getAbsMax(data, 0, data.length);
    }

    public static short getAbsMax(short[] data, int x, int w) {
        int i;
        short temp;
        short valueAbsMax = (short) Math.abs(data[x]);

        int iStart = x + 1;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            temp = (short) Math.abs(data[i]);
            if (valueAbsMax < temp)
                valueAbsMax = temp;
        }

        return valueAbsMax;
    }

    public static short getAbsMax(short[][] data) {
        return getAbsMax(data, 0, 0, data[0].length, data.length);
    }

    public static short getAbsMax(short[][] data, int x, int y, int w, int h) {
        int i;
        short temp;
        short valueAbsMax = getAbsMax(data[y], x, w);

        int iStart = y + 1;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            temp = getAbsMax(data[i], x, w);
            if (valueAbsMax < temp)
                valueAbsMax = temp;
        }

        return valueAbsMax;
    }
    
    public static short getAbsMax(short[][][] data) {
        return getAbsMax(data, 0, 0, 0, data[0][0].length, data[0].length, data.length);
    }

    public static short getAbsMax(short[][][] data, int x, int y, int z, int w, int h, int d) {
        int i;
        short temp;
        short valueAbsMax = getAbsMax(data[z], x, y, w, h);

        int iStart = z + 1;
        int iEnd = z + d;
        for (i = iStart; i < iEnd; i++) {
            temp = getAbsMax(data[i], x, y, w, h);
            if (valueAbsMax < temp)
                valueAbsMax = temp;
        }

        return valueAbsMax;
    }

    public static short getMean(short[] data) {
        return getMean(data, 0, data.length);
    }

    public static short getMean(short[] data, int x, int w) {
        return (short) (getSum(data, x, w) / w);
    }

    public static short getMean(short[][] data) {
        return getMean(data, 0, 0, data[0].length, data.length);
    }

    public static short getMean(short[][] data, int x, int y, int w, int h) {
        int sum = getSum(data, x, y, w, h);
        int mean = sum / (w * h); 
        return (short) mean;
    }

    public static int getSum(short[] data) {
        return getSum(data, 0, data.length);
    }

    public static int getSum(short[] data, int x, int w) {
        int i;
        int valueSum = 0;

        int iStart = x;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            valueSum += data[i];
        }

        return valueSum;
    }

    public static int getSum(short[][] data) {
        return getSum(data, 0, 0, data[0].length, data.length);
    }

    public static int getSum(short[][] data, int x, int y, int w, int h) {
        int i;
        int valueSum = 0;

        int iStart = y;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            valueSum += getSum(data[i], x, w);
        }

        return valueSum;
    }

    public static short[] subtract(short[] data, short num) {
        return subtract(data, num, 0, data.length);
    }

    public static short[] subtract(short[] data, short num, int x, int w) {
        int i;
        int iStart = x;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            data[i] -= num;
        }
        return data;
    }

    public static short[] subtract(short[] data0, short data1[]) {
        return subtract(data0, data1, 0, data0.length);
    }

    public static short[] subtract(short[] data0, short[] data1, int x, int w) {
        int i;
        int iStart = x;
        int iEnd = x + w;
        for (i = iStart; i < iEnd; i++) {
            data0[i] -= data1[i];
        }
        return data0;
    }

    public static short[][] subtract(short[][] data, short num) {
        return subtract(data, num, 0, 0, data[0].length, data.length);
    }

    public static short[][] subtract(short[][] data, short num, int x, int y,
            int w, int h) {
        int i;
        int iStart = y;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            subtract(data[i], num, x, w);
        }

        return data;
    }

    public static short[][] subtract(short[][] data0, short[][] data1) {
        return subtract(data0, data1, 0, 0, data0[0].length, data0.length);
    }

    public static short[][] subtract(short[][] data0, short[][] data1, int x,
            int y, int w, int h) {
        int i;
        int iStart = y;
        int iEnd = y + h;
        for (i = iStart; i < iEnd; i++) {
            subtract(data0[i], data1[i], x, w);
        }

        return data0;
    }
    
    public static void convertDataType(short[] src, int srcPos, float[] dest, int destPos, int len) {
		for (int x = 0; x < len; x++)
			dest[x + destPos] = (float) src[x + srcPos];
    }
}
