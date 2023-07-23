package com.example.demo;

import com.example.demo.utils.BufferComparisonResult;
import com.example.demo.utils.RleEncoderDecoder;
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
import java.util.List;
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
		// hard coded file in resources
		String sourceGifPathName = "src/main/resources/source_gif_image.gif";
		try {
			inputFile = new File(sourceGifPathName);
			reader = ImageIO.getImageReadersByFormatName("gif").next();
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
			log.debug("GIF Image frame(s) number = [{}]", noi);
			// buffer to write output gif
			BufferedImage firstOutBuffer = null;
			// buffer to compare current and next frame
			BufferedImage nextOutBuffer = null;
			// buffer to write difference gif
			BufferedImage differenceBuffer = null;

			/// prepare output path
			Path outPath = Path.of("output");
			Path currentRelativePath = Paths.get("", outPath.toString());
			Path currentOut = currentRelativePath.toAbsolutePath();
			File dirToCheck = new File(currentOut.toString());
			log.debug("Checking if /output exists... : {}", dirToCheck);
			if (!dirToCheck.exists()) {
				Files.createDirectory(currentOut);
			}

			RleEncoderDecoder rleEncoderDecoder = new RleEncoderDecoder();
			// loop over GIF's internal frames
			for (int i = 0; i < noi; i++) {
				log.debug("Frame number = {}", i);
				BufferedImage image = reader.read(i);
				IIOMetadata metadata = reader.getImageMetadata(i);

				Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
//				Node tree = metadata.getAsTree("javax_imageio_1.0"); // doesn't work for me
//				Node tree = metadata.getAsTree("javax_imageio_gif_stream_1.0");// doesn't work for me
				NodeList children = tree.getChildNodes();
				log.trace("Image descriptor(s) number = {}", children.getLength());

				// loop over frame's descriptors
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
							differenceBuffer = new BufferedImage(
									imageAttr.get("imageWidth"), imageAttr.get("imageHeight"), BufferedImage.TYPE_INT_ARGB);
							firstOutBuffer.getGraphics().drawImage(
									image, imageAttr.get("imageLeftPosition"),
									imageAttr.get("imageTopPosition"), null);
							nextOutBuffer.getGraphics().drawImage(
									image, imageAttr.get("imageLeftPosition"),
									imageAttr.get("imageTopPosition"), null);
							differenceBuffer.getGraphics().drawImage(
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
				BufferComparisonResult comparisonResult =
						ImageBufferUtils.compareAndCalculateBufferedImagesEquality(
						firstOutBuffer, nextOutBuffer, differenceBuffer);
				// compare two frames
				if (comparisonResult.isEqual() && i != 0) {
					log.debug("Skipping EQUAL frame(s) at i = {}", i);
				} else {
					// write changed 'first' into separate GIF file
					ImageBufferUtils.writeFileImageFrame(firstOutBuffer, currentOut, i, "_source");
					if (i != 0) {
						// write 'difference' data as GIF file
						ImageBufferUtils.writeFileImageFrame(differenceBuffer, currentOut, i, "_diff");

						// compute RLE diff using int array
						Integer[] outputArray = comparisonResult.outputDifferenceResultArray();
						List<Integer> encodedData = rleEncoderDecoder.encodeArray(outputArray);

						// HERE was pass encodedData from Android App to Watches client...

						// decode RLE data on Android client
						List<Integer> decodedAgainData = rleEncoderDecoder.decodeArray(encodedData.toArray(Integer[]::new));

						// that is the NEW image composed of previous frame + RLE data applied to it
						BufferedImage alteredImage = ImageBufferUtils
								.applyRleDiffToBufferedImage(firstOutBuffer, decodedAgainData);
						ImageBufferUtils.writeFileImageFrame(alteredImage, currentOut, i, "_rle_composed");

						// check if images are equals (rle composed and next source original)
						if (ImageBufferUtils.isBufferedImageEqual(nextOutBuffer, alteredImage)) {
							String error = "WOW, something WORKS WRONG! Images MUST BE EQUAL at frame #" + i;
							log.error(error);
							throw new RuntimeException(error);
						}
					}
					// next frame has changes
					// copy content from 'next' to 'first'
					firstOutBuffer = ImageBufferUtils.deepCopy(nextOutBuffer);
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