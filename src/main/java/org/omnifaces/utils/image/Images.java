/*
 * Copyright 2018 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import static javax.imageio.ImageIO.read;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public final class Images {

	private Images() {
		//
	}

	public static BufferedImage toBufferedImage(byte[] content) throws IOException {
		return read(new ByteArrayInputStream(content));
	}

	public static byte[] toPng(BufferedImage image) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(image, "png", output);
		return output.toByteArray();
	}

	public static byte[] toJpg(BufferedImage image) throws IOException {
		// Start with a white layer to have images with an alpha layer handled correctly.
		BufferedImage newBufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		newBufferedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

		// Manually get the ImageWriter to be able to adjust quality
		ImageWriter writer = ImageIO.getImageWritersBySuffix("jpg").next();
		ImageWriteParam imageWriterParam = writer.getDefaultWriteParam();
		imageWriterParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		imageWriterParam.setCompressionQuality(1f);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		writer.setOutput(new MemoryCacheImageOutputStream(output));
		writer.write(null, new IIOImage(newBufferedImage, null, null), imageWriterParam);
		writer.dispose();

		return output.toByteArray();
	}

	public static BufferedImage cropImage(BufferedImage image, int desiredWidth, int desiredHeight) {
		boolean cropHorizontally = (image.getWidth() > desiredWidth);

		int x = cropHorizontally ? (image.getWidth() - desiredWidth) / 2 : 0;
		int y = cropHorizontally ? 0 : (image.getHeight() - desiredHeight) / 2;

		return image.getSubimage(x, y, desiredWidth, desiredHeight);
	}

	/**
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

		double currentAspectRatio = image.getWidth() * 1.0 / image.getHeight();

		if (currentAspectRatio == desiredAspectRatio) {
			return image;
		}

		boolean cropHorizontally = (currentAspectRatio > desiredAspectRatio);

		int desiredWidth = cropHorizontally ? (int) (image.getHeight() * desiredAspectRatio) : image.getWidth();
		int desiredHeight = cropHorizontally ? image.getHeight() : (int) (image.getWidth() / desiredAspectRatio);

		return cropImage(image, desiredWidth, desiredHeight);
	}

	public static BufferedImage cropToSquareImage(BufferedImage image) {
		boolean cropHorizontally = (image.getWidth() > image.getHeight());

		int desiredSize = cropHorizontally ? image.getHeight() : image.getWidth();

		return cropImage(image, desiredSize, desiredSize);
	}

	public static BufferedImage progressiveBilinearDownscale(BufferedImage image, int desiredWidth, int desiredHeight) {
		BufferedImage rescaledImage = image;

		while (rescaledImage.getWidth() > desiredWidth || rescaledImage.getHeight() > desiredHeight) {
			int nextWidth = max(rescaledImage.getWidth() / 2, desiredWidth);
			int nextHeight = max(rescaledImage.getHeight() / 2, desiredHeight);

			BufferedImage nextScaledImage = new BufferedImage(nextWidth, nextHeight, image.getTransparency() == OPAQUE ? TYPE_INT_RGB : TYPE_INT_ARGB);
			Graphics2D graphics = nextScaledImage.createGraphics();
			graphics.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
			graphics.drawImage(rescaledImage, 0, 0, nextWidth, nextHeight, null);
			graphics.dispose();

			rescaledImage = nextScaledImage;
		}

		return rescaledImage;
	}

}