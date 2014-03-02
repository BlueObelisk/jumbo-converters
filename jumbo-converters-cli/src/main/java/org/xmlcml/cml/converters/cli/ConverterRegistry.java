package org.xmlcml.cml.converters.cli;

import java.io.InputStream;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterList;
import org.xmlcml.util.JumboException;

/**
 * @author Sam Adams
 */
public class ConverterRegistry {

    public static final String CONVERTER_FILE = "META-INF/jumbo-converters";

    private static final Map<Type,ConverterInfo> map = new LinkedHashMap<Type, ConverterInfo>();

    static {
        try {
        	ClassLoader ldr = ConverterRegistry.class.getClassLoader();

            Enumeration<URL> e = ldr.getResources(CONVERTER_FILE);
//            System.err.println("Classpath "+System.getProperty("java.class.path"));
            for (URL url : Collections.list(e)) {
                System.err.println("reading "+url);
                InputStream is = url.openStream();
                try {
                    for (String line : IOUtils.readLines(is)) {
                        int comment = line.indexOf('#');
                        if (comment >= 0) {
                            line = line.substring(0, comment);
                        }
                        String s = line.trim();
                        if (s.length() > 0) {
                            try {
                                Class<?> clazz = Class.forName(s);
                                ConverterList list = (ConverterList) clazz.newInstance();
                                for (ConverterInfo converterInfo : list.listConverters()) {
                                    register(converterInfo);
                                }
                            } catch (Exception ex) {
                                System.err.println("Error loading converter");
                                ex.printStackTrace();
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
    
    public static Map<Type,ConverterInfo> getMap() {
    	return map;
    }


    public static Converter findConverter(String intype, String outtype) {
        Type t = new Type(intype, outtype);
        ConverterInfo info = map.get(t);
        if (info != null) {
            try {
                return info.getConverterClass().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Error creating converter", e);
            }
        }
        return null;
    }


    public static List<ConverterInfo> getConverterList() {
        return new ArrayList<ConverterInfo>(map.values());
    }


    private static synchronized void register(ConverterInfo converterInfo) {
        String intype = converterInfo.getInType();
        String outtype = converterInfo.getOutType();
        if (intype != null && outtype != null) {
	        Type t = new Type(intype, outtype);
	        if (map.containsKey(t)) {
	            throw new IllegalArgumentException("Converter "+intype+" >> "+outtype+" already registered");
	        }
	        map.put(t, converterInfo);
        } else {
        	System.out.println("NULL types for "+converterInfo.getConverterClass()+" ("+intype+", "+outtype+")");
        }
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
                return (this.in == null || t.in == null || this.out == null || t.out == null) ? false : this.in.equals(t.in) && this.out.equals(t.out);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int inhash = (in != null) ? in.hashCode()*31 : 137;
            int outhash = (out != null) ? out.hashCode() : 0;
            return inhash + outhash;
        }

        public String toString() {
            return in + " >> " + out;
        }

    }

}
