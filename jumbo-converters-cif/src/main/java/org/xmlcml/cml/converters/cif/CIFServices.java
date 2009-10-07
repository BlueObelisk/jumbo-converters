package org.xmlcml.cml.converters.cif;

import static org.xmlcml.cml.base.CMLConstants.CML_NS;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Text;
import nu.xom.XPathContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLCml;


public class CIFServices {

	private static final Logger LOG = Logger.getLogger(CIFServices.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public CIFServices() {
		clearVars();
	}
	
	private void clearVars() {
		
	}
	
	/**
	 * Extracts the URL of the ellipsoid plot from the checkCif and downloads it
	 * @param doc
	 * @param pathMinusMime
	 */
	@SuppressWarnings("unused")
	private void getPlatonImage(Document doc, String pathMinusMime) {
		// get platon from parsed checkcif/store
		Nodes platonLinks = doc.query("//x:checkCif/x:calculated/x:dataBlock/x:platon/x:link", new XPathContext("x", "http://journals.iucr.org/services/cif"));
		if (platonLinks.size() > 0) {
			URL url = null;
			try {
				String imageLink = platonLinks.get(0).getValue();
				String prefix = imageLink.substring(0, imageLink.lastIndexOf("/")+1);
				String file = imageLink.substring(imageLink.lastIndexOf("/")+1);
				url = new URL(prefix+URLEncoder.encode(file,"UTF-8")); 
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			BufferedImage image = null;
			try {
				image = ImageIO.read(url);
				image = image.getSubimage(14, 15, 590, 443);
				ImageIO.write(image, "jpeg", new File(pathMinusMime+".platon.jpeg"));
			} catch (IOException e) {
				System.err.println("ERROR: could not read PLATON image");
			}
		}	
	}
	
	/**
	 * Adds an element containing the article DOI
	 * @param cml
	 * @param pathMinusMime
	 */
	public void addDoi(CMLCml cml, String pathMinusMime) {
		String parent = pathMinusMime.substring(0,pathMinusMime.lastIndexOf(File.separator));
		parent = new File(parent).getParent();
		String doiPath = parent+pathMinusMime.substring(pathMinusMime.lastIndexOf(File.separator),pathMinusMime.lastIndexOf("_"));
		doiPath = doiPath.replaceAll("sup[\\d]*", "")+".doi";
		if (new File(doiPath).exists()) {
			String doiString = CrystalEyeUtils.file2String(doiPath);
			Element doi = new Element("scalar", CML_NS);
			doi.addAttribute(new Attribute("dictRef", "idf:doi"));
			doi.appendChild(new Text(doiString));
			cml.appendChild(doi);
		}
	}

}
