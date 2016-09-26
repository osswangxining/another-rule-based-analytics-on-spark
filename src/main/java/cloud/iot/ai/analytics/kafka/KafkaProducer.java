package cloud.iot.ai.analytics.kafka;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaProducer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3715538221874701488L;
	public static final String METADATA_BROKER_LIST_KEY = "metadata.broker.list";
	public static final String SERIALIZER_CLASS_KEY = "serializer.class";
	public static final String SERIALIZER_CLASS_VALUE = "kafka.serializer.StringEncoder";

	private static KafkaProducer instance = new KafkaProducer();
	public static final String KAFKA_POOL = "kafka.pool";
	private static Map<Long, Producer<String, String>> _pool = new ConcurrentHashMap<Long, Producer<String, String>>();

	public static KafkaProducer getInstance(String brokerList) {
		long threadId = Thread.currentThread().getId();
		Producer<String, String> producer = _pool.get(threadId);
		System.out.println("producer:" + producer + ", thread:" + threadId);

		if (producer == null) {

			Preconditions.checkArgument(StringUtils.isNotBlank(brokerList), "kafka brokerList is blank...");

			// set properties
			Properties properties = new Properties();
			properties.put(METADATA_BROKER_LIST_KEY, brokerList);
			properties.put(SERIALIZER_CLASS_KEY, SERIALIZER_CLASS_VALUE);
			properties.put("kafka.message.CompressionCodec", "1");
			properties.put("client.id", "streaming-kafka-output");
			ProducerConfig producerConfig = new ProducerConfig(properties);

			producer = new Producer<String, String>(producerConfig);

			_pool.put(threadId, producer);
		}

		return instance;
	}

	public Producer<String, String> getProducer() {
		long threadId = Thread.currentThread().getId();

		return _pool.get(threadId);
	}

	public void send(KeyedMessage<String, String> keyedMessage) {
		Producer<String, String> producer = getProducer();
		System.out.println("send <- producer:" + producer);
		if (producer != null)
			producer.send(keyedMessage);
	}

	public void send(List<KeyedMessage<String, String>> keyedMessageList) {
		Producer<String, String> producer = getProducer();
		System.out.println("send <- producer:" + producer);
		if (producer != null)
			producer.send(keyedMessageList);
	}

	public void shutdown() {
		Producer<String, String> producer = getProducer();
		System.out.println("send <- producer:" + producer);
		if (producer != null)
			producer.close();

	}
}
