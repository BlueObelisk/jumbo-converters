package org.xmlcml.cml.converters.spectrum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.jcamp.parser.JCAMPReader;
import org.jcamp.spectrum.MassSpectrum;
import org.jcamp.spectrum.NMRSpectrum;
import org.jcamp.spectrum.Spectrum;
import org.junit.Assert;
import org.junit.Test;

/**
 * copied from JCAMP distrib to make sure the library works
 * in this context
 * 
 * @author pm286
 *
 */
public class JCampTest {
	
	@Test
    public void testSpinworks() throws Exception{

        StringBuffer fileData = new StringBuffer(1000);
        File file = new File("src/test/resources/spectrum/jdx/spinworks.dx");
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
    public void testMoreThan49Peaks() throws Exception{

        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/spectrum/jdx/1567755.jdx"));
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
