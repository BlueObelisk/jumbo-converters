package org.xmlcml.cml.converters.spectrum.jdx;

import java.util.Collection;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jcamp.math.AxisMap;
import org.jcamp.parser.JCAMPException;
import org.jcamp.parser.JCAMPReader;
import org.jcamp.spectrum.IDataArray1D;
import org.jcamp.spectrum.IOrderedDataArray1D;
import org.jcamp.spectrum.ISpectrumIdentifier;
import org.jcamp.spectrum.Spectrum;
import org.jcamp.spectrum.Spectrum1D;
import org.jcamp.spectrum.Spectrum2D;
import org.jcamp.spectrum.notes.Note;
import org.xmlcml.cml.base.CC;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLParameterList;
import org.xmlcml.cml.element.CMLSpectrum;
import org.xmlcml.cml.element.CMLSpectrumData;
import org.xmlcml.cml.element.CMLXaxis;
import org.xmlcml.cml.element.CMLYaxis;
import org.xmlcml.cml.element.CMLSpectrum.SpectrumType;

public class JDX2CMLConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(JDX2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	};
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/jdx",
		"-odir", "../temp",
		"-is", "jdx",
		"-os", "cml",
		"-converter", "org.xmlcml.cml.converters.spectrum.jdx.JDX2CMLConverter"
	};
	public final static String JDX_PREFIX = "jdx";
	
	private CMLSpectrum cmlSpectrum = null;
	private SpectrumType spectrumType = null;
	
	
	public Type getInputType() {
		return Type.JDX;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts an JDX object to CMLSpect. returns cml:cml
	 * 
	 * @param lines JCAMP in any ASCII format
	 */
	public Element convertToXML(List<String> lines) {
		CMLCml cml = null;
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line + CC.S_NEWLINE);
		}
		String jcampString = sb.toString();
		cml = convertToCML(jcampString);
		return cml;
	}

	/**
	 * @param jcampString
	 * @throws RuntimeException
	 */
	private CMLCml convertToCML(String jcampString) throws RuntimeException {
		CMLCml cml = null;
		JCAMPReader jcamp = JCAMPReader.getInstance();
		Spectrum jcampSpectrum = null;
		try {
			jcampSpectrum = jcamp.createSpectrum(jcampString);
		} catch (JCAMPException e) {
			throw new RuntimeException("Cannot parse JCAMP", e);
		}
		cml = new CMLCml();
		cmlSpectrum = new CMLSpectrum();
		cml.appendChild(cmlSpectrum);
		createSpectrumFromJCamp(jcampSpectrum);
		return cml;
	}

	/**
	 * @param jcampSpectrum
	 */
	private void createSpectrumFromJCamp(Spectrum jcampSpectrum) {
		getSpectrumType(jcampSpectrum);
		cmlSpectrum.setType(spectrumType.toString());
		
		String xLabel = jcampSpectrum.getXAxisLabel();
		if ("m/z".equals(xLabel)) {
			xLabel = "M/Z";          // this seems to be variable case on different runs?
		}
		String yLabel = jcampSpectrum.getYAxisLabel();
		// not quite sure what these do...
		@SuppressWarnings("unused")
		AxisMap xAxisMap = jcampSpectrum.getXAxisMap();
		@SuppressWarnings("unused")
		AxisMap yAxisMap = jcampSpectrum.getYAxisMap();
		
		if (jcampSpectrum instanceof Spectrum1D) {
			CMLXaxis xaxis = new CMLXaxis();
			CMLYaxis yaxis = new CMLYaxis();
			Spectrum1D spectrum1d = (Spectrum1D) jcampSpectrum;
			CMLSpectrumData spectrumData = new CMLSpectrumData();
			cmlSpectrum.addSpectrumData(spectrumData);
			
			IOrderedDataArray1D xData = spectrum1d.getXData();
			CMLArray cmlXData = new CMLArray(xData.toArray());
			spectrumData.addXaxis(xaxis);
			xaxis.addArray(cmlXData);
			xaxis.setTitle(xLabel);
			
			IDataArray1D yData = spectrum1d.getYData();
			CMLArray cmlYData = new CMLArray(yData.toArray());
			spectrumData.addYaxis(yaxis);
			yaxis.addArray(cmlYData);
			yaxis.setTitle(yLabel);
			
		} else if (jcampSpectrum instanceof Spectrum2D) {
			throw new RuntimeException("Cannot support 2D spectrum");
		}
		
//		origin is business process not coordinate
//		System.out.println("ORIGIN (business) "+jcampSpectrum.getOrigin());
//		System.out.println("OWNER "+jcampSpectrum.getOwner());
		
		String title = jcampSpectrum.getTitle();
		cmlSpectrum.setTitle(title);
		addNotes(jcampSpectrum);
	}

	/**
	 * @param jcampSpectrum
	 * @throws RuntimeException
	 */
	private void addNotes(Spectrum jcampSpectrum) throws RuntimeException {
		// vendor specific and other metadata
		Collection notes = jcampSpectrum.getNotes();
		if (notes.size() > 0) {
			CMLParameterList parameterList = new CMLParameterList();
			for (Object obj : notes) {
				Note note = (Note) obj;
				CMLParameter parameter = new CMLParameter();
				parameter.setDictRef(JDX_PREFIX+CC.S_COLON+note.getDescriptor().toString().trim());
				parameter.setCMLValue(note.getValue().toString().trim());
				parameterList.addParameter(parameter);
			}
			cmlSpectrum.addParameterList(parameterList);
		}
	}
	
	private SpectrumType getSpectrumType(Spectrum jcampSpectrum) {
		int identifier = jcampSpectrum.getIdentifier();
/**		
 * 			from JCAMP
		   public final static int UNKNOWN = 0;
		    public final static int INFRARED = 2 << 0;
		    public final static int IR = 2 << 0;
		    public final static int RAMAN = 2 << 1;
		    public final static int ULTRAVIOLET = 2 << 2;
		    public final static int UV = 2 << 2;
		    public final static int FLUORESCENCE = 2 << 3;
		    public final static int NMR = 2 << 4;
		    public final static int MASS = 2 << 5;
		    public final static int MS = 2 << 5;
		    public final static int CHROMATOGRAM = 2 << 16;
		    public final static int GC = 2 << 17 | CHROMATOGRAM;
		    public final static int LC = 2 << 18 | CHROMATOGRAM;
		    public final static int FID = 2 << 23;
		    public final static int SPEC2D = 2 << 24;
		    public final static int NMRFID = NMR | FID;
		    public final static int NMR2D = NMR | SPEC2D;
		    public final static int FLUORESCENCE2D = FLUORESCENCE | SPEC2D;
		    public final static int GCMS = MS | GC;
		    public final static int LCMS = MS | LC;
*/		
		spectrumType = SpectrumType.UNKNOWN;
		if (identifier == ISpectrumIdentifier.UNKNOWN) {
			//
		} else if (identifier == ISpectrumIdentifier.IR) {
			spectrumType = SpectrumType.IR;
		} else if (identifier == ISpectrumIdentifier.RAMAN) {
			spectrumType = SpectrumType.RAMAN;
		} else if (identifier == ISpectrumIdentifier.ULTRAVIOLET) {
			spectrumType = SpectrumType.UV;
		} else if (identifier == ISpectrumIdentifier.FLUORESCENCE) {
			spectrumType = SpectrumType.FLUORESCENCE;
		} else if (identifier == ISpectrumIdentifier.NMR) {
			spectrumType = SpectrumType.NMR;
		} else if (identifier == ISpectrumIdentifier.MASS) {
			spectrumType = SpectrumType.MASS;
		} else if (identifier == ISpectrumIdentifier.CHROMATOGRAM ||
			identifier == ISpectrumIdentifier.GC ||
			identifier == ISpectrumIdentifier.LC) {
			spectrumType = SpectrumType.CHROMATOGRAM;
		} else {
			spectrumType = SpectrumType.UNSUPPORTED;
		}
		return spectrumType;
	}

	@SuppressWarnings("unused")
	private void debug(AxisMap axisMap) {
		System.out.println(axisMap.getData());
		System.out.println(axisMap.getDataRange());
		System.out.println(axisMap.getFullViewRange());
		System.out.println(axisMap.getGrid());
		System.out.println(axisMap.getMapRange());
		System.out.println(axisMap.getZoomRange());
	}
	/**
	 * @param jcampSpectrum
	 */
	@SuppressWarnings("unused")
	private void debug(Spectrum jcampSpectrum) {
		System.out.println("XLAB "+jcampSpectrum.getXAxisLabel());
		AxisMap xAxisMap = jcampSpectrum.getXAxisMap();
		System.out.println("XAXIS "+xAxisMap);
		System.out.println("YLAB "+jcampSpectrum.getYAxisLabel());
		AxisMap yAxisMap = jcampSpectrum.getYAxisMap();
		System.out.println("YAXIS "+yAxisMap);
		System.out.println("OR "+jcampSpectrum.getOrigin());
		System.out.println("TITLE "+jcampSpectrum.getTitle());
		System.out.println("ID "+jcampSpectrum.getIdentifier());
		Collection notes = jcampSpectrum.getNotes();
		for (Object obj : notes) {
			System.out.println("NOTE "+jcampSpectrum.getOwner());
		}
		System.out.println("OWNER "+jcampSpectrum.getOwner());
		if (jcampSpectrum instanceof Spectrum1D) {
			Spectrum1D spectrum1d = (Spectrum1D) jcampSpectrum;
			IOrderedDataArray1D xData = spectrum1d.getXData();
			CMLArray cmlXData = new CMLArray(xData.toArray());
//			cmlXData.debug("XDATA");
			IDataArray1D yData = spectrum1d.getYData();
			CMLArray cmlYData = new CMLArray(yData.toArray());
//			cmlXData.debug("YDATA");
		}
	}

}
