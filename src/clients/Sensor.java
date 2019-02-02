package clients;

public class Sensor {

	int UserID;
	int RoomID;
	String TagID;
	String RoomName;
	String SensorName;
	String TimeInserted;
	boolean Locked;
	String SuccessFailure;


	public Sensor(int UserID, int RoomID, String TagID, String RoomName, String SensorName, String TimeInserted,  boolean Locked, String SuccessFailure) {
		super();
		this.UserID = UserID;
		this.RoomID = RoomID;
		this.TagID = TagID;
		this.RoomName = RoomName;
		this.SensorName = SensorName;
		this.TimeInserted = TimeInserted;
		this.Locked = Locked;
		this.SuccessFailure = SuccessFailure;
	}

	public Sensor(int UserID, int RoomID, String TagID, String SensorName) {
		super();
		this.UserID = UserID;
		this.RoomID = RoomID;
		this.TagID = TagID;
		this.RoomName = "unknown";
		this.SensorName = SensorName;
		this.TimeInserted = "unknown";
		this.Locked = true; // locked by default
		this.SuccessFailure = "unknown";
	}

	public Sensor(String SensorName) {
		super();
		this.UserID = 0;
		this.RoomID = 0;
		this.TagID = "unknown";
		this.RoomName = "unknown";
		this.SensorName = SensorName;
		this.TimeInserted = "unknown";
		this.Locked = true; //locked by default
		this.SuccessFailure = "unknown"; 
	}
	
	public Sensor() {
		super();
		this.UserID = 0;
		this.RoomID = 0;
		this.TagID = "unknown";
		this.RoomName = "unknown";
		this.SensorName = "unknown";
		this.TimeInserted = "unknown";
		this.Locked = true; // Locked by default
		this.SuccessFailure = "unknown";
	}

	public int getUserID() {
		return UserID;
	}

	public void setUserID(int UserID) {
		this.UserID = UserID;
	}
	
	public int getRoomID() {
		return RoomID;
	}

	public void setRoomID(int RoomID) {
		this.RoomID = RoomID;
	}
	
	public String getTagID() {
		return TagID;
	}

	public void setTagID(String TagID) {
		this.TagID = TagID;
	}
	
	public String getRoomName() {
		return RoomName;
	}

	public void setRoomName(String RoomName) {
		this.RoomName = RoomName;
	}
	
	
	public String getSensorName() {
		return SensorName;
	}

	public void setSensorName(String SensorName) {
		this.SensorName = SensorName;
	}
	
	public String getTimeInserted() {
		return TimeInserted;
	}

	public void setTimeInserted(String SensorValue) {
		this.TimeInserted = SensorValue;
	}
	
	public boolean getLocked() {
		return Locked;
	}

	public void setLocked(boolean Locked) {
		this.Locked = Locked;
	}

	
	public String getSuccessFailure() {
		return SuccessFailure;
	}

	public void setSuccessFailure(String SuccessFailure) {
		this.SuccessFailure = SuccessFailure;
	}


	@Override
	public String toString() {
		return "Sensor [UserID="+ UserID + ", RoomID=" + RoomID + ", TagID=" + TagID + " SensorName=" + SensorName +
				", TimeInserted=" + TimeInserted + ", Success=" + SuccessFailure + ", Locked=" + Locked + "]";
	}

}
