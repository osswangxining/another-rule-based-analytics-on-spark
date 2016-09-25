package cloud.iot.ai.analytics.element;

import org.w3c.dom.Element;

public class Report extends AssertionOrReportElement {
	private static final long serialVersionUID = 2829854386852399193L;

	public Report(String test) {
		setTest(test);
	}

	public Element toElement() {
		return toElement("report");
	}
}
