package edu.ttu.cvial.imageio;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import edu.ttu.cvial.wavelet.DWT;

public class ImageIO {

    public static final int ALL_COMPONENTS = -1;

    public static float[][][] readFloat(String fileName) throws IOException {
        // Read the image into a buffer
        BufferedImage imageBuffer = javax.imageio.ImageIO.read(new File(
                fileName));

        int channels = imageBuffer.getColorModel().getNumComponents();
        int rows = imageBuffer.getHeight();
        int cols = imageBuffer.getWidth();
        float[][][] imageArray = new float[channels][rows][cols];

        // Copy the image from buffer to an array
        WritableRaster tempRaster = imageBuffer.getRaster();
        int b, y;
        for (b = 0; b < channels; b++)
            for (y = 0; y < rows; y++)
                tempRaster.getSamples(0, y, cols, 1, b, imageArray[b][y]);

        return imageArray;
    }

    public static float[][][] readWithPaddingForDWTFloat(String fileName,
            int wtLevel) throws IOException {

        int i, j, k, tempInt;

        // Read the image into a buffer
        BufferedImage imageBuffer = javax.imageio.ImageIO.read(new File(
                fileName));

        // Calculate the rows and cols for padded image.
        int channels = imageBuffer.getColorModel().getNumComponents();
        int rowsReal = imageBuffer.getHeight();
        int colsReal = imageBuffer.getWidth();
        int resolutionBase = 1 << (wtLevel + 1);
        int rowsExtra = resolutionBase - rowsReal % resolutionBase;
        int colsExtra = resolutionBase - colsReal % resolutionBase;
        int rows = (rowsExtra == resolutionBase) ? rowsReal
                : (rowsReal + rowsExtra);
        int cols = (colsExtra == resolutionBase) ? colsReal
                : (colsReal + colsExtra);
        float[][][] imageArray = new float[channels][rows][cols];

        // Copy the content from buffer to array.
        WritableRaster tempRaster = imageBuffer.getRaster();
        int b, y;
        for (b = 0; b < channels; b++)
            for (y = 0; y < rowsReal; y++)
                tempRaster.getSamples(0, y, colsReal, 1, b, imageArray[b][y]);

        // Pad the rows using mirroring.
        if (rows > rowsReal) {
            for (k = 0; k < channels; k++)
                for (i = rowsReal; i < rows; i++) {
                    tempInt = 2 * rowsReal - i - 1;
                    tempInt = (tempInt < 0) ? 0 : tempInt;
                    for (j = 0; j < colsReal; j++)
                        imageArray[k][i][j] = imageArray[k][tempInt][j];
                }
        }

        // Pad the cols using mirroring.
        if (cols > colsReal) {
            for (k = 0; k < channels; k++)
                for (i = 0; i < rows; i++)
                    for (j = colsReal; j < cols; j++) {
                        tempInt = 2 * colsReal - j - 1;
                        if (tempInt < 0)
                            tempInt = 0;
                        imageArray[k][i][j] = imageArray[k][i][tempInt];
                    }
        }

        return imageArray;
    }

    public static class ImageInfo {
        public int numOfComponents;

        public int numOfRows;

        public int numOfCols;

        public int rowsReal;

        public int colsReal;

        public int bitsPerElement;

        public int colorSpace;
    }

