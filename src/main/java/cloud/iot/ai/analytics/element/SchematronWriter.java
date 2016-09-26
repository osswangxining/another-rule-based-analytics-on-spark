package cloud.iot.ai.analytics.element;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import cloud.iot.ai.analytics.xml.DOM2Writer;

public class SchematronWriter {
	private static Logger logger = Logger.getLogger(SchematronWriter.class.getName());
	private String defaultCharsetName = "UTF-8";

	public void writeSchematron(Schema schema, OutputStream output) {
		writeSchematron(schema, output, this.defaultCharsetName);
	}

	public void writeSchematron(Schema schema, OutputStream output, String charsetName) {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(output, charsetName);
			writeSchematron(schema, writer, charsetName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void writeSchematron(Schema schema, Writer writer) {
		writeSchematron(schema, writer, this.defaultCharsetName);
	}

	public void writeSchematron(Schema schema, Writer writer, String charsetName) {
		String METHOD_NAME = "writeSchematron";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SchematronWriter", METHOD_NAME);
		}
		if (schema != null) {
			try {
				String content = DOM2Writer.serializeAsString(schema.toElement(), false, charsetName);
				writer.write(content);
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (logger.isLoggable(Level.FINER))
			logger.exiting("SchematronWriter", METHOD_NAME);
	}
}
