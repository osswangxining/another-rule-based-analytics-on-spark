package cloud.iot.ai.analytics.element;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cloud.iot.ai.analytics.util.QNameUtils;
import cloud.iot.ai.analytics.util.StringUtils;
import cloud.iot.ai.analytics.util.URLUtils;
import cloud.iot.ai.analytics.util.XMLUtils;
import cloud.iot.ai.analytics.util.XPathUtils;

public class SchematronReader {
	private static Logger logger = Logger.getLogger(SchematronReader.class.getName());

	public Schema readSchematron(String schURI) throws IOException {
		return readSchematron(null, schURI);
	}

	public Schema readSchematron(String contextURI, String schURI) throws IOException {
		URL url = URLUtils.getURL(null, schURI);
		if (contextURI != null) {
			URL contextURL = URLUtils.getURL(null, contextURI);
			url = URLUtils.getURL(contextURL, schURI);
		}
		if (url != null) {
			InputStream input = url.openStream();
			return readSchematron(url.toString(), input);
		}
		return null;
	}

	public Schema readSchematron(String documentBaseURI, InputStream input) throws IOException {
		Document doc = XMLUtils.read(input);
		return readSchematron(documentBaseURI, doc);
	}

	public Schema readSchematron(String documentBaseURI, Document doc) throws IOException {
		if (doc == null)
			return null;
		Element element = doc.getDocumentElement();
		return readSchematron(documentBaseURI, element);
	}

	public Schema readSchematron(String documentBaseURI, Element element) throws IOException {
		return newSchema(documentBaseURI, element);
	}

