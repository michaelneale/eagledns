package se.unlogic.standardutils.xml;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

public interface XSLTCacher {

	public void reloadStyleSheet() throws TransformerConfigurationException;
	public Transformer getTransformer() throws TransformerConfigurationException;
	public void unload();
	//TODO public Document getDocument();
}
