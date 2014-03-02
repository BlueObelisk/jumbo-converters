package org.xmlcml.cml.converters.cif;

import nu.xom.Document;
import nu.xom.ParsingException;
import org.apache.commons.io.IOUtils;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sam Adams
 */
public class CIFDictionary {

    private static final CIFDictionary INSTANCE;

    private CMLDictionary cifDictionary;
    private Map<String,CMLEntry> idMap;

    static {
        INSTANCE = new CIFDictionary();
    }

    public static CIFDictionary getInstance() {
        return INSTANCE;
    }

    private CIFDictionary()     {

        try {
            loadDict("/cif-dictionary.cml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void loadDict(String path) throws ParsingException, IOException {
        CMLBuilder builder = new CMLBuilder();
        InputStream is = findInput(path);
        Document doc ;
        try {
            doc = builder.build(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
        CMLDictionary dict = (CMLDictionary) doc.getRootElement();

        Map<String,CMLEntry> idMap = new HashMap<String,CMLEntry>();
        for (CMLEntry entry : dict.getEntryElements()) {
            idMap.put(entry.getId(), entry);
        }
        this.cifDictionary = dict;
        this.idMap = idMap;
    }

    private InputStream findInput(String path) throws FileNotFoundException {
        File file = new File(path);
        if (file.isFile()) {
            return new BufferedInputStream(new FileInputStream(file));
        }
        InputStream in = getClass().getResourceAsStream(path);
        if (in != null) {
            return in;
        }
        throw new FileNotFoundException("File not found: "+path);
    }


    public String getDataType(String id) {
        CMLEntry entry = idMap.get(id);
        if (entry != null) {
            return entry.getDataType();
        }
        return null;
    }

    public String getUnits(String id) {
        CMLEntry entry = idMap.get(id);
        if (entry != null) {
            return entry.getUnits();
        }
        return null;
    }

}
