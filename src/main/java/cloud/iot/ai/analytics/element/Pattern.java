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

public class Pattern extends ContextElement {
	private static final long serialVersionUID = -2246292159992700962L;
	private String title = null;

	private String isA = null;

	private boolean hasPopulated = false;

	private List<Rule> rules = new ArrayList<Rule>();

	public Pattern(Schema schema, String id, boolean _abstract, String title) {
		setId(id);
		setAbstract(_abstract);
		setTitle(title);
		setSchema(schema);
		if (getSchema() != null)
			getSchema().addPattern(this);
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Rule> getRules() {
		return this.rules;
	}

	public void addRule(Rule rule) {
		this.rules.add(rule);
	}

	public Element toElement() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			Document document = builder.newDocument();
			Element element = document.createElementNS(getTargetNamespaceURI(), "pattern");
			element.setPrefix(getPrefix());
			if (getId() != null)
				element.setAttribute("id", getId());
			if (getName() != null)
				element.setAttribute("name", getName());
			if (getTitle() != null)
				element.setAttribute("title", getTitle());
			printAttrs(element);

			for (Iterator<Entry<QName, Let>> iter = this.lets.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<QName, Let> entry = (Map.Entry<QName, Let>) iter.next();
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

			for (Iterator<Rule> iterator = this.rules.iterator(); iterator.hasNext();) {
				Rule rule = (Rule) iterator.next();
				XMLUtils.insertElement(element, rule.toElement());
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

	public Schema getRoot() {
		return getSchema();
	}

	public String getIsA() {
		return this.isA;
	}

	public void setIsA(String isA) {
		this.isA = isA;
	}

	public Pattern getConcretePattern() {
		Pattern result = this;
		if (!isHasPopulated()) {
			Pattern referedPattern = getRoot().getPattern(getIsA());
			if (referedPattern != null) {
				getInclusions().addAll(referedPattern.getInclusions());
				getRules().addAll(referedPattern.getRules());
				Map<QName, Let> newLets = referedPattern.getLets();
				newLets.putAll(result.getLets());
				setLets(newLets);
			}
			result.setHasPopulated(true);
		}
		return this;
	}

	public boolean isHasPopulated() {
		return this.hasPopulated;
	}

	public void setHasPopulated(boolean hasPopulated) {
		this.hasPopulated = hasPopulated;
	}
}
