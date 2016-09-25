package cloud.iot.ai.analytics.validator;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.NamespaceContext;

import cloud.iot.ai.analytics.element.Schema;

public class SchematronNamespaceContext implements NamespaceContext {
	private static Logger logger = Logger.getLogger(SchematronNamespaceContext.class.getName());
	private Schema schema = null;

	public SchematronNamespaceContext(Schema schema) {
		this.schema = schema;
	}

	public String getNamespaceURI(String prefix) {
		String METHOD_NAME = "getNamespaceURI";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SchematronNamespaceContext", METHOD_NAME);
		}
		String result = null;
		if (this.schema != null) {
			Map<String, String> namespaces = this.schema.getPrefixUriMap();
			result = (String) namespaces.get(prefix);
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("SchematronNamespaceContext", METHOD_NAME);
		}
		return result;
	}

	public String getPrefix(String namespaceURI) {
		String METHOD_NAME = "getPrefix";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SchematronNamespaceContext", METHOD_NAME);
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("SchematronNamespaceContext", METHOD_NAME);
		}
		return null;
	}

	public Iterator<String> getPrefixes(String namespaceURI) {
		String METHOD_NAME = "getPrefixes";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SchematronNamespaceContext", METHOD_NAME);
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("SchematronNamespaceContext", METHOD_NAME);
		}
		return null;
	}
}
