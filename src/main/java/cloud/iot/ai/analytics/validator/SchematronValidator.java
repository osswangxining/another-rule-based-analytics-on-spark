package cloud.iot.ai.analytics.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunctionResolver;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cloud.iot.ai.analytics.element.Active;
import cloud.iot.ai.analytics.element.Assertion;
import cloud.iot.ai.analytics.element.AssertionOrReportElement;
import cloud.iot.ai.analytics.element.ContextElement;
import cloud.iot.ai.analytics.element.Diagnostic;
import cloud.iot.ai.analytics.element.Diagnostics;
import cloud.iot.ai.analytics.element.Let;
import cloud.iot.ai.analytics.element.Pattern;
import cloud.iot.ai.analytics.element.Phase;
import cloud.iot.ai.analytics.element.Report;
import cloud.iot.ai.analytics.element.Rule;
import cloud.iot.ai.analytics.element.Schema;
import cloud.iot.ai.analytics.element.TextElement;
import cloud.iot.ai.analytics.element.ValueOfElement;
import cloud.iot.ai.analytics.function.FunctionContext;
import cloud.iot.ai.analytics.output.Formatter;
import cloud.iot.ai.analytics.output.TextOutputFormatter;
import cloud.iot.ai.analytics.util.XPathUtils;

public class SchematronValidator {
	private static Logger logger = Logger.getLogger(SchematronValidator.class.getName());
	private static Map<ContextElement, XPath> xpathInstaceMap = new HashMap<ContextElement, XPath>();

	private SchematronNamespaceContext nsContext = null;

	private Formatter formatter = null;

	private XPathFunctionResolver functionResolver = null;

	private List<String> outputs = new ArrayList<String>();

	private List<Element> firedRules = new ArrayList<Element>();

	private List<Element> failedAssertions = new ArrayList<Element>();

	private List<Element> successfulReports = new ArrayList<Element>();

	public SchematronValidator() {
		this(new TextOutputFormatter());
	}

	public SchematronValidator(Formatter formatter) {
		setFormatter(formatter);
	}

	public void validate(Document doc, Schema schema) throws XPathExpressionException {
		if (doc != null)
			execute(doc.getDocumentElement(), schema);
	}

	public void validate(Element element, Schema schema) throws XPathExpressionException {
		execute(element, schema);
	}

	private void execute(Object context, Schema schema) throws XPathExpressionException {
		String METHOD_NAME = "execute";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SchematronValidator", METHOD_NAME);
		}

		if ((context == null) || (schema == null)) {
			return;
		}
		this.nsContext = new SchematronNamespaceContext(schema);

		populateLet(schema, context);

		String defaultPhase = schema.getDefaultPhase();
		if ("#ALL".equals(defaultPhase)) {
			List<Pattern> patterns = schema.getPatterns();
			for (Iterator<Pattern> iter = patterns.iterator(); iter.hasNext();) {
				Pattern pattern = (Pattern) iter.next();
				execute(context, pattern, schema);
			}
		} else {
			List<Phase> phases = schema.getPhases();
			Set<String> activePatternIds = new HashSet<String>();
			for (Iterator<Phase> iter = phases.iterator(); iter.hasNext();) {
				Phase phase = (Phase) iter.next();
				if (defaultPhase.equals(phase.getId())) {
					List<Active> actives = phase.getActives();
					for (Iterator<Active> iterator = actives.iterator(); iterator.hasNext();) {
						Active active = (Active) iterator.next();
						activePatternIds.add(active.getPattern());
					}
				}
			}
			List<Pattern> patterns = schema.getPatterns();
			for (Iterator<Pattern> iter = patterns.iterator(); iter.hasNext();) {
				Pattern pattern = (Pattern) iter.next();
				if (activePatternIds.contains(pattern.getId())) {
					execute(context, pattern, schema);
				}
			}
		}

