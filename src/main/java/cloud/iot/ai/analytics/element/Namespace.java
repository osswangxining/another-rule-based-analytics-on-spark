package cloud.iot.ai.analytics.element;

public class Namespace extends SchematronElement {
	private static final long serialVersionUID = -6857809228573286278L;
	private String uri = null;

	private String prefix = null;

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getUri() {
		return this.uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
