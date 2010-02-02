package se.unlogic.standardutils.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.xml.XMLUtils;

public class XMLSettingNode implements SettingNode {

	private final Document doc;
	private final Element element;
	private final XPath xpath;


	public XMLSettingNode(String path) throws SAXException, IOException, ParserConfigurationException {

		this.doc = XMLUtils.parseXmlFile(path, false,false);
		this.element = this.doc.getDocumentElement();

		this.xpath = XPathFactory.newInstance().newXPath();

	}

	public XMLSettingNode(Document doc) {

		this.doc = doc;
		this.element = doc.getDocumentElement();

		this.xpath = XPathFactory.newInstance().newXPath();
	}

	public XMLSettingNode(Document doc, Element element) {

		this.doc = doc;
		this.element = element;

		this.xpath = XPathFactory.newInstance().newXPath();

	}

	public Boolean getBoolean(String expression) {

		return Boolean.valueOf(this.getString(expression));

	}

	public boolean getPrimitiveBoolean(String expression) {

		return Boolean.parseBoolean(this.getString(expression));

	}

	public Double getDouble(String expression) {

		String value = this.getString(expression);

		if(value != null){
			return NumberUtils.toDouble(value);
		}

		return null;

	}

	public List<Double> getDoubles(String expression) {

		NodeList nodes = this.getNodeList(expression);

		List<Double> doubles = new ArrayList<Double>();

		for (int i = 0; i < nodes.getLength(); i++) {

			String value = nodes.item(i).getTextContent();

			if(value != null){
				Double numberValue = NumberUtils.toDouble(value);

				if(numberValue != null){
					doubles.add(numberValue);
				}
			}
		}

		return doubles;

	}

	public int getInt(String expression) {

		String value = this.getString(expression);

		if(value != null){
			return NumberUtils.toInt(value);
		}

		return 0;

	}

	public Integer getInteger(String expression) {

		String value = this.getString(expression);

		if(value != null){
			return NumberUtils.toInt(value);
		}

		return null;

	}

	public List<Integer> getIntegers(String expression) {

		NodeList nodes = this.getNodeList(expression);

		List<Integer> integers = new ArrayList<Integer>();

		for (int i = 0; i < nodes.getLength(); i++) {

			String value = nodes.item(i).getTextContent();

			if(value != null){
				Integer numberValue = NumberUtils.toInt(value);

				if(numberValue != null){
					integers.add(numberValue);
				}
			}
		}

		return integers;

	}

	public Long getLong(String expression) {

		String value = this.getString(expression);

		if(value != null){
			return NumberUtils.toLong(value);
		}

		return null;

	}

	public List<Long> getLongs(String expression) {

		NodeList nodes = this.getNodeList(expression);

		List<Long> longs = new ArrayList<Long>();

		for (int i = 0; i < nodes.getLength(); i++) {

			String value = nodes.item(i).getTextContent();

			if(value != null){
				Long numberValue = NumberUtils.toLong(value);

				if(numberValue != null){
					longs.add(numberValue);
				}
			}
		}

		return longs;

	}

	public XMLSettingNode getSetting(String expression) {

		Element element = this.getElement(expression);

		return new XMLSettingNode(this.doc, element);

	}

	public List<XMLSettingNode> getSettings(String expression) {

		NodeList nodes = this.getNodeList(expression);

		List<XMLSettingNode> settingNodes = new ArrayList<XMLSettingNode>();

		for(int i = 0; i < nodes.getLength(); i++){

			settingNodes.add(new XMLSettingNode(this.doc, (Element)nodes.item(i)));

		}

		return settingNodes;

	}

	public String getString(String expression) {

		try {
			return this.xpath.evaluate(expression, this.element);
		} catch (XPathExpressionException e) {
			return null;
		}

	}

	public List<String> getStrings(String expression) {

		NodeList nodes = this.getNodeList(expression);

		List<String> strings = new ArrayList<String>();

		for (int i = 0; i < nodes.getLength(); i++) {

			strings.add(nodes.item(i).getTextContent());

		}

		return strings;

	}

	private NodeList getNodeList(String expression){

		try {
			return (NodeList) this.xpath.evaluate(expression, this.element, XPathConstants.NODESET);
		} catch(XPathExpressionException e) {
			return null;
		}

	}

	private Element getElement(String expression){

		try {
			return (Element) this.xpath.evaluate(expression, this.element, XPathConstants.NODE);
		} catch(XPathExpressionException e) {
			return null;
		}

	}

}