		if (logger.isLoggable(Level.FINER))
			logger.exiting("SchematronValidator", METHOD_NAME);
	}

	private void execute(Object context, Pattern pattern, Schema schema) throws XPathExpressionException {
		if (!pattern.isAbstract()) {
			Pattern concretePattern = pattern.getConcretePattern();
			populateLet(concretePattern, context);

			List<Rule> rules = concretePattern.getRules();
			for (Iterator<Rule> iterator = rules.iterator(); iterator.hasNext();) {
				Rule rule = (Rule) iterator.next();
				execute(context, rule, schema);
			}
		}
	}

	private void execute(Object context, Rule rule, Schema schema) throws XPathExpressionException {
		String METHOD_NAME = "execute";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SchematronValidator", METHOD_NAME);
		}
		if (!rule.isAbstract()) {
			String ruleContextXPathExpr = rule.getContext();

			XPath patternXPath = getXPath(rule.getPattern());
			NodeList ruleCtx = (NodeList) XPathUtils.getValue(patternXPath, context, ruleContextXPathExpr,
					XPathConstants.NODESET);
			int len = ruleCtx.getLength();
			if ((ruleCtx != null) && (len > 0)) {
				if (rule.isEnableAll()) {
					for (int i = 0; i < len; i++) {
						fire(rule, ruleCtx.item(i), schema);
					}
				} else {
					fire(rule, ruleCtx, schema);
				}
			}

		}

		if (logger.isLoggable(Level.FINER))
			logger.exiting("SchematronValidator", METHOD_NAME);
	}

	private void fire(Rule rule, Object ruleCtx, Schema schema) throws XPathExpressionException {
		List<String> referencedRules = rule.getReferencedRules();
		for (Iterator<String> iter = referencedRules.iterator(); iter.hasNext();) {
			String ruleId = (String) iter.next();
			Rule referedRule = schema.getRule(ruleId);
			rule.appendRule(referedRule);
		}

		populateLet(rule, ruleCtx);

		List<Report> reports = rule.getReports();
		XPath ruleXPath = getXPath(rule);
		for (Iterator<Report> iter = reports.iterator(); iter.hasNext();) {
			Report report = (Report) iter.next();
			execute(report, ruleXPath, ruleCtx, schema);
		}
		List<Assertion> assertions = rule.getAssertions();
		for (Iterator<Assertion> iter = assertions.iterator(); iter.hasNext();) {
			Assertion assertion = (Assertion) iter.next();
			execute(assertion, ruleXPath, ruleCtx, schema);
		}
	}

	private void execute(AssertionOrReportElement elem, XPath ruleXPath, Object ruleCtx, Schema schema)
			throws XPathExpressionException {
		String METHOD_NAME = "execute";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SchematronValidator", METHOD_NAME);
		}
		String conditionXPathExpr = elem.getTest();

		Object b = XPathUtils.getValue(ruleXPath, ruleCtx, conditionXPathExpr, XPathConstants.BOOLEAN);
		boolean test = (b != null) && (Boolean.parseBoolean(b.toString()));

		boolean enabledReport = (elem instanceof Report) & test;
		boolean enabledAssertion =  (elem instanceof Assertion) & !test;
		if (enabledReport || enabledAssertion) {
			List<TextElement> contents = elem.getContent();
			String output = generateOutput(ruleXPath, ruleCtx, contents, this.nsContext);
			this.outputs.add(output);
			getFormatter().format(output);

			List<String> diagnosticIds = elem.getDiagnosticIdList();
			for (Iterator<String> iter = diagnosticIds.iterator(); iter.hasNext();) {
				String diagId = (String) iter.next();
				Diagnostics diagnostics = schema.getDiagnostics();
				if (diagnostics != null) {
					Diagnostic diagnostic = (Diagnostic) diagnostics.getDiagnostics().get(diagId);
					List<TextElement> diagContents = diagnostic.getContent();
					String diagOutput = generateOutput(ruleXPath, ruleCtx, diagContents, this.nsContext);
					this.outputs.add(diagOutput);
					getFormatter().format(diagOutput);
				}
			}
			Node node = null;
			if ((ruleCtx instanceof Node)) {
				node = (Node) ruleCtx;
			}
			String location = XPathUtils.getXPath(node);
			elem.setLocation(location);
			if ((elem instanceof Assertion))
				addFailedAssertions(elem.toElement());
			else if ((elem instanceof Report)) {
				addSuccessfulReports(elem.toElement());
			}
		}

		if (logger.isLoggable(Level.FINER))
			logger.exiting("SchematronValidator", METHOD_NAME);
	}

	private String generateOutput(XPath xpath, Object context, List<TextElement> contents,
			SchematronNamespaceContext nsContext) throws XPathExpressionException {
		String METHOD_NAME = "generateOutput";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SchematronValidator", METHOD_NAME);
		}
		StringBuffer sb = new StringBuffer();
		if ((contents != null) && (contents.size() > 0)) {
			for (Iterator<TextElement> iter = contents.iterator(); iter.hasNext();) {
				TextElement textElem = (TextElement) iter.next();
				if ((textElem instanceof ValueOfElement)) {
					Object newContent = XPathUtils.getValue(xpath, context, ((ValueOfElement) textElem).getSelect(),
							XPathConstants.STRING);
					if (newContent != null)
						sb.append(newContent);
				} else {
					sb.append(textElem.toString()).append(" ");
				}
			}
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("SchematronValidator", METHOD_NAME);
		}
		return sb.toString();
	}

	private void populateLet(ContextElement ctxElem, Object context) {
		XPath ctxElemXPath = getXPath(ctxElem);
		Map<QName, Let> lets = ctxElem.getNeedPopulatedLets();
		for (Iterator<Let> iter = lets.values().iterator(); iter.hasNext();) {
			Let let = (Let) iter.next();
			try {
				NodeList objCtx = (NodeList) XPathUtils.getValue(ctxElemXPath, context, let.getValue().toString(),
						XPathConstants.NODESET);
				let.setDataType(XPathConstants.NODESET);
				let.setNewValue(objCtx);
			} catch (XPathExpressionException e) {
				try {
					Object objCtx = XPathUtils.getValue(ctxElemXPath, context, let.getValue().toString(),
							XPathConstants.STRING);
					let.setDataType(XPathConstants.STRING);
					let.setNewValue(objCtx);
				} catch (XPathExpressionException localXPathExpressionException1) {
				}
			}
		}
	}

	private XPath getXPath(ContextElement ctxElem) {
		XPath xpathInstace = (XPath) xpathInstaceMap.get(ctxElem);
		if (xpathInstace == null) {
			xpathInstace = XPathFactory.newInstance().newXPath();
			xpathInstace.setNamespaceContext(this.nsContext);
			xpathInstace.setXPathVariableResolver(new XPathVariableContext(ctxElem));
			FunctionContext funcContext = new FunctionContext(new XPathFunctionResolver[] { getFunctionResolver() });
			xpathInstace.setXPathFunctionResolver(funcContext);
			xpathInstaceMap.put(ctxElem, xpathInstace);
		}
		return xpathInstace;
	}

	public Formatter getFormatter() {
		if (this.formatter == null)
			this.formatter = new TextOutputFormatter();
		return this.formatter;
	}

	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	public XPathFunctionResolver getFunctionResolver() {
		return this.functionResolver;
	}

	public void setFunctionResolver(XPathFunctionResolver functionResolver) {
		this.functionResolver = functionResolver;
	}

	public SchematronNamespaceContext getNsContext() {
		return this.nsContext;
	}

	public List<String> getOutputs() {
		return this.outputs;
	}

	public List<Element> getFailedAssertions() {
		return this.failedAssertions;
	}

	public List<Element> getFiredRules() {
		return this.firedRules;
	}

	public List<Element> getSuccessfulReports() {
		return this.successfulReports;
	}

	public void addFailedAssertions(Element failedAssertion) {
		this.failedAssertions.add(failedAssertion);
	}

	public void addFiredRule(Element firedRule) {
		this.firedRules.add(firedRule);
	}

	public void addSuccessfulReports(Element successfulReport) {
		this.successfulReports.add(successfulReport);
	}
}
