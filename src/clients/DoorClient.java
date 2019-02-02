package clients;

import com.phidget22.PhidgetException;
import com.phidget22.RCServo;
import com.phidget22.RFID;

import mqtt.subscriber.Subscriber;
import mqtt.utils.Utils;

public class DoorClient {
	private static final long serialVersionUID = 1L;
	private static final String sensorServerURL = "http://localhost:8080/15068126_Mobile_Dev_1CWK50_Server/SensorServerDB";
	static Subscriber sub = new Subscriber();
	static Motor door;
	static RFIDClient rfid;
	static String topic;

	// Door client for RFID testing
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		sub.start();
		// Zero out lock:
		RCServo rcServo = null;
		try {
			rcServo = new RCServo();
		} catch (PhidgetException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}
		door = new Motor(rcServo);
		RFID phid = null;
		try {
			phid = new RFID();
		} catch (PhidgetException e3) {
			// TODO Auto-generated catch block
			System.out.println("Phidget exception: ");
			e3.printStackTrace();
		}
		rfid = new RFIDClient(phid);
		System.out.println("Selecting door:");
		String doorSerialNum = Integer.toString(door.getSerialNum(rcServo));
		String roomSerialNum = Integer.toString(rfid.getSerialNum(phid));
		System.out.println("Door #: " + doorSerialNum + ", Room #: " + roomSerialNum);
		topic = Utils.fetchDoorName(sensorServerURL, doorSerialNum).replace("\"", "");
		System.out.println("Door " + topic + " selected");
		System.out.println("Press your tag to the reader to attempt to open the door . . .");
		// manually select door for this reader (as it is one physical device) and try
		try {
			rfid.RFIDListener(topic, phid, roomSerialNum);
		} catch (PhidgetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
