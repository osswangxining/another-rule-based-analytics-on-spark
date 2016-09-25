package cloud.iot.ai.analytics.element;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import cloud.iot.ai.analytics.util.DOM2Writer;

public class TextElement extends SchematronElement {
	private static final long serialVersionUID = 2407173275003966164L;
	private String text = null;

	private Node node = null;

	public TextElement() {
	}

	public TextElement(Node node) {
		setNode(node);
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String toString() {
		if ((this.text == null) && (this.node != null)) {
			Writer writer = new StringWriter();
			DOM2Writer.serialize(this.node, writer);
			this.text = writer.toString();
		}
		return this.text == null ? "" : this.text;
	}

	public Node toNode() {
		if (this.node != null)
			return this.node;
		Text text = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			Document document = builder.newDocument();

			if (getText() != null)
				text = document.createTextNode(getText());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return text;
	}

	public void setNode(Node node) {
		this.node = node;
	}
}
