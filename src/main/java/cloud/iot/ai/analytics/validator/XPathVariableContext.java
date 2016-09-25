package cloud.iot.ai.analytics.validator;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathVariableResolver;

import cloud.iot.ai.analytics.element.ContextElement;
import cloud.iot.ai.analytics.element.Let;
import cloud.iot.ai.analytics.element.Pattern;
import cloud.iot.ai.analytics.element.Rule;
import cloud.iot.ai.analytics.element.Schema;

public class XPathVariableContext implements XPathVariableResolver {
	private static Logger logger = Logger.getLogger(XPathVariableContext.class.getName());
	private ContextElement ctxElem = null;

	public XPathVariableContext(ContextElement ctxElem) {
		this.ctxElem = ctxElem;
	}

	public Object resolveVariable(QName qName) {
		String METHOD_NAME = "resolveVariable";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XPathVariableContext", METHOD_NAME);
		}
		if (this.ctxElem == null)
			return null;
		if ((this.ctxElem instanceof Schema))
			return getValue(qName, (Schema) this.ctxElem);
		if ((this.ctxElem instanceof Pattern))
			return getValue(qName, (Pattern) this.ctxElem);
		if ((this.ctxElem instanceof Rule)) {
			return getValue(qName, (Rule) this.ctxElem);
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XPathVariableContext", METHOD_NAME);
		}
		return null;
	}

	private Object getValue(QName qName, Schema schema) {
		Let let = (Let) schema.getLets().get(qName);
		return let == null ? null : let.getNewValue();
	}

	private Object getValue(QName qName, Pattern pattern) {
		Let let = (Let) pattern.getLets().get(qName);
		if (let != null)
			return let.getNewValue();
		if ((let == null) && (pattern.getSchema() != null))
			return getValue(qName, pattern.getSchema());
		return null;
	}

	private Object getValue(QName qName, Rule rule) {
		Let let = (Let) rule.getLets().get(qName);
		if (let != null)
			return let.getNewValue();
		if ((let == null) && (rule.getPattern() != null))
			return getValue(qName, rule.getPattern());
		return null;
	}
}