	private Schema newSchema(String documentBaseURI, Element element) throws IOException {
		String METHOD_NAME = "newSchema";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SchematronReader", METHOD_NAME);
		}
		Schema schema = null;
		if (element != null) {
			String localName = element.getLocalName();
			String prefix = element.getPrefix();
			String namespaceURI = element.getNamespaceURI();

			if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(namespaceURI)) {
				if ("schema".equals(localName)) {
					schema = new Schema(namespaceURI, prefix == null ? "sch" : prefix);
					schema.setDocumentBaseURI(documentBaseURI);
					String id = XMLUtils.getAttribute(element, "id");
					String name = XMLUtils.getAttribute(element, "name");
					String defaultPhase = XMLUtils.getAttribute(element, "defaultPhase");
					schema.setId(id);
					schema.setName(name);
					schema.setDefaultPhase(defaultPhase);

					schema.setRich(element);

					List<Element> childElements = XMLUtils.getChildElements(element);
					for (Iterator<Element> iter = childElements.iterator(); iter.hasNext();) {
						Element elem = (Element) iter.next();

						if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(elem.getNamespaceURI())) {
							if ("title".equals(elem.getLocalName())) {
								schema.setTitle(elem.getTextContent());
								continue;
							}
						}
						if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(elem.getNamespaceURI())) {
							if ("ns".equals(elem.getLocalName())) {
								schema.addPrefixUri(XMLUtils.getAttribute(elem, "prefix"),
										XMLUtils.getAttribute(elem, "uri"));

								continue;
							}

						}

						if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(elem.getNamespaceURI())) {
							if ("pattern".equals(elem.getLocalName())) {
								newPattern(documentBaseURI, schema, elem);
								continue;
							}
						}
						if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(elem.getNamespaceURI())) {
							if ("let".equals(elem.getLocalName())) {
								newLet(documentBaseURI, schema, elem, schema);
								continue;
							}
						}
						if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(elem.getNamespaceURI())) {
							if ("include".equals(elem.getLocalName())) {
								newInclusion(documentBaseURI, schema, elem);
								continue;
							}
						}
						if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(elem.getNamespaceURI())) {
							if ("phase".equals(elem.getLocalName())) {
								newPhase(documentBaseURI, schema, elem);
								continue;
							}
						}
						if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(elem.getNamespaceURI())) {
							if ("diagnostics".equals(elem.getLocalName())) {
								newDiagnostics(documentBaseURI, schema, elem);
								continue;
							}
						}
						if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(elem.getNamespaceURI())) {
							if ("p".equals(elem.getLocalName())) {
								newP(documentBaseURI, schema, elem);
							}
						}
					}
				}
			}
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("SchematronReader", METHOD_NAME);
		}
		return schema;
	}

	private void newPhase(String documentBaseURI, Schema schema, Element elem) throws IOException {
		String METHOD_NAME = "newPhase";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SchematronReader", METHOD_NAME);
		}

		String id = XMLUtils.getAttribute(elem, "id");
		Phase phase = new Phase(id);
		schema.addPhase(phase);
		phase.setRich(elem);
		List<Element> childElements = XMLUtils.getChildElements(elem);
		for (Iterator<Element> iter = childElements.iterator(); iter.hasNext();) {
			Element childElem = (Element) iter.next();
			if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(childElem.getNamespaceURI())) {
				if ("let".equals(childElem.getLocalName())) {
					newLet(documentBaseURI, phase, childElem, schema);
					continue;
				}
			}
			if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(childElem.getNamespaceURI())) {
				if ("active".equals(childElem.getLocalName())) {
					String patternId = XMLUtils.getAttribute(childElem, "pattern");
					Active active = new Active(patternId);
					phase.addActive(active);
					continue;
				}
			}
			if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(childElem.getNamespaceURI())) {
				if ("include".equals(childElem.getLocalName())) {
					newInclusion(documentBaseURI, phase, childElem);
					continue;
				}
			}
			if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(childElem.getNamespaceURI())) {
				if ("p".equals(childElem.getLocalName())) {
					newP(documentBaseURI, phase, childElem);
				}
			}
		}
		if (logger.isLoggable(Level.FINER))
			logger.exiting("SchematronReader", METHOD_NAME);
	}

	private Pattern newPattern(String documentBaseURI, Schema schema, Element element) throws IOException {
		String METHOD_NAME = "newPattern";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SchematronReader", METHOD_NAME);
		}
		Pattern pattern = null;

		String id = XMLUtils.getAttribute(element, "id");
		String name = XMLUtils.getAttribute(element, "name");
		String _abstract = XMLUtils.getAttribute(element, "abstract");
		String title = XMLUtils.getAttribute(element, "title");
		String isA = XMLUtils.getAttribute(element, "is-a");
		pattern = new Pattern(schema, id, (_abstract != null) && (_abstract.toLowerCase().equals("true")), title);
		pattern.setName(name);
		pattern.setIsA(isA);
		pattern.setRich(element);
		List<Element> childElements = XMLUtils.getChildElements(element);
		for (Iterator<Element> iter = childElements.iterator(); iter.hasNext();) {
			Element childElem = (Element) iter.next();
			if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(childElem.getNamespaceURI())) {
				if ("let".equals(childElem.getLocalName())) {
					newLet(documentBaseURI, pattern, childElem, schema);
					continue;
				}
			}
			if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(childElem.getNamespaceURI())) {
				if ("rule".equals(childElem.getLocalName())) {
					newRule(documentBaseURI, pattern, childElem);
					continue;
				}
			}
			if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(childElem.getNamespaceURI())) {
				if ("include".equals(childElem.getLocalName())) {
					newInclusion(documentBaseURI, pattern, childElem);
					continue;
				}
			}
			if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(childElem.getNamespaceURI())) {
				if ("param".equals(childElem.getLocalName())) {
					newLet(documentBaseURI, pattern, childElem, schema);
					continue;
				}
			}
			if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(childElem.getNamespaceURI())) {
				if ("p".equals(childElem.getLocalName())) {
					newP(documentBaseURI, pattern, childElem);
				}
			}
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("SchematronReader", METHOD_NAME);
		}
		return pattern;
	}

	private Rule newRule(String documentBaseURI, Pattern pattern, Element element) throws IOException {
		String METHOD_NAME = "newRule";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SchematronReader", METHOD_NAME);
		}
		Rule rule = null;

		String id = XMLUtils.getAttribute(element, "id");
		String name = XMLUtils.getAttribute(element, "name");
		String _abstract = XMLUtils.getAttribute(element, "abstract");
		String context = XMLUtils.getAttribute(element, "context");
		String role = XMLUtils.getAttribute(element, "role");
		String subject = XMLUtils.getAttribute(element, "subject");
		String flag = XMLUtils.getAttribute(element, "flag");
		Linkable linkable = new Linkable(role, subject);
		boolean isAbstract = (_abstract != null) && (_abstract.toLowerCase().equals("true"));

		if ((isAbstract) || (context != null)) {
			rule = new Rule(pattern, id, isAbstract, context);
			rule.setName(name);
			rule.setLinkable(linkable);
			rule.setRich(element);
			rule.setFlag(flag);

			List<Element> childElements = XMLUtils.getChildElements(element);
			for (Iterator<Element> iter = childElements.iterator(); iter.hasNext();) {
				Element childElem = (Element) iter.next();

				if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(childElem.getNamespaceURI())) {
					if (("assert".equals(childElem.getLocalName())) || ("report".equals(childElem.getLocalName()))) {
						newAssertionOrReportElement(documentBaseURI, rule, childElem);

						continue;
					}
				}
				if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(childElem.getNamespaceURI())) {
					if ("let".equals(childElem.getLocalName())) {
						newLet(documentBaseURI, rule, childElem, pattern.getSchema());

						continue;
					}
				}
				if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(childElem.getNamespaceURI())) {
					if ("include".equals(childElem.getLocalName())) {
						newInclusion(documentBaseURI, rule, childElem);
						continue;
					}
				}
				if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(childElem.getNamespaceURI())) {
					if ("extends".equals(childElem.getLocalName())) {
						String ruleId = XMLUtils.getAttribute(childElem, "rule");
						if (ruleId != null)
							rule.addReferencedRule(ruleId);
					}
				}
			}
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("SchematronReader", METHOD_NAME);
		}

		return rule;
	}

	private AssertionOrReportElement newAssertionOrReportElement(String documentBaseURI, Rule rule, Element element) {
		String METHOD_NAME = "newAssertionOrReportElement";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SchematronReader", METHOD_NAME);
		}
		AssertionOrReportElement result = null;
		if (element != null) {
			if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(element.getNamespaceURI())) {
				if ("assert".equals(element.getLocalName())) {
					String test = XMLUtils.getAttribute(element, "test");
					if (test != null) {
						result = new Assertion(test);
						rule.addAssertion((Assertion) result);
					}
				} else if ("report".equals(element.getLocalName())) {
					String test = XMLUtils.getAttribute(element, "test");
					if (test != null) {
						result = new Report(test);
						rule.addReport((Report) result);
					}
				}
			}

			if (result != null) {
				String id = XMLUtils.getAttribute(element, "id");
				String diagnostics = XMLUtils.getAttribute(element, "diagnostics");
				String role = XMLUtils.getAttribute(element, "role");
				String subject = XMLUtils.getAttribute(element, "subject");
				String flag = XMLUtils.getAttribute(element, "flag");
				Linkable linkable = new Linkable(role, subject);
				result.setId(id);
				result.setLinkable(linkable);
				result.setRich(element);
				result.setFlag(flag);
				if (diagnostics != null) {
					result.setDiagnostics(diagnostics);
					result.setDiagnosticIdList(StringUtils.parseNMTokens(diagnostics));
				}
				NodeList childNodes = element.getChildNodes();
				if (childNodes != null) {
					for (int i = 0; i < childNodes.getLength(); i++) {
						Node child = childNodes.item(i);
						TextElement outputElem = getOutputElement(child);
						if (outputElem != null) {
							result.addContent(outputElem);
						}
					}
				}
			}
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("SchematronReader", METHOD_NAME);
		}
		return result;
	}

	private Let newLet(String documentBaseURI, ContextElement parent, Element element, Schema schema) {
		Let result = null;
		String name = XMLUtils.getAttribute(element, "name");
		if (name == null)
			name = XMLUtils.getAttribute(element, "formal");
		String value = XMLUtils.getAttribute(element, "value");
		if (value == null)
			value = XMLUtils.getAttribute(element, "actual");
		Object _value = value;
		if ((name != null) && (value != null) && (schema != null) && (value.length() > 0) && (name.length() > 0)) {
			QName dataType = null;
			boolean constant = false;

			char c = value.trim().charAt(0);
			int index1 = value.indexOf("'");
			int index2 = value.indexOf("'", index1 + 1);
			if ((c >= '0') && (c <= '9')) {
				_value = new Double(0.0D);
				try {
					_value = Double.valueOf(value);
				} catch (NumberFormatException localNumberFormatException) {
				}
				dataType = XPathConstants.NUMBER;
				constant = true;
			} else if ((c == '\'') && (index1 > -1) && (index2 > index1)) {
				_value = value.substring(index1 + 1, index2);
				dataType = XPathConstants.STRING;
				constant = true;
			}

			Map<String, String> prefixNSContext = schema.getPrefixUriMap();
			result = new Let(QNameUtils.getQName(name.trim(), prefixNSContext), _value, dataType, constant);
			parent.addLet(result);
		}
		return result;
	}

	private void newInclusion(String documentBaseURI, ContextElement parent, Element elem) throws IOException {
		String METHOD_NAME = "newInclusion";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SchematronReader", METHOD_NAME);
		}
		String href = XMLUtils.getAttribute(elem, "href");
		if (href != null) {
			Inclusion inclusion = new Inclusion(href);

			String documentUri = href;
			String fragmentId = null;
			int index = href.indexOf("#");
			if (index > -1) {
				documentUri = href.substring(0, href.indexOf("#"));

				fragmentId = href.substring(href.lastIndexOf("#") + 1);
			}
			try {
				URL url = URLUtils.getURL(null, documentUri);
				if (documentBaseURI != null) {
					URL contextURL = URLUtils.getURL(null, documentBaseURI);
					url = URLUtils.getURL(contextURL, documentUri);
				}
				Document doc = XMLUtils.read(url.openStream());
				if (doc == null)
					return;
				Element rootElem = doc.getDocumentElement();
				String prefix = XMLUtils.getPrefixFromNamespaceURI(doc, parent.getTargetNamespaceURI());
				if ((prefix == null) || (prefix.equals("")))
					prefix = "";
				else
					prefix = prefix + ":";
				XPath xpath = XPathFactory.newInstance().newXPath();
				xpath.setNamespaceContext(new nsContext(parent.getTargetNamespaceURI()));
				Node node = null;
				if ((fragmentId == null) || (fragmentId.trim().equals(""))) {
					node = (Node) XPathUtils.getValue(xpath, rootElem, "//*[local-name(.)='schema']",
							XPathConstants.NODE);
					if ((node != null) && (node.getNodeType() == 1)) {
						Schema includedSchema = newSchema(url.toString(), (Element) node);
						inclusion.setValue(includedSchema);
						parent.getRoot().append(includedSchema);
					}
				} else {
					String xpathExpr = "//*[@id='" + fragmentId + "']";
					if ((parent instanceof Schema))
						xpathExpr = "//*[@id='" + fragmentId + "' and local-name(.)='" + "pattern" + "']";
					else if ((parent instanceof Pattern))
						xpathExpr = "//*[@id='" + fragmentId + "' and local-name(.)='" + "rule" + "']";
					else if ((parent instanceof Rule)) {
						xpathExpr = "//*[@id='" + fragmentId + "' and (local-name(.)='" + "assert"
								+ "' or local-name(.)='" + "report" + "']";
					}
					node = (Node) XPathUtils.getValue(xpath, rootElem, xpathExpr, XPathConstants.NODE);
					if ((node != null) && (node.getNodeType() == 1)) {
						SchematronElement includedElem = newSchematronElement(url.toString(), parent, (Element) node);
						inclusion.setValue(includedElem);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}

		}

		if (logger.isLoggable(Level.FINER))
			logger.exiting("SchematronReader", METHOD_NAME);
	}

	private SchematronElement newSchematronElement(String documentBaseURI, SchematronElement parent, Element element)
			throws IOException {
		if ((parent == null) || (element == null))
			return null;
		if ((parent instanceof Schema))
			return newPattern(documentBaseURI, (Schema) parent, element);
		if ((parent instanceof Pattern))
			return newRule(documentBaseURI, (Pattern) parent, element);
		if ((parent instanceof Rule)) {
			return newAssertionOrReportElement(documentBaseURI, (Rule) parent, element);
		}

		return null;
	}

	private Diagnostics newDiagnostics(String documentBaseURI, Schema schema, Element element) throws IOException {
		Diagnostics diagnostics = new Diagnostics();
		List<Element> childElements = XMLUtils.getChildElements(element);
		for (Iterator<Element> iter = childElements.iterator(); iter.hasNext();) {
			Element childElem = (Element) iter.next();
			if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(childElem.getNamespaceURI())) {
				if ("diagnostic".equals(childElem.getLocalName())) {
					String id = XMLUtils.getAttribute(childElem, "id");

					Diagnostic diagnostic = new Diagnostic();
					diagnostic.setId(id);
					diagnostic.setRich(childElem);
					NodeList childNodes = childElem.getChildNodes();
					if (childNodes != null) {
						for (int i = 0; i < childNodes.getLength(); i++) {
							Node child = childNodes.item(i);
							TextElement outputElem = getOutputElement(child);
							if (outputElem != null) {
								diagnostic.addContent(outputElem);
							}
						}
					}
					if (id != null)
						diagnostics.addDiagnostic(id, diagnostic);
				}
			}
		}
		schema.setDiagnostics(diagnostics);
		return diagnostics;
	}

	private P newP(String documentBaseURI, ContextElement ctxElem, Element element) throws IOException {
		P para = new P();

		String id = XMLUtils.getAttribute(element, "id");
		String clsValue = XMLUtils.getAttribute(element, "class");
		String icon = XMLUtils.getAttribute(element, "icon");
		para.setId(id);
		para.setClsValue(clsValue);
		para.setIcon(icon);
		NodeList childNodes = element.getChildNodes();
		if (childNodes != null) {
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node child = childNodes.item(i);
				TextElement outputElem = getOutputElement(child);
				if (outputElem != null)
					para.addContent(outputElem);
			}
		}
		ctxElem.addPara(para);
		return para;
	}

	private TextElement getOutputElement(Node child) {
		if (child.getNodeType() == 1) {
			Element elem = (Element) child;
			if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(child.getNamespaceURI())) {
				if ("value-of".equals(child.getLocalName())) {
					String select = XMLUtils.getAttribute(elem, "select");
					select = select == null ? "." : select;
					return new ValueOfElement(select);
				}

				if ("name".equals(child.getLocalName())) {
					String path = XMLUtils.getAttribute(elem, "path");
					path = "name(" + (path == null ? "." : path) + ")";
					return new ValueOfElement(path);
				}
				if ("emph".equals(child.getLocalName())) {
					return new Emph(elem);
				}
				if ("dir".equals(child.getLocalName())) {
					Dir dir = new Dir(elem);
					String value = XMLUtils.getAttribute(elem, "value");
					dir.setValue(value);
					return dir;
				}
				if ("span".equals(child.getLocalName())) {
					Span span = new Span(elem);
					String clsValue = XMLUtils.getAttribute(elem, "class");
					span.setClsValue(clsValue);
					return span;
				}
			}
		}

		return new TextElement(child);
	}

	class nsContext implements NamespaceContext {
		private String ns = null;

		nsContext(String ns) {
			this.ns = ns;
		}

		public String getNamespaceURI(String prefix) {
			return this.ns;
		}

		public String getPrefix(String namespaceURI) {
			return null;
		}

		public Iterator<String> getPrefixes(String namespaceURI) {
			return null;
		}
	}
}
