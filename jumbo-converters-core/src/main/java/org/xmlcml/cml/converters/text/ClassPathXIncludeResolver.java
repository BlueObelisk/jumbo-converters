package org.xmlcml.cml.converters.text;

import nu.xom.*;

import java.io.IOException;
import java.io.InputStream;

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
            if (base.startsWith("classpath://")) {
                base = base.substring(12);
            } else {
                throw new RuntimeException("Unsupported base URI: "+base);
            }
            String uri = base+href;
            InputStream in = ClassPathXIncludeResolver.class.getResourceAsStream(uri);
            if (in == null) {
                throw new RuntimeException("cannot locate included file: "+uri);
            }
            Document doc;
            try {
                doc = builder.build(in);
            } finally {
                in.close();
            }
            resolveIncludes(doc, builder);

            Element include = doc.getRootElement();
            doc.setRootElement(new Element("foo"));
            ParentNode parent = element.getParent();
            include.setBaseURI("classpath://"+uri);
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
