package cloud.iot.ai.analytics.element;

import org.w3c.dom.Element;

public class Assertion extends AssertionOrReportElement {
	private static final long serialVersionUID = 8447150395403204349L;

	public Assertion(String test) {
		setTest(test);
	}

	public Element toElement() {
		return toElement("assert");
	}
}
