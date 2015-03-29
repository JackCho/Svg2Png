package me.ele.batik;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class Converter {
	private Transcoder transcoder = new PNGTranscoder();

	/**
	 * Transcodes an SVGResource into a PNG.
	 *
	 * @param svgResource
	 *            the input SVG
	 * @param density
	 *            the density to output the PNG; determines scaling
	 * @param destination
	 *            the output destination
	 * @param baseDensity
	 * 			  the base density
	 */
	void transcode(SVGResource svgResource, Density density, File destination, float baseDensity) {
		if (!svgResource.canBeRead()) {
			System.err.println("Cannot convert SVGResource "
					+ svgResource.getFileName() + "; file cannot be parsed");
			return;
		}
		
		float ratio = density.multiplier / baseDensity;
		int outWidth = Math.round(svgResource.getWidth() * ratio);
		int outHeight = Math.round(svgResource.getHeight() * ratio);
		transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, new Float(
				outWidth));
		transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, new Float(
				outHeight));

		String svgURI = svgResource.getFile().toURI().toString();
		TranscoderInput input = new TranscoderInput(svgURI);

		try {
			OutputStream outStream = new FileOutputStream(destination);
			TranscoderOutput output = new TranscoderOutput(outStream);
			transcoder.transcode(input, output);
			outStream.flush();
			outStream.close();
		} catch (TranscoderException e) {
			System.err.println("Could not transcode "
					+ svgResource.getFileName());
			destination.delete();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
