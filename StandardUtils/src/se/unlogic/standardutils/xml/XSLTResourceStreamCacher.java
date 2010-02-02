package se.unlogic.standardutils.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

public class XSLTResourceStreamCacher implements XSLTCacher {

	private Templates cachedXSLT;
	private Class<?> resourceClass;
	private String path;
	
	public XSLTResourceStreamCacher(Class<?> resourceClass, String path) throws TransformerConfigurationException {
		this.cacheStyleSheet(resourceClass, path);
	}

	public Transformer getTransformer()	throws TransformerConfigurationException {
		return this.cachedXSLT.newTransformer();
	}

	public void reloadStyleSheet() throws TransformerConfigurationException {
		this.cacheStyleSheet(this.resourceClass,this.path);
	}

	private void cacheStyleSheet(Class<?> resourceClass, String path) throws TransformerConfigurationException{
		InputStream inputStream = resourceClass.getResourceAsStream(path); 
		Source xsltSource = new StreamSource(inputStream);
		TransformerFactory transFact = TransformerFactory.newInstance();			
		Templates tempCachedXSLT = transFact.newTemplates(xsltSource);
		
		try {inputStream.close();} catch (IOException e) {}
		
		this.cachedXSLT = tempCachedXSLT;
		this.resourceClass = resourceClass;
		this.path = path;
	}
	
	public void unload() {}

	@Override
	protected void finalize() {
		this.unload();
	}

}
