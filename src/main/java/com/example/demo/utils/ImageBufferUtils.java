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

    public static boolean isBufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
        Objects.requireNonNull(img1, "img1 is NULL");
        Objects.requireNonNull(img2, "img2 is NULL");
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
            for (int x = 0; x < img1.getWidth(); x++) {
                for (int y = 0; y < img1.getHeight(); y++) {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y))
                        return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        Objects.requireNonNull(bi, "BufferedImage is NULL");
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static void writeFileImageFrame(BufferedImage firstOutBuffer, Path outPath, int i) throws IOException {
        Objects.requireNonNull(firstOutBuffer, "firstOutBuffer is NULL");
        Objects.requireNonNull(outPath, "outPath is NULL");
        File outputFile = new File(i + ".gif");
        Path outFullPath = Path.of(outPath.toString(), outputFile.getName());
        log.debug("File [{}] will be written to = '{}'", i, outFullPath);
        ImageIO.write(firstOutBuffer, "GIF", outFullPath.toFile());
        log.debug("Written frame '{}' to = '{}'", i, outFullPath);
    }

}
