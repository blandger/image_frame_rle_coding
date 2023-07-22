package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
public class ImageBufferUtils {

    protected ImageBufferUtils() {
    }

    public static boolean isBufferedImagesEqual(BufferedImage sourceImage,
                                                BufferedImage comparedImage,
                                                BufferedImage targetDiffImage) {
        Objects.requireNonNull(sourceImage, "sourceImage is NULL");
        Objects.requireNonNull(comparedImage, "comparedImage is NULL");
        Objects.requireNonNull(comparedImage, "targetDiffImage is NULL");
        boolean isEquals = true;
        int changedPixelsNumber = 0;
        if (sourceImage.getWidth() == comparedImage.getWidth() && sourceImage.getHeight() == comparedImage.getHeight()) {
            for (int x = 0; x < sourceImage.getWidth(); x++) {
                for (int y = 0; y < sourceImage.getHeight(); y++) {
                    if (sourceImage.getRGB(x, y) != comparedImage.getRGB(x, y)) {
                        isEquals = false;
                        targetDiffImage.setRGB(x, y, sourceImage.getRGB(x, y) - comparedImage.getRGB(x, y));
                        changedPixelsNumber++;
                    } else {
                        targetDiffImage.setRGB(x, y, 0);
                    }
                }
            }
        } else {
            return false;
        }
        if (!isEquals) {
            log.debug("Diff has '{}' changed pixels in frame", changedPixelsNumber);
        }
        return isEquals;
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
