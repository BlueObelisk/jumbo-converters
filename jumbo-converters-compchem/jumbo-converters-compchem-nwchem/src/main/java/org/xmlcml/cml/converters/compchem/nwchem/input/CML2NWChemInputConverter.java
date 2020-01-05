package org.xmlcml.cml.converters.compchem.nwchem.input;

import java.io.File;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

//import org.lensfield.api.LensfieldParameter;
import org.xmlcml.cml.converters.CMLCommon;
import org.xmlcml.cml.converters.compchem.input.BasisSet;
import org.xmlcml.cml.converters.compchem.input.CML2CompchemInputConverter;
import org.xmlcml.cml.converters.compchem.input.CalculationComponents;
import org.xmlcml.cml.converters.compchem.input.Method;
import org.xmlcml.cml.converters.compchem.input.MethodBasis;
import org.xmlcml.cml.converters.compchem.input.Parameter;
import org.xmlcml.cml.converters.compchem.nwchem.NWChemCommon;
import org.xmlcml.euclid.Util;

public class CML2NWChemInputConverter extends CML2CompchemInputConverter  {

	private static final String CALCULATION_COMPONENTS_XML =
		"org/xmlcml/cml/converters/compchem/nwchem/input/calculationComponents.xml";
	private static final String METHOD_BASIS_XML =
		"org/xmlcml/cml/converters/compchem/nwchem/input/methodBasis.xml";
	private static final String DEFAULT_TEMPLATE_PATH =
		"/org/xmlcml/cml/converters/compchem/nwchem/in/";
	private static final String DEFAULT_TEMPLATE_NAME = "nwchem.ftl";

	private MethodBasis methodBasis;
	private CalculationComponents calculationComponents;
	private List<Parameter> parameterList;

//	@LensfieldParameter(name="basis", optional=true)
	private String basis = BasisSet.DEFAULT;

//	@LensfieldParameter(name="method", optional=true)
	private String method = Method.DEFAULT;

	public CML2NWChemInputConverter() {
		super(DEFAULT_TEMPLATE_PATH, DEFAULT_TEMPLATE_NAME);
	}


	@Override
	protected MethodBasis getMethodBasis() {
		if (methodBasis == null) {
			methodBasis = loadDefaultMethodBasis();
		}
		return methodBasis;
	}

	public void setMethodBasis(MethodBasis methodBasis) {
		this.methodBasis = methodBasis;
	}

	private MethodBasis loadDefaultMethodBasis() {
		try {
			InputStream inputStream = Util.getResourceUsingContextClassLoader(
					METHOD_BASIS_XML, this.getClass());
			try {
				MethodBasis methodBasis = new MethodBasis(inputStream);
				return methodBasis;
			} finally {
				inputStream.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot load method basis: "+METHOD_BASIS_XML, e);
		}
	}

	@Override
	protected CalculationComponents getCalculationComponents() {
		if (calculationComponents == null) {
			calculationComponents = loadDefaultCalculationComponents();
		}
		return calculationComponents;
	}

	public void setCalculationComponents(
			CalculationComponents calculationComponents) {
		this.calculationComponents = calculationComponents;
	}

	private CalculationComponents loadDefaultCalculationComponents() {
		try {
			InputStream inputStream = Util.getResourceUsingContextClassLoader(
					CALCULATION_COMPONENTS_XML, this.getClass());
			try {
				CalculationComponents calculationComponents = new CalculationComponents(inputStream);
				return calculationComponents;
			} finally {
				inputStream.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot load calculation components: "+CALCULATION_COMPONENTS_XML, e);
		}
	}

	@Override
	public void convert(InputStream inputStream, OutputStream outputStream) {

		createAndAddParameters();
		super.convert(inputStream, outputStream);
	}

	private void createAndAddParameters() {
		parameterList = new ArrayList<Parameter>();
		parameterList.add(new BasisSet(basis));
		parameterList.add(new Method(method));
		this.methodBasis = new MethodBasis(parameterList);
	}

	public static void main(String[] args) throws Exception {
		String moleculeFile = (args.length > 0) ? args[0] : "src/test/resources/compchem/nwchem/input/testmol/ch3f_rot.xml";
		String methodBasisFile = (args.length > 1) ? args[1] : "src/test/resources/compchem/nwchem/input/methodBasis.xml";
		String componentsFile = (args.length > 2) ? args[2] : "src/test/resources/compchem/nwchem/input/calculationComponents.xml";
		String textFile = (args.length > 3) ? args[3] : "test/test.out";
		String title = (args.length > 4) ? args[4] : "test title";
		String id = (args.length > 5) ? args[5] : "abx127";
		CML2NWChemInputConverter converter = new CML2NWChemInputConverter();
//		converter.setId(id);
//		converter.setTitle(title);
//		converter.setMethodBasis(new MethodBasis(new FileInputStream(methodBasisFile)));
//		converter.setCalculationComponents(new CalculationComponents(new FileInputStream(componentsFile)));
		converter.convert(new File(moleculeFile), new File(textFile));
	}

	@Override
	public String getRegistryInputType() {
		return CMLCommon.CML;
	}

	@Override
	public String getRegistryOutputType() {
		return NWChemCommon.INPUT;
	}

	@Override
	public String getRegistryMessage() {
		return "Convert CML to NWChem Input";
	}


}