    public static float[][][] readWithPaddingForDWTFloat(String fileName,
            int wtLevel, ImageInfo info) throws IOException {

        int i, j, k, tempInt;

        // Read the image into a buffer
        BufferedImage imageBuffer = javax.imageio.ImageIO.read(new File(
                fileName));

        // Calculate the rows and cols for padded image.
        int channels = imageBuffer.getColorModel().getNumComponents();
        int rowsReal = imageBuffer.getHeight();
        int colsReal = imageBuffer.getWidth();
        int resolutionBase = 1 << (wtLevel + 1);
        int rowsExtra = resolutionBase - rowsReal % resolutionBase;
        int colsExtra = resolutionBase - colsReal % resolutionBase;
        int rows = (rowsExtra == resolutionBase) ? rowsReal
                : (rowsReal + rowsExtra);
        int cols = (colsExtra == resolutionBase) ? colsReal
                : (colsReal + colsExtra);
        float[][][] imageArray = new float[channels][rows][cols];
        info.numOfComponents = channels;
        info.numOfRows = rows;
        info.numOfCols = cols;
        info.colsReal = colsReal;
        info.rowsReal = rowsReal;
        info.bitsPerElement = 8;

        // Copy the content from buffer to array.
        WritableRaster tempRaster = imageBuffer.getRaster();
        int b, y;
        for (b = 0; b < channels; b++)
            for (y = 0; y < rowsReal; y++)
                tempRaster.getSamples(0, y, colsReal, 1, b, imageArray[b][y]);

        // Pad the rows using mirroring.
        if (rows > rowsReal) {
            for (k = 0; k < channels; k++)
                for (i = rowsReal; i < rows; i++) {
                    tempInt = 2 * rowsReal - i - 1;
                    tempInt = (tempInt < 0) ? 0 : tempInt;
                    for (j = 0; j < colsReal; j++)
                        imageArray[k][i][j] = imageArray[k][tempInt][j];
                }
        }

        // Pad the cols using mirroring.
        if (cols > colsReal) {
            for (k = 0; k < channels; k++)
                for (i = 0; i < rows; i++)
                    for (j = colsReal; j < cols; j++) {
                        tempInt = 2 * colsReal - j - 1;
                        if (tempInt < 0)
                            tempInt = 0;
                        imageArray[k][i][j] = imageArray[k][i][tempInt];
                    }
        }

        return imageArray;
    }

    public static ImageCoeff readWithPadding(String fileName, ImageCoeff image,
            int dwtLevel) throws IOException {
        // Read the image into a buffer
        BufferedImage imageBuffer = javax.imageio.ImageIO.read(new File(
                fileName));
        return readWithPadding(imageBuffer, image, dwtLevel);
    }

    public static ImageCoeff readWithPadding(RenderedImage imageBuffer,
            ImageCoeff image, int dwtLevel) throws IOException {
        // Calculate the rows and cols for padded image.
        int channels = imageBuffer.getColorModel().getNumComponents();
        int rowsReal = imageBuffer.getHeight();
        int colsReal = imageBuffer.getWidth();
        int rows = DWT.getPaddedSize(rowsReal, dwtLevel);
        int cols = DWT.getPaddedSize(colsReal, dwtLevel);

        image.newCoeffs(cols, rows, channels);
        Properties prop = image.addPropertySet(ImageCoeff.PROPSET_ORG);
        prop.setProperty(ImageCoeff.PROP_HEIGHT, String.valueOf(rowsReal));
        prop.setProperty(ImageCoeff.PROP_WIDTH, String.valueOf(colsReal));
        prop.setProperty(ImageCoeff.PROP_BANDS, String.valueOf(channels));
        prop.setProperty(ImageCoeff.PROP_COLORSPACE, channels == 1 ? "Grayscale" : "RGB");
        prop.setProperty(ImageCoeff.PROP_BITSPERELEMENT, String.valueOf(8));

        prop = image.addPropertySet(ImageCoeff.PROPSET_PADDED);
        prop.setProperty(ImageCoeff.PROP_HEIGHT, String.valueOf(rows));
        prop.setProperty(ImageCoeff.PROP_WIDTH, String.valueOf(cols));
        prop.setProperty(ImageCoeff.PROP_BANDS, String.valueOf(channels));
        
        // Copy the content from buffer to array.
        int[] imageRow = new int[cols];
        Raster tempRaster = imageBuffer.getData();
        for (int b = 0; b < channels; b++) {
            for (int y = 0; y < rowsReal; y++) {
                tempRaster.getSamples(0, y, colsReal, 1, b, imageRow);
                for (int x = 0; x < colsReal; x++) {
                    image.setCoeff(x, y, b, imageRow[x]);
                }

                // Pad the x-direction using mirroring.
                for (int x = colsReal; x < cols; x++) {
                    int xMirror = colsReal - 1 - x % colsReal;
                    image.setCoeff(x, y, b, imageRow[xMirror]);
                }
            }
        }

        // Pad the y-direction using mirroring.
        if (rows > rowsReal) {
            for (int b = 0; b < channels; b++) {
                for (int y = rowsReal; y < rows; y++) {
                    int yMirror = rowsReal - 1 - y % rowsReal;
                    for (int x = 0; x < cols; x++) {
                        image.setCoeff(x, y, b, image
                                .getCoeffInt(x, yMirror, b));
                    }
                }
            }
        }

        return image;
    }

