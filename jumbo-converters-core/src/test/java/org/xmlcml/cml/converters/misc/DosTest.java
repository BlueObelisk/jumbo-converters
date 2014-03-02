package org.xmlcml.cml.converters.misc;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class DosTest {

	public static void readStdin() throws Exception {
		InputStream is = System.in;
		while (true) {
			int b = is.read();
			if (b == -1) break;
			System.out.print("?"+((char)b));
		}
	}
	
	public static void main(String[] args) throws Exception {
		readStdin();
	}
}
