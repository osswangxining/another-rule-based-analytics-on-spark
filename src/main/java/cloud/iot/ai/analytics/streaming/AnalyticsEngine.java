package cloud.iot.ai.analytics.streaming;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.ConsumerStrategy;
import org.apache.spark.streaming.kafka010.HasOffsetRanges;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.kafka010.OffsetRange;
import org.apache.spark.streaming.kafka010.CanCommitOffsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cloud.iot.ai.analytics.Validator;
import cloud.iot.ai.analytics.kafka.KafkaProducer;
import kafka.common.TopicAndPartition;
import kafka.message.MessageAndMetadata;
import kafka.producer.KeyedMessage;
import kafka.serializer.StringDecoder;
import scala.Tuple2;

public class AnalyticsEngine {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage: AnalyticsEngine <brokers> <topics>\n"
					+ "  <brokers> is a list of one or more Kafka brokers\n"
					+ "  <topics> is a list of one or more kafka topics to consume from\n\n");
			System.exit(1);
		}

		final String brokers = args[0];
		String topics = args[1];

		final AnalyticsEngineManager engineManager = new AnalyticsEngineManager();

		// Create context with 2 second batch interval
		SparkConf sparkConf = new SparkConf().setAppName("AnotherAnalyticsEngineUsingSpark").setMaster("local[2]");
		sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(1));

		// HashSet<String> topicsSet = new
		// HashSet<String>(Arrays.asList(topics.split(",")));
		Map<String, Object> kafkaParams = new HashMap<String, Object>();
		kafkaParams.put("metadata.broker.list", brokers);
		kafkaParams.put("bootstrap.servers", brokers);
		kafkaParams.put("spark.streaming.kafka.maxRatePerPartition", "100");
		kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put("group.id", "MyAnalyticsEngineConsumerGroup1");
		kafkaParams.put("enable.auto.commit", false);

		System.out.println("initiate config....");
		HashSet<String> configTopicSet = new HashSet<String>(Arrays.asList("metadata_rule".split(",")));
		processRuleUpdate(jssc, brokers, configTopicSet, engineManager);
		System.out.println("waiting for config update....");

		final JsonParser parser = new JsonParser();

		System.out.println("initiate messages....");
		// Create direct kafka stream with brokers and topics
		ConsumerStrategy<String, String> consumerStrategy = ConsumerStrategies
				.Subscribe(Arrays.asList(topics.split(",")), kafkaParams);
		JavaInputDStream<ConsumerRecord<String, String>> streams = KafkaUtils.createDirectStream(jssc,
				LocationStrategies.PreferConsistent(), consumerStrategy);
		// streams.print();
		// streams.foreachRDD(new VoidFunction<JavaRDD<ConsumerRecord<String,
		// String>>>() {
		//
		// @Override
		// public void call(JavaRDD<ConsumerRecord<String, String>> t) throws Exception
		// {
		// //OffsetRange[] offsetRanges = ((HasOffsetRanges) t.rdd()).offsetRanges();
		//
		// List<ConsumerRecord<String, String>> records = t.collect();
		// records.forEach(consumerRecord -> {
		// String key = consumerRecord.key();
		// long offset = consumerRecord.offset();
		// int partition = consumerRecord.partition();
		// String topic = consumerRecord.topic();
		// String value = consumerRecord.value();
		// //System.out.println("consumerRecord:" + consumerRecord.toString());
		// String condition = engineManager.getRule("device001");
		// System.out.println("condition:" + condition);
		// JsonObject conditionJO = (JsonObject) parser.parse(condition);
		// boolean valid = Validator.getInstance().execute(value,
		// conditionJO.getAsJsonPrimitive("condition").getAsString());
		// if (valid) {
		// JsonObject event = (JsonObject) parser.parse(value);
		// long time2 = new Date().getTime() / 1000;
		// String key1 = "" + time2;
		// long gap = time2 -
		// event.getAsJsonObject("d").getAsJsonPrimitive("timestamp").getAsLong();
		// String message = "report[" + gap + "]:" + value;
		// KeyedMessage<String, String> keyedMessage = new KeyedMessage<String,
		// String>("rule.output", key1,
		// message);
		// KafkaProducer.getInstance(brokers).getProducer().send(keyedMessage);
		// }
		// });
		//
		// // some time later, after outputs have completed
		// //((CanCommitOffsets) streams.inputDStream()).commitAsync(offsetRanges);
		// }
		//
		// });
		streams.foreachRDD(rdd -> {
			OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
			rdd.collect().forEach(consumerRecord -> {
				String key = consumerRecord.key();
				long offset = consumerRecord.offset();
				int partition = consumerRecord.partition();
				String topic = consumerRecord.topic();
				String value = consumerRecord.value();
				System.out.println("consumerRecord:" + consumerRecord.toString());
				String condition = engineManager.getRule("device001");
				System.out.println("condition:" + condition);
				if (condition != null) {
					JsonObject conditionJO = (JsonObject) parser.parse(condition);
					boolean valid = Validator.getInstance().execute(value,
							conditionJO.getAsJsonPrimitive("condition").getAsString());
					if (valid) {
						JsonObject event = (JsonObject) parser.parse(value);
						long time2 = new Date().getTime() / 1000;
						String key1 = "" + time2;
						long gap = time2 - event.getAsJsonObject("d").getAsJsonPrimitive("timestamp").getAsLong();
						String message = "report[" + gap + "]:" + value;
						KeyedMessage<String, String> keyedMessage = new KeyedMessage<String, String>("rule.output",
								key1, message);
						KafkaProducer.getInstance(brokers).getProducer().send(keyedMessage);
					}
				}
			});

			// some time later, after outputs have completed
			((CanCommitOffsets) streams.inputDStream()).commitAsync(offsetRanges);
		});
		// JavaPairInputDStream<String, String> messages =
		// KafkaUtils.createDirectStream(jssc, String.class, String.class,
		// StringDecoder.class, StringDecoder.class, kafkaParams, topicsSet);

		// Map<TopicAndPartition, Long> topicAndPartition = new HashMap<>();
		// topicAndPartition.put(new TopicAndPartition("test-topic", 0), 1L);
		// topicAndPartition.put(new TopicAndPartition("metadata_rule", 0), 1L);
		//
		// class MessageAndMetadataFunction implements
		// Function<MessageAndMetadata<String, String>, String>
		// {
		//
		// @Override
		// public String call(MessageAndMetadata<String, String> v1)
		// throws Exception {
		// // nothing is printed here
		// System.out.println("topic = " + v1.topic() + ", partition = " +
		// v1.partition() + ",key:" +v1.key() + ",message:" + v1.message());
		// return v1.topic();
		// }
		//
		// }
		// JavaInputDStream< String> messages = KafkaUtils.createDirectStream(jssc,
		// String.class, String.class,
		// StringDecoder.class, StringDecoder.class, String.class, kafkaParams,
		// topicAndPartition, new MessageAndMetadataFunction());
		System.out.println("Listening kafka messages....");
		// Get the data

		// messages.foreachRDD(rdd -> {
		// OffsetRange[] offsets = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
		// Arrays.asList(offsets).forEach(offset -> {
		// String topic = offset.topic();
		// int partition = offset.partition();
		// long fromOffset = offset.fromOffset();
		// long untilOffset = offset.untilOffset();
		// System.out.println("topic:" + topic + ",partition:" + partition +
		// ",fromOffset:" + fromOffset
		// + ",untilOffset:" + untilOffset);
		// });
		// rdd.collect().forEach(element -> System.out.println(element));
		// // JavaPairRDD<List<String>, Long> partitions = rdd.glom().zipWithIndex();
		// // partitions.foreach(partition -> {
		// // List<String> consumerRecord = partition._1();
		// // Long index = partition._2();
		// // System.out.println("index:" + index);
		// // consumerRecord.forEach(record -> {
		// //// String _1 = record._1();
		// //// String _2 = record._2();
		// //// System.out.println("_1:" + _1 + ", _2:" + _2);
		// // System.out.println("record:" + record);
		// // });
		// // });
		// });
		// messages.foreachRDD(new VoidFunction<JavaPairRDD<String, String>>() {
		//
		// @Override
		// public void call(JavaPairRDD<String, String> v1) throws Exception {
		// Map<String, String> events = v1.collectAsMap();
		//
		// System.out.println("New RDD call, size=" + events.size());
		//
		// events.forEach((k, v) -> {
		// String condition = engineManager.getRule("device001");
		// System.out.println("condition:" + condition);
		// JsonObject conditionJO = (JsonObject) parser.parse(condition);
		// boolean valid = Validator.getInstance().execute(v,
		// conditionJO.getAsJsonPrimitive("condition").getAsString());
		// if (valid) {
		// System.out.println(k + ":" + v);
		// JsonObject event = (JsonObject) parser.parse(v);
		// long time2 = new Date().getTime() / 1000;
		// String key = "" + time2;
		// long gap = time2 -
		// event.getAsJsonObject("d").getAsJsonPrimitive("timestamp").getAsLong();
		// String message = "report[" + gap + "]:" + v;
		// String topic = "rule.output";
		// KeyedMessage<String, String> keyedMessage = new KeyedMessage<String,
		// String>(topic, key,
		// message);
		// KafkaProducer.getInstance(brokers).getProducer().send(keyedMessage);
		// }
		// });
		//
		// }
		// });

		// JavaDStream<String> data = messages.map(new Function<Tuple2<String, String>,
		// String>() {
		// @Override
		// public String call(Tuple2<String, String> tuple2) {
		// String key = tuple2._1();
		// String value = tuple2._2();
		// System.out.println("key:" + key + ", value:" + value);
		//
		// JSONObject root = new JSONObject();
		// try {
		// root.put("t", key);
		// root.put("d", new JSONObject(value));
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// return root.toString();
		// }
		// });
		// System.out.println("Validating messages....");
		// JavaDStream<String> words = data.flatMap(new FlatMapFunction<String,
		// String>() {
		// @Override
		// public Iterable<String> call(String content) {
		// System.out.println("content:" + content);
		// long time = 0L;
		// JSONObject data = new JSONObject();
		// JSONObject root = new JSONObject();
		// String tenantId = "";
		// try {
		// root = new JSONObject(content);
		//
		// data = root.optJSONObject("d");
		// tenantId = root.optString("t");
		// String t = (data.optString("time") == null) ? "0" : data.optString("time");
		// time = Long.parseLong(t);
		// } catch (Exception e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// System.out.println("tenantId:" + tenantId + ", time:" + time + ", d:" +
		// data);
		// // System.out.println("rule:" + rule);
		// System.out.println("root:" + root);
		// String condition = "@.d.temp>=60 || @.d.temp<=20";
		// System.out.println("condition:" + condition);
		// boolean valid = Validator.getInstance().execute(content, condition);
		// TextOutputFormatter outputFormatter = new TextOutputFormatter();
		// List<String> outputs = outputFormatter.getResult();
		// if (outputs != null) {
		// System.out.println("outputs:" + outputs.size());
		// int index = 1;
		// for (String s : outputs) {
		// System.out.println("#[" + (index++) + "]" + s);
		// long time2 = new Date().getTime();
		// String key = "" + time2;
		// long gap = time2 - time;
		// String message = "report[" + gap + "]:" + s;
		// String topic = "output.test4spark";
		// KeyedMessage<String, String> keyedMessage = new KeyedMessage<String,
		// String>(topic, key,
		// message);
		// KafkaProducer.getInstance(brokers).getProducer().send(keyedMessage);
		// }
		// }
		//
		// return outputs;
		// // return Lists.newArrayList(x);
		// }
		// });
		System.out.println("Complete validation....");
		// words.print();

		// Start the computation
		jssc.start();
		try {
			jssc.awaitTermination();
		} catch (InterruptedException e) {
			e.printStackTrace();
			jssc.ssc().sc().cancelAllJobs();
			jssc.stop(true, false);
			System.exit(-1);
		}
	}

	private static void processRuleUpdate(JavaStreamingContext jssc, String brokers, Set<String> topicsSet,
			final AnalyticsEngineManager engineManager) {
		Map<String, Object> kafkaParams = new HashMap<String, Object>();
		kafkaParams.put("metadata.broker.list", brokers);
		kafkaParams.put("bootstrap.servers", brokers);
		kafkaParams.put("spark.streaming.kafka.maxRatePerPartition", "100");
		kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put("group.id", "MyAnalyticsEngineConsumerGroup1");
		kafkaParams.put("enable.auto.commit", false);
		kafkaParams.put("auto.offset.reset", "earliest");

		System.out.println("Initiate kafka messages for rules....");
		// Create direct kafka stream with brokers and topics
		ConsumerStrategy<String, String> consumerStrategy = ConsumerStrategies.Subscribe(topicsSet, kafkaParams);
		JavaInputDStream<ConsumerRecord<String, String>> streams = KafkaUtils.createDirectStream(jssc,
				LocationStrategies.PreferConsistent(), consumerStrategy);

		System.out.println("Waiting for kafka messages of rules....");

		// Get the data
		streams.foreachRDD(rdd -> {
			rdd.collect().forEach(consumerRecord -> {
				String key = consumerRecord.key();
				long offset = consumerRecord.offset();
				int partition = consumerRecord.partition();
				String topic = consumerRecord.topic();
				String value = consumerRecord.value();
				System.out.println("consumerRecord:" + consumerRecord.toString());
				System.out.println("[ruleupdate]key:" + key + ", value:" + value);

				engineManager.getEngine().addRule(key, value);
			});

			OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
			// some time later, after outputs have completed
			((CanCommitOffsets) streams.inputDStream()).commitAsync(offsetRanges);
		});

		System.out.println("Prepare rule validation....");

	}
}
