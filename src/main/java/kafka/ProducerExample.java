package kafka;

import org.apache.kafka.clients.producer.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public class ProducerExample {

	private String topicName;
	private static String kafkaClusterIP = "192.168.1.104:9092,192.168.1.105:9092";

	public ProducerExample(String name){
		topicName = name;
	}

	public void sendRecords() throws IOException, InterruptedException {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaClusterIP);//kafka clusterIP
		props.put("acks", "1");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		BufferedReader br =  new BufferedReader(new FileReader("./data/record.json"));//record file path
		Producer<String, String> producer = new KafkaProducer<>(props);
		int i = 0;//message key
		String record;
		//send record to kafka
		while((record = br.readLine())!=null) {
			producer.send(new ProducerRecord<String, String>(topicName, Integer.toString(i), record), new Callback() {
				public void onCompletion(RecordMetadata metadata, Exception e) {
					if (e != null)
						e.printStackTrace();
					System.out.println("The offset of the record we just sent is: " + metadata.offset());
				}
			});
			i++;
		}
		producer.close();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		new ProducerExample("test-topic").sendRecords();
	}

}
