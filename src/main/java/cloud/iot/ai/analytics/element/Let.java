package cloud.iot.ai.analytics.element;

import javax.xml.namespace.QName;

public class Let extends SchematronElement {
	private static final long serialVersionUID = 4270664707493208406L;
	private QName qName = null;

	private Object value = null;

	private Object newValue = null;

	private QName dataType = null;

	private boolean constant = false;

	public Let(QName name, Object value, QName dataType, boolean constant) {
		setQName(name);
		setValue(value);
		setDataType(dataType);
		setConstant(constant);
		setNewValue(value);
	}

	public QName getQName() {
		return this.qName;
	}

	public void setQName(QName qName) {
		this.qName = qName;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public QName getDataType() {
		return this.dataType;
	}

	public void setDataType(QName dataType) {
		this.dataType = dataType;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (this.qName != null) {
			sb.append("name:").append(this.qName.toString()).append(";");
		}
		if (this.value != null) {
			sb.append("value:").append(this.value.toString()).append(";");
		}
		if (this.newValue != null) {
			sb.append("newValue:").append(this.newValue.toString()).append(";");
		}
		if (this.dataType != null) {
			sb.append("dataType:").append(this.dataType.toString()).append(".");
		}
		return sb.toString();
	}

	public boolean isConstant() {
		return this.constant;
	}

	public void setConstant(boolean constant) {
		this.constant = constant;
	}

	public Object getNewValue() {
		return this.newValue;
	}

	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}
}
