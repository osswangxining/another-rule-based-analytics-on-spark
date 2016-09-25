package cloud.iot.ai.analytics.element;

public class Linkable extends SchematronElement {
	private static final long serialVersionUID = 2998412264583345279L;
	private String role = null;

	private String subject = null;

	public Linkable(String role, String subject) {
		setRole(role);
		setSubject(subject);
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}
