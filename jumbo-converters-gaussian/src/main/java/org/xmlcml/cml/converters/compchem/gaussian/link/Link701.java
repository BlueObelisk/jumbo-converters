package org.xmlcml.cml.converters.compchem.gaussian.link;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianLink;


/**
 * 
 * @author pm286
 *
 *Typical:

 */
public class Link701 extends GaussianLink {
	private static Logger LOG = Logger.getLogger(Link701.class);
	
//	private static String LINK_REGEX = " (\\d+)/(\\d+=\\-?\\d+(,\\d+=\\-?\\d+)*)/(\\d+(,\\d+)*)(\\-?\\d+)?\\;";
//	static  {
//		LINK_PATTERN = Pattern.compile(LINK_REGEX);
//	};

    public Link701() {
	}
    
    @Override
    protected String getTitle() {
    	return "1-electron integral first or second derivatives";	
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
