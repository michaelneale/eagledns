package se.unlogic.standardutils.xml;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Deprecated
public class XMLConfigHandler {

	private Document doc = null;

	public XMLConfigHandler(String path) throws SAXException, IOException, ParserConfigurationException {
		doc = XMLUtils.parseXmlFile(path, false,false);
	}

	public String getSetting(String setting){
		try{
			return doc.getElementsByTagName(setting).item(0).getFirstChild().getNodeValue();
		}catch (Exception e){return null;}
	}

	public ArrayList<String> getSettings(String name){

		NodeList nodelist = this.doc.getElementsByTagName(name);

		if(nodelist != null && nodelist.getLength() > 0){
			ArrayList<String> settingsList = new ArrayList<String>();

			for(int i=0;i<nodelist.getLength();i++){
				try{
					if(nodelist.item(i).getFirstChild().getNodeValue() != null){
						settingsList.add(nodelist.item(i).getFirstChild().getNodeValue());
					}
				}catch(NullPointerException e){}
			}

			if(settingsList.size() > 0){
				return settingsList;
			}
		}
		return null;
	}

	public boolean setSetting(String setting, String value){
		return false;
	}
	public boolean addSetting(String setting, String value){
		return false;
	}
	public boolean removeSetting(String setting){
		return false;
	}
}
