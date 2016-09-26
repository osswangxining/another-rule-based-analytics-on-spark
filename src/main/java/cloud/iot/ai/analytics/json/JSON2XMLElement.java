package cloud.iot.ai.analytics.json;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cloud.iot.ai.analytics.xml.XMLUtils;

public class JSON2XMLElement {

	public static Element convert(String jsonString) {
		if (jsonString == null)
			return null;

		org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
		String string = XML.toString(jsonObject);
		if (string == null)
			return null;

		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<root>"
				+ string+ "</root>";
		System.out.println(xml);
		
		Document doc = null;
		try {
			doc = XMLUtils.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (doc == null) {
			return null;
		}
		Element elem = doc.getDocumentElement();
		return elem;
	}

	public static Element convert(org.json.JSONObject jsonObject) {
		if (jsonObject == null)
			return null;

		String string = XML.toString(jsonObject);
		if (string == null)
			return null;

		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<root>"
				+ string+ "</root>";
		System.out.println(xml);
		
		Document doc = null;
		try {
			doc = XMLUtils.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (doc == null) {
			return null;
		}
		Element elem = doc.getDocumentElement();
		return elem;
	}

	// public static Element convert(InputStream inputStream) {
	// JSONObject.
	// org.json.JSONObject jsonFileObject = new
	// org.json.JSONObject(inputStream);
	// return null;
	// }
}
