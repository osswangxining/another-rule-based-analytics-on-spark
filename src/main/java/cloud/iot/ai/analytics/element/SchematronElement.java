package cloud.iot.ai.analytics.element;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class SchematronElement implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -432201205108170237L;

	private String targetNamespaceURI = null;

	private String prefix = null;

	private List<TextElement> contents = new ArrayList<TextElement>();

	public List<TextElement> getContent() {
		return this.contents;
	}

	public void addContent(TextElement content) {
		this.contents.add(content);
	}

	public String getTargetNamespaceURI() {
		if (SchematronConstants.SCHEMATRON_NAMESPACE_URI.contains(this.targetNamespaceURI)) {
			return this.targetNamespaceURI;
		}
		return SchematronConstants.ISO_SCHEMATRON_NAMESPACE_URI;
	}

	public String getPrefix() {
		return this.prefix == null ? "sch" : this.prefix;
	}

	public void setTargetNamespaceURI(String targetNamespaceURI) {
		this.targetNamespaceURI = targetNamespaceURI;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
