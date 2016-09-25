package cloud.iot.ai.analytics.element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ValueOfElement extends TextElement {
	private static final long serialVersionUID = 8218604458829620763L;
	private String select = null;

	public ValueOfElement(String select) {
		setSelect(select);
	}

	public String getSelect() {
		return this.select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (this.select != null) {
			sb.append("<").append("value-of").append(" select=\"").append(this.select).append("\"/>");
		}
		return sb.toString();
	}

	public Node toNode() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			Document document = builder.newDocument();
			Element element = null;
			if (getSelect() != null) {
				element = document.createElementNS(getTargetNamespaceURI(), "value-of");
				element.setPrefix(getPrefix());
				element.setAttribute("select", getSelect());
			}
			return element;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
