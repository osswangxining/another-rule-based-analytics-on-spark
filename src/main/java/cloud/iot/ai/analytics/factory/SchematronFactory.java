package cloud.iot.ai.analytics.factory;

import cloud.iot.ai.analytics.element.SchematronReader;
import cloud.iot.ai.analytics.element.SchematronWriter;
import cloud.iot.ai.analytics.validator.SchematronValidator;

public class SchematronFactory {
	public static SchematronFactory INSTANCE = new SchematronFactory();

	private SchematronFactory(){
		
	}
	
	public SchematronValidator newSchematronValidator() {
		return new SchematronValidator();
	}

	public SchematronReader newSchematronReader() {
		return new SchematronReader();
	}

	public SchematronWriter newSchematronWriter() {
		return new SchematronWriter();
	}
}
