package cloud.iot.ai.analytics.element;

import java.util.ArrayList;
import java.util.List;

public class Diagnostic extends Rich {
	private static final long serialVersionUID = 2798554269471082321L;
	private String id = null;

	private List<TextElement> contents = new ArrayList<TextElement>();

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<TextElement> getContent() {
		return this.contents;
	}

	public void addContent(TextElement content) {
		this.contents.add(content);
	}
}
