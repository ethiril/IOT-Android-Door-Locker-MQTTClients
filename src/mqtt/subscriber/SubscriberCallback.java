package mqtt.subscriber;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.eclipse.paho.client.mqttv3.*;
import com.google.gson.Gson;
import com.phidget22.RCServo;

import clients.Motor;
import clients.Sensor;
import mqtt.utils.Utils;

public class SubscriberCallback implements MqttCallback {

	public static final String userid = "15068126";
	public static String sensorServerURL = "http://localhost:8080/15068126_Mobile_Dev_1CWK50_Server/SensorServerDB";
	private Gson gson = new Gson();
	Motor motor;

	@Override
	public void connectionLost(Throwable cause) {
		// This is called when the connection is lost. We could reconnect here.

	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		RCServo rcServo = new RCServo();
		motor = new Motor(rcServo);
		System.out.println("Message arrived. Topic: " + topic + "  Message: " + message.toString());
		// Move motor to open, then shut after pausing
		Sensor sensor = gson.fromJson(message.toString(), Sensor.class); // converts from json
		String motorSerial = Integer.toString(motor.getSerialNum(rcServo));
		String doorName = Utils.fetchDoorName(sensorServerURL, motorSerial).replace("\"", "");
		System.out.println("Door name: " + doorName);
		int roomid = Utils.fetchRoomID(sensorServerURL, doorName);
		System.out.println("Room id: " + roomid);
		boolean lockStatus = Utils.checkLock(sensorServerURL, roomid);
		if (topic.equals("/" + doorName)) {
			String lock = (lockStatus) ? "locked" : "unlocked";
			System.out.println("Room " + roomid + " is " + lock);
			if (lockStatus == false && sensor.getSuccessFailure().equals("success")) {
				System.out.println("Door is opening - User matched and door is not locked.");
				// verify if door is locked and if user can access it then attempt to open the
				// door
				motor.openCloseDoor(rcServo);
				sendAttemptsToServer(message.toString());
			} else {
				sendAttemptsToServer(message.toString());
				System.out.println("Door remaining closed.");
			}
		} else if (topic.equals("/" + doorName + "/unlock")) {
			if (lockStatus == true) {
				System.out.println("Unlocking the door");
				motor.openLock(rcServo);
				updateLock(message.toString());
				sendAttemptsToServer(message.toString());
			} else {
			}
		} else if (topic.equals("/" + doorName + "/lock")) {
			if (lockStatus == false) {
				System.out.println("Locking the door");
				motor.closeLock(rcServo);
				updateLock(message.toString());
				sendAttemptsToServer(message.toString());
			} else {
			}
		} else if ((userid + "/LWT").equals(topic)) {
			System.err.println("Sensor gone!");
		} else {
			System.out.println("The current connected sensor is: " + sensor.getSensorName()
					+ ", which is not the correct rfidreader for this door");
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
	}

	private void updateLock(String doorInJson) {
		Sensor sensor = gson.fromJson(doorInJson, Sensor.class); // converts from json
		Utils.tryLock(sensorServerURL, sensor.getLocked(), sensor.getTagID(), sensor.getRoomID());
	}

	private String sendAttemptsToServer(String doorInJson) {
		try {
			doorInJson = URLEncoder.encode(doorInJson, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String fullURL = sensorServerURL + "?sensordata=" + doorInJson;
		System.out.println("Sending Data To: " + fullURL);
		String result = Utils.tryRequest(fullURL);
		return result;
	}

}
