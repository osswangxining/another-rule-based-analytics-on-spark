package cloud.iot.ai.analytics.element;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Inclusion extends SchematronElement {
	private static final long serialVersionUID = 4043610585978915766L;
	private static Logger logger = Logger.getLogger(Inclusion.class.getName());
	private String href = null;

	private SchematronElement value = null;

	static {
		if (logger.isLoggable(Level.FINEST))
			logger.logp(Level.FINEST, "", "", "$URL$ $Rev$");
	}

	public Inclusion(String href) {
		setHref(href);
	}

	public String getHref() {
		return this.href;
	}

	public void setHref(String href) {
		String METHOD_NAME = "setHref";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("Inclusion", METHOD_NAME);
		}
		this.href = href;

		if (logger.isLoggable(Level.FINER))
			logger.exiting("Inclusion", METHOD_NAME);
	}

	public SchematronElement getValue() {
		return this.value;
	}

	public void setValue(SchematronElement value) {
		this.value = value;
	}
}
