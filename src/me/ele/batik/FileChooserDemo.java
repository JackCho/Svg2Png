package me.ele.batik;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


public class FileChooserDemo extends JPanel implements ActionListener {

	private static final long serialVersionUID = -7147519751622735793L;
	
	private static final String newline = "\n";
	private JButton openButton;
	private JTextArea log;
	private JFileChooser fc;
	
	private String svg_folder;
	private static final int DPI = 72;

	public FileChooserDemo() {
		super(new BorderLayout());

		// Create the log first, because the action listeners
		// need to refer to it.
		log = new JTextArea();
		log.setMargin(new Insets(15, 15, 15, 15));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);
		logScrollPane.setSize(1500, 1500);

		fc = new JFileChooser();
		// fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		openButton = new JButton("打开一个SVG文件或文件夹");
		openButton.addActionListener(this);

		JPanel buttonPanel = new JPanel(); // use FlowLayout
		buttonPanel.add(openButton);

		// Add the buttons and the log to this panel.
		add(buttonPanel, BorderLayout.PAGE_START);
		add(logScrollPane, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		
		
		int returnVal = fc.showOpenDialog(FileChooserDemo.this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File seleFile = fc.getSelectedFile();
		// This is where a real application would open the file.
		String str = "Opening: ";
		append(str + seleFile.getAbsolutePath() + "." + newline);
		log.setCaretPosition(log.getDocument().getLength());
		
		File folder = fc.getSelectedFile();
		if (!folder.isDirectory()) {
			if (!folder.getAbsolutePath().endsWith(".svg")) {
				append(folder.getAbsolutePath() + " is not a svg file" + newline);
			} else {
				svg_folder = folder.getParent();
				convertOneSvgFile(new Converter(), folder);
			}
			return;
		}
		
		svg_folder = folder.getAbsolutePath();
		
		File[] files = folder.listFiles();
		if (files == null || files.length == 0) {
			append(folder.getAbsolutePath() + " does not have a file" + newline);
			return;
		}
		
		Converter converter = new Converter();
		for (File file : files) {
			if (!file.getName().endsWith(".svg")) {
				append(file.getAbsolutePath() + " is not a svg file" + newline);
				continue;
			}
			
			convertOneSvgFile(converter, file);
		}

	}

	private void convertOneSvgFile(Converter converter, File file) {
		SVGResource svgResource = new SVGResource(file, DPI);
		for (Density density : Density.values()) {
			append(file.getName() + " convert to " + density.name() + " start" + newline);
			File destination = new File(getResourceDir(density), getDestinationFile(file.getName()));
			converter.transcode(svgResource, density, destination);
			append(file.getName() + " convert to " + density.name() + " finish" + newline);
		}
		append(file.getName() + " convert all finished" + newline);
	}


	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("svg批量转png");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState( Frame.MAXIMIZED_BOTH );

		// Add content to the window.
		frame.add(new FileChooserDemo());

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}
	
	private File getResourceDir(Density density) {
		File file = new File(svg_folder, "/drawable-" + density.name().toLowerCase());
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	private String getDestinationFile(String name) {
		int suffixStart = name.lastIndexOf('.');
		return suffixStart == -1 ? name : name.substring(0, suffixStart) + ".png";
	}
	
	private void append(String text) {
		log.append(text);
		log.setCaretPosition(log.getDocument().getLength());
	}
}
