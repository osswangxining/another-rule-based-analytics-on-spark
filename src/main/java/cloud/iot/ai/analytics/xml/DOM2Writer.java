package cloud.iot.ai.analytics.xml;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cloud.iot.ai.analytics.util.StringUtils;

public class DOM2Writer {
  private static Logger logger = Logger.getLogger(DOM2Writer.class.getName());
  private static final String CLASS_NAME = "DOM2Writer";
  private static final String XML10_DECL_START = "<?xml version=\"1.0\" encoding=\"";
  private static final String XML10_DECL_END = "\"?>";
  private static final String XML10_DECL_NO_ENCODING = "<?xml version=\"1.0\"?>";

  static {
    if (logger.isLoggable(Level.FINEST))
      logger.logp(Level.FINEST, "", "", "$URL$ $Rev$");
  }

  public static String serializeAsString(Element element, boolean omitXMLDeclaration, String encoding) {
    String METHOD_NAME = "serializeAsString";

    if (logger.isLoggable(Level.FINER)) {
      logger.entering("DOM2Writer", METHOD_NAME);
    }
    StringWriter sw = new StringWriter();
    if (!omitXMLDeclaration) {
      if (encoding == null)
        sw.write(XML10_DECL_NO_ENCODING);
      else
        sw.write(XML10_DECL_START + encoding + XML10_DECL_END);
    }
    serialize(element, sw);

    if (logger.isLoggable(Level.FINER)) {
      logger.exiting("DOM2Writer", METHOD_NAME);
    }
    return sw.toString();
  }

  public static String toString(Node node) {
    String METHOD_NAME = "toString";

    if (logger.isLoggable(Level.FINER)) {
      logger.entering("DOM2Writer", METHOD_NAME);
    }
    Writer writer = new StringWriter();
    serialize(node, writer);

    if (logger.isLoggable(Level.FINER)) {
      logger.exiting("DOM2Writer", METHOD_NAME);
    }
    return writer.toString();
  }

  public static void serialize(Node node, Writer writer) {
    String METHOD_NAME = "serialize";

    if (logger.isLoggable(Level.FINER)) {
      logger.entering("DOM2Writer", METHOD_NAME);
    }
    serialize(node, null, null, writer);

    if (logger.isLoggable(Level.FINER))
      logger.exiting("DOM2Writer", METHOD_NAME);
  }

  public static void serialize(Node node, Map<String, String> namespaces, String encoding, Writer writer) {
    String METHOD_NAME = "serialize";

    if (logger.isLoggable(Level.FINER)) {
      logger.entering("DOM2Writer", METHOD_NAME);
    }
    PrintWriter out = new PrintWriter(writer);
    if (namespaces == null) {
      namespaces = new HashMap<String, String>();
    }
    namespaces.put("xml", XMLUtils.NS_URI_XML);
    serializeNode(node, namespaces, out, encoding);

    if (logger.isLoggable(Level.FINER))
      logger.exiting("DOM2Writer", METHOD_NAME);
  }

