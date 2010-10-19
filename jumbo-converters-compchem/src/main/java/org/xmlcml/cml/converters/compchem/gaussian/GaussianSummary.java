package org.xmlcml.cml.converters.compchem.gaussian;

import java.util.Map;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.ChemistrySummary;

/**
 * not sure what this is doing
 * */

/**
 * Generates a structure summary page as XHTML.
 * 
 * @author pm286
 */
public class GaussianSummary extends ChemistrySummary {

	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(GaussianSummary.class);
	
	String RAW_GAU_MIME = ".log";
	String COMPLETE_GAU_CML_MIME = ".gau.cml";

	/**
	 */
	public GaussianSummary() {
	}
	
	protected String getTemplateName() {
//		return "gaussian"+File.separator+"gaussian.ftl";
		return "compchem.ftl";
	}

   @Override
   public Map<String, String> toMap() {
      Map<String, String> root =  super.toMap();
		root.put("RAW_MIME", RAW_GAU_MIME);
		root.put("COMPLETE_CML_MIME", COMPLETE_GAU_CML_MIME);
		root.put("program", "Gaussian");
      return root;
	}

		
}
