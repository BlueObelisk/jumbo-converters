package org.xmlcml.cml.converters.registry;

import org.apache.commons.io.IOUtils;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.util.JumboException;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @author Sam Adams
 */
public class ConverterRegistry {

    public static final String CONVERTER_FILE = "META-INF/jumbo-converters";

    private static final Map<Type,ConverterInfo> map = new LinkedHashMap<Type, ConverterInfo>();

    static {
        try {
            ClassLoader ldr = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> e = ldr.getResources(CONVERTER_FILE);
            for (URL url : Collections.list(e)) {
                InputStream is = url.openStream();
                try {
                    for (String line : IOUtils.readLines(is)) {
                        int comment = line.indexOf('#');
                        if (comment >= 0) {
                            line = line.substring(0, comment);
                        }
                        String s = line.trim();
                        if (s.length() > 0) {
                            Class<ConverterList> clazz = (Class<ConverterList>) Class.forName(s);
                            ConverterList list = clazz.newInstance();
                            for (ConverterInfo converter : list.listConverters()) {
                                register(converter);
                            }
                        }
                    }
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading converter files");
            e.printStackTrace();
        }
    }


    public static Converter findConverter(String intype, String outtype) {
        Type t = new Type(intype, outtype);
        ConverterInfo info = map.get(t);
        if (info != null) {
            try {
                return info.getConverterClass().newInstance();
            } catch (Exception e) {
                throw new JumboException("Error creating converter", e);
            }
        }
        return null;
    }


    public static List<ConverterInfo> getConverterList() {
        return new ArrayList<ConverterInfo>(map.values());
    }


    private static synchronized void register(ConverterInfo converter) {
        String intype = converter.getInType();
        String outtype = converter.getOutType();
        Type t = new Type(intype, outtype);
        if (map.containsKey(t)) {
            throw new IllegalArgumentException("Converter "+intype+" >> "+outtype+" already registered");
        }
        map.put(t, converter);
    }


    public static class Type {

        private final String in, out;

        Type(String in, String out) {
            this.in = in;
            this.out = out;
        }

        public String getIn() {
            return in;
        }

        public String getOut() {
            return out;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof Type) {
                Type t = (Type) o;
                return this.in.equals(t.in) && this.out.equals(t.out);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return in.hashCode()*31 + out.hashCode();
        }

        public String toString() {
            return in + " >> " + out;
        }

    }

}
