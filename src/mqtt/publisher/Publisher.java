package mqtt.publisher;

import org.eclipse.paho.client.mqttv3.*;


public class Publisher {

	public static final String BROKER_URL = "tcp://broker.hivemq.com:1883";
	public static final String userid = "15068126";


	private MqttClient client;

	public Publisher() {

		try {
			client = new MqttClient(BROKER_URL, userid);
			// create mqtt session
			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(false);
			options.setWill(client.getTopic(userid + "/LWT"), "Defaulted".getBytes(), 0, false);
			client.connect(options);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	// Specific publishing methods for particular phidgets

    public void publishJson(String json, String topic) throws MqttException {
    	final MqttTopic jsonTopic = client.getTopic(topic);
    	try {
    	jsonTopic.publish(new MqttMessage(json.getBytes()));
    	} catch (MqttException e1) {
    		e1.printStackTrace();
    	}
    	System.out.println("Publishing data. Topic: " + jsonTopic.getName() + " Message: " + json);
    };

}
