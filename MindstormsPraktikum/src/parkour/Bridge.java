package parkour;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import logic.Drive;

/**
 * Implements the logic to beat the bridge obstacle.
 * 
 * @author Group 1
 */
public class Bridge {

	private static final float DISTANCE_TO_GROUND = 0.010f; // in m 
	// The navigation class.
	private Drive drive;
	private EV3MediumRegulatedMotor sonicMotor;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private EV3UltrasonicSensor sonicSensor;
	
	
	private final int SONIC_SENSOR_WALL_POS = -30;
	private final int SONIC_SENSOR_GROUND_POS = -90;
	private SampleProvider distanceProvider;
	
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public Bridge(Drive drive) {
		this.drive = drive;
	}
	
	public Bridge(Drive drive, EV3MediumRegulatedMotor sonicMotor, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, EV3UltrasonicSensor sonicSensor ){
		this.drive = drive;
		this.sonicMotor = sonicMotor;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.sonicSensor = sonicSensor;
		this.distanceProvider = sonicSensor.getDistanceMode();
	}
	
	public int standLeft = 100;
	public int standRight = 80;
	public int standRightCorrection = 120;
	
	public void run(){
		initSonicMotor();
		float [] samples = new float[distanceProvider.sampleSize()];
		for(int i = 0; i <= 10; i++) {
			 distanceProvider.fetchSample(samples, 0);
			 LCD.drawString(String.valueOf(samples[0]), 0, 0);
			 Delay.msDelay(1000);
		}
		
		bridgeRoutine();
	}



	private void bridgeRoutine() {
		float [] samples = new float[distanceProvider.sampleSize()];
		float curPos = 0;
		while(true){
			 distanceProvider.fetchSample(samples, 0);
			 curPos = samples[0];
			 if(curPos > DISTANCE_TO_GROUND) {
				 drive.setSpeedLeftMotor(standRightCorrection);
			 } else {
				 drive.moveForward(standLeft, standRight);	
			 }
		}
		
	}

	private void sonicTest() {
		float [] samples = new float[distanceProvider.sampleSize()];
		for(int i = 0; i <= 10; i++) {
			distanceProvider.fetchSample(samples, 0);
			LCD.drawString(String.valueOf(samples[0]), 0,0);
			Delay.msDelay(1000);
			LCD.clear();
		}
		
	}
	
	

	private void initSonicMotor() {
		sonicMotor.rotate(SONIC_SENSOR_WALL_POS,true);
		Delay.msDelay(1000);
		sonicMotor.rotate(SONIC_SENSOR_GROUND_POS, true);
		Delay.msDelay(1000);
	//	sonicMotor.rotate(-SONIC_SENSOR_GROUND_POS, true);
	}
}
