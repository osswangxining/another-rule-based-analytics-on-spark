package cloud.iot.ai.analytics.function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cloud.iot.ai.analytics.xml.XPathUtils;


public class CrossCalculateXPathFunction implements XPathFunction {
	

	private static Logger logger = Logger.getLogger(CrossCalculateXPathFunction.class.getName());


	private Element elem = null;

	public CrossCalculateXPathFunction(Element elem) {
		this.elem = elem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.xpath.XPathFunction#evaluate(java.util.List)
	 */
	public Object evaluate(List args) throws XPathFunctionException {
		Object result = null;
		if (args != null && args.size() > 0) {
			Object arg = args.get(0);
			String xpathExpr = null;
			if (arg instanceof NodeList) {
				if (((NodeList) arg).getLength() > 0) {
					xpathExpr = ((NodeList) arg).item(0).getNodeValue();
				}

			} else if (arg instanceof String) {
				xpathExpr = (String) arg;
			}
			if (xpathExpr == null)
				return null;

//			System.out.println(xpathExpr);
			List<String> expressions = getExpressions(xpathExpr);
			for (Iterator iterator = expressions.iterator(); iterator.hasNext();) {
				String expr = (String) iterator.next();
				XPath xpath = XPathFactory.newInstance().newXPath();
				try {
					if (elem != null) {
						NodeList nodeList = (NodeList) XPathUtils.getValue(xpath,
								elem, XPathUtils.formatXPathExpr(expr),
								XPathConstants.NODESET);
						if (nodeList == null || nodeList.getLength() == 0){
							result = null;
							break;
						}
						else
							result = nodeList;

					}
				} catch (XPathExpressionException e) {
					e.printStackTrace();
				}
			}			
		}
		return result;
	}

	public List<String> getExpressions(String str) {
		List<String> result = new ArrayList<String>();
		if (str == null)
			return result;

		String[] temp = str.split("\\{");
		if (temp != null && temp.length > 0) {
			for (int i = 0; i < temp.length; i++) {
				if (temp[i].indexOf("}") > 0) {
					// System.out.println(temp[i]);
					result.add(temp[i].substring(0, temp[i].indexOf("}")));
				}
			}
		}
		return result;
	}

}
