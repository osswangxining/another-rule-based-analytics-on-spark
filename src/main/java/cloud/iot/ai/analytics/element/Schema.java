package cloud.iot.ai.analytics.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cloud.iot.ai.analytics.util.QNameUtils;
import cloud.iot.ai.analytics.xml.DOM2Writer;
import cloud.iot.ai.analytics.xml.XMLUtils;

public class Schema extends ContextElement {
	private static final long serialVersionUID = -5618454545113936771L;
	private String schemaVersion = null;

	private String defaultPhase = "#ALL";

	private String queryBinding = null;

	private String title = null;

	private String documentBaseURI = null;

	private Map<String, String> prefixUriMap = new HashMap<String, String>();

	private Map<String, String> uriPrefixMap = new HashMap<String, String>();

	private List<Pattern> patterns = new ArrayList<Pattern>();

	private List<Phase> phases = new ArrayList<Phase>();

	private Diagnostics diagnostics = null;

	public Schema(String targetNamespace, String prefix) {
		setTargetNamespaceURI(targetNamespace);
		setPrefix(prefix);
	}

	public String getDefaultPhase() {
		return this.defaultPhase;
	}

	public void setDefaultPhase(String defaultPhase) {
		if (defaultPhase == null)
			defaultPhase = "#ALL";
		this.defaultPhase = defaultPhase;
	}

	public String getQueryBinding() {
		return this.queryBinding;
	}

	public void setQueryBinding(String queryBinding) {
		this.queryBinding = queryBinding;
	}

	public String getSchemaVersion() {
		return this.schemaVersion;
	}

	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, String> getPrefixUriMap() {
		return this.prefixUriMap;
	}

	public Map<String, String> getUriPrefixMap() {
		return this.uriPrefixMap;
	}

	public void addPrefixUri(String prefix, String uri) {
		if (!this.prefixUriMap.containsKey(prefix))
			this.prefixUriMap.put(prefix, uri);
		if (!this.uriPrefixMap.containsKey(uri))
			this.uriPrefixMap.put(uri, prefix);
	}

	public List<Pattern> getPatterns() {
		return this.patterns;
	}

	public void addPattern(Pattern pattern) {
		this.patterns.add(pattern);
	}

	public void addPatterns(List<Pattern> patterns) {
		this.patterns.addAll(patterns);
	}

	public void append(Schema includedSchema) {
		if (includedSchema != null) {
			addPatterns(includedSchema.getPatterns());
			addInclusions(includedSchema.getInclusions());
			addLets(includedSchema.getLets());
			Map<String, String> prefixUris = includedSchema.getPrefixUriMap();
			for (Iterator<Map.Entry<String, String>> iter = prefixUris.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<String, String> entry = iter.next();
				addPrefixUri((String) entry.getKey(), (String) entry.getValue());
			}
		}
	}

	public Element toElement() {
		Element schemaElement = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			Document document = builder.newDocument();

			schemaElement = document.createElementNS(getTargetNamespaceURI(), "schema");
			schemaElement.setPrefix(getPrefix());
			if (getId() != null)
				schemaElement.setAttribute("id", getId());
			if (getName() != null) {
				schemaElement.setAttribute("name", getName());
			}
			if(getDefaultPhase() != null) {
				schemaElement.setAttribute("defaultPhase", getDefaultPhase());
			}

			if (this.title != null) {
				Element titleElement = document.createElementNS(getTargetNamespaceURI(), "title");
				titleElement.setTextContent(this.title);
				titleElement.setPrefix(getPrefix());
				schemaElement.appendChild(titleElement);
			}
			printAttrs(schemaElement);

			for (Iterator<Map.Entry<String, String>> iter = this.uriPrefixMap.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<String, String> ns =  iter.next();
				if (ns.getKey() != null) {
					Element nsElement = document.createElementNS(getTargetNamespaceURI(), "ns");
					nsElement.setAttribute("uri", (String) ns.getKey());
					nsElement.setAttribute("prefix", (String) ns.getValue());
					nsElement.setPrefix(getPrefix());
					schemaElement.appendChild(nsElement);
				}
			}

			for (Iterator<Map.Entry<QName, Let>> iter = this.lets.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<QName, Let> entry = iter.next();
				Element letElement = document.createElementNS(getTargetNamespaceURI(), "let");
				letElement.setAttribute("name",
						QNameUtils.getNameWithPrefix((QName) entry.getKey(), this.uriPrefixMap));
				letElement.setAttribute("value", ((Let) entry.getValue()).getValue().toString());
				letElement.setPrefix(getPrefix());
				schemaElement.appendChild(letElement);
			}

			for (Iterator<Inclusion> iter = getInclusions().iterator(); iter.hasNext();) {
				Inclusion inclusion = (Inclusion) iter.next();
				String href = inclusion.getHref();
				if ((href != null) && (!href.equals(""))) {
					Element elem = document.createElementNS(getTargetNamespaceURI(), "include");
					elem.setAttribute("href", inclusion.getHref());
					schemaElement.appendChild(elem);
				}
			}

			for (Iterator<Pattern> iter = this.patterns.iterator(); iter.hasNext();) {
				Pattern pattern = (Pattern) iter.next();
				XMLUtils.insertElement(schemaElement, pattern.toElement());
			}

			if (this.diagnostics != null) {
				XMLUtils.insertElement(schemaElement, this.diagnostics.toElement());
			}

			for (Iterator<P> iter = getParas().iterator(); iter.hasNext();) {
				P para = (P) iter.next();
				XMLUtils.insertElement(schemaElement, para.toElement());
			}

			for (Iterator<Phase> iter = getPhases().iterator(); iter.hasNext();) {
				Phase phase = (Phase) iter.next();
				XMLUtils.insertElement(schemaElement, phase.toElement());
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return schemaElement;
	}

	public String toString() {
		String content = DOM2Writer.serializeAsString(toElement(), false, "UTF-8");
		return content;
	}

	public String getDocumentBaseURI() {
		return this.documentBaseURI;
	}

	public void setDocumentBaseURI(String documentBaseURI) {
		this.documentBaseURI = documentBaseURI;
	}

	public Schema getRoot() {
		return this;
	}

	public List<Phase> getPhases() {
		return this.phases;
	}

	public void addPhase(Phase phase) {
		this.phases.add(phase);
	}

	public Rule getRule(String ruleId) {
		if (ruleId == null)
			return null;
		Rule result = null;
		for (Iterator<Pattern> iter = this.patterns.iterator(); iter.hasNext();) {
			Pattern pattern = (Pattern) iter.next();
			List<Rule> rules = pattern.getRules();
			for (Iterator<Rule> iterator = rules.iterator(); iterator.hasNext();) {
				Rule rule = (Rule) iterator.next();
				if (ruleId.equals(rule.getId())) {
					result = rule;
					break;
				}
			}
		}
		return result;
	}

	public Pattern getPattern(String patternId) {
		if (patternId == null)
			return null;
		Pattern result = null;
		for (Iterator<Pattern> iter = this.patterns.iterator(); iter.hasNext();) {
			Pattern pattern = (Pattern) iter.next();
			if (patternId.equals(pattern.getId())) {
				result = pattern;
				break;
			}
		}
		return result;
	}

	public Diagnostics getDiagnostics() {
		return this.diagnostics;
	}

	public void setDiagnostics(Diagnostics diagnostics) {
		this.diagnostics = diagnostics;
	}
}
