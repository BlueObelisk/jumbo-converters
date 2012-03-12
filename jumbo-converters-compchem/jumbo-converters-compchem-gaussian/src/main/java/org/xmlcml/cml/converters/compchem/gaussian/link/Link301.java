package org.xmlcml.cml.converters.compchem.gaussian.link;

import org.apache.log4j.Logger;


/**
 * 
 * @author pm286
 *
 *Typical:

 */
public class Link301 extends GaussianLink {
	private static Logger LOG = Logger.getLogger(Link301.class);
	
//	private static String LINK_REGEX = " (\\d+)/(\\d+=\\-?\\d+(,\\d+=\\-?\\d+)*)/(\\d+(,\\d+)*)(\\-?\\d+)?\\;";
//	static  {
//		LINK_PATTERN = Pattern.compile(LINK_REGEX);
//	};

    public Link301() {
    	LOG.debug("Constructoe");
	}
    
    @Override
    protected String getTitle() {
    	return "Generates basis set information";	
     }
	
//	public CMLModule convert2CML() {
//		cmlModule = new CMLModule();
//		line_num = 0;
//	    previous_line = null;
//	    
//        while (line_num < lineList.size()) {
//        	line = lineList.get(line_num++);
//        }
//        return cmlModule;
//	}
	
}
