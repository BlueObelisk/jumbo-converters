package org.xmlcml.cml.converters.text;

import nu.xom.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author Sam Adams
 */
public class ClassPathXIncludeResolver {

    public final static String XINCLUDE_NS = "http://www.w3.org/2001/XInclude";

    public static void resolveIncludes(Document document) throws IOException, ParsingException {
        resolveIncludes(document, new Builder());
    }

    public static void resolveIncludes(Document document, Builder builder) throws IOException, ParsingException {
        resolve(document.getRootElement(), builder);
    }

    private static void resolve(Element element, Builder builder) throws IOException, ParsingException {
        if (isIncludeElement(element)) {
            String href = element.getAttributeValue("href");
            String base = element.getBaseURI();
            URI u = URI.create(base).resolve(href);
            String uri = u.toString();
            if (uri.startsWith("classpath:")) {
                uri = uri.substring(10);
            } else {
                throw new RuntimeException("Unsupported XInclude URI: "+uri);
            }
            InputStream in = ClassPathXIncludeResolver.class.getResourceAsStream(uri);
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

}
