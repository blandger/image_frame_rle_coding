package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.example.demo.utils.ImageBufferUtils;

@Slf4j
public class DemoApplication {
	static String[] imageatt = new String[]{
			"imageLeftPosition",
			"imageTopPosition",
			"imageWidth",
			"imageHeight"
	};

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		log.debug("DemoApp is starting...");

		// source image preparation
		ImageReader reader = null;
		File inputFile = null;
		try {
			reader = ImageIO.getImageReadersByFormatName("gif").next();
			String sourceGifPathName = "src/main/resources/source_gif_image.gif";
			inputFile = new File(sourceGifPathName);
			log.debug("Image path is '{}'", inputFile.getAbsolutePath());
		} catch (Exception e) {
			log.error("Source image preparation failed with error, sorry...");
			e.printStackTrace();
		}

		try (
				ImageInputStream ciis = ImageIO.createImageInputStream(inputFile);
		) {
			reader.setInput(ciis, false);

			int noi = reader.getNumImages(true);
			log.debug("Image frame(s) number = [{}]", noi);
			BufferedImage firstOutBuffer = null;
			BufferedImage nextOutBuffer = null;

			/// prepare output path
			Path outPath = Path.of("output");
			Path currentRelativePath = Paths.get("", outPath.toString());
			Path currentOut = currentRelativePath.toAbsolutePath();
			File dirToCheck = new File(currentOut.toString());
			log.debug("Checking if /output exists... : {}", dirToCheck);
			if (!dirToCheck.exists()) {
				Files.createDirectory(currentOut);
			}

			for (int i = 0; i < noi; i++) {
				log.debug("Frame number = {}", i);
				BufferedImage image = reader.read(i);
				IIOMetadata metadata = reader.getImageMetadata(i);

				Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
//				Node tree = metadata.getAsTree("javax_imageio_1.0");
//				Node tree = metadata.getAsTree("javax_imageio_gif_stream_1.0");
				NodeList children = tree.getChildNodes();
				log.trace("Image descriptor(s) number = {}", children.getLength());

				for (int j = 0; j < children.getLength(); j++) {
					Node nodeItem = children.item(j); // image attribute
					String nodeName = nodeItem.getNodeName(); // image attribute name
					log.trace("Node attribute item = [{}], name = {}", j, nodeName);

					if (nodeName.equals("ImageDescriptor")) {
						// only image descriptor is needed
						Map<String, Integer> imageAttr = new HashMap<>();

						for (int k = 0; k < imageatt.length; k++) {
							NamedNodeMap attr = nodeItem.getAttributes();
							Node attnode = attr.getNamedItem(imageatt[k]);
							imageAttr.put(imageatt[k], Integer.valueOf(attnode.getNodeValue()));
						}
						if (i == 0) {
							log.trace("Fill two buffers = [{}]", i);
							firstOutBuffer = new BufferedImage(
									imageAttr.get("imageWidth"), imageAttr.get("imageHeight"), BufferedImage.TYPE_INT_ARGB);
							nextOutBuffer = new BufferedImage(
									imageAttr.get("imageWidth"), imageAttr.get("imageHeight"), BufferedImage.TYPE_INT_ARGB);
							firstOutBuffer.getGraphics().drawImage(
									image, imageAttr.get("imageLeftPosition"),
									imageAttr.get("imageTopPosition"), null);
							nextOutBuffer.getGraphics().drawImage(
									image, imageAttr.get("imageLeftPosition"),
									imageAttr.get("imageTopPosition"), null);
						} else {
							log.trace("Fill NEXT buffer = [{}]", i);
							// read only next frame
							nextOutBuffer.getGraphics().drawImage(
									image, imageAttr.get("imageLeftPosition"),
									imageAttr.get("imageTopPosition"), null);
						}
					}
				}
				// compare two frames
				if (ImageBufferUtils.isBufferedImagesEqual(firstOutBuffer, nextOutBuffer) && i != 0) {
					log.debug("Skipping EQUAL frame(s) at i = {}", i);
				} else {
					// next frame has changes
					// copy content from 'next' to 'first'
					firstOutBuffer = ImageBufferUtils.deepCopy(nextOutBuffer);
					// write changed 'first' into separate GIF file
					ImageBufferUtils.writeFileImageFrame(firstOutBuffer, currentOut, i);
				}
			}
			long end = System.currentTimeMillis();
			long elapsed = end - start;
			log.debug("DemoApp finished in '{}' msec ('{}' seconds)", elapsed, elapsed / 1000);
		} catch (IOException e) {
			log.error("App has failed with error, sorry...");
			e.printStackTrace();
		}
	}

}