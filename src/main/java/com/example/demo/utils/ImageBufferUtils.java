package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Utility class to manipulate BufferImage instances
 */
@Slf4j
public class ImageBufferUtils {

    protected ImageBufferUtils() {
    }

    /**
     * Takes two buffers to compare (source + compared). Check if any pixel is different.
     * When where is difference that pixel is stored into targetDiff buffer.
     *
     * @param sourceImage     first frame to compare against
     * @param comparedImage   second frame to compare to
     * @param targetDiffImage difference buffer if frames are not equal
     * @return true if there is any difference between source and compared images + output int diff array
     */
    public static BufferComparisonResult compareAndCalculateBufferedImagesEquality(
            BufferedImage sourceImage,
            BufferedImage comparedImage,
            BufferedImage targetDiffImage) {
        Objects.requireNonNull(sourceImage, "sourceImage is NULL");
        Objects.requireNonNull(comparedImage, "comparedImage is NULL");
        Objects.requireNonNull(targetDiffImage, "targetDiffImage is NULL");

        boolean isEquals = true; // result
        int differentPixelsNumberCount = 0; // counter
        int outputArrayIndex = 0; // counter
//        int arraySize = sourceImage.getWidth() * sourceImage.getHeight();
        Integer[] outputDifferenceResultArray = null;

        // calculate ONLY for frames with THE SAME size
        int sourceImageWidth = sourceImage.getWidth();
        int sourceImageHeight = sourceImage.getHeight();
        if (sourceImageWidth == comparedImage.getWidth() && sourceImageHeight == comparedImage.getHeight()) {
            int arraySize = sourceImageWidth * sourceImageHeight;
            outputDifferenceResultArray = new Integer[arraySize];


            for (int x = 0; x < sourceImageWidth; x++) {
                for (int y = 0; y < sourceImageHeight; y++) {
                    if (sourceImage.getRGB(x, y) != comparedImage.getRGB(x, y)) {
                        isEquals = false;
                        // take one pixel difference data
                        int rgbDifference = sourceImage.getRGB(x, y) - comparedImage.getRGB(x, y);
                        // assign data to new diff buffer
                        targetDiffImage.setRGB(x, y, rgbDifference);
                        // assign data to output array
                        outputDifferenceResultArray[outputArrayIndex] = rgbDifference;
                        differentPixelsNumberCount++;
                    } else {
                        targetDiffImage.setRGB(x, y, 0);
                        outputDifferenceResultArray[outputArrayIndex] = 0;
                    }
                }
                outputArrayIndex++;
            }
        } else {
            return new BufferComparisonResult(false, outputDifferenceResultArray);
        }
        if (!isEquals) {
            log.debug("Diff has '{}' changed pixels in frame", differentPixelsNumberCount);
        }
        return new BufferComparisonResult(isEquals, outputDifferenceResultArray);
    }

    public static boolean isBufferedImageEqual(
            BufferedImage sourceImage,
            BufferedImage comparedImage) {
        Objects.requireNonNull(sourceImage, "sourceImage is NULL");
        Objects.requireNonNull(comparedImage, "comparedImage is NULL");

        // check ONLY for frames with THE SAME size
        if (sourceImage.getWidth() == comparedImage.getWidth() && sourceImage.getHeight() == comparedImage.getHeight()) {
            for (int x = 0; x < sourceImage.getWidth(); x++) {
                for (int y = 0; y < sourceImage.getHeight(); y++) {
                    if (sourceImage.getRGB(x, y) != comparedImage.getRGB(x, y)) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public static BufferedImage applyRleDiffToBufferedImage(
            BufferedImage sourceImage,
            List<Integer> rleDecodedData) {
        Objects.requireNonNull(sourceImage, "sourceImage is NULL");
        Objects.requireNonNull(rleDecodedData, "comparedImage is NULL");

        int rleDataSize = rleDecodedData.size();
        if (rleDataSize == 0) {
            throw new IllegalArgumentException("rle data can't be empty");
        }
        int expectedArraySize = sourceImage.getWidth() * sourceImage.getHeight();
        if (expectedArraySize != rleDataSize) {
            throw new IllegalArgumentException(
                    "rle data size (" + rleDataSize + ") must be equal image size (" + expectedArraySize + ")");
        }

        BufferedImage outputBuffer = new BufferedImage(
                sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        int index = 0;
        // check ONLY for frames with THE SAME size
        for (int x = 0; x < sourceImage.getWidth(); x++) {
            for (int y = 0; y < sourceImage.getHeight(); y++) {
                int sourceRgbPixel = sourceImage.getRGB(x, y);
                int alteredPixel = sourceRgbPixel + rleDecodedData.get(index);
                outputBuffer.setRGB(x, y, alteredPixel);
                index++;
            }
        }
        return outputBuffer;
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        Objects.requireNonNull(bi, "BufferedImage is NULL");
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static void writeFileImageFrame(
            BufferedImage firstOutBuffer, Path outPath, int i,
            String fileNamePrefix) throws IOException {
        Objects.requireNonNull(firstOutBuffer, "firstOutBuffer is NULL");
        Objects.requireNonNull(outPath, "outPath is NULL");
        Objects.requireNonNull(outPath, "fileNamePrefix is NULL");
        File outputFile = new File(i + fileNamePrefix + ".gif");
        Path outFullPath = Path.of(outPath.toString(), outputFile.getName());
        log.debug("File [{}] will be written to = '{}'", i, outFullPath);
        ImageIO.write(firstOutBuffer, "GIF", outFullPath.toFile());
        log.debug("Written frame '{}' to = '{}'", i, outFullPath);
    }

}
