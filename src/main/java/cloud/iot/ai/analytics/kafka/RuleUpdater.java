package cloud.iot.ai.analytics.kafka;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuleUpdater implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6876708192874901880L;

	private static Map<String, String> rules = new ConcurrentHashMap<String, String>();

	public static String getRule(String id) {
		return rules.get(id);
	}

	public static void addRule(String id, String rule) {
		System.out.println("id:" + id + ",rule:" + rule);

		if (id == null || rule == null) {
			return;
		}
		rules.put(id, rule);

	}
}
