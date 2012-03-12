package org.xmlcml.cml.converters.cml;

import java.io.InputStream;
import java.io.OutputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Serializer;

import org.lensfield.api.io.StreamOut;
import org.xmlcml.cml.base.CMLConstants;

public class XMLSplitter {

	private String xpath;
	private InputStream in;
//    private MultiStreamOut out;
    private OutputStream out;
	
	public void run() throws Exception {
//		Builder builder = new Builder();
//		Document doc = builder.build(in);
//		Nodes nodes = doc.query(xpath, CMLConstants.CML_XPATH);
//		for (int i = 0; i < nodes.size(); i++) {
//			Element element = (Element) nodes.get(i);
//			Element copy = (Element) element.copy();
//			StreamOut stream = out.next();
//			try {
//				Serializer ser = new Serializer(stream);
//				ser.write(new Document(copy));
//				ser.flush();
//			} finally {
//				stream.close();
//			}
//		}
		throw new RuntimeException("NYI");
	}
	
}
