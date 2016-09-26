package cloud.iot.ai.analytics.streaming;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cloud.iot.ai.analytics.element.Schema;
import cloud.iot.ai.analytics.element.SchematronReader;
import cloud.iot.ai.analytics.factory.SchematronFactory;

public class AnalyticsEngineManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6211107514105612267L;

	private AnalyticsEngineManager engine = null;

	private static Map<String, Schema> rules = new ConcurrentHashMap<String, Schema>();

	public Schema getRule(String id) {
		for (Iterator<Entry<String, Schema>> iterator = rules.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Schema> entry = iterator.next();
			String key = entry.getKey();
			Schema value = entry.getValue();
			System.out.println("key:" + key + ",value:" + value);
		}
		return rules.get(id);
	}

	public void addRule(String id, String rule) {
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

	public AnalyticsEngineManager getEngine() {
		if (engine == null) {
			engine = new AnalyticsEngineManager();
		}

		return engine;
	}
}
