package cloud.iot.ai.analytics.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

public class Rule extends ContextElement {
	private static final long serialVersionUID = 6156895700438295580L;
	private String flag = null;

	private String context = null;

	private Linkable linkable = null;

	private List<String> referencedRules = new ArrayList<String>();

	private List<Assertion> assertions = new ArrayList<Assertion>();

	private List<Report> reports = new ArrayList<Report>();

	private Pattern pattern = null;

	private boolean enableAll = true;

	public Rule(Pattern pattern, String id, boolean _abstract, String context) {
		setId(id);
		setAbstract(_abstract);
		setContext(context);
		setPattern(pattern);
		if (pattern != null)
			pattern.addRule(this);
	}

	public String getContext() {
		return this.context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getFlag() {
		return this.flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public List<Assertion> getAssertions() {
		return this.assertions;
	}

	public void addAssertion(Assertion assertion) {
		this.assertions.add(assertion);
	}

	public List<Report> getReports() {
		return this.reports;
	}

	public void addReport(Report report) {
		this.reports.add(report);
	}

	public Element toElement() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			Document document = builder.newDocument();
			Element element = document.createElementNS(getTargetNamespaceURI(), "rule");
			element.setPrefix(getPrefix());
			if (getId() != null)
				element.setAttribute("id", getId());
			if (getName() != null)
				element.setAttribute("name", getName());
			if (getContext() != null)
				element.setAttribute("context", getContext());
			if (getFlag() != null)
				element.setAttribute("flag", getFlag());
			if (getLinkable() != null) {
				if (getLinkable().getRole() != null)
					element.setAttribute("role", getLinkable().getRole());
				if (getLinkable().getSubject() != null)
					element.setAttribute("subject", getLinkable().getSubject());
			}
			printAttrs(element);

			for (Iterator<Entry<QName, Let>> iter = this.lets.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<QName, Let> entry = iter.next();
				Element letElement = document.createElementNS(getTargetNamespaceURI(), "let");
				letElement.setAttribute("name",
						QNameUtils.getNameWithPrefix((QName) entry.getKey(), this.pattern.getSchema() == null ? null
								: this.pattern == null ? null : this.pattern.getSchema().getUriPrefixMap()));
				letElement.setAttribute("value", ((Let) entry.getValue()).getValue().toString());
				letElement.setPrefix(getPrefix());
				element.appendChild(letElement);
			}

			for (Iterator<Assertion> iterator = this.assertions.iterator(); iterator.hasNext();) {
				Assertion assertion = (Assertion) iterator.next();
				XMLUtils.insertElement(element, assertion.toElement());
			}

			for (Iterator<Report> iterator = this.reports.iterator(); iterator.hasNext();) {
				Report report = (Report) iterator.next();
				XMLUtils.insertElement(element, report.toElement());
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
			return element;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Pattern getPattern() {
		return this.pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public boolean isEnableAll() {
		return this.enableAll;
	}

	public void setEnableAll(boolean enableAll) {
		this.enableAll = enableAll;
	}

	public Schema getRoot() {
		return getPattern().getSchema();
	}

	public List<String> getReferencedRules() {
		return this.referencedRules;
	}

	public void addReferencedRule(String referencedRules) {
		this.referencedRules.add(referencedRules);
	}

	public Map<QName, Let> getNeedPopulatedLets() {
		Map<QName, Let> nonPopulatedLets = new LinkedHashMap<QName, Let>();
		for (Iterator<Entry<QName, Let>> iter = this.lets.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<QName, Let> entry = iter.next();
			if (!((Let) entry.getValue()).isConstant()) {
				nonPopulatedLets.put((QName) entry.getKey(), (Let) entry.getValue());
			}

		}

		return nonPopulatedLets;
	}

	public void appendRule(Rule rule) {
		if (rule != null) {
			this.assertions.addAll(rule.getAssertions());
			this.reports.addAll(rule.getReports());
			Map<QName, Let> newLets = rule.getLets();
			newLets.putAll(this.lets);
			this.lets = newLets;
		}
	}

	public Linkable getLinkable() {
		return this.linkable;
	}

	public void setLinkable(Linkable linkable) {
		this.linkable = linkable;
	}
}
