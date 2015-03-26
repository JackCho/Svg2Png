package me.ele.batik;

import java.io.File;
import java.io.IOException;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.UnitProcessor;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

public class SVGResource {


    private File file;

    // Basic way to tell if we could read the file or not
    private boolean canBeRead = false;

    // Data read from the file, if it exists
    private int width;
    private int height;

    SVGResource(File file, int dpi) {
        this.file = file;

        readSvgInfo(dpi);
    }

    private void readSvgInfo(int dpi) {
        if (!file.exists()) {
            return;
        }

        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
        SVGDocument document;
        try {
            document = (SVGDocument) factory.createDocument(file.toURI().toString());
        }
        catch (IOException e) {
        	System.err.println("Could not read SVG resource" + file.getName());
            return;
        }

        SVGSVGElement svgElement = document.getRootElement();

        UserAgent userAgent = new DensityUserAgent(dpi);
        BridgeContext bridgeContext = new BridgeContext(userAgent);
        org.apache.batik.parser.UnitProcessor.Context context = UnitProcessor.createContext(bridgeContext, svgElement);

        width = (int) UnitProcessor.svgHorizontalLengthToUserSpace(svgElement.getWidth().getBaseVal().getValueAsString(), "",context);
        height = (int) UnitProcessor.svgVerticalLengthToUserSpace(svgElement.getWidth().getBaseVal().getValueAsString(), "",context);

        canBeRead = true;
    }

    private static final class DensityUserAgent extends UserAgentAdapter {

        private float pixelUnitToMillimeter;

        private DensityUserAgent(int dpi) {
            pixelUnitToMillimeter = (2.54f / dpi) * 10;
        }

        @Override
		public float getPixelUnitToMillimeter() {
            return pixelUnitToMillimeter;
        }
    }
    
    public boolean canBeRead() {
    	return canBeRead;
    }
    
    public String getFileName() {
    	return file.getName();
    }

	public File getFile() {
		return file;
	}

	public boolean isCanBeRead() {
		return canBeRead;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setFile(File file) {
		this.file = file;
	}
    
    
}
