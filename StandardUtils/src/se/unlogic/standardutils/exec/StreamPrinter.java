package se.unlogic.standardutils.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;



/**
 * A simple buffered {@link StreamHandler} implementation that prints the input from the {@link InputStream} to the given {@link OutputStream}. If no {@link OutputStream} is given it defaults to System.out.<p>
 * 
 * This implementation is based on the {@link PrintWriter}, {@link InputStreamReader} and {@link BufferedReader} classes.
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 *
 */
public class StreamPrinter extends StreamHandler {

	private InputStream is;
	private String prefix;
	private OutputStream os;

	public StreamPrinter(InputStream is, String prefix, OutputStream os) {
		super();
		this.is = is;
		this.os = os;
		this.prefix = prefix;
	}

	public StreamPrinter(InputStream is, String prefix) {
		super();
		this.is = is;
		this.prefix = prefix;
	}

	public StreamPrinter(InputStream is) {
		super();
		this.is = is;
	}

	public StreamPrinter() {}

	@Override
	public void run() {
		try {
			PrintWriter pw = null;

			if (os != null) {
				pw = new PrintWriter(os);
			}else{
				pw = new PrintWriter(System.out);
			}

			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String line = null;

			if(prefix != null){

				while ((line = br.readLine()) != null) {

					pw.println(prefix + line);
				}

			}else{

				while ((line = br.readLine()) != null) {

					pw.println(line);
				}
			}

			if (pw != null) {
				pw.flush();
			}

			br.close();
			isr.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public InputStream getIs() {
		return is;
	}

	@Override
	public void setIs(InputStream is) {
		this.is = is;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {

		this.prefix = prefix;
	}

	public OutputStream getOs() {
		return os;
	}

	public void setOs(OutputStream os) {
		this.os = os;
	}
}
