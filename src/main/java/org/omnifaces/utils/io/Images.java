package org.omnifaces.utils.io;

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
		boolean cropHorizontal = (image.getHeight() == desiredHeight);

		int x = cropHorizontal ? (image.getWidth() - desiredWidth) / 2 : 0;
		int y = cropHorizontal ? 0 : (image.getHeight() - desiredHeight) / 2;

		return image.getSubimage(x, y, desiredWidth, desiredHeight);
	}

	public static BufferedImage cropToSquareImage(BufferedImage image) {
		boolean cropHorizontal = image.getWidth() > image.getHeight();

		int largestSize = cropHorizontal ? image.getWidth() : image.getHeight();
		int smallestSize = cropHorizontal ? image.getHeight() : image.getWidth();

		int cropStart = (largestSize / 2) - (smallestSize / 2);

		int x = cropHorizontal ? cropStart : 0;
		int y = cropHorizontal ? 0 : cropStart;

		return image.getSubimage(x, y, smallestSize, smallestSize);
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