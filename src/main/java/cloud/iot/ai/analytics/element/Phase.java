package cloud.iot.ai.analytics.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cloud.iot.ai.analytics.util.QNameUtils;
import cloud.iot.ai.analytics.util.XMLUtils;

public class Phase extends ContextElement {
	private static final long serialVersionUID = -5555887078052975549L;
	private List<Active> actives = new ArrayList<Active>();

	public Phase(String id) {
		setId(id);
	}

	public List<Active> getActives() {
		return this.actives;
	}

	public void addActive(Active active) {
		this.actives.add(active);
	}

	public Schema getRoot() {
		return getSchema();
	}

	public Element toElement() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			Document document = builder.newDocument();
			Element element = document.createElementNS(getTargetNamespaceURI(), "phase");
			element.setPrefix(getPrefix());
			if (getId() != null)
				element.setAttribute("id", getId());
			if (getName() != null)
				element.setAttribute("name", getName());
			printAttrs(element);

			for (Iterator<Entry<QName, Let>> iter = this.lets.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<QName, Let> entry = iter.next();
				Element letElement = document.createElementNS(getTargetNamespaceURI(), "let");
				letElement.setAttribute("name", QNameUtils.getNameWithPrefix((QName) entry.getKey(),
						getSchema() == null ? null : getSchema().getUriPrefixMap()));
				letElement.setAttribute("value", ((Let) entry.getValue()).getValue().toString());
				letElement.setPrefix(getPrefix());
				element.appendChild(letElement);
			}

			for (Iterator<Inclusion> iter = getInclusions().iterator(); iter.hasNext();) {
				Inclusion inclusion = (Inclusion) iter.next();
				String href = inclusion.getHref();
				if ((href != null) && (!href.equals(""))) {
					Element elem = document.createElementNS(getTargetNamespaceURI(), "include");
					elem.setAttribute("href", inclusion.getHref());
					element.appendChild(elem);
				}
			}

			for (Iterator<Active> iterator = this.actives.iterator(); iterator.hasNext();) {
				Active active = (Active) iterator.next();
				XMLUtils.insertElement(element, active.toElement());
			}

			for (Iterator<P> iter = getParas().iterator(); iter.hasNext();) {
				P para = (P) iter.next();
				XMLUtils.insertElement(element, para.toElement());
			}

			return element;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
