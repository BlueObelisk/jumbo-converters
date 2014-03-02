

import java.io.InputStream;
import java.io.OutputStream;



public class DosTest {

	public static StringBuffer readStdin() throws Exception {
		InputStream is = System.in;
		StringBuffer sb = new StringBuffer();
		while (true) {
			int b = is.read();
			if (b == -1) break;
			sb.append((char)b);
		}
		return sb;
	}
	
	public static void writeStdout(StringBuffer sb) throws Exception {
		OutputStream os = System.out;
		for (int i = 0; i < sb.length(); i++) {
			int c = sb.charAt(i);
			os.write(c);
		}
	}
	
	public static void main(String[] args) throws Exception {
		StringBuffer sb = readStdin();
		writeStdout(sb);
	}
}