  private static void serializeNode(Node node, Map<String, String> namespaces, PrintWriter out, String encoding) {

    String METHOD_NAME = "serializeNode";
    // log entry to the method
    if (logger.isLoggable(Level.FINER)) {
      logger.entering(CLASS_NAME, METHOD_NAME);
    }
    if (node == null) {
      return;
    }

    boolean hasChildren = false;
    switch (node.getNodeType()) {
    case Node.DOCUMENT_NODE: {
      NodeList children = node.getChildNodes();
      if (children != null) {
        for (int i = 0; i < children.getLength(); i++) {
          serializeNode(children.item(i), namespaces, out, encoding);
        }
      }
      break;
    }
    case Node.ELEMENT_NODE: {
      out.print('<' + node.getNodeName());
      String prefix = node.getPrefix();
      String namespaceURI = node.getNamespaceURI();

      if (prefix != null && namespaceURI != null && !namespaceURI.equals(namespaces.get(prefix))) {
        serializeNamespace(node, namespaces, out);
      }

      NamedNodeMap attrs = node.getAttributes();
      if (attrs != null) {
        int len = attrs.getLength();
        for (int i = 0; i < len; i++) {
          Attr attr = (Attr) attrs.item(i);
          serialzeAttribute(attr, namespaces, out);
        }
      }

      NodeList children = node.getChildNodes();
      if (children != null) {
        hasChildren = (children.getLength() > 0);
        if (hasChildren) {
          out.print('>');
        }
        int len = children.getLength();
        for (int i = 0; i < len; i++) {
          serializeNode(children.item(i), namespaces, out, encoding);
        }
      } else {
        hasChildren = false;
      }

      if (!hasChildren) {
        out.print("/>");
      }
      break;
    }

    case Node.ENTITY_REFERENCE_NODE: {
      out.print('&');
      out.print(node.getNodeName());
      out.print(';');
      break;
    }

    case Node.CDATA_SECTION_NODE: {
      out.println("<![CDATA[");
      out.println(node.getNodeValue());
      out.println("]]>");
      break;
    }

    case Node.TEXT_NODE: {
      out.print(StringUtils.encode(node.getNodeValue()));
      break;
    }

    case Node.COMMENT_NODE: {
      out.println("<!--");
      out.println(node.getNodeValue());
      out.println("-->");
      break;
    }

    case Node.PROCESSING_INSTRUCTION_NODE: {
      serialzeProcessingInstructionNode(node, out);
      break;
    }
    }

    if (node.getNodeType() == Node.ELEMENT_NODE && hasChildren == true) {
      out.print("</");
      out.print(node.getNodeName());
      out.print('>');
      hasChildren = false;
    }
    // log exit from the method
    if (logger.isLoggable(Level.FINER)) {
      logger.exiting(CLASS_NAME, METHOD_NAME);
    }
  }

  private static void serialzeAttribute(Attr attr, Map<String, String> namespaces, PrintWriter out) {
    out.print(' ' + attr.getNodeName() + "=\"" + StringUtils.encode(attr.getValue()) + '"');

    String prefix = attr.getPrefix();
    String namespaceURI = attr.getNamespaceURI();

    if ((prefix != null) && (namespaceURI != null) && (!namespaceURI.equals(namespaces.get(prefix))))
      serializeNamespace(attr, namespaces, out);
  }

  private static void serialzeProcessingInstructionNode(Node node, PrintWriter out) {
    out.print("<?");
    out.print(node.getNodeName());
    String data = node.getNodeValue();
    if ((data != null) && (data.length() > 0)) {
      out.print(' ');
      out.print(data);
    }
    out.println("?>");
  }

  private static void serializeNamespace(Node node, Map<String, String> namespaces, PrintWriter out) {
    String METHOD_NAME = "printNamespace";

    if (logger.isLoggable(Level.FINER)) {
      logger.entering("DOM2Writer", METHOD_NAME);
    }
    if (node != null) {
      Element owner = null;
      if (node.getNodeType() == 2)
        owner = ((Attr) node).getOwnerElement();
      else if (node.getNodeType() == 1) {
        owner = (Element) node;
      }
      if (owner != null) {
        String namespaceURI = node.getNamespaceURI();
        String prefix = node.getPrefix();

        if ((!"http://www.w3.org/2000/xmlns/".equals(namespaceURI)) || (!"xmlns".equals(prefix))) {
          if (XMLUtils.getAttributeNS(owner, "http://www.w3.org/2000/xmlns/", prefix) == null)
            out.print(" xmlns:" + prefix + "=\"" + namespaceURI + '"');
        } else {
          prefix = node.getLocalName();
          namespaceURI = node.getNodeValue();
        }
        namespaces.put(prefix, namespaceURI);
      }
    }

    if (logger.isLoggable(Level.FINER))
      logger.exiting("DOM2Writer", METHOD_NAME);
  }
}
