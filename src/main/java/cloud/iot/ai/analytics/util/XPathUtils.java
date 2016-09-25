package cloud.iot.ai.analytics.util;

import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XPathUtils {

	public static Object getValue(XPath xpath, Object context, String xpathExpression, QName returnType)
			throws XPathExpressionException {
		Object result = null;
		XPathExpression expr = xpath.compile(xpathExpression);
		if ((context instanceof Node)) {
			if (xpathExpression.equals("position()"))
				result = Integer.valueOf(XMLUtils.getPreviousTypedNodeSize((Node) context, (short) 1) + 1);
			else if (xpathExpression.equals("last()"))
				result = Integer.valueOf(XMLUtils.getPreviousTypedNodeSize((Node) context, (short) 1)
						+ XMLUtils.getNextTypedNodeSize((Node) context, (short) 1) + 1);
			else
				result = expr.evaluate(context, returnType);
		} else if ((context instanceof NodeList)) {
			int len = ((NodeList) context).getLength();

			if (len > 0)
				result = expr.evaluate(context, returnType);
		} else if ((context instanceof InputSource)) {
			result = expr.evaluate((InputSource) context, returnType);
		} else {
			result = expr.evaluate(context, returnType);
		}

		return result;
	}

	public static String formatXPathExpr(String expr) {
		String result = expr;
		if ((expr != null) && (!expr.startsWith("/")))
			result = "//" + expr;
		return result;
	}

	public static String getXPath(Node node) {
		if (node == null)
			return null;
		StringBuffer sb = new StringBuffer();
		Stack<Node> stack = new Stack<Node>();
		stack.push(node);
		Node parent = node.getParentNode();
		do {
			stack.push(parent);

			parent = parent.getParentNode();

			if (parent == null)
				break;
		} while (parent.getNodeType() != 9);

		while (!stack.isEmpty()) {
			Node n = (Node) stack.pop();
			if (n.getNodeType() == 1) {
				sb.append("/").append(n.getNodeName());
				int index = 1;
				Node previousSibling = n.getPreviousSibling();
				QName qname = QNameUtils.getQName(n);
				while (previousSibling != null) {
					if ((previousSibling.getNodeType() == n.getNodeType())
							&& (QNameUtils.matches(qname, previousSibling))) {
						index++;
					}
					previousSibling = previousSibling.getPreviousSibling();
				}
				sb.append("[" + index + "]");
			} else if (n.getNodeType() == 3) {
				sb.append("/text()");
			}
		}
		return sb.toString();
	}
}
