package cloud.iot.ai.analytics.kafka;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cloud.iot.ai.analytics.element.Schema;
import cloud.iot.ai.analytics.element.SchematronReader;
import cloud.iot.ai.analytics.factory.SchematronFactory;

public class RuleUpdater implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6876708192874901880L;

	private static Map<String, Schema> rules = new ConcurrentHashMap<String, Schema>();

	public static Schema getRule(String id) {
		return rules.get(id);
	}

	public static void addRule(String id, String rule) {
		System.out.println("id:" + id + ",rule:" + rule);

		if (id == null || rule == null) {
			return;
		}
		SchematronReader reader = SchematronFactory.INSTANCE.newSchematronReader();

		try {
			Schema schema = reader.readSchematron(null, new ByteArrayInputStream(rule.getBytes("UTF-8")));
			rules.put(id, schema);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
