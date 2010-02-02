package se.unlogic.standardutils.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Collection;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUtils {

	private static DocumentBuilder documentBuilder;
	private static DocumentBuilder namespaceAwareDocumentBuilder;

	static {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

			documentBuilder = documentBuilderFactory.newDocumentBuilder();

			documentBuilderFactory.setNamespaceAware(true);

			namespaceAwareDocumentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static Document createDomDocument() {

		return documentBuilder.newDocument();
	}

	public static Document createNamespaceAwareDomDocument() {

		return namespaceAwareDocumentBuilder.newDocument();
	}

	public static String toString(Document doc, String encoding, boolean indent) throws TransformerFactoryConfigurationError, TransformerException {
		Source source = new DOMSource(doc);
		StringWriter sw = new StringWriter();
		Result result = new StreamResult(sw);

		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.ENCODING, encoding);

		if (indent) {
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}

		xformer.transform(source, result);

		return sw.getBuffer().toString();
	}

	public static void toString(Document doc, String encoding, Writer w, boolean indent) throws TransformerFactoryConfigurationError, TransformerException {
		Source source = new DOMSource(doc);
		Result result = new StreamResult(w);

		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.ENCODING, encoding);

		if (indent) {
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}

		xformer.transform(source, result);
	}

	public static Document parseXmlFile(String filename, boolean validating, boolean namespaceAware) throws SAXException, IOException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setNamespaceAware(namespaceAware);
		factory.setValidating(validating);

		Document doc = factory.newDocumentBuilder().parse(new File(filename));

		return doc;
	}

	public static Document parseXmlFile(URI uri, boolean validating) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(validating);
		Document doc = factory.newDocumentBuilder().parse(uri.toString());
		return doc;
	}

	public static Document parseXmlFile(File f, boolean validating) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(validating);
		Document doc = factory.newDocumentBuilder().parse(f);
		return doc;
	}

	public static Document parseXmlFile(InputStream stream, boolean validating) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(validating);
		Document doc = factory.newDocumentBuilder().parse(stream);
		return doc;
	}

	public static Element createElement(String name, Object value, Document doc) {
		Element element = doc.createElement(name);
		element.appendChild(doc.createTextNode(value.toString()));
		return element;
	}

	public static Element createCDATAElement(String name, Object value, Document doc) {
		Element element = doc.createElement(name);
		element.appendChild(doc.createCDATASection(value.toString()));
		return element;
	}

	public static void writeXmlFile(Document doc, File file, boolean indent) throws TransformerFactoryConfigurationError, TransformerException {
		// Prepare the DOM document for writing
		Source source = new DOMSource(doc);

		// Prepare the output file
		Result result = new StreamResult(file);

		// Write the DOM document to the file
		Transformer xformer = TransformerFactory.newInstance().newTransformer();

		if (indent) {
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}

		xformer.transform(source, result);

	}

	public static void writeXmlFile(Document doc, File file, Entry<String,String>... outputKeys) throws TransformerFactoryConfigurationError, TransformerException {
		// Prepare the DOM document for writing
		Source source = new DOMSource(doc);

		// Prepare the output file
		Result result = new StreamResult(file);

		// Write the DOM document to the file
		Transformer xformer = TransformerFactory.newInstance().newTransformer();

		if(outputKeys != null){

			for(Entry<String,String> entry : outputKeys){

				xformer.setOutputProperty(entry.getKey(), entry.getValue());
			}
		}

		xformer.transform(source, result);

	}

	public static void writeXmlFile(Document doc, String filename, boolean indent) throws TransformerFactoryConfigurationError, TransformerException {

		// Prepare the output file
		File file = new File(filename);


		writeXmlFile(doc, file, indent);

	}

	public static void append(Document doc, Element targetElement, Collection<? extends XMLable> beans) {

		if (beans != null && !beans.isEmpty()) {

			for (XMLable xmlable : beans) {
				targetElement.appendChild(xmlable.toXML(doc));
			}
		}
	}

	public static void append(Document doc, Element targetElement, String elementName, Collection<? extends XMLable> beans) {

		if (beans != null && !beans.isEmpty()) {

			Element subElement = doc.createElement(elementName);
			targetElement.appendChild(subElement);

			for (XMLable xmlable : beans) {
				subElement.appendChild(xmlable.toXML(doc));
			}
		}
	}

	public static void appendNewCDATAElement(Document doc, Element targetElement, String elementName, String value) {

		if(value != null){
			targetElement.appendChild(createCDATAElement(elementName, value, doc));
		}
	}

	public static void appendNewElement(Document doc, Element targetElement, String elementName, String value) {

		if(value != null){
			targetElement.appendChild(createElement(elementName, value, doc));
		}
	}

	public static void appendNewCDATAElement(Document doc, Element targetElement, String elementName, Object value) {

		if(value != null){
			appendNewCDATAElement(doc,targetElement,elementName,value.toString());
		}
	}

	public static void appendNewElement(Document doc, Element targetElement, String elementName, Object value) {

		if(value != null){
			appendNewElement(doc,targetElement,elementName,value.toString());
		}

	}

	/**
	 * Adds or replaces node in parent.
	 * @param parent
	 * @param node
	 * @throws Exception - Node cannot exist more than once,
	 * i.e. multiple nodes with the same name cannot exist in parent.
	 */
	public static void replaceSingleNode(Element parent, final Node node) throws RuntimeException {
		NodeList nodes = parent.getElementsByTagName(node.getNodeName());

		if(nodes.getLength() > 1) {
			throw new RuntimeException("Parent element contains multiple nodes with the name " + node.getNodeName());
		}
		if(nodes.getLength() == 0) {
			parent.appendChild(node);
		} else {
			parent.replaceChild(node, nodes.item(0));
		}
	}
}
