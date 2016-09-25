package cloud.iot.ai.analytics.element;

import org.w3c.dom.Node;

public class Dir extends TextElement {
	private static final long serialVersionUID = -3538685171984588548L;
	private String value = "ltr";

	public Dir(Node node) {
		super(node);
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
