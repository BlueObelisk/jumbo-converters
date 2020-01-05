package org.xmlcml.cml.converters.cml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

//import org.lensfield.api.LensfieldInput;
//import org.lensfield.api.LensfieldOutput;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLCml;

/** merge 2 aligned sets of CML files
 * 
 * @author pm286
 *
 */
public class CMLMerger {

//	@LensfieldInput(name="stream1")
	private InputStream stream1;
	
//	@LensfieldInput(name="stream2")
	private InputStream stream2;
	
//	@LensfieldOutput
	private OutputStream output;
	
	public void run() throws ValidityException, ParsingException, IOException {
		CMLBuilder builder = new CMLBuilder();
		
		Document doc1 = builder.build(stream1);
		Document doc2 = builder.build(stream2);
		
		// merge together
		CMLCml cmlOut = new CMLCml();
		cmlOut.appendChild(doc1.getRootElement().copy());
		cmlOut.appendChild(doc2.getRootElement().copy());
		
		Serializer ser = new Serializer(output);
		ser.write(new Document(cmlOut));

	}
	
}
