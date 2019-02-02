package clients;

import com.google.gson.Gson;
import com.phidget22.*;

import mqtt.publisher.Publisher;
import mqtt.utils.Utils;

public class RFIDClient {
	private static String sensorServerURL = "http://localhost:8080/15068126_Mobile_Dev_1CWK50_Server/SensorServerDB";
	private Gson gson = new Gson();
	private Publisher publisher = new Publisher();
	private final String defaultID = "15068126";
	static RFID phid;
	
	public static void main(String[] args) throws PhidgetException {
		phid = new RFID();
		new RFIDClient(phid);
	}

	public RFIDClient(RFID phid) {
		// Test the connection
		try {
			phid = new RFID();
		} catch (PhidgetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Testing the RFID reader connectivity");
		try {
			phid.open(500);
			phid.setAntennaEnabled(true);
			Thread.sleep(500);
			phid.close();
		} catch (PhidgetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void RFIDListener(String topic, RFID phid, String roomSerialNum) throws PhidgetException {
		// Make the RFID Phidget able to detect loss or gain of an RFID card
		phid.addTagListener(new RFIDTagListener() {
			public void onTag(RFIDTagEvent e) {
				System.out.println("Tag Read: " + e.getTag());
				String TimeInserted = Utils.convertUnixTime(System.currentTimeMillis() / 1000L);
				String tag = e.getTag();
				int user = Utils.fetchUser(sensorServerURL, tag);
				String doorName = topic;
				int roomNumber = Utils.fetchRoomID(sensorServerURL, doorName);
				System.out.println("Door " + doorName + " is ID: " + roomNumber);
				boolean lockStatus = Utils.checkLock(sensorServerURL, roomNumber);
				if (Utils.verifyUser(sensorServerURL, roomNumber, tag)) {
					Sensor sensor = new Sensor(user, roomNumber, tag, doorName, roomSerialNum, TimeInserted, lockStatus,
							"success");
					String sensorJson = gson.toJson(sensor);
					try {
						publisher.publishJson(sensorJson, "/" + doorName);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					Sensor sensor = new Sensor(user, roomNumber, tag, doorName, roomSerialNum, TimeInserted, lockStatus,
							"failure");
					String sensorJson = gson.toJson(sensor);
					try {
						publisher.publishJson(sensorJson, "/" + doorName);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					System.out.println("Door remaining closed, Tag is not permitted to open this door.");
				}
				System.out.println(e.getSource());
			}
		});

		phid.addTagLostListener(new RFIDTagLostListener() {
			public void onTagLost(RFIDTagLostEvent e) {
				System.out.println("Tag Lost: " + e.getTag());
			}
		});

		try {
			// Open and start detecting RFID cards
			phid.open(); 
			phid.setAntennaEnabled(true);
			System.out.println("\n\n Gathering data \n\n");
		} catch (Exception e) {
		}
	}
	
	public int getSerialNum(RFID rfid) {
		int ser = 0;
		try {
		rfid.open(2000);			
		System.out.println("Fetching RFID Serial Number: ");
		rfid.addTagListener(new RFIDTagListener() {
			public void onTag(RFIDTagEvent e) {
			}
		});
		ser = rfid.getDeviceSerialNumber();
		rfid.close(); } catch (Exception e) {
			System.out.println(e);
		}
		return ser;
	}
}