    public static void write(float[][][] imageArray, String formatName,
            String fileName) throws IOException {
        BufferedImage imageBuffer = toBufferedImage(imageArray);
        javax.imageio.ImageIO
                .write(imageBuffer, formatName, new File(fileName));
    }

    public static BufferedImage toBufferedImage(float[][][] imageArray, int x,
            int y, int w, int h) {
        int b = imageArray.length;
        int imageType = (b == 1) ? BufferedImage.TYPE_BYTE_GRAY
                : BufferedImage.TYPE_3BYTE_BGR;
        BufferedImage imageBuffer = new BufferedImage(w, h, imageType);
        WritableRaster tempRaster = imageBuffer.getRaster();

        float value;
        int bi, yi, yia, xi, xia, bufi;

        int[] buf = new int[w * b];
        for (yi = y, yia = 0; yia < h; ++yi, ++yia) {
            for (bi = 0; bi < b; bi++) {
                for (xi = x, xia = 0, bufi = bi; xia < w; ++xi, ++xia, bufi += b) {
                    value = imageArray[bi][yi][xi];
                    int valueInt = (int) value;
                    if (value - valueInt > 0.5)
                        ++valueInt;
                    valueInt = (valueInt < 0 ? 0 : (valueInt > 255 ? 255
                            : valueInt));
                    buf[bufi] = valueInt;
                }
            }
            tempRaster.setPixels(0, yia, w, 1, buf);
        }

        return imageBuffer;
    }

    public static BufferedImage toBufferedImage(ImageCoeff image, int x,
            int y, int w, int h) {
        Properties prop = image.getPropertySet(ImageCoeff.PROPSET_ORG);
        int b = Integer.parseInt(prop.getProperty(ImageCoeff.PROP_BANDS));
        int imageType = (b == 1) ? BufferedImage.TYPE_BYTE_GRAY
                : BufferedImage.TYPE_3BYTE_BGR;
        

        if (x < 0)
            x = 0;
        
        if (x >= image.getWidth())
            x = image.getWidth() - 1;
        
        if (y < 0)
            y = 0;
        
        if (y >= image.getHeight())
            y = image.getHeight() - 1;
        
        if (y + h >= image.getHeight()) {
            h = image.getHeight() - y;
        }
        
        if (x + w >= image.getWidth()) {
            w = image.getWidth() - x;
        }
        
        BufferedImage imageBuffer = new BufferedImage(w, h, imageType);
        WritableRaster tempRaster = imageBuffer.getRaster();

        double valueDouble;
        int valueInt;
        int type = image.getType();
        int bi, yi, yia, xi, xia, bufi;

        int[] buf = new int[w * b];
        for (yi = y, yia = 0; yia < h; ++yi, ++yia) {
            for (bi = 0; bi < b; bi++) {
                for (xi = x, xia = 0, bufi = bi; xia < w; ++xi, ++xia, bufi += b) {
                    if (type == ImageCoeff.TYPE_FLOAT || type == ImageCoeff.TYPE_DOUBLE) {
                        valueDouble = image.getCoeffDouble(xi, yi, bi);
                        valueInt = (int) valueDouble;
                        if (valueDouble - valueInt > 0.5)
                            ++valueInt;
                    } else {
                        valueInt = image.getCoeffInt(xi, yi, bi);
                    }
                    valueInt = (valueInt < 0 ? 0 : (valueInt > 255 ? 255
                            : valueInt));
                    buf[bufi] = valueInt;
                }
            }
            tempRaster.setPixels(0, yia, w, 1, buf);
        }

        return imageBuffer;
    }

