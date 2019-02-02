package clients;

import com.phidget22.PhidgetException;
import com.phidget22.RCServo;

public class Motor {
	static RCServo rcServo;  
    
	public static void main(String[] args) throws Exception {        
        rcServo = new RCServo();
	}
	
	public Motor(RCServo rcServo) {
		try {
			rcServo = new RCServo();
		} catch (PhidgetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public void openCloseDoor(RCServo rcServo) throws Exception{
    	rcServo = new RCServo();

    	rcServo.open(5000);
    	rcServo.setTargetPosition(0);
    	rcServo.setEngaged(true);
    	
    	try {
        	rcServo.setTargetPosition(180); // Opens the door
        	Thread.sleep(5000); // waits 5 seconds
        	
        	rcServo.setTargetPosition(0); //closes the door
        	Thread.sleep(3000);
        	
    	} catch (PhidgetException e) {
    		System.out.println(e.getDescription());
    	}
    	rcServo.close();
    }
    
    public void closeLock(RCServo rcServo) throws Exception {
    	rcMove(180, rcServo);
    }
    
    public void openLock(RCServo rcServo) throws Exception {
    	rcMove(0, rcServo);
    }
    
    public void rcMove(double angle, RCServo rcServo) throws Exception{
    	rcServo = new RCServo();
    	rcServo.open(4000);
		rcServo.setTargetPosition(angle);
    	rcServo.setEngaged(true);
    	Thread.sleep(4000);
    	rcServo.close();
    }
    
    public int getSerialNum(RCServo rcServo) {
    	try {
        	rcServo.open(500);
        	System.out.println("Fetching Motor Serial Number");
			int ser = rcServo.getDeviceSerialNumber();
	    	rcServo.close();
	    	return ser;
		} catch (PhidgetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0; // default answer
    }
}
