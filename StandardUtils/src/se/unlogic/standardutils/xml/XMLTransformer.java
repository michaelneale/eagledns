package se.unlogic.standardutils.xml;

import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XMLTransformer {
	
	public static void transformToWriter(Transformer transformer, Document doc, Writer writer, String encoding) throws TransformerException{
		transformer.setOutputProperty(OutputKeys.ENCODING,encoding);
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
	}	
}