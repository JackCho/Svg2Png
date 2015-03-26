package me.ele.batik;

import java.io.File;

public class Main {

	private static final String SVG_FOLDER = "/Users/caoyubin/Desktop/test";
	private static final int DPI = 72;

	public static void main(String[] args) {
		
		File folder = new File(SVG_FOLDER);
		if (!folder.exists()) {
			throw new RuntimeException(SVG_FOLDER + " does not exists");
		}

		if (!folder.isDirectory()) {
			throw new RuntimeException(SVG_FOLDER + " is not a folder");
		}

		File[] files = folder.listFiles();
		if (files == null || files.length == 0) {
			throw new RuntimeException(SVG_FOLDER + " does not have a file");
		}

		Converter converter = new Converter();
		for (File file : files) {
			if (!file.getName().endsWith(".svg")) {
				continue;
			}
			SVGResource svgResource = new SVGResource(file, DPI);
			for (Density density : Density.values()) {
				File destination = new File(getResourceDir(density), getDestinationFile(file.getName()));
				converter.transcode(svgResource, density, destination);
			}

		}

	}

	private static File getResourceDir(Density density) {
		File file = new File(SVG_FOLDER, "/drawable-" + density.name().toLowerCase());
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	private static String getDestinationFile(String name) {
		int suffixStart = name.lastIndexOf('.');
		return suffixStart == -1 ? name : name.substring(0, suffixStart)
				+ ".png";
	}
}
