package cloud.iot.ai.analytics.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cloud.iot.ai.analytics.util.XMLUtils;

public abstract class AssertionOrReportElement extends Rich {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4419962754368420653L;

	private String test = null;

	private String flag = null;

	private String id = null;

	private String location = null;

	private String diagnostics = null;

	private Linkable linkable = null;

	private List<String> diagnosticIdList = new ArrayList<String>();

	public String getDiagnostics() {
		return this.diagnostics;
	}

	public void setDiagnostics(String diagnostics) {
		this.diagnostics = diagnostics;
	}

	public String getFlag() {
		return this.flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTest() {
		return this.test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public abstract Element toElement();

	public Element toElement(String tag) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			Document document = builder.newDocument();
			Element element = document.createElementNS(getTargetNamespaceURI(), tag);
			element.setPrefix(getPrefix());
			if (getId() != null)
				element.setAttribute("id", getId());
			if (getTest() != null)
				element.setAttribute("test", getTest());
			if (getFlag() != null)
				element.setAttribute("flag", getFlag());
			if (getLocation() != null)
				element.setAttribute("location", getLocation());
			if (getLinkable() != null) {
				if (getLinkable().getRole() != null)
					element.setAttribute("role", getLinkable().getRole());
				if (getLinkable().getSubject() != null)
					element.setAttribute("subject", getLinkable().getSubject());
			}
			printAttrs(element);

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

	public List<String> getDiagnosticIdList() {
		return this.diagnosticIdList;
	}

	public void setDiagnosticIdList(List<String> diagnosticIdList) {
		this.diagnosticIdList = diagnosticIdList;
	}

	public Linkable getLinkable() {
		return this.linkable;
	}

	public void setLinkable(Linkable linkable) {
		this.linkable = linkable;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
