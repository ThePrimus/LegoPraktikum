package parkour;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import logic.Drive;
/**
 * Implements the logic to beat the chain bridge obstacle.
 * 
 * @author Group 1
 */
public class ChainBridge {


	private static final float DISTANCE_TO_GROUND = 0.08f; // in m 
	/*
	 * The navigation class.
	 */
	private Drive drive;
	
	/*
	 * The distance between the two walls of the final spurt (measured from sonic sensor,
	 * when robot is at the left wall).
	 */
	private static final float DISTANCE_TO_TURN_RIGHT = 0.05f;

	private SampleProvider distanceProvider;

	private int standLeftCorrection;

	private int standRightCorrection;
	
	private final int SONIC_SENSOR_WALL_POS = -30;
	private final int SONIC_SENSOR_GROUND_POS = -100;

	private int standRight;

	private int standLeft;
	private EV3MediumRegulatedMotor sonicMotor;
	private boolean runStartBridge = false;
	
	

	public static boolean PROGRAM_STOP = false;
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public ChainBridge(Drive drive, EV3UltrasonicSensor sonicSensor, EV3MediumRegulatedMotor sonicMotor) {
		this.drive = drive;
		this.sonicMotor = sonicMotor;
		this.distanceProvider = sonicSensor.getDistanceMode();
		this.standLeftCorrection = (int) (drive.maxSpeed()*0.3);
		this.standRightCorrection = (int) drive.maxSpeed();
		this.standLeft = (int) (drive.maxSpeed());
		this.standRight = (int) (drive.maxSpeed()*0.9);
	}
	
	
	/**
	 * Solution for the chained bridge.
	 *
	 * Idea: move forward until the bar code is scanned, that informs about the end of the
	 * obstacle. Correct movement to left if the sonic sensor measures a high distance (abbys detected).
	 */
	public void run() {
		initSonicMotor();
		startBridgeRoutine();
		sonicMotor.rotate(SONIC_SENSOR_GROUND_POS,true);
		Delay.msDelay(1000);
		sonicMotor.stop();
	//	bridgeRoutine();
	}
	
	private void startBridgeRoutine() {
		runStartBridge  = true;
		while (runStartBridge) {
			float [] sonicSensorResults = new float[distanceProvider.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			
			if (sonicSensorResults[0] > 1) {
				runStartBridge = false;
			}
			
			if (sonicSensorResults[0] > DISTANCE_TO_TURN_RIGHT) {
				//drive.moveFor
				
			} else {
				LCD.drawString("LEFT", 0, 0);
				Delay.msDelay(1000);
			}
		}
		
	}
	
	private void initSonicMotor() {
		sonicMotor.rotate(SONIC_SENSOR_WALL_POS,true);
		Delay.msDelay(1000);
		sonicMotor.stop();
	//	sonicMotor.rotate(-SONIC_SENSOR_GROUND_POS, true);
	}
	
	private void bridgeRoutine() {
		PROGRAM_STOP = false;
		float curPos = 0;
		

		 drive.setSpeedLeftMotor(standLeft);
		 drive.setSpeedRightMotor(standRight);
		int count = 0;
		while(!PROGRAM_STOP ){
			count++;
			if(count > 100000) {
				PROGRAM_STOP = true;
			}
			float [] samples = new float[distanceProvider.sampleSize()];
			 distanceProvider.fetchSample(samples, 0);
			 curPos = samples[0];
			 

			 if(curPos > DISTANCE_TO_GROUND) {
				 drive.setSpeedLeftMotor(standLeftCorrection);
				 drive.setSpeedRightMotor(standRightCorrection);
			 } else {
				 drive.setSpeedLeftMotor(standLeft);
				 drive.setSpeedRightMotor(standRight);
			 }
		
		}
		drive.stop();
	}

	
	public void end() {
		PROGRAM_STOP = true;
		runStartBridge = true;
	}
}
