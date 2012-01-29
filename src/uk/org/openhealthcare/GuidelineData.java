/*
*    Copyright (C) 2011  Open Health Care, R.Jones, Dr. VJ Joshi
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.org.openhealthcare;

import java.io.File;
import java.io.FileInputStream;
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
import android.os.Environment;

public class GuidelineData {

final Map<String, GuidelineItem> map = new HashMap<String,GuidelineItem>();
	
	public GuidelineData(Context ctx) throws IOException, ParserConfigurationException, SAXException
	{
		// Load the XML from the assets folder and parse it into the map
		InputStream inp = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ "nice_guidance" + File.separator + "xml/guidelines.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = null;
		try {
			doc = db.parse( inp );
		} catch(Exception e) {
			System.out.println(e.toString());
		}
		 
		NodeList nodeList = doc.getElementsByTagName("guideline");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			Element elem = (Element)node;
			GuidelineItem item = new GuidelineItem();
			item.name = elem.getElementsByTagName("title").item(0).getFirstChild().getNodeValue().trim();
			item.url = elem.getElementsByTagName("url").item(0).getFirstChild().getNodeValue().trim();
			item.code = elem.getAttribute("code");
			item.category = elem.getAttribute("category");
			item.subcategory = elem.getAttribute("subcategory");
			map.put( item.name, item );
		}
	};
	
	GuidelineItem Get(String k) {
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
