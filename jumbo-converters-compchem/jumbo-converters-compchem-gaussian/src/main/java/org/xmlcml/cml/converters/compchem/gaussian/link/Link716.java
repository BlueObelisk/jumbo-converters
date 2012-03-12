package org.xmlcml.cml.converters.compchem.gaussian.link;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLVector3;

public class Link716 extends GaussianLink {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(Link716.class);
	
    public Link716() {
	}
    
    @Override
    protected String getTitle() {
    	return "Processes information for optimizations and frequencies";
    }
	
	public CMLModule convert2CML() {
		cmlModule = new CMLModule();
		line_num = 0;
	    previous_line = null;
        while (line_num < lineList.size()) {
        	line = lineList.get(line_num++);
            if (line.startsWith(" SCF Done:")) {
                // Repeatedly read SCF energy and replace the SCF energy value in mDoc.
                // Not efficient but insure to get the last one. However, if there is an
                // unexpected thing in the file, there might be a problem.
                readSCFDone();
            } else if (line.startsWith(" Harmonic frequencies")) {
                readHarmonicFrequencyData();
            } else if (line.startsWith(" Rotational symmetry number")) {
                readRotationalSymmetry();
            } else if (line.startsWith(" Principal axes and moments of inertia")) {
                readPrincipalMOI();
            } else if (line.startsWith(" Rotational constants (GHZ):")) {
                readRotationalConstants();
            }
        }
        return cmlModule;
	}

    private void readPrincipalMOI() {
//     EIGENVALUES --  2570.725683144.539184053.58698
//           X            0.71302   0.27610   0.64449
//           Y            0.16513   0.82722  -0.53707
//           Z            0.68142  -0.48937  -0.54423
        // Read the number line.
        readLine();
        try {
        	
        	double[] values = new double[3];
            CMLVector3 pmoi =  new CMLVector3();
            pmoi.setTitle("moi components");
            pmoi.setDictRef("cml:moicomponents");
            // Read eigen values
            readLine();
            String I1 = line.substring(21, 31);
            String I2 = line.substring(31, 41);
            String I3 = line.substring(41, 51);
            values[0] = Double.parseDouble(I1);
            values[1] = Double.parseDouble(I2);
            values[2] = Double.parseDouble(I3);
            pmoi.setXMLContent(values);
            String x1,x2,x3;
            double[][] matrix = new double[3][3];
            for (int i = 0; i < 3; i++) {
                readLine();
                x1 = line.substring(21, 31);
                x2 = line.substring(31, 41);
                x3 = line.substring(41, 51);
                matrix[0][i] = Double.parseDouble(x1);
                matrix[1][i] = Double.parseDouble(x2);
                matrix[2][i] = Double.parseDouble(x3);
            }
            CMLMatrix moimatrix =  new CMLMatrix(matrix);
            moimatrix.setTitle("moi axes");
            moimatrix.setDictRef("cml:moiaxes");
            CMLProperty property = new CMLProperty();
            property.setTitle("MOI");
            property.appendChild(pmoi);
            property.appendChild(moimatrix);
            cmlModule.appendChild(property);
//            mDoc.setPricipleMOI(pmoi);
        } catch (NumberFormatException ex) {
        	throw new RuntimeException("Bad moi: "+ex);
            // Use the one calculated in GenericOBReader
        }
    }

    private void readRotationalConstants() {
        Vector<String> rot = tokenizeLine(line);
        CMLVector3 rotConst = new CMLVector3();
        double[] values = new double[3];
        values[2] = Double.parseDouble(rot.get(rot.size() - 1));
        values[1] = Double.parseDouble(rot.get(rot.size() - 2));
        values[0] = Double.parseDouble(rot.get(rot.size() - 3));
        rotConst.setXMLContent(values);
        cmlModule.appendChild(rotConst);
    }

    private void readRotationalSymmetry() {
        Vector<String> sym = tokenizeLine(line);
    	@SuppressWarnings("unused")
        String sym_str = (String)sym.lastElement();
	@SuppressWarnings("unused")
        CMLScalar symNumber = new CMLScalar();
    }

