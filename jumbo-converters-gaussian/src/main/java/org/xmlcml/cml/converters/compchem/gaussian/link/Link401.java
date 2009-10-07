package org.xmlcml.cml.converters.compchem.gaussian.link;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianLink;


/**
 * 
 * @author pm286
 *
 *Typical:

 */
public class Link401 extends GaussianLink {
	private static Logger LOG = Logger.getLogger(Link401.class);
	
//	private static String LINK_REGEX = " (\\d+)/(\\d+=\\-?\\d+(,\\d+=\\-?\\d+)*)/(\\d+(,\\d+)*)(\\-?\\d+)?\\;";
//	static  {
//		LINK_PATTERN = Pattern.compile(LINK_REGEX);
//	};

    public Link401() {
	}
    
    @Override
    protected String getTitle() {
    	return "Forms the initial MO guess";	
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
