package org.xmlcml.cml.converters.compchem.gaussian.link;

import org.apache.log4j.Logger;


/**
 * 
 * @author pm286
 *
 *Typical:

 */
public class Link103 extends GaussianLink {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(Link103.class);
	
//	private static String LINK_REGEX = " (\\d+)/(\\d+=\\-?\\d+(,\\d+=\\-?\\d+)*)/(\\d+(,\\d+)*)(\\-?\\d+)?\\;";
//	static  {
//		LINK_PATTERN = Pattern.compile(LINK_REGEX);
//	};

    public Link103() {
	}
    
    @Override
    protected String getTitle() {
    	return "Berny optimizations to minima and TS, STQN transition state searches";	
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
