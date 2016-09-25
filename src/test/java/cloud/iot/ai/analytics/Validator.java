package cloud.iot.ai.analytics;

import cloud.iot.ai.analytics.element.Schema;
import cloud.iot.ai.analytics.element.SchematronReader;
import cloud.iot.ai.analytics.factory.SchematronFactory;
import cloud.iot.ai.analytics.function.XPathCustomFunctionResolver;
import cloud.iot.ai.analytics.output.TextOutputFormatter;
import cloud.iot.ai.analytics.util.XMLUtils;
import cloud.iot.ai.analytics.validator.SchematronValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.xpath.XPathExpressionException;
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

        Document doc = XMLUtils.read(Validator.class.getResourceAsStream("/1.xml"));
        if (doc == null) {
            System.out.println("xml file does exist.");
            return;
        }
        Element elem = doc.getDocumentElement();
        SchematronReader reader = SchematronFactory.INSTANCE
                .newSchematronReader();
        Schema schema = reader.readSchematron(null, Validator.class.getResourceAsStream("/1.sch"));

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
