package cloud.iot.ai.analytics;

import java.util.List;

import com.jayway.jsonpath.JsonPath;

public class Validator {
	private static class ValidatorHolder {
		private static final Validator INSTANCE = new Validator();
	}

	private Validator() {

	}

	public static final Validator getInstance() {
		return ValidatorHolder.INSTANCE;
	}

	public boolean execute(String content, String condition) {
		List<String> result = JsonPath.parse(content).read("$[?(" + condition + ")]");
		return !result.isEmpty();
	}
}
