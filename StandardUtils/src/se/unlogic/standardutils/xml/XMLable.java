package se.unlogic.standardutils.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface XMLable {

	public Node toXML(Document doc);
}
