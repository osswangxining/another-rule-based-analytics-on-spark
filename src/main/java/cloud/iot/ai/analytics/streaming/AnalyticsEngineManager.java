package cloud.iot.ai.analytics.streaming;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnalyticsEngineManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6211107514105612267L;

	private AnalyticsEngineManager engine = null;

	private static Map<String, String> rules = new ConcurrentHashMap<String, String>();

	public String getRule(String id) {
		return rules.get(id);
	}

	public void addRule(String id, String rule) {
		System.out.println("id:" + id + ",rule:" + rule);

		if (id == null || rule == null) {
			return;
		}
		rules.put(id, rule);

	}

	public AnalyticsEngineManager getEngine() {
		if (engine == null) {
			engine = new AnalyticsEngineManager();
		}

		return engine;
	}
}
