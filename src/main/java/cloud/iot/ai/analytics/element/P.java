package cloud.iot.ai.analytics.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cloud.iot.ai.analytics.xml.XMLUtils;

public class P extends SchematronElement {
	private static final long serialVersionUID = 5512928319109103101L;
	private String id = null;

	private String clsValue = null;

	private String icon = null;

	private List<TextElement> contents = new ArrayList<TextElement>();

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<TextElement> getContent() {
		return this.contents;
	}

	public void addContent(TextElement content) {
		this.contents.add(content);
	}

	public String getClsValue() {
		return this.clsValue;
	}

	public void setClsValue(String clsValue) {
		this.clsValue = clsValue;
	}

	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Element toElement() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			Document document = builder.newDocument();
			Element element = document.createElementNS(getTargetNamespaceURI(), "p");
			element.setPrefix(getPrefix());
			if (getId() != null)
				element.setAttribute("id", getId());
			if (getClsValue() != null)
				element.setAttribute("class", getClsValue());
			if (getIcon() != null)
				element.setAttribute("icon", getIcon());
			for (Iterator<TextElement> iterator = this.contents.iterator(); iterator.hasNext();) {
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
