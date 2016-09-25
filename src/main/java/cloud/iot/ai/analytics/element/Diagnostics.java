package cloud.iot.ai.analytics.element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cloud.iot.ai.analytics.util.XMLUtils;

public class Diagnostics extends SchematronElement {
	private static final long serialVersionUID = 7452439072930166486L;
	private Map<String, Diagnostic> diagnostics = new HashMap<String, Diagnostic>();

	public Map<String, Diagnostic> getDiagnostics() {
		return this.diagnostics;
	}

	public void addDiagnostic(String identifier, Diagnostic diagnostic) {
		this.diagnostics.put(identifier, diagnostic);
	}

	public Element toElement() {
		if (this.diagnostics.size() == 0)
			return null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			Document document = builder.newDocument();
			Element parentElement = document.createElementNS(getTargetNamespaceURI(), "diagnostics");
			for (Iterator<Diagnostic> iter = this.diagnostics.values().iterator(); iter.hasNext();) {
				Diagnostic diag = (Diagnostic) iter.next();
				Element element = document.createElementNS(getTargetNamespaceURI(), "diagnostic");
				element.setPrefix(getPrefix());
				if (diag.getId() != null) {
					element.setAttribute("id", diag.getId());
				}
				diag.printAttrs(element);
				for (Iterator<TextElement> iterator = diag.getContent().iterator(); iterator.hasNext();) {
					TextElement child = (TextElement) iterator.next();

					XMLUtils.insertElement(element, child.toNode());
				}

				parentElement.appendChild(element);
			}

			return parentElement;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
