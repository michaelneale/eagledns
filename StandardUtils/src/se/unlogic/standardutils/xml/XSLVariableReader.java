package se.unlogic.standardutils.xml;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XSLVariableReader {

	private final Document doc;
	private final XPath xpath = XPathFactory.newInstance().newXPath();;

	public XSLVariableReader(Document doc){
		this.doc = doc;
	}

	public XSLVariableReader(URI uri) throws SAXException, IOException, ParserConfigurationException{
		this.doc = XMLUtils.parseXmlFile(uri, false);
	}

	public XSLVariableReader(String filePath) throws SAXException, IOException, ParserConfigurationException{
		this.doc = XMLUtils.parseXmlFile(filePath, false,false);
	}

	public XSLVariableReader(File file) throws SAXException, IOException, ParserConfigurationException{
		this.doc = XMLUtils.parseXmlFile(file, false);
	}

	public String getValue(String name){
		try {
			return this.xpath.evaluate("//variable[@name='" + name + "']/text()", this.doc.getDocumentElement());
		} catch (XPathExpressionException e) {
			return null;
		}
	}
}
