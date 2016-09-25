package cloud.iot.ai.analytics.element;

import org.w3c.dom.Node;

public class Span extends TextElement {
	private static final long serialVersionUID = 8389100055450928550L;
	private String clsValue = null;

	public Span(Node node) {
		super(node);
	}

	public String getClsValue() {
		return this.clsValue;
	}

	public void setClsValue(String clsValue) {
		this.clsValue = clsValue;
	}
}
