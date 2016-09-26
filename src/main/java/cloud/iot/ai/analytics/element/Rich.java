package cloud.iot.ai.analytics.element;

import org.w3c.dom.Element;

import cloud.iot.ai.analytics.xml.XMLUtils;

public class Rich extends SchematronElement {
	private static final long serialVersionUID = 4532737988316699122L;
	private String icon = null;

	private String see = null;

	private String fpi = null;

	private String lang = null;

	private String space = "default";

	public Rich() {
	}

	public Rich(String icon, String see, String fpi, String lang, String space) {
		setIcon(icon);
		setSee(see);
		setFpi(fpi);
		setLang(lang);
		setSpace(space);
	}

	public String getFpi() {
		return this.fpi;
	}

	public void setFpi(String fpi) {
		this.fpi = fpi;
	}

	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getLang() {
		return this.lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getSee() {
		return this.see;
	}

	public void setSee(String see) {
		this.see = see;
	}

	public String getSpace() {
		return this.space;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	protected void printAttrs(Element element) {
		if (element == null)
			return;
		if (getIcon() != null) {
			element.setAttribute("icon", getIcon());
		}
		if (getSee() != null) {
			element.setAttribute("see", getSee());
		}
		if (getFpi() != null) {
			element.setAttribute("fpi", getFpi());
		}
		if (getLang() != null) {
			element.setAttribute("xml:lang", getLang());
		}
		if (getSpace() != null)
			element.setAttribute("xml:space", getSpace());
	}

	protected void setRich(Element element) {
		String icon = XMLUtils.getAttribute(element, "icon");
		String see = XMLUtils.getAttribute(element, "see");
		String fpi = XMLUtils.getAttribute(element, "fpi");
		String lang = XMLUtils.getAttribute(element, "xml:lang");
		String space = XMLUtils.getAttribute(element, "xml:space");
		setIcon(icon);
		setSee(see);
		setFpi(fpi);
		setLang(lang);
		setSpace(space);
	}
}