    public static BufferedImage toBufferedImage(float[][][] imageArray) {
        return toBufferedImage(imageArray, 0, 0, imageArray[0][0].length,
                imageArray[0].length);
    }

    public static float[][][] toArrayFloat(RenderedImage image, int x, int y,
            int w, int h, int[] wtLevels) {

        int bi, yi, yia, xi, xia, bufi;
        int b = image.getColorModel().getNumComponents();
        int rows = h;
        int cols = w;

        // Calculate the minimum rows and cols so that DWT can be performed.
        if (wtLevels != null) {
            int wtLevelsMax = 0;
            for (bi = 0; bi < Math.min(b, wtLevels.length); bi++)
                wtLevelsMax = Math.max(wtLevels[bi], wtLevelsMax);

            int resolutionBase = 1 << (wtLevelsMax + 1);
            int rowsExtra = resolutionBase - h % resolutionBase;
            int colsExtra = resolutionBase - w % resolutionBase;
            rows = (rowsExtra == resolutionBase) ? h : (h + rowsExtra);
            cols = (colsExtra == resolutionBase) ? w : (w + colsExtra);
        }

        float[][][] imageArray = new float[b][rows][cols];

        // Copy the content from buffer to array.
        Raster tempRaster = image.getData();

        // Slower method.
        // for (bi = 0; bi < b; bi++)
        // for (yi = y, yia = 0; yia < h; yi++, yia++)
        // tempRaster.getSamples(x, yi, w, 1, bi, imageArray[bi][yia]);

        int[] buf = new int[w * b];
        for (yi = y, yia = 0; yia < h; ++yi, ++yia) {
            tempRaster.getPixels(x, yi, w, 1, buf);
            for (bi = 0; bi < b; bi++) {
                for (xi = x, xia = 0, bufi = bi; xia < w; ++xi, ++xia, bufi += b) {
                    imageArray[bi][yia][xi] = (float) buf[bufi];
                }
            }
        }

        // Pad the rows using mirroring.
        if (rows > h) {
            int h2 = h << 1;
            for (bi = 0; bi < b; bi++)
                for (yi = h; yi < rows; yi++) {
                    yia = h2 - yi - 1;
                    yia = (yia > 0) ? yia : -yia;
                    for (xi = 0; xi < w; xi++)
                        imageArray[bi][yi][xi] = imageArray[bi][yia][xi];
                }
        }

        // Pad the cols using mirroring.
        if (cols > w) {
            int w2 = w << 1;
            for (bi = 0; bi < b; bi++)
                for (yi = 0; yi < rows; yi++)
                    for (xi = w; xi < cols; xi++) {
                        xia = w2 - xi - 1;
                        xia = xia > 0 ? xia : -xia;
                        imageArray[bi][yi][xi] = imageArray[bi][yi][xia];
                    }
        }

        return imageArray;
    }

    public static float[][][] toArrayFloat(RenderedImage image, int[] wtLevels) {
        return toArrayFloat(image, 0, 0, image.getWidth(), image.getHeight(),
                wtLevels);
    }

    public static float[][][] toArrayFloat(RenderedImage image, int wtLevel) {
        return toArrayFloat(image, 0, 0, image.getWidth(), image.getHeight(),
                (new int[] { wtLevel }));
    }

    public static float[][][] toArrayFloat(RenderedImage image) {
        return toArrayFloat(image, 0, 0, image.getWidth(), image.getHeight(),
                null);
    }
}
