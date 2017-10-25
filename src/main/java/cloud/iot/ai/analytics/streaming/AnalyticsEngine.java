package cloud.iot.ai.analytics.streaming;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

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
		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(1));

		HashSet<String> topicsSet = new HashSet<String>(Arrays.asList(topics.split(",")));
		HashMap<String, String> kafkaParams = new HashMap<String, String>();
		kafkaParams.put("metadata.broker.list", brokers);
		kafkaParams.put("spark.streaming.kafka.maxRatePerPartition", "100");

		System.out.println("initiate config....");
		HashSet<String> configTopicSet = new HashSet<String>(Arrays.asList("config".split(",")));
		// processRuleUpdate(jssc, brokers, configTopicSet, engineManager);
		System.out.println("waiting for config update....");

		System.out.println("initiate messages....");
		// Create direct kafka stream with brokers and topics
		JavaPairInputDStream<String, String> messages = KafkaUtils.createDirectStream(jssc, String.class, String.class,
				StringDecoder.class, StringDecoder.class, kafkaParams, topicsSet);
		System.out.println("Listening kafka messages....");
		// Get the data
		messages.foreachRDD(new VoidFunction<JavaPairRDD<String, String>>() {

			@Override
			public void call(JavaPairRDD<String, String> v1) throws Exception {
				Map<String, String> events = v1.collectAsMap();
				events.forEach((k, v) -> System.out.println(k + ":" + v));
				System.out.println("New RDD call, size=" + events.size());

			}
		});

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
		HashMap<String, String> kafkaParams = new HashMap<String, String>();
		kafkaParams.put("metadata.broker.list", brokers);
		kafkaParams.put("auto.offset.reset", "smallest");

		System.out.println("Initiate kafka messages for rules....");
		// Create direct kafka stream with brokers and topics
		JavaPairInputDStream<String, String> rules = KafkaUtils.createDirectStream(jssc, String.class, String.class,
				StringDecoder.class, StringDecoder.class, kafkaParams, topicsSet);
		System.out.println("Waiting for kafka messages of rules....");

		// Get the data
		JavaDStream<String> data = rules.map(new Function<Tuple2<String, String>, String>() {
			@Override
			public String call(Tuple2<String, String> tuple2) {
				String key = tuple2._1();
				String value = tuple2._2();
				System.out.println("key:" + key + ", value:" + value);

				engineManager.getEngine().addRule(key, value);

				return key;
			}
		});

		data.print();
		System.out.println("Prepare rule validation....");

	}
}
