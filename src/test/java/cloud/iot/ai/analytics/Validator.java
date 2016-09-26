package cloud.iot.ai.analytics;

import cloud.iot.ai.analytics.element.Schema;
import cloud.iot.ai.analytics.element.SchematronReader;
import cloud.iot.ai.analytics.factory.SchematronFactory;
import cloud.iot.ai.analytics.function.XPathCustomFunctionResolver;
import cloud.iot.ai.analytics.json.JSON2XMLElement;
import cloud.iot.ai.analytics.output.TextOutputFormatter;
import cloud.iot.ai.analytics.validator.SchematronValidator;
import cloud.iot.ai.analytics.xml.XMLUtils;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.xpath.XPathExpressionException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

public class Validator {

    public static void main(String[] args) {

        try {
            run();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void run() throws UnsupportedEncodingException, IOException, XPathExpressionException {

//        Document doc = XMLUtils.read(Validator.class.getResourceAsStream("/1.xml"));
//        if (doc == null) {
//            System.out.println("xml file does exist.");
//            return;
//        }
//    	String jsonString = IOUtils.toString(Validator.class.getResourceAsStream("/2.json"));
    	String content = "{\"d\":{\"time\":1474984406506,\"house\":{\"roof\":\"\",\"owner\":\"owner8\",\"wall\":\"\"}},\"t\":\"tenant001\"}";
    	long time = 0L;
		JSONObject data = new JSONObject();
		JSONObject root = new JSONObject();
		String tenantId = "";
    	try {
			root = new JSONObject(content);

			data = root.optJSONObject("d");
			tenantId = root.optString("t");
			String t = (data.optString("time") == null)?"0":data.optString("time");
			time = Long.parseLong(t);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("tenantId:" + tenantId+ ", time:" + time + ", d:" + data);
//		System.out.println("rule:" + rule);

		Element elem = JSON2XMLElement.convert(root);
		
//        Element elem = JSON2XMLElement.convert(content);
        SchematronReader reader = SchematronFactory.INSTANCE
                .newSchematronReader();
        
        String rule = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><sch:schema xmlns:sch=\"http://www.ascc.net/xml/schematron\" defaultPhase=\"built\" icon=\"http://www.ascc.net/xml/resource/schematron/bilby.jpg\"><sch:pattern xmlns:sch=\"http://purl.oclc.org/dsdl/schematron\" id=\"completed\" name=\"Construction Checks\"><sch:rule context=\"//house\"><sch:assert test=\"count(wall) &lt; 4 \">A house should have 1-4 walls</sch:assert><sch:assert test=\"builder\">An incomplete house must have a builder assigned to it</sch:assert><sch:assert test=\"not(owner)\">An incomplete house cannot have an owner</sch:assert><sch:report test=\"not(roof)\">The house is incomplete, it still needs a roof</sch:report></sch:rule><sch:p>Constraints which are applied during construction</sch:p></sch:pattern><sch:pattern id=\"admin2\" name=\"Final Checks\"><sch:rule context=\"/house\"><sch:assert test=\"count(wall) = 4\">A house should have 4 walls</sch:assert><sch:assert test=\"owner\">An incomplete house must have an owner</sch:assert><sch:assert test=\"not(builder)\">An incomplete house doesn't need a builder</sch:assert><sch:report test=\"roof\">The house is incomplete, it still needs a roof</sch:report></sch:rule><sch:p>Constraints which are applied after construction</sch:p></sch:pattern><sch:p>This is an example schema for the <emph>Building Projects XML</emph> language.</sch:p><sch:phase id=\"underConstruction\"><sch:active pattern=\"construction\"/><sch:active pattern=\"admin\"/></sch:phase><sch:phase id=\"built\"><sch:active pattern=\"completed\"/><sch:active pattern=\"admin\"/></sch:phase></sch:schema>";

        //        Schema schema = reader.readSchematron(null, Validator.class.getResourceAsStream("/1.sch"));
       System.out.println(rule);
        Schema schema = reader.readSchematron(null, new ByteArrayInputStream(rule.getBytes("UTF-8")));

        SchematronValidator validator = SchematronFactory.INSTANCE
                .newSchematronValidator();
        TextOutputFormatter outputFormatter = new TextOutputFormatter();
        validator.setFormatter(outputFormatter);
        validator.setFunctionResolver(new XPathCustomFunctionResolver(elem));
        validator.validate(elem, schema);

        List<String> outputs = outputFormatter.getResult();
        if (outputs != null) {
            System.out.println(outputs.size());
            int index = 1;
            for (String s : outputs) {
                System.out.println("#[" + (index++) + "]" + s);
            }
        }
    }

}
