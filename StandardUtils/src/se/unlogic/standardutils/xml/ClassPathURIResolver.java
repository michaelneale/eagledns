package se.unlogic.standardutils.xml;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;


public class ClassPathURIResolver implements URIResolver {

	private static final ClassPathURIResolver CLASS_PATH_URI_RESOLVER = new ClassPathURIResolver();
	
	public static ClassPathURIResolver getInstance(){
		
		return CLASS_PATH_URI_RESOLVER;
	}
	
	private static final String PREFIX = "classpath://";
	
	public Source resolve(String href, String base) throws TransformerException {
		
		if(href.startsWith(PREFIX) && href.length() > PREFIX.length()){
			
			String classPath = "/" + href.substring(PREFIX.length());
						
			return new StreamSource(this.getClass().getResource(classPath).toString());
			
		}else{
			
			return null;
		}
	}
}
