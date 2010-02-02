package se.unlogic.standardutils.hddtemp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLGenerator;

@XMLElement
public class Drive implements Elementable{

	@XMLElement
	private int temp;
	
	@XMLElement
	private String type;
	
	@XMLElement
	private String device;
	
	
	public Drive(int temp, String type, String device)
	{
		this.temp = temp;
		this.device = device;
		this.type = type;
	}
	
	public int getTemp() {
		return temp;
	}
	public void setTemp(int temp) {
		this.temp = temp;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;	}

	@Override
	public String toString() {

		return device + " " + temp + "°" + "(" + type + ")";
	}

	public Element toXML(Document doc) {
		
		return XMLGenerator.toXML(this, doc);
	}
}
