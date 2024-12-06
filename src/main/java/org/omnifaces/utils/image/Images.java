/*
 * Copyright 2021 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.utils.image;

import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;
import static java.awt.Transparency.OPAQUE;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.max;
import static java.util.stream.IntStream.range;
import static javax.imageio.ImageIO.read;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public final class Images {

	private Images() {
		//
	}

	public static BufferedImage toBufferedImage(byte[] content) throws IOException {
		return read(new ByteArrayInputStream(content));
	}

	public static byte[] toPng(BufferedImage image) throws IOException {
		var output = new ByteArrayOutputStream();
		ImageIO.write(image, "png", output);
		return output.toByteArray();
	}

	public static byte[] toJpg(BufferedImage image) throws IOException {
		// Start with a white layer to have images with an alpha layer handled correctly.
		var newBufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		newBufferedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

		// Manually get the ImageWriter to be able to adjust quality
		var writer = ImageIO.getImageWritersBySuffix("jpg").next();
		var imageWriterParam = writer.getDefaultWriteParam();
		imageWriterParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		imageWriterParam.setCompressionQuality(1f);

		var output = new ByteArrayOutputStream();
		writer.setOutput(new MemoryCacheImageOutputStream(output));
		writer.write(null, new IIOImage(newBufferedImage, null, null), imageWriterParam);
		writer.dispose();

		return output.toByteArray();
	}

	public static BufferedImage cropImage(BufferedImage image, int desiredWidth, int desiredHeight) {
		var cropHorizontally = image.getWidth() > desiredWidth;

		var x = cropHorizontally ? (image.getWidth() - desiredWidth) / 2 : 0;
		var y = cropHorizontally ? 0 : (image.getHeight() - desiredHeight) / 2;

		return image.getSubimage(x, y, desiredWidth, desiredHeight);
	}

	/*
	 * Examples of aspect ratios:
	 * 1:1 = 1.0 (will delegate to cropToSquareImage())
	 * 4:3 = 1.33333
	 * 3:2 = 1.5
	 * 16:9 = 1.77778
	 */
	public static BufferedImage cropImage(BufferedImage image, double desiredAspectRatio) {
		if (desiredAspectRatio == 1.0) {
			return cropToSquareImage(image);
		}

		var currentAspectRatio = image.getWidth() * 1.0 / image.getHeight();

		if (currentAspectRatio == desiredAspectRatio) {
			return image;
		}

		var cropHorizontally = currentAspectRatio > desiredAspectRatio;
		var desiredWidth = cropHorizontally ? (int) (image.getHeight() * desiredAspectRatio) : image.getWidth();
		var desiredHeight = cropHorizontally ? image.getHeight() : (int) (image.getWidth() / desiredAspectRatio);
		return cropImage(image, desiredWidth, desiredHeight);
	}

	public static BufferedImage cropToSquareImage(BufferedImage image) {
		var cropHorizontally = image.getWidth() > image.getHeight();
		var desiredSize = cropHorizontally ? image.getHeight() : image.getWidth();
		return cropImage(image, desiredSize, desiredSize);
	}

	public static BufferedImage progressiveBilinearDownscale(BufferedImage image, int desiredWidth, int desiredHeight) {
		var rescaledImage = image;

		while (rescaledImage.getWidth() > desiredWidth || rescaledImage.getHeight() > desiredHeight) {
			var nextWidth = max(rescaledImage.getWidth() / 2, desiredWidth);
			var nextHeight = max(rescaledImage.getHeight() / 2, desiredHeight);
			var nextScaledImage = new BufferedImage(nextWidth, nextHeight, image.getTransparency() == OPAQUE ? TYPE_INT_RGB : TYPE_INT_ARGB);
			var graphics = nextScaledImage.createGraphics();
			graphics.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
			graphics.drawImage(rescaledImage, 0, 0, nextWidth, nextHeight, null);
			graphics.dispose();
			rescaledImage = nextScaledImage;
		}

		return rescaledImage;
	}

    public static BufferedImage grayscale(BufferedImage image) {
        var grayscale = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        var g = grayscale.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return grayscale;
    }

    /**
     * https://apiumhub.com/tech-blog-barcelona/introduction-perceptual-hashes-measuring-similarity/
     */
	public static String computePerceptualHash(BufferedImage image, int size, int base) {
        if (size < 8 || size > 32) {
            throw new IllegalArgumentException("size " + size + " must be between 8 and 32");
        }
        if (base != 2 && base != 10 && base != 16 && base != 32 && base != 36 && base != 64) {
            throw new IllegalArgumentException("base " + base + " must be 2, 10, 16, 32, 36 or 64");
        }

        var grayscale = grayscale(progressiveBilinearDownscale(image, size, size));
        Consumer<Consumer<Integer>> forEachPixel = perPixel -> range(0, size).forEach(x -> range(0, size).forEach(y -> perPixel.accept(grayscale.getRGB(x, y) & 0xFF)));
        var totalPixelValue = new AtomicInteger();
        forEachPixel.accept(pixel -> totalPixelValue.addAndGet(pixel));
        var averagePixelValue = totalPixelValue.get() / (size * size);
        var perceptualHash = new StringBuilder();
        forEachPixel.accept(pixel -> perceptualHash.append(pixel > averagePixelValue ? "1" : "0"));
        var hash = perceptualHash.toString();

        if (base == 2) {
            return hash;
        } else {
            var integer = new BigInteger(hash, 2);

            if (base == 64) {
                return Base64.getEncoder().encodeToString(integer.toByteArray());
            } else {
                return integer.toString(base);
            }
        }
    }
}