    private void readSCFDone() {
        if(line.startsWith(" SCF Done:")) {
            String str_spe = new String();
            StringTokenizer tempStringTokenizer = new StringTokenizer(line);
            while (tempStringTokenizer.hasMoreTokens()) {
                str_spe = ((String) tempStringTokenizer.nextElement()).trim();
                if (str_spe.startsWith("E(") && str_spe.endsWith(")")) {
                	CMLScalar scf = new CMLScalar(str_spe);
                	scf.setDictRef("gau:scftype");
                	cmlModule.appendChild(scf);
                } else {
                    try {
                        Double val_spe = new Double(str_spe);
                    	CMLScalar scfval = new CMLScalar(val_spe);
                    	scfval.setDictRef("gau:scfval");
                    	cmlModule.appendChild(scfval);
                        break;
                    } catch (NumberFormatException ex) {

                    }
                }
            }
        }
    }

    private void readHarmonicFrequencyData() {
        while (!readLine().startsWith(" ----")) {
            if (line.startsWith(" Frequencies --  ")) {
                //Vector<String> f_numbers = tokenizeLine(previous_line);
                int number_of_f_read = tokenizeLine(previous_line).size();
                Vector<String> f_values = tokenizeLine(line);
                readLine();
                Vector<String> reduceMass_values = tokenizeLine(line);
                readLine();
                Vector<String> forceConst_values = tokenizeLine(line);
                readLine();
                Vector<String> IRInten = tokenizeLine(line);
                readLine();
                List<CMLProperty> f_data_list = new ArrayList<CMLProperty>();
                for (int j = 0; j < number_of_f_read; j++) {
                	CMLProperty f_data = new CMLProperty();
                	f_data.setTitle("frequency");
                	f_data.setDictRef("cml:frequency");
                    double frequency = Double.parseDouble(f_values.get(f_values.size() - number_of_f_read + j));
                    CMLScalar freq = new CMLScalar(frequency);
                    freq.setTitle("frequency");
                    freq.setDictRef("cml:freq");
                    f_data.appendChild(freq);
                    double reducedMass = Double.parseDouble(reduceMass_values.get(reduceMass_values.size() - number_of_f_read + j));
                    CMLScalar red = new CMLScalar(reducedMass);
                    red.setTitle("reducedMass");
                    red.setDictRef("cml:redMass");
                    f_data.appendChild(red);
                    double forceConstant = Double.parseDouble(forceConst_values.get(forceConst_values.size() - number_of_f_read + j));
                    CMLScalar fc = new CMLScalar(forceConstant);
                    fc.setTitle("forceConstant");
                    fc.setDictRef("cml:forceConstant");
                    f_data.appendChild(fc);
                    double iRIntensity = Double.parseDouble(IRInten.get(IRInten.size() - number_of_f_read + j));
                    CMLScalar ir = new CMLScalar(iRIntensity);
                    ir.setTitle("IRIntensity");
                    ir.setDictRef("cml:IRIntensity");
                    f_data.appendChild(ir);
                    cmlModule.appendChild(f_data);
                    f_data_list.add(f_data);
                }

                int natoms = molecule.getAtoms().size();
                for (int i = 0; i < natoms; i++) {
                    readLine();
                    Vector<String> displacement_values = tokenizeLine(line);
                    for (int j = 0; j < number_of_f_read; j++) {
                    	CMLProperty f_data = new CMLProperty();
                    	f_data.setTitle("displacement");
                    	f_data.setDictRef("cml:displacement");
                        double dx = Double.parseDouble(displacement_values.get(displacement_values.size() - 3 * (number_of_f_read - j)));
                        double dy = Double.parseDouble(displacement_values.get(displacement_values.size() - 3 * (number_of_f_read - j) + 1));
                        double dz = Double.parseDouble(displacement_values.get(displacement_values.size() - 3 * (number_of_f_read - j) + 2));
                        CMLVector3 vect = new CMLVector3(dx, dy, dz);
                        f_data_list.get(j).appendChild(vect);
                    }
                }
            }
        }
    }

    private Vector<String> tokenizeLine(String str) {
        Vector<String> vec_str =  new Vector<String>();
        StringTokenizer tempStringTokenizer = new StringTokenizer(str);
        while (tempStringTokenizer.hasMoreTokens()) {
            vec_str.add(((String) tempStringTokenizer.nextElement()).trim());
        }
        return vec_str;
    }

}
