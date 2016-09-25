package cloud.iot.ai.analytics.util;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.w3c.dom.Node;

public class QNameUtils {
	private static Logger logger = Logger.getLogger(QNameUtils.class.getName());

	public static QName getQName(Node node) {
		String METHOD_NAME = "getQName";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("QNameUtils", METHOD_NAME);
		}
		QName result = null;
		if ((node != null) && (node.getLocalName() != null)) {
			result = new QName(node.getNamespaceURI(), node.getLocalName());
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("QNameUtils", METHOD_NAME);
		}
		return result;
	}

	public static boolean equals(QName qname1, QName qname2) {
		String METHOD_NAME = "equals";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("QNameUtils", METHOD_NAME);
		}
		boolean result = false;
		if ((qname1 == null) && (qname2 == null))
			result = true;
		else if ((qname1 == null) && (qname2 != null))
			result = false;
		else if (qname1 != null) {
			result = qname1.equals(qname2);
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("QNameUtils", METHOD_NAME);
		}
		return result;
	}

	public static boolean matches(QName qname, Node node) {
		String METHOD_NAME = "matches";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("QNameUtils", METHOD_NAME);
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("QNameUtils", METHOD_NAME);
		}
		return equals(qname, getQName(node));
	}

	public static String getNameWithPrefix(QName qName, Map<String, String> nsPrefixContext) {
		String METHOD_NAME = "getNameWithPrefix";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("QNameUtils", METHOD_NAME);
		}
		String result = null;
		if (qName != null) {
			String nsUri = qName.getNamespaceURI();
			String prefix = qName.getPrefix();
			String localName = qName.getLocalPart();
			if ((prefix == null) && (nsPrefixContext != null)) {
				prefix = (String) nsPrefixContext.get(nsUri);
			}
			result = prefix + ":" + localName;
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("QNameUtils", METHOD_NAME);
		}
		return result;
	}

	public static QName getQName(String name, Map<String, String> prefixNSContext) {
		String METHOD_NAME = "getQName";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("QNameUtils", METHOD_NAME);
		}
		QName result = null;
		if (name != null) {
			int index = name.indexOf(":");
			String localName = name.substring(index + 1);
			String ns = null;

			String prefix = name.substring(0, index + 1);
			if ((prefix != null) && (prefixNSContext != null))
				ns = (String) prefixNSContext.get(prefix);
			result = new QName(ns, localName, prefix == null ? "" : prefix);
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("QNameUtils", METHOD_NAME);
		}
		return result;
	}
}
