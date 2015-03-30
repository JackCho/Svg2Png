package me.ele.batik;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FileChooserDemo extends JPanel implements ActionListener {

	private static final long serialVersionUID = -7147519751622735793L;

	private static final String newline = "\n";
	private JSpinner spinner;
	private JButton openButton;
	private JButton startButton;
	private JTextArea logTextArea;
	private JFileChooser fileChooser;

	private String svgFolder;
	private static final int DPI = 72;
	private float baseDensity = Density.MDPI.getMultiplier();

	public FileChooserDemo(Container pane) {
		super(new BorderLayout());

		pane.setSize(700, 700);
		JPanel card = new JPanel();
		card.setPreferredSize(new Dimension(1000, 50));

		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		openButton = new JButton("打开一个SVG文件或文件夹");
		openButton.addActionListener(this);

		startButton = new JButton("开始转换");
		startButton.addActionListener(this);

		SpinnerModel spinnerModel = new SpinnerListModel(Density.values());
		spinnerModel.setValue(Density.MDPI);
		spinner = new JSpinner(spinnerModel);
		if (spinner.getEditor() instanceof JSpinner.DefaultEditor) {
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner
					.getEditor();
			editor.getTextField().setEnabled(true);
			editor.getTextField().setEditable(false);
		}
		JComponent field = ((JSpinner.DefaultEditor) spinner.getEditor());
		Dimension prefSize = field.getPreferredSize();
		prefSize = new Dimension(200, prefSize.height);
		field.setPreferredSize(prefSize);
		spinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Density density = (Density) spinner.getValue();
				baseDensity = density.getMultiplier();
			}
		});

		card.add(spinner);
		card.add(openButton);
		card.add(startButton);

		logTextArea = new JTextArea();
		logTextArea.setMargin(new Insets(15, 15, 15, 15));
		logTextArea.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(logTextArea);
		logScrollPane.setPreferredSize(new Dimension(1000, 500));

		pane.add(card, BorderLayout.PAGE_START);
		pane.add(logScrollPane, BorderLayout.PAGE_END);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openButton) {
			int returnVal = fileChooser.showOpenDialog(FileChooserDemo.this);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File seleFile = fileChooser.getSelectedFile();
			String str = "Opening: ";
			append(str + seleFile.getAbsolutePath() + "." + newline);
			logTextArea.setCaretPosition(logTextArea.getDocument().getLength());

			File folder = fileChooser.getSelectedFile();
			if (!folder.isDirectory()) {
				if (!folder.getAbsolutePath().endsWith(".svg")) {
					append(folder.getAbsolutePath() + " is not a svg file"
							+ newline);
				}
			}

			svgFolder = folder.getAbsolutePath();

		} else if (e.getSource() == startButton) {
			if (svgFolder == null) {
				JOptionPane.showMessageDialog(null, "请先选择文件", "提示: ",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			Converter converter = new Converter();
			File folder = new File(svgFolder);
			if (!folder.isDirectory()) {
				convertOneSvgFile(converter, folder);
				return;
			}
			
			File[] files = folder.listFiles();
			if (files == null || files.length == 0) {
				String string = " does not have a svg file";
				append(folder.getAbsolutePath() + string + newline);
				return;
			}

			for (File file : files) {

				if (!file.getName().endsWith(".svg")) {
					append(file.getAbsolutePath() + " is not a svg file"
							+ newline);
					continue;
				}

				convertOneSvgFile(converter, file);
			}
		}

	}

	private void convertOneSvgFile(Converter converter, File file) {
		SVGResource svgResource = new SVGResource(file, DPI);
		for (Density density : Density.values()) {
			File destination = new File(getResourceDir(density),
					getDestinationFile(file.getName()));
			converter.transcode(svgResource, density, destination, baseDensity);
			append(file.getName() + " convert to " + density.name() + " finish"
					+ newline);
		}
		append(file.getName() + " convert all finished" + newline);
	}

	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("svg批量转png");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setExtendedState(Frame.MAXIMIZED_BOTH);

		// Add content to the window.
		frame.add(new FileChooserDemo(frame.getContentPane()));

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
		File folder = new File(svgFolder);
		if (!folder.isDirectory()) {
			svgFolder = folder.getParent();
		}
		File file = new File(svgFolder, "/drawable-"
				+ density.name().toLowerCase());
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	private String getDestinationFile(String name) {
		int suffixStart = name.lastIndexOf('.');
		return suffixStart == -1 ? name : name.substring(0, suffixStart)
				+ ".png";
	}

	private void append(String text) {
		logTextArea.append(text);
		logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
	}
}
