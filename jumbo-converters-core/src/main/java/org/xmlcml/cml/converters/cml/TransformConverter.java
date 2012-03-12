package org.xmlcml.cml.converters.cml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.text.Template;

/** this class takes XML input
 * 
 * @author pm286
 *
 */
public class TransformConverter extends AbstractTransformConverter {
	private static Logger LOG = Logger.getLogger(TransformConverter.class);

	private JumboParameter transformer = new JumboParameter(JumboParameter.TRANSFORMER, "", true);

	public TransformConverter() {
		super();
	}

	public TransformConverter(Element templateElement) {
		super(templateElement);
		this.setTemplate(new Template(templateElement));
	}

	private static void ensureStaticTemplate(String transformer) {
		if (transformTemplate == null) {
			try {
				InputStream templateStream = createTemplateStream(transformer);
				Element templateElement = new Builder().build(templateStream, createBaseURI()).getRootElement();
				transformTemplate = new Template(templateElement);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void convert(InputStream in, OutputStream out) {
		
		ensureStaticTemplate(transformer.getValue());
		try {
			Element inElement = new CMLBuilder().build(in).getRootElement();
			transformTemplate.applyMarkup(inElement);
			CMLUtil.debug(inElement, out, 0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected static InputStream createTemplateStream(String template) throws IOException {
		return new FileInputStream(template);
	}

	public static String createBaseURI() {
		return ".";
	}

	public static void main(String[] args) throws IOException {
		runMain(args);
	}
	
	public static void runMain(String[] args) throws IOException {
		if (args.length != 1) {
			usage();
		} else {
			String template = args[0];
			TransformConverter tc = TransformConverter.createTemplateConverter(new FileInputStream(template));
			File in = new File(args[0]);
			File out = new File(args[1]);
			tc.convert(in, out);
		}
	}

	private static void usage() {
		// TODO Auto-generated method stub
		
	}

	public static TransformConverter createTemplateConverter(InputStream templateStream) {
		Element templateElement = null;
		TransformConverter converter = null;
		try {
			templateElement = new Builder().build(templateStream, createBaseURI()).getRootElement();
			converter = new TransformConverter(templateElement);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create template: ", e);
		}
		return converter;
	}
	
	@Override
	public String getRegistryInputType() {
		return null;
	}
	
	@Override
	public String getRegistryOutputType() {
		return null;
	}
	
	@Override
	public String getRegistryMessage() {
		return "null";
	}


}