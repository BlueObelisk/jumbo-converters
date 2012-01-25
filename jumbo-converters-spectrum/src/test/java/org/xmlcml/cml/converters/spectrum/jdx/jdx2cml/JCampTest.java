package org.xmlcml.cml.converters.spectrum.jdx.jdx2cml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import nu.xom.Element;

import org.jcamp.parser.JCAMPReader;
import org.jcamp.spectrum.MassSpectrum;
import org.jcamp.spectrum.NMRSpectrum;
import org.jcamp.spectrum.Spectrum;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.spectrum.SpectrumCommon;

/**
 * copied from JCAMP distrib to make sure the library works
 * in this context
 * 
 * @author pm286
 *
 */
public class JCampTest {
	
	@Test
	@Ignore // TODO fix file name
    public void testSpinworks() throws Exception{

        StringBuffer fileData = new StringBuffer(1000);
        File file = new File(SpectrumCommon.JDX_JDX2CML_DIR+"/in/"+"spinworks.dx");
        System.out.println(file.getAbsolutePath());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();


        Spectrum jcampSpectrum = JCAMPReader.getInstance().createSpectrum(fileData.toString());
        if (!(jcampSpectrum instanceof NMRSpectrum)) {
        	throw new Exception("Spectrum in file is not an NMR spectrum!");
        }
        NMRSpectrum nmrspectrum = (NMRSpectrum) jcampSpectrum;
        if (nmrspectrum.hasPeakTable()) {
        	Assert.assertEquals(16384, nmrspectrum.getPeakTable().length);
        }
    }

	@Test
	public void testClacetop() {
		File clacetop = new File("src/test/resources/"+SpectrumCommon.JDX_JDX2CML_DIR+"/in/clacetop.jdx");
		JDX2CMLConverter converter = new JDX2CMLConverter();
		Element element = converter.convertToXML(clacetop);
//		CMLUtil.debug(element, "NEW JCXML");
	}
	@Test
	@Ignore // TODO fix reading
    public void testMoreThan49Peaks() throws Exception{

        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(SpectrumCommon.JDX_JDX2CML_DIR+"/in/1567755.jdx"));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();


        Spectrum jcampSpectrum = JCAMPReader.getInstance().createSpectrum(fileData.toString());
        if (!(jcampSpectrum instanceof MassSpectrum)) {
        	throw new Exception("Spectrum in file is not an NMR spectrum!");
        }
        MassSpectrum massspectrum = (MassSpectrum) jcampSpectrum;
        if (massspectrum.hasPeakTable()) {
        	Assert.assertEquals(54, massspectrum.getPeakTable().length);
        }
    }

}
