package uk.org.openhealthcare;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import android.content.Context;

public class GuidelineData {

	final Map<String, String> map = new HashMap<String,String>();
	
	public GuidelineData(Context ctx) throws IOException, ParserConfigurationException, SAXException
	{
		// Load the XML from the assets folder and parse it into the map
		InputStream inp = ctx.getAssets().open("xml/guidelines.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = null;
		try {
			doc = db.parse( inp );
		} catch(Exception e) {
			System.out.println(e.toString());
		}
		
		NodeList nodeList = doc.getElementsByTagName("guide");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			Element elem = (Element)node;
			String nm = elem.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
			String url = elem.getElementsByTagName("url").item(0).getFirstChild().getNodeValue();
			map.put( nm, url );
		}
	};
	
	String Get(String k) {
		return map.get(k);
	}
	
	String[] GetKeys() {
		// Fetch a sorted version of the keys from the map
		Object[] objs = map.keySet().toArray();
		String[]items = new String[objs.length];
		for( int i = 0; i < objs.length; i++ )
			items[i] = objs[i].toString();
		Arrays.sort(items);
		return items;
	};
	
}
