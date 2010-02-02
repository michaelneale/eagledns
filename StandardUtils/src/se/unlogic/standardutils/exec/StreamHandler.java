package se.unlogic.standardutils.exec;

import java.io.InputStream;

/**
 * Abstract class for handling output from {@link InputStream}'s.<p>
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 *
 */
public abstract class StreamHandler extends Thread{

	public abstract void setIs(InputStream is);

}