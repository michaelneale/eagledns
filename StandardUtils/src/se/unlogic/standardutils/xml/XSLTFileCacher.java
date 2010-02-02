package se.unlogic.standardutils.xml;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class XSLTFileCacher implements XSLTCacher{
	
	private Templates cachedXSLT;
	private File file;
	private URIResolver uriResolver;

	public void reloadStyleSheet() throws TransformerConfigurationException{
		this.cacheStyleSheet(file);
	}
	
	public XSLTFileCacher(File f) throws TransformerConfigurationException{
		this.file = f;
		this.cacheStyleSheet(f);	
	}
	
	public XSLTFileCacher(File file, URIResolver uriResolver) throws TransformerConfigurationException {

		this.file =  file;
		this.uriResolver = uriResolver;
		this.cacheStyleSheet(file);	
	}

	public Transformer getTransformer() throws TransformerConfigurationException{
		return cachedXSLT.newTransformer();
	}
	
	private void cacheStyleSheet(File f) throws TransformerConfigurationException{
		Source xsltSource = new StreamSource(f);
		TransformerFactory transFact = TransformerFactory.newInstance();
		
		if(uriResolver != null){
			transFact.setURIResolver(uriResolver);			
		}
		
		Templates tempCachedXSLT = transFact.newTemplates(xsltSource);		
		cachedXSLT = tempCachedXSLT;
		this.file = f;
	}
	
	public void unload(){

	}

	@Override
	protected void finalize(){
		this.unload();
	}

	@Override
	public String toString() {
		if(this.file == null){
			return "XSLTCacher (no stylesheet cached)";
		}else{
			return "XSLTCacher (" + this.file + ")";
		}
	}
}
