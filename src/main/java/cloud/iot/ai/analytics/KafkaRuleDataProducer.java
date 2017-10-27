package cloud.iot.ai.analytics;

import java.text.DecimalFormat;
import java.util.Random;

import com.google.gson.JsonObject;

import cloud.iot.ai.analytics.kafka.KafkaProducer;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

public class KafkaRuleDataProducer {
	double currentTemp = 65.0;
	Random rand = new Random();
	DecimalFormat df = new DecimalFormat("#.##");

	public String get() {

		JsonObject obj = new JsonObject();
		obj.addProperty("condition", "@.d.temp>=72 || @.d.temp<=60");
		return obj.toString();
	}

	public static void main(String[] args) {
		String brokers = "localhost:9092";
		Producer<String, String> producer = KafkaProducer.getInstance(brokers).getProducer();

		KafkaRuleDataProducer instance = new KafkaRuleDataProducer();

		String topic = "metadata_rule";

		String message = instance.get();
		KeyedMessage<String, String> keyedMessage = new KeyedMessage<String, String>(topic, "device001", message);
		producer.send(keyedMessage);
		System.out.println("message[rule] is sent.");
	}

}
