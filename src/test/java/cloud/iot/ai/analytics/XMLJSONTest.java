package cloud.iot.ai.analytics;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class XMLJSONTest {
	public static String readFile(String filepath) throws FileNotFoundException, IOException {

		StringBuilder sb = new StringBuilder();
		InputStream in = new FileInputStream(filepath);
		Charset encoding = Charset.defaultCharset();

		Reader reader = new InputStreamReader(in, encoding);

		int r = 0;
		while ((r = reader.read()) != -1)// Note! use read() rather than
											// readLine()
											// Can process much larger files
											// with read()
		{
			char ch = (char) r;
			sb.append(ch);
		}

		in.close();
		reader.close();

		return sb.toString();
	}

	public static void writeFile(String filepath, String output) throws FileNotFoundException, IOException {
		FileWriter ofstream = new FileWriter(filepath);
		try (BufferedWriter out = new BufferedWriter(ofstream)) {
			out.write(output);
		}
	}

	public static String convert(String json, String root) throws JSONException {
		org.json.JSONObject jsonFileObject = new org.json.JSONObject(json);
		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<" + root + ">"
				+ org.json.XML.toString(jsonFileObject) + "</" + root + ">";
		return xml;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		URL inputURL = XMLJSONTest.class.getResource("/input.json");
		URL outputURL = XMLJSONTest.class.getResource("/output.xml");
		System.out.println(inputURL);
		System.out.println(outputURL);

		// Read JSON File
		long startTime = System.currentTimeMillis();
		String json = readFile(inputURL.getFile());// Read File
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println("Read File Duration: " + duration);

		// Convert JSON to XML
		startTime = System.currentTimeMillis();
		String xml = convert(json, "root");// State name of root element tag
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Process Data Duration: " + duration);
		System.out.println(xml);
		
		JSONObject jsonObject = XML.toJSONObject(xml);
		System.out.println(jsonObject);
		// Write XML File
		// startTime = System.currentTimeMillis();
		// writeFile(outputURL.getFile(), xml);
		// endTime = System.currentTimeMillis();
		// duration = endTime - startTime;
		// System.out.println("Write File Duration: " + duration);

	}

}
