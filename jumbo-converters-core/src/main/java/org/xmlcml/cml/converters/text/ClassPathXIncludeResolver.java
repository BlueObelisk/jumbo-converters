package org.xmlcml.cml.converters.text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Util;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParentNode;
import nu.xom.ParsingException;

/**
 * @author Sam Adams
 */
public class ClassPathXIncludeResolver {

	public final static Logger LOG = Logger.getLogger(ClassPathXIncludeResolver.class);
    public static final String CLASSPATH = "classpath:";
	private static final String HREF = "href";
	public final static String XINCLUDE_NS = "http://www.w3.org/2001/XInclude";

    public static void resolveIncludes(Document document) throws IOException, ParsingException {
        resolveIncludes(document, new Builder());
    }

    public static void resolveIncludes(Document document, Builder builder) throws IOException, ParsingException {
        resolve(document.getRootElement(), builder);
    }

    private static void resolve(Element element, Builder builder) throws IOException, ParsingException {
        if (isIncludeElement(element)) {
            String href = element.getAttributeValue(HREF);
            String base = element.getBaseURI();
            URI u = URI.create(base).resolve(href);
            String uri = u.toString();
            if (uri.startsWith(CLASSPATH+"//")) {
                uri = uri.substring((CLASSPATH+"//").length());
            } else if (uri.startsWith(CLASSPATH)) {
                    uri = uri.substring(CLASSPATH.length());
            } else {
                throw new RuntimeException("Unsupported XInclude URI (should start with "+CLASSPATH+", base="+base+"): "+uri);
            }
//            LOG.debug("resolving: "+uri);
            // don't know why this doesn't work
            InputStream in = ClassPathXIncludeResolver.class.getResourceAsStream(uri);
            if (in == null) {
            	in =Util.getInputStreamFromResource(uri);
            }
            if (in == null) {
                throw new RuntimeException("cannot locate included file: "+uri);
            }
            Document doc;
            try {
                doc = builder.build(in);
            } catch (Exception e) {
            	throw new RuntimeException("cannot parse: "+uri, e);
            } finally {
                in.close();
            }

            doc.setBaseURI(u.toString());
            resolveIncludes(doc, builder);

            Element include = doc.getRootElement();
            doc.setRootElement(new Element("foo"));
            ParentNode parent = element.getParent();
            parent.replaceChild(element, include);

        } else {
            Elements children = element.getChildElements();
            for (int i = 0; i < children.size(); i++) {
                resolve(children.get(i), builder);
            }
        }
    }


    private static boolean isIncludeElement(Element element) {
        return element.getLocalName().equals("include")  && element.getNamespaceURI().equals(XINCLUDE_NS);
    }
    
    /**
     * create classpath from class/package name
     * leaves / at end
     * "my.org" = >> "my/org/"
     * @param clazz
     * @return classpath
     */
    public static String createClasspath(Class clazz) {
    	String pckage = clazz.getPackage().getName();
    	String s = pckage.toString();
    	s = s.replaceAll("\\.", "/")+"/";
    	return s;
    }

}
