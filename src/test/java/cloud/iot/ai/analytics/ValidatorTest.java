package cloud.iot.ai.analytics;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

public class ValidatorTest {

	public static void main(String[] args) {

		try {
			run();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void run() throws UnsupportedEncodingException, IOException {

		// Document doc = XMLUtils.read(Validator.class.getResourceAsStream("/1.xml"));
		// if (doc == null) {
		// System.out.println("xml file does exist.");
		// return;
		// }
		// String jsonString =
		// IOUtils.toString(Validator.class.getResourceAsStream("/2.json"));
		String content = "{\"d\":{\"time\":1474984406506,\"house\":{\"roof\":\"\",\"owner\":\"owner8\",\"wall\":\"\"}},\"t\":\"tenant001\"}";
		long time = 0L;
		JSONObject data = new JSONObject();
		JSONObject root = new JSONObject();
		String tenantId = "";
		try {
			root = new JSONObject(content);

			data = root.optJSONObject("d");
			tenantId = root.optString("t");
			String t = (data.optString("time") == null) ? "0" : data.optString("time");
			time = Long.parseLong(t);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("tenantId:" + tenantId + ", time:" + time + ", d:" + data);
		// System.out.println("rule:" + rule);

	}

}
