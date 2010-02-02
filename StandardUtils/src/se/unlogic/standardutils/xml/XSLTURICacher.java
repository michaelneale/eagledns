package se.unlogic.standardutils.xml;

import java.net.URI;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class XSLTURICacher implements XSLTCacher {

	private Templates cachedXSLT;
	private URI uri;
	private URIResolver uriResolver;
	
	public XSLTURICacher(URI uri) throws TransformerConfigurationException {
		super();
		this.uri = uri;
		this.reloadStyleSheet();
	}

	public XSLTURICacher(URI uri, URIResolver uriResolver) throws TransformerConfigurationException {

		this.uri = uri;
		this.uriResolver = uriResolver;
		this.reloadStyleSheet();
	}

	public Transformer getTransformer()	throws TransformerConfigurationException {
		return cachedXSLT.newTransformer();
	}

	public void reloadStyleSheet() throws TransformerConfigurationException {
		
		TransformerFactory transFact = TransformerFactory.newInstance();
		
		if(uriResolver != null){
			transFact.setURIResolver(uriResolver);			
		}
		
		
		this.cachedXSLT = transFact.newTemplates(new StreamSource(uri.toString()));
	}

	public void unload() {}

}
