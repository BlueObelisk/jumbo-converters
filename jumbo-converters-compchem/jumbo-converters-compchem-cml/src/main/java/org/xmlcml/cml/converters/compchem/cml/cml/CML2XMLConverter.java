package org.xmlcml.cml.converters.compchem.cml.cml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Element;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;
import org.xmlcml.cml.converters.text.TemplateConverter;

/** this class takes XML input (it should really be in CORE?)
 * 
 * @author pm286
 *
 */
public class CML2XMLConverter extends CompchemTemplateConverter {

	@Override
	public Type getInputType() {
		return Type.CML;
	}

	public CML2XMLConverter(Element templateElement) {
//		super(templateElement, "cml", "cml");
		super(templateElement);
	}

	/*
	 * (non-Javadoc)
	 * @see org.xmlcml.cml.converters.AbstractConverter#convertToXML(nu.xom.Element)
	 */
	@Override
	public Element convertToXML(Element element) {
		Element newElement = CMLElement.createCMLElement(element);
		template.applyMarkup(newElement);
		return newElement;
	}
	
	public static void main(String[] args) throws IOException {
		runMain(args, "cml", "cml", "topTemplate.xml");
	}
	
	public static void runMain(String[] args, String code, String fileType,
			String topTemplate) throws IOException {
		if (args.length != 2) {
			usage();
		} else {
			TemplateConverter tc = CML2XMLConverter.createTemplateConverter(code, fileType, topTemplate);
			File in = new File(args[0]);
			File out = new File(args[1]);
			tc.convert(in, out);
		}
	}

	public static TemplateConverter createTemplateConverter(InputStream templateStream, String codeBase, String fileType) {
		Element templateElement = null;
		TemplateConverter converter = null;
		try {
			templateElement = new Builder().build(templateStream, createBaseURI(codeBase, fileType)).getRootElement();
			converter = new CML2XMLConverter(templateElement);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create template: ", e);
		}
		return converter;
	}
	
	private static TemplateConverter createTemplateConverter(String code, String fileType, String topTemplate) throws IOException {
		InputStream templateStream = createTemplateStream(code, fileType, topTemplate);
		TemplateConverter tc = CML2XMLConverter.createTemplateConverter(templateStream, code, fileType);
		return tc;
	}

}