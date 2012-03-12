package org.xmlcml.cml.converters.compchem.gaussian.link;

import org.apache.log4j.Logger;


/**
 * 
 * @author pm286
 *
 *Typical:

 */
public class Link601 extends GaussianLink {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(Link601.class);
	
//	private static String LINK_REGEX = " (\\d+)/(\\d+=\\-?\\d+(,\\d+=\\-?\\d+)*)/(\\d+(,\\d+)*)(\\-?\\d+)?\\;";
//	static  {
//		LINK_PATTERN = Pattern.compile(LINK_REGEX);
//	};

    public Link601() {
	}
    
    @Override
    protected String getTitle() {
    	return "Population and related analyses (including multipole moments)";	
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
