package cloud.iot.ai.analytics.function;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionResolver;

import org.w3c.dom.Element;

public class XPathCustomFunctionResolver implements XPathFunctionResolver {
	private static Logger logger = Logger.getLogger(XPathCustomFunctionResolver.class.getName());
	private final static QName QNAME_ABS_URI = new QName("http://cloud.iot.ai.analytics.function", "crossCalculate");

	private Element elem = null;

	public XPathCustomFunctionResolver(Element elem) {
		this.elem = elem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.xpath.XPathFunctionResolver#resolveFunction(javax.xml.namespace
	 * .QName, int)
	 */
	public XPathFunction resolveFunction(QName functionName, int arity) {
		String METHOD_NAME = "resolveFunction";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XPathCustomFunctionResolver", METHOD_NAME);
		}
		if (QNAME_ABS_URI.equals(functionName)) {
			return new CrossCalculateXPathFunction(this.elem);
		}
		if (logger.isLoggable(Level.FINER))
			logger.exiting("XPathCustomFunctionResolver", METHOD_NAME);
		return null;
	}

}
