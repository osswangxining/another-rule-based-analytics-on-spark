package cloud.iot.ai.analytics.validator;
=======
package cloud.iot.ai.analytics.util;
>>>>>>> branch 'master' of https://github.com/osswangxining/cloud-iot-analytics-engine.git

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLSchemaValidator {
	private static Logger logger = Logger.getLogger(XMLSchemaValidator.class.getName());

	public static String validateXMLBySchema(InputStream xmlInput, Source[] schemaInputs,
			LSResourceResolver resourceResolver) {
		return validateXMLBySchema(new InputSource(xmlInput), schemaInputs, resourceResolver);
	}

	public static String validateXMLBySchema(InputStream xmlInput, InputStream[] schemaInputs,
			LSResourceResolver resourceResolver) {
		return validateXMLBySchema(new InputSource(xmlInput), schemaInputs, resourceResolver);
	}

	public static String validateXMLBySchema(InputSource xmlInput, InputStream[] schemaInputs,
			LSResourceResolver resourceResolver) {
		if (schemaInputs != null) {
			Source[] schemaSource = new StreamSource[schemaInputs.length];
			for (int i = 0; i < schemaInputs.length; i++) {
				schemaSource[i] = new StreamSource(schemaInputs[i]);
			}
			return validateXMLBySchema(xmlInput, schemaSource, resourceResolver);
		}
		return null;
	}

	public static String validateXMLBySchema(InputSource xmlInput, Source[] schemaInputs,
			LSResourceResolver resourceResolver) {
		String METHOD_NAME = "validateXMLBySchema";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLSchemaValidator", METHOD_NAME);
		}
		StringBuffer errMsg = new StringBuffer();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);

		SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		try {
			schemaFactory.setResourceResolver(resourceResolver);
			factory.setSchema(schemaFactory.newSchema(schemaInputs));
			DocumentBuilder builder = factory.newDocumentBuilder();

			builder.setErrorHandler(new CustomErrorHandler(new PrintWriter(new OutputStreamWriter(System.out))));

			builder.parse(xmlInput);
		} catch (SAXException e) {
			errMsg.append(e.getLocalizedMessage());
		} catch (ParserConfigurationException e) {
			errMsg.append(e.getLocalizedMessage());
		} catch (IOException e) {
			errMsg.append(e.getLocalizedMessage());
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLSchemaValidator", METHOD_NAME);
		}
		return errMsg.toString().equals("") ? null : errMsg.toString();
	}

	private static class CustomErrorHandler implements ErrorHandler {
		PrintWriter writer = null;

		public CustomErrorHandler(PrintWriter writer) {
			this.writer = writer;
		}

		private String getParseException(SAXParseException spe) {
			if (spe == null)
				return null;
			if (spe.getSystemId() == null) {
				return "Line=" + spe.getLineNumber() + ": " + spe.getMessage();
			}
			return "URI=" + spe.getSystemId() + " Line=" + spe.getLineNumber() + ": " + spe.getMessage();
		}

		public void error(SAXParseException exception) throws SAXException {
			throw new SAXException("Error: " + getParseException(exception));
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			throw new SAXException("Fatal Error: " + getParseException(exception));
		}

		public void warning(SAXParseException exception) throws SAXException {
			if (this.writer != null)
				this.writer.print("Warning: " + getParseException(exception));
		}
	}
}
