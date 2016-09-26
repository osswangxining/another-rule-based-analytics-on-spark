package cloud.iot.ai.analytics.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xerces.xni.NamespaceContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class XMLUtils {
	private static Logger logger = Logger.getLogger(XMLUtils.class.getName());
	private static final String CLASS_NAME = "XMLUtils";
	public static final String NS_URI_XMLNS = "http://www.w3.org/2000/xmlns/";
	public static String NS_URI_XML = "http://www.w3.org/XML/1998/namespace";
	public static final String ATTR_XMLNS = "xmlns";
	public static final String TAG_XML = "xml";

	public static Element getFirstChildElement(Element element) {
		String METHOD_NAME = "getFirstChildElement";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		Element elem = null;
		if (element != null) {
			for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
				if (node.getNodeType() == 1) {
					elem = (Element) node;
				}
			}
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return elem;
	}

	public static Element getPreviousSiblingElement(Element element) {
		String METHOD_NAME = "getPreviousSiblingElement";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		Element elem = null;
		if (element != null) {
			for (Node node = element.getPreviousSibling(); node != null; node = node.getNextSibling()) {
				if (node.getNodeType() == 1) {
					elem = (Element) node;
				}
			}
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return elem;
	}

	public static Element getNextSiblingElement(Element element) {
		String METHOD_NAME = "getNextSiblingElement";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		Element elem = null;
		if (element != null) {
			for (Node node = element.getNextSibling(); node != null; node = node.getNextSibling()) {
				if (node.getNodeType() == 1) {
					elem = (Element) node;
				}
			}
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return elem;
	}

	public static String getPrefixFromNamespaceURI(Document context, String namespaceURI) {
		String METHOD_NAME = "getPrefixFromNamespaceURI";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		String prefix = null;
		if (context != null) {
			Element att_ele = prepareCondition(context);
			NamedNodeMap atts = att_ele.getAttributes();
			for (int i = 0; i < atts.getLength(); i++) {
				Attr attr = (Attr) atts.item(i);

				if (attr.getNodeValue().equals(namespaceURI)) {
					if ("xmlns".equals(attr.getLocalName()))
						prefix = "";
					else
						prefix = attr.getLocalName();
				}
				if (prefix != null) {
					break;
				}
			}
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return prefix;
	}

	private static Element prepareCondition(Document doc) {
		Element rootNode = doc.getDocumentElement();
		Element nsElement = doc.createElement("NameSpaceElement");
		getNameSpaceElement(nsElement, rootNode);
		return nsElement;
	}

	public static void getNameSpaceElement(Element nsElem, Element father) {
		Document doc = nsElem.getOwnerDocument();

		for (int i = 0; i < father.getAttributes().getLength(); i++) {
			Attr attr = (Attr) father.getAttributes().item(i);
			if (attr.getNamespaceURI() != null) {
				if (attr.getNamespaceURI().equals(NamespaceContext.XMLNS_URI)) {
					if (nsElem.getAttributeNodeNS(attr.getNamespaceURI(), attr.getLocalName()) == null)
						nsElem.getAttributes().setNamedItemNS(doc.importNode(attr, true));
				}
			}
		}
		List<Element> children = getChildElements(father);
		for (Iterator<Element> iter = children.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			getNameSpaceElement(nsElem, element);
		}
	}

	public static String getNamespaceURIFromPrefix(Node context, String prefix) {

		String METHOD_NAME = "getNamespaceURIFromPrefix";
		// log entry to the method
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(CLASS_NAME, METHOD_NAME);
		}
		String namespaceURI = null;
		if (context != null) {
			short nodeType = context.getNodeType();
			Node tempNode = null;

			switch (nodeType) {
			case Node.ATTRIBUTE_NODE: {
				tempNode = ((Attr) context).getOwnerElement();
				break;
			}
			case Node.ELEMENT_NODE: {
				tempNode = (Element) context;
				break;
			}
			default: {
				tempNode = context.getParentNode();
				break;
			}
			}

			while (tempNode != null && tempNode.getNodeType() == Node.ELEMENT_NODE) {
				Element tempEl = (Element) tempNode;
				namespaceURI = (prefix == null) ? getAttribute(tempEl, ATTR_XMLNS)
						: getAttributeNS(tempEl, NS_URI_XMLNS, prefix);

				if (namespaceURI == null)
					tempNode = tempEl.getParentNode();
				else
					break;
			}
		}
		// log exit from the method
		if (logger.isLoggable(Level.FINER)) {
			logger.exiting(CLASS_NAME, METHOD_NAME);
		}
		return namespaceURI;
	}

	public static boolean isEqualByNS(Element element1, Element element2) {
		String METHOD_NAME = "isEqualByNS";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		boolean isEqual = false;
		if ((element1 == null) && (element2 == null)) {
			isEqual = true;
		} else if ((element1 != null) && (element2 != null)) {
			String namespaceURI1 = element1.getNamespaceURI();
			String namespaceURI2 = element2.getNamespaceURI();
			if ((namespaceURI1 == null) && (namespaceURI2 == null))
				isEqual = true;
			else if ((namespaceURI1 != null) && (namespaceURI2 != null) && (namespaceURI1.equals(namespaceURI2))) {
				isEqual = true;
			}

		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return isEqual;
	}

	public static boolean isEqualByLocalName(Element element1, Element element2) {
		String METHOD_NAME = "isEqualByLocalName";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		boolean isEqual = false;
		if ((element1 == null) && (element2 == null)) {
			isEqual = true;
		} else if ((element1 != null) && (element2 != null)) {
			String localName1 = element1.getLocalName();
			String localName2 = element2.getLocalName();
			if ((localName1 == null) && (localName2 == null))
				isEqual = true;
			else if ((localName1 != null) && (localName2 != null) && (localName1.equals(localName2))) {
				isEqual = true;
			}

		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return isEqual;
	}

	public static boolean isEqualByValue(Element element1, Element element2) {
		String METHOD_NAME = "isEqualByNS";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		boolean isEqual = false;
		if ((element1 == null) && (element2 == null)) {
			isEqual = true;
		} else if ((element1 != null) && (element2 != null)) {
			String nodeValue1 = element1.getNodeValue();
			String nodeValue2 = element2.getNodeValue();
			if ((nodeValue1 == null) && (nodeValue2 == null))
				isEqual = true;
			else if ((nodeValue1 != null) && (nodeValue2 != null) && (nodeValue1.equals(nodeValue2))) {
				isEqual = true;
			}

		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return isEqual;
	}

	public static boolean isEqualByAttr(Element element1, Element element2, String attrNamespaceURI,
			String attrLocalName) {
		String METHOD_NAME = "isEqualByLocalName";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		boolean isEqual = false;
		if ((element1 == null) && (element2 == null)) {
			isEqual = true;
		} else if ((element1 != null) && (element2 != null)) {
			String attrValue1 = element1.getAttributeNS(attrNamespaceURI, attrLocalName);
			String attrValue2 = element2.getAttributeNS(attrNamespaceURI, attrLocalName);
			if ((attrValue1 == null) && (attrValue2 == null))
				isEqual = true;
			else if ((attrValue1 != null) && (attrValue2 != null) && (attrValue1.equals(attrValue2))) {
				isEqual = true;
			}

		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return isEqual;
	}

	public static boolean isEqualByAttr(Element element1, Element element2, String attrName) {
		String METHOD_NAME = "isEqualByLocalName";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		boolean isEqual = false;
		if ((element1 == null) && (element2 == null)) {
			isEqual = true;
		} else if ((element1 != null) && (element2 != null)) {
			String attrValue1 = element1.getAttribute(attrName);
			String attrValue2 = element2.getAttribute(attrName);
			if ((attrValue1 == null) && (attrValue2 == null))
				isEqual = true;
			else if ((attrValue1 != null) && (attrValue2 != null) && (attrValue1.equals(attrValue2))) {
				isEqual = true;
			}

		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return isEqual;
	}

	public static void insertElement(Element parent, Node child) {
		String METHOD_NAME = "insertElement";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		if ((parent != null) && (child != null)) {
			Document doc = parent.getOwnerDocument();
			if (doc != null) {
				parent.appendChild(doc.importNode(child, true));
			}

		}

		if (logger.isLoggable(Level.FINER))
			logger.exiting("XMLUtils", METHOD_NAME);
	}

	public static boolean isEqual(Element element, String namespaceURI, String localName) {
		String METHOD_NAME = "isEqual";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		boolean isEqual = false;
		if (element != null) {
			String _namespaceURI = element.getNamespaceURI();
			String _localName = element.getLocalName();
			boolean isEqualByNS = ((_namespaceURI == null) && (namespaceURI == null))
					|| ((_namespaceURI != null) && (_namespaceURI.equals(namespaceURI)));
			boolean isEqualByLocalName = ((_localName == null) && (localName == null))
					|| ((_localName != null) && (_localName.equals(localName)));
			isEqual = (isEqualByNS) && (isEqualByLocalName);
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return isEqual;
	}

	public static Document read(InputStream is) {
		String METHOD_NAME = "read";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(true);
		factory.setValidating(false);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
			doc.getDocumentElement();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return doc;
	}

	public static List<Attr> getAttributes(Element element) {
		String METHOD_NAME = "getAttributes";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		String prefix = null;
		List<Attr> attrs = new Vector<Attr>();
		if (element != null) {
			NamedNodeMap attributes = element.getAttributes();
			if (attributes != null) {
				for (int i = 0; i < attributes.getLength(); i++) {
					Node node = attributes.item(i);
					if ((node != null) && (node.getNodeType() == 2)) {
						String nodename = node.getNodeName();
						prefix = node.getPrefix();

						if ((!"xmlns".equals(nodename)) && (!"xmlns".equals(prefix))) {
							attrs.add((Attr) node);
						}
					}
				}
			}
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return attrs;
	}

	public static String getAttribute(Element element, String attrName) {
		String METHOD_NAME = "getAttribute";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		String attrValue = null;
		Attr attr = element.getAttributeNode(attrName);
		if (attr != null) {
			attrValue = attr.getValue();
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return attrValue;
	}

	public static String getAttributeNS(Element element, String namespaceURI, String localPart) {
		String METHOD_NAME = "getAttributeNS";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		String attrValue = null;
		if (element != null) {
			Attr attr = element.getAttributeNodeNS(namespaceURI, localPart);
			if (attr != null) {
				attrValue = attr.getValue();
			}
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return attrValue;
	}

	public static String getNodeTagValue(Node node, short nodeType) {
		String METHOD_NAME = "getNodeTagValue";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		String result = "";
		switch (nodeType) {
		case 1:
			result = ((Element) node).getTagName();
		case 3:
			result = ((Text) node).getData();
		case 7:
			result = ((ProcessingInstruction) node).getData();
		case 2:
		case 4:
		case 5:
		case 6:
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return result;
	}

	public static Node getNextTypedNode(Node node, short nodeType) {
		String METHOD_NAME = "getNextTypedNode";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		Node result = null;
		if (node != null) {
			node = node.getNextSibling();
			while ((node != null) && (node.getNodeType() != nodeType)) {
				node = node.getNextSibling();
			}
			if (node.getNodeType() == nodeType) {
				result = node;
			}
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return result;
	}

	public static Node getPreviousTypedNode(Node node, short nodeType) {
		String METHOD_NAME = "getPreviousTypedNode";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		Node result = null;
		if (node != null) {
			node = node.getPreviousSibling();
			while ((node != null) && (node.getNodeType() != nodeType)) {
				node = node.getPreviousSibling();
			}
			if (node.getNodeType() == nodeType) {
				result = node;
			}
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return result;
	}

	public static List<Element> getChildElements(Node node) {
		String METHOD_NAME = "getChildElements";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		List<Element> result = new ArrayList<Element>();
		if (node != null) {
			NodeList childNodes = node.getChildNodes();
			if (childNodes != null) {
				for (int i = 0; i < childNodes.getLength(); i++) {
					Node child = childNodes.item(i);
					if ((child != null) && (child.getNodeType() == 1)) {
						result.add((Element) child);
					}
				}
			}
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return result;
	}

	public static int getPreviousTypedNodeSize(Node node, short nodeType) {
		String METHOD_NAME = "getPreviousTypedNodeSize";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		int size = 0;
		if (node != null) {
			QName qname = QNameUtils.getQName(node);
			node = node.getPreviousSibling();
			while (node != null) {
				if ((node.getNodeType() == nodeType) && (QNameUtils.matches(qname, node))) {
					size++;
				}
				node = node.getPreviousSibling();
			}
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return size;
	}

	public static int getNextTypedNodeSize(Node node, short nodeType) {
		String METHOD_NAME = "getPreviousTypedNodeSize";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("XMLUtils", METHOD_NAME);
		}
		int size = 0;
		if (node != null) {
			QName qname = QNameUtils.getQName(node);
			node = node.getNextSibling();
			while (node != null) {
				if ((node.getNodeType() == nodeType) && (QNameUtils.matches(qname, node))) {
					size++;
				}
				node = node.getNextSibling();
			}
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("XMLUtils", METHOD_NAME);
		}
		return size;
	}
}
