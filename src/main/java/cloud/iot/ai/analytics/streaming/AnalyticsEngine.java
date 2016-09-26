package cloud.iot.ai.analytics.streaming;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.xpath.XPathExpressionException;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;

import com.google.common.collect.Lists;

import cloud.iot.ai.analytics.element.Schema;
import cloud.iot.ai.analytics.element.SchematronReader;
import cloud.iot.ai.analytics.factory.SchematronFactory;
import cloud.iot.ai.analytics.function.XPathCustomFunctionResolver;
import cloud.iot.ai.analytics.json.JSON2XMLElement;
import cloud.iot.ai.analytics.kafka.KafkaProducer;
import cloud.iot.ai.analytics.output.TextOutputFormatter;
import cloud.iot.ai.analytics.validator.SchematronValidator;
import cloud.iot.ai.analytics.xml.DOM2Writer;
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
		SparkConf sparkConf = new SparkConf().setAppName("JavaDirectKafkaWordCount");
		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));

		HashSet<String> topicsSet = new HashSet<String>(Arrays.asList(topics.split(",")));
		HashMap<String, String> kafkaParams = new HashMap<String, String>();
		kafkaParams.put("metadata.broker.list", brokers);

//		final String rule = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><sch:schema xmlns:sch=\"http://www.ascc.net/xml/schematron\" defaultPhase=\"built\" icon=\"http://www.ascc.net/xml/resource/schematron/bilby.jpg\"><sch:pattern xmlns:sch=\"http://purl.oclc.org/dsdl/schematron\" id=\"completed\" name=\"Construction Checks\"><sch:rule context=\"/house\"><sch:assert test=\"count(wall) &lt; 4 \">A house should have 1-4 walls</sch:assert><sch:assert test=\"builder\">An incomplete house must have a builder assigned to it</sch:assert><sch:assert test=\"not(owner)\">An incomplete house cannot have an owner</sch:assert><sch:report test=\"not(roof)\">The house is incomplete, it still needs a roof</sch:report></sch:rule><sch:p>Constraints which are applied during construction</sch:p></sch:pattern><sch:pattern id=\"admin2\" name=\"Final Checks\"><sch:rule context=\"/house\"><sch:assert test=\"count(wall) = 4\">A house should have 4 walls</sch:assert><sch:assert test=\"owner\">An incomplete house must have an owner</sch:assert><sch:assert test=\"not(builder)\">An incomplete house doesn't need a builder</sch:assert><sch:report test=\"roof\">The house is incomplete, it still needs a roof</sch:report></sch:rule><sch:p>Constraints which are applied after construction</sch:p></sch:pattern><sch:p>This is an example schema for the <emph>Building Projects XML</emph> language.</sch:p><sch:phase id=\"underConstruction\"><sch:active pattern=\"construction\"/><sch:active pattern=\"admin\"/></sch:phase><sch:phase id=\"built\"><sch:active pattern=\"completed\"/><sch:active pattern=\"admin\"/></sch:phase></sch:schema>";

		// final KafkaProducer kafkaProducer =
		// KafkaProducer.getInstance(brokers);
		// final Producer<String, String> producer =
		// kafkaProducer.getProducer();
		System.out.println("initiate config....");
		HashSet<String> configTopicSet = new HashSet<String>(Arrays.asList("config".split(",")));
		processRuleUpdate(jssc, brokers, configTopicSet, engineManager);
		System.out.println("waiting for config update....");

		System.out.println("initiate messages....");
		// Create direct kafka stream with brokers and topics
		JavaPairInputDStream<String, String> messages = KafkaUtils.createDirectStream(jssc, String.class, String.class,
				StringDecoder.class, StringDecoder.class, kafkaParams, topicsSet);
		System.out.println("Listening kafka messages....");
		// Get the data
		JavaDStream<String> data = messages.map(new Function<Tuple2<String, String>, String>() {
			@Override
			public String call(Tuple2<String, String> tuple2) {
				String key = tuple2._1();
				String value = tuple2._2();
				System.out.println("key:" + key + ", value:" + value);

				JSONObject root = new JSONObject();
				try {
					root.put("t", key);
					root.put("d", new JSONObject(value));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return root.toString();
			}
		});
		System.out.println("Validating messages....");
		JavaDStream<String> words = data.flatMap(new FlatMapFunction<String, String>() {
			@Override
			public Iterable<String> call(String content) {
				System.out.println("content:" + content);
				long time = 0L;
				JSONObject data = new JSONObject();
				JSONObject root = new JSONObject();
				String tenantId = "";
				try {
					root = new JSONObject(content);

					data = root.optJSONObject("d");
					tenantId = root.optString("t");
					String t = (data.optString("time") == null)?"0":data.optString("time");
					time = Long.parseLong(t);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("tenantId:" + tenantId+ ", time:" + time + ", d:" + data);
//				System.out.println("rule:" + rule);

				Element elem = JSON2XMLElement.convert(root);

				try {
					Schema schema = engineManager.getEngine().getRule(tenantId);
					if (schema != null) {
						System.out.println(schema.toString());
						System.out.println(DOM2Writer.serializeAsString(elem, false, "UTF-8"));
						SchematronValidator validator = SchematronFactory.INSTANCE.newSchematronValidator();
						TextOutputFormatter outputFormatter = new TextOutputFormatter();
						validator.setFormatter(outputFormatter);
						validator.setFunctionResolver(new XPathCustomFunctionResolver(elem));
						validator.validate(elem, schema);
						List<String> outputs = outputFormatter.getResult();
						if (outputs != null) {
							System.out.println("outputs:" + outputs.size());
							int index = 1;
							for (String s : outputs) {
								System.out.println("#[" + (index++) + "]" + s);
								long time2 = new Date().getTime();
								String key = "" + time2;
								long gap = time2 - time;
								String message = "report[" + gap + "]:" + s;
								String topic = "output.test4spark";
								KeyedMessage<String, String> keyedMessage = new KeyedMessage<String, String>(topic, key,
										message);
								KafkaProducer.getInstance(brokers).getProducer().send(keyedMessage);
							}
						}

						return outputs;
					}
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// return Lists.newArrayList(x);
				return Lists.newArrayList();
			}
		});
		System.out.println("Complete validation....");
		words.print();

		// Start the computation
		jssc.start();
		jssc.awaitTermination();
	}

	private static void processRuleUpdate(JavaStreamingContext jssc, String brokers,
			Set<String> topicsSet, final AnalyticsEngineManager engineManager) {
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
