package se.unlogic.standardutils.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface Elementable extends XMLable {

	public Element toXML(Document doc);
}
