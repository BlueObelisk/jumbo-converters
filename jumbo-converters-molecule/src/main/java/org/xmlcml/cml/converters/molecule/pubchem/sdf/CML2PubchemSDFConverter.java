package org.xmlcml.cml.converters.molecule.pubchem.sdf;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.molecule.mdl.CML2SDFConverter;
import org.xmlcml.cml.converters.molecule.mdl.MDLConverter.CoordType;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;

/**
 *
 * TODO some really grotty hardcoding of URLs in here.
 *
 * @author pm286
 * @author ojd20
 */
public class CML2PubchemSDFConverter extends CML2SDFConverter implements
		Converter {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(CML2PubchemSDFConverter.class);

	public final static String CRYSTALEYE_SITE = "http://wwmm.ch.cam.ac.uk/crystaleye/summary";
	
	// acta/e/2009/06-00/data/bg2255/bg2255sup1_I/bg2255sup1_I.cif.summary.html	
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/cml",
		"-odir", "../temp",
		"-is", "cml",
		"-os", "mol",
		"-converter", "org.xmlcml.cml.converters.molecule.mdl.CML2MDLConverter"
	};
	
	public final static String[] testArgs = {
		"-quiet",
		"-sd", "src/test/resources/cml",
		"-odir", "../temp",
		"-is", "cml",
		"-os", "mol",
		"-converter", "org.xmlcml.cml.converters.molecule.mdl.CML2MDLConverter"
	};
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.SDF;
	}

	public CML2PubchemSDFConverter() {
		super();
		this.getMdlConverter().setCoordType(CoordType.THREED);
	}
	
	@Override
	protected List<CMLProperty> getPropertyList() {
		
		List<CMLProperty> propertyList = new ArrayList<CMLProperty>();
		
		propertyList.add(getDatasourceRegID());
		propertyList.add(getDatabaseURLProperty());
		propertyList.add(getExtSubstanceProperty());
		
		return propertyList;
	}
	
	private CMLProperty getDatasourceRegID() {
		return makeProperty("PUBCHEM_EXT_DATASOURCE_REGID", this.getFileId());
	}

	private CMLProperty getDatabaseURLProperty() {
		return makeProperty("PUBCHEM_EXT_DATASOURCE_URL", "http://wwmm.ch.cam.ac.uk/crystaleye");
	}

//http://wwmm.ch.cam.ac.uk/crystaleye/summary/acta/e/2009/06-00/data/bg2255/bg2255sup1_I/bg2255sup1_I.cif.summary.html	
	private CMLProperty getExtSubstanceProperty() {
		String fileId = this.getFileId();
		fileId = trimFileId(fileId);
		return makeProperty("PUBCHEM_EXT_SUBSTANCE_URL", CRYSTALEYE_SITE+"/"+getJournal()+"/"+fileId+"/"+fileId+"sup1_I"+"/"+fileId+"sup1_I.cif.summary.html");
//		http://wwmm.ch.cam.ac.uk/crystaleye/summary/acta/e/2009/03-00/data/bg2221/bg2221sup1_I/bg2221sup1_I.cif.summary.html
	}

	private String trimFileId(String s) {
		int index = s.indexOf("sup");
		return (index >=0) ? s.substring(0, index) : s;
	}
	
	private CMLProperty makeProperty(String tag, String value) {
		CMLProperty property = new CMLProperty();
		property.setRole(tag);
		property.addScalar(new CMLScalar(value));
		return property;
	}
	
	private String getJournal() {
		return "acta/e/2009/03-00/data";
	}
	

}
