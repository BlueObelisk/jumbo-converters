package org.xmlcml.cml.converters.cif;
         
import static org.xmlcml.euclid.EuclidConstants.S_NEWLINE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cif.CIF;
import org.xmlcml.cif.CIFException;
import org.xmlcml.cif.CIFParser;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;

/** converts CML to from CIF
 * 
 * @author pm286
 *
 */
public class CIF2CIFXMLConverter extends AbstractConverter {
	
	private static final Logger LOG = Logger
	.getLogger(CIF2CIFXMLConverter.class);
	
    CIFParser parser = null;
    
    // control fields
    private boolean echoInput = false;
    private boolean debug = false;
    private boolean noGlobal = false;
    private boolean mergeGlobal = false;
    private boolean checkDuplicates = false;
    private boolean trimElements = true;
    private boolean skipHeader = false;
    private boolean skipErrors = false;
    private boolean checkDoubles = false;

	public Type getInputType() {
		return Type.CIF;
	}
	
	public Type getOutputType() {
		return Type.XML;
	}
	
	/**
	 * converts a CIF object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param lines
	 */
	public Element convertToXML(List<String> lines) {
		CIF cifxml = parseLegacy(lines);
		// force ID attribute from file
		if (fileId != null) {
			
			// kept in in case the code below doesn't work
			//cifxml.addAttribute(new Attribute("id", fileId));

			try {
				cifxml.setId(fileId);
			} catch (CIFException e) {
				// TODO Auto-generated catch block
				LOG.error("CIF id bad: " + e.getMessage()) ;
			}
		}
		return cifxml;
	}
	
    /** constructor.
	 *
	 */
	public CIF2CIFXMLConverter() {
		init();
	}
	
    protected void init() {
    	
        parser = null;
        echoInput = false;
        debug = false;
        noGlobal = false;
        mergeGlobal = false;
        checkDuplicates = false;
        skipHeader = false;
        skipErrors = false;
        trimElements = true;
        checkDoubles = false;
        trimElements = true;
	}
    
//    /** control parser via a String.
//     * the values and actions are described in the Control enum.
//     * @param controlStrings must be value of an enum.
//     */
//    private void setControls(String... controlStrings) {
//        for (String controlString : controlStrings) {
//            Control control = null;
//            for (Control c : Control.values()) {
//                if (c.name().equals(controlString)) {
//                    control = c;
//                    break;
//                }
//            }
//            if (control == null) {
//                throw new RuntimeException("Bad control string: "+controlString);
//            } else if (control.equals(Control.NO_GLOBAL)) {
//                noGlobal = true;
//            } else if (control.equals(Control.MERGE_GLOBAL)) {
//                mergeGlobal = true;
//            } else if (control.equals(Control.DEBUG)) {
//                debug = true;
////                if (parser != null) {
////                    parser.setDebug(debug);
////                }
//            } else if (control.equals(Control.ECHO_INPUT)) {
//                echoInput = true;
////                if (parser != null) {
////                    parser.setEchoInput(echoInput);
////                }
//            } else if (control.equals(Control.CHECK_DUPLICATES)) {
//                checkDuplicates = true;
////                if (parser != null) {
////                    parser.setCheckDuplicates(checkDuplicates);
////                }
//            } else if (control.equals(Control.SKIP_HEADER)) {
//                skipHeader = true;
////                if (parser != null) {
////                    parser.setSkipHeader(skipHeader);
////                }
//            } else if (control.equals(Control.SKIP_ERRORS)) {
//                skipErrors = true;
////                if (parser != null) {
////                    parser.setSkipErrors(skipErrors);
////                }
//            }
//            if (control != null) {
//            	this.getConverterLog().addToLog(Level.INFO, "set "+control);
//            }
//        }
//    }
    
    
	/** read CIF.
	 * @param reader
	 * @param fileId
	 * @throws IOException
	 * @return document (CMLCml root with CMLCml children from each CIF
	 * currently null as no CML
	 */
	public CIF parseLegacy(List<String> stringList) {
		init();
		clearAndSetParserControls();
		StringBuilder sb = new StringBuilder();
		for (String s : stringList) {
			sb.append(s).append(S_NEWLINE);
		}
		CIF cif = null;
		try {
			StringReader sr = new StringReader(sb.toString());
			BufferedReader br = new BufferedReader(sr);
			try {
				Document document = parser.parse(br);
				cif = (CIF) document.getRootElement();
			} catch (IOException e) {
            	this.getConverterLog().addToLog(Level.ERROR, "parse CIF fail"+e.getMessage());
				throw new RuntimeException("parse CIF fail", e);
			}
			IOUtils.closeQuietly(br);
		} catch (CIFException e) {
        	this.getConverterLog().addToLog(Level.ERROR, "parse CIF fail"+e.getMessage());
			throw new RuntimeException("Cannot parse CIF: "+e);
		}
		return cif;
	}


	private void clearAndSetParserControls() {
		init();
        if (parser == null) {
            parser = new CIFParser();
        }
        parser.setEchoInput(echoInput);
        parser.setDebug(debug);
        parser.setCheckDuplicates(checkDuplicates);
        parser.setSkipHeader(skipHeader);
        parser.setSkipErrors(skipErrors);
	}
    
    /** check doubles.
     * 
     * @return checkDoubles
     */
	public boolean isCheckDoubles() {
		return checkDoubles;
	}

	/**
	 * 
	 * @param checkDoubles
	 */
	public void setCheckDoubles(boolean checkDoubles) {
		this.checkDoubles = checkDoubles;
	}

	/**
	 * @return checkDuplicates
	 */
	public boolean isCheckDuplicates() {
		return checkDuplicates;
	}

	/**
	 * 
	 * @param checkDuplicates
	 */
	public void setCheckDuplicates(boolean checkDuplicates) {
		this.checkDuplicates = checkDuplicates;
	}

	/**
	 * @return debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * 
	 * @param debug
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * 
	 * @return echoInput
	 */
	public boolean isEchoInput() {
		return echoInput;
	}

	/**
	 * 
	 * @param echoInput
	 */
	public void setEchoInput(boolean echoInput) {
		this.echoInput = echoInput;
	}

	/**
	 * 
	 * @return isMergeGlobal
	 */
	public boolean isMergeGlobal() {
		return mergeGlobal;
	}

	/**
	 * 
	 * @param mergeGlobal
	 */
	public void setMergeGlobal(boolean mergeGlobal) {
		this.mergeGlobal = mergeGlobal;
	}

	/**
	 * 
	 * @return noGlobal
	 */
	public boolean isNoGlobal() {
		return noGlobal;
	}

	/**
	 * 
	 * @param noGlobal
	 */
	public void setNoGlobal(boolean noGlobal) {
		this.noGlobal = noGlobal;
	}

	/**
	 * 
	 * @return skipErrors
	 */
	public boolean isSkipErrors() {
		return skipErrors;
	}

	/**
	 * 
	 * @param skipErrors
	 */
	public void setSkipErrors(boolean skipErrors) {
		this.skipErrors = skipErrors;
	}

	/**
	 * 
	 * @return skipHeader
	 */
	public boolean isSkipHeader() {
		return skipHeader;
	}

	/**
	 * 
	 * @param skipHeader
	 */
	public void setSkipHeader(boolean skipHeader) {
		this.skipHeader = skipHeader;
	}

	/**
	 * 
	 * @return trimElements
	 */
	public boolean isTrimElements() {
		return trimElements;
	}

	/**
	 * 
	 * @param trimElements
	 */
	public void setTrimElements(boolean trimElements) {
		this.trimElements = trimElements;
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}

}

