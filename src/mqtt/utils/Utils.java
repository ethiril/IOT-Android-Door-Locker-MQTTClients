package mqtt.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Utils {

	private static final Random random = new Random();

	private static Gson gson = new Gson();

	public static String getMacAddress() {
		String result = "";

		try {
			for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
				byte[] hardwareAddress = ni.getHardwareAddress();

				if (hardwareAddress != null) {
					for (int i = 0; i < hardwareAddress.length; i++)
						result += String.format((i == 0 ? "" : "-") + "%02X", hardwareAddress[i]);
					return result;
				}
			}

		} catch (SocketException e) {
			System.out.println("Could not find out MAC Adress. Exiting Application ");
			System.exit(1);
		}
		return result;
	}

	public static String convertUnixTime(long unixtime) {
		Date date = new java.util.Date(unixtime * 1000L);
		// the format of your date
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = sdf.format(date);
		return formattedDate;
	}

	public static boolean verifyUser(String sensorServerURL, int roomNumber, String tagid) {
		String fullURL = null;
		try {
			fullURL = sensorServerURL + "?verifyroomaccess=" + URLEncoder.encode(
					"{\"RoomID\":\"" + Integer.toString(roomNumber) + "\", \"TagID\":\"" + tagid + "\"}", "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String result = tryRequest(fullURL);
		JsonElement jElem = gson.fromJson(result, JsonElement.class);
		JsonObject obj = jElem.getAsJsonObject();
		// Remove the JSON syntax and return the room id
		String accessStr = obj.get("access").toString().replaceAll("^\"|\"$", "");
		boolean access = Boolean.parseBoolean(accessStr);
		return access;
	}

	// check if the door is locked
	public static boolean checkLock(String sensorServerURL, int roomid) {
		String fullURL = null;
		try {
			fullURL = sensorServerURL + "?getlockstatus="
					+ URLEncoder.encode("{\"RoomID\":\"" + roomid + "\"}", "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String result = tryRequest(fullURL);
		JsonElement jElem = gson.fromJson(result, JsonElement.class);
		JsonObject obj = jElem.getAsJsonObject();
		// Remove the JSON syntax and return the room id
		String lockedStr = obj.get("locked").toString().replaceAll("^\"|\"$", "");
		boolean locked = Boolean.valueOf(lockedStr);
		return locked;
	}

	// try to update the lock
	public static String tryLock(String sensorServerURL, boolean lockstatus, String tagid, int roomid) {
		String fullURL = null;
		try {
			fullURL = sensorServerURL + "?updatelock=" + URLEncoder.encode(
					"{\"Locked\":\"" + lockstatus + "\",\"RoomID\":\"" + roomid + "\", \"TagID\":\"" + tagid + "\"}",
					"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
			String result = tryRequest(fullURL);
			JsonElement jElem = gson.fromJson(result, JsonElement.class);
			JsonObject obj = jElem.getAsJsonObject();
			String userStr = obj.get("access").toString().replaceAll("^\"|\"$", "");
			return userStr;
	}

	public static int fetchUser(String sensorServerURL, String tagid) {
		String fullURL = null;
		try {
			fullURL = sensorServerURL + "?getuser=" + URLEncoder.encode("{\"TagID\":\"" + tagid + "\"}", "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String result = tryRequest(fullURL);
		JsonElement jElem = gson.fromJson(result, JsonElement.class);
		JsonObject obj = jElem.getAsJsonObject();
		// Remove the JSON syntax and return the room id
		String userStr = obj.get("user").toString().replaceAll("^\"|\"$", "");
		int user = Integer.parseInt(userStr);
		return user;
	}
	
	
	public static String fetchDoorName (String sensorServerURL, String SerialNum) {
		String fullURL = null;
		try {
			fullURL = sensorServerURL + "?getroombyserial=" + URLEncoder.encode("{\"SerialNum\":\"" + SerialNum + "\"}", "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String result = tryRequest(fullURL);
		JsonElement jElem = gson.fromJson(result, JsonElement.class);
		JsonObject obj = jElem.getAsJsonObject();
		// Remove the JSON syntax and return the room id
		String idStr = obj.get("RoomName").toString();
		return idStr;
	}
	
	public static int fetchRoomID(String sensorServerURL, String roomName) {
		String fullURL = null;
		try {
			fullURL = sensorServerURL + "?getroomid=" + URLEncoder.encode("{\"RoomName\":\"" + roomName + "\"}", "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String result = tryRequest(fullURL);
		JsonElement jElem = gson.fromJson(result, JsonElement.class);
		JsonObject obj = jElem.getAsJsonObject();
		// Remove the JSON syntax and return the room id
		String idStr = obj.get("RoomID").toString().replaceAll("^\"|\"$", "");
		int roomid = Integer.parseInt(idStr);
		return roomid;
	}
	
	public static String[] fetchRooms(String sensorServerURL) {
		String fullURL = sensorServerURL + "?getrooms";
		String result = tryRequest(fullURL).replaceAll("\\[", "").replaceAll("\\]","").replace("\"", "");
		String[] rooms = result.split(",");;
		return rooms;
	}
	
	public static String[] getTopics(String sensorServerURL) {
		String[] topics = fetchRooms(sensorServerURL); 
		for (int i = 0; i < topics.length; i++) {
			topics[i] = "/" + topics[i];
		}
		return topics;
	}

	public static String tryRequest(String fullURL) {
		String result = "";
		String line;
		try {
			URL url = new URL(fullURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static int createRandomNumberBetween(int min, int max) {

		return random.nextInt(max - min + 1) + min;
	}

	public static void waitFor(int numSeconds) {
		// pauses for numSeconds
		try {
			Thread.sleep(numSeconds * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}