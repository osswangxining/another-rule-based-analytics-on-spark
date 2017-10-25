package cloud.iot.ai.analytics;

import java.text.DecimalFormat;
import java.util.Random;

import com.google.gson.JsonObject;

import cloud.iot.ai.analytics.kafka.KafkaProducer;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

public class KafkaDataProducer {
	double currentTemp = 65.0;
	Random rand = new Random();
	DecimalFormat df = new DecimalFormat("#.##");

	public String get(int i) {
		double newTemp = rand.nextGaussian() + currentTemp;
		currentTemp = newTemp;

		JsonObject obj = new JsonObject();
		JsonObject d = new JsonObject();
		obj.add("d", d);
		d.addProperty("name", "device001");
		d.addProperty("temp", Double.parseDouble(df.format(currentTemp)));
		d.addProperty("timestamp", System.currentTimeMillis()/1000);
		d.addProperty("id", (i+1));
		return obj.toString();
	}

	public static void main(String[] args) {
		String brokers = "localhost:9092";
		Producer<String, String> producer = KafkaProducer.getInstance(brokers).getProducer();

		KafkaDataProducer instance = new KafkaDataProducer();

		String topic = "test-topic";

		for (int i = 0; i < 100; i++) {
			String message = instance.get(i);
			KeyedMessage<String, String> keyedMessage = new KeyedMessage<String, String>(topic, "device001", message);
			producer.send(keyedMessage);
			System.out.println("message[" + (i + 1) + "] is sent.");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
