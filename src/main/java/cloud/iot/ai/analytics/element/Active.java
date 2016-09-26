package cloud.iot.ai.analytics.element;

import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cloud.iot.ai.analytics.xml.XMLUtils;

public class Active extends SchematronElement {
	private static final long serialVersionUID = 4138745637595190028L;
	private String pattern = null;

	public Active(String pattern) {
		setPattern(pattern);
	}

	public String getPattern() {
		return this.pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Element toElement() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			Document document = builder.newDocument();
			Element element = document.createElementNS(getTargetNamespaceURI(), "active");
			element.setPrefix(getPrefix());
			if (getPattern() != null) {
				element.setAttribute("pattern", getPattern());
			}
			for (Iterator<TextElement> iterator = getContent().iterator(); iterator.hasNext();) {
				TextElement child = (TextElement) iterator.next();
				XMLUtils.insertElement(element, child.toNode());
			}
			return element;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
