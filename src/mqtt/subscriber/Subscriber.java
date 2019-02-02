package mqtt.subscriber;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import mqtt.utils.Utils;

public class Subscriber {

	private static String sensorServerURL = "http://localhost:8080/15068126_Mobile_Dev_1CWK50_Server/SensorServerDB";
	public static final String BROKER_URL = "tcp://broker.hivemq.com:1883";

	// We have to generate a unique Client id.
	public static final String userid = "15068126";
	String clientId = userid + "-sub";
	private MqttClient mqttClient;

	public Subscriber() {

		try {
			mqttClient = new MqttClient(BROKER_URL, clientId);

		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void start() {
		try {
			mqttClient.setCallback(new SubscriberCallback());
			mqttClient.connect();
			String[] rooms = Utils.fetchRooms(sensorServerURL);
			String topics = "";
			for (int i = 0; i < rooms.length; i++) {
				mqttClient.subscribe("/" + rooms[i], 1);
				mqttClient.subscribe("/" + rooms[i] + "/lock", 1);
				mqttClient.subscribe("/" + rooms[i] + "/unlock", 1);
			
				topics = topics + "/" + rooms[i] + ", " + rooms[i] + "/lock" + ", " + rooms[i] + "/unlock";
			}
			System.out.println("Subscriber is now listening to these topics: " + topics);

		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String... args) {
		final Subscriber subscriber = new Subscriber();
		subscriber.start();
	}

}
