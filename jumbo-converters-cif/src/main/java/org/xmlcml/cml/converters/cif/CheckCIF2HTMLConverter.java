package org.xmlcml.cml.converters.cif;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Element;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;

public class CheckCIF2HTMLConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(CheckCIF2HTMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public Type getInputType() {
		return Type.HTML;
	}

	public Type getOutputType() {
		return Type.XML;
	}
	
	/**
	 * converts a CML object to XYZ. assumes a single CMLMolecule as descendant
	 * of root
	 * 
	 * @param in input stream
	 */
	@Override
	public Element convertToXML(Element xml) {
		CMLElement xmlOut = null;
		return xmlOut;
	}

	/**
	 * Post CIF off to the checkCif service and grabs the returned HTML
	 * @param cifPath
	 * @return
	 */
	public String calculateCheckcif(String cifPath) {
		PostMethod filePost = null;
		InputStream in = null;
		String checkcif = "";

		int maxTries = 5;
		int count = 0;
		boolean finished = false;
		try {
			while(count < maxTries && !finished) {
				count++;
				File f = new File(cifPath);
				filePost = new PostMethod(
				"http://dynhost1.iucr.org/cgi-bin/checkcif.pl");
				Part[] parts = { new FilePart("file", f),
						new StringPart("runtype", "fullpublication"),
						new StringPart("UPLOAD", "Send CIF for checking") };
				filePost.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
						new DefaultHttpMethodRetryHandler(5, false));
				filePost.setRequestEntity(new MultipartRequestEntity(parts,
						filePost.getParams()));
				HttpClient client = new HttpClient();
				int statusCode = client.executeMethod(filePost);
				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Could not connect to the IUCr Checkcif service.");
					continue;
				}
				in = filePost.getResponseBodyAsStream();
				checkcif = IOUtils.stream2String(in);
				in.close();
				if (checkcif.length() > 0) {
					finished = true;
				}
			}
		} catch (IOException e) {
			System.err.println("Error calculating checkcif.");
		} finally {
			if (filePost != null) {
				filePost.releaseConnection();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					System.err.println("Error closing InputStream: "+in);
				}
			}
		}
		return checkcif;
	}

	
//	/**
//	 * Calculate the checkCif for this CML and converts the result of the checkCif
//	 * into XML and appends this to the in process CML. Also downloads the ellipsoid plot which
//	 * is linked to from the checkCif html 
//	 * @param cml
//	 * @param pathMinusMime
//	 */
//	private void handleCheckcifs(CMLCml cml, String pathMinusMime) {
//		String depositedCheckcifPath = pathMinusMime.substring(0,pathMinusMime.lastIndexOf(File.separator));
//		String depCCParent = new File(depositedCheckcifPath).getParent();
//		depositedCheckcifPath = depCCParent+pathMinusMime.substring(pathMinusMime.lastIndexOf(File.separator),pathMinusMime.lastIndexOf("_"))+".deposited.checkcif.html";
//		String calculatedCheckcifPath = pathMinusMime+".calculated.checkcif.html";
//		File depositedCheckcif = new File(depositedCheckcifPath);
//		File calculatedCheckcif = new File(calculatedCheckcifPath);
//		// FIXME to work with CheckCIF
////		if (depositedCheckcif.exists()) {
////			String contents = CrystalEyeUtils.file2String(depositedCheckcifPath);
////			Document deposDoc = new CheckCifParser(contents).parseDeposited();
////			cml.appendChild(deposDoc.getRootElement().copy());
////		}
////		if (calculatedCheckcif.exists()) {
////			String contents = CrystalEyeUtils.file2String(calculatedCheckcifPath);
////			Document calcDoc = new CheckCifParser(contents).parseCalculated();
////			cml.appendChild(calcDoc.getRootElement().copy());
////			this.getPlatonImage(calcDoc, pathMinusMime);	
////		}
//	}

	@Override
	public int getConverterVersion() {
		return 0;
	}

}
