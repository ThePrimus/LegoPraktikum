package parkour;

import java.awt.Button;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import logic.Drive;

/**
 * Implements the logic to beat the bridge obstacle.
 * 
 * @author Group 1
 */
public class Bridge implements Runnable {

	private static final float DISTANCE_TO_GROUND = 0.15f; // in m 
	// The navigation class.
	private Drive drive;
	private EV3MediumRegulatedMotor sonicMotor;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private EV3UltrasonicSensor sonicSensor;
	
	private final int SONIC_SENSOR_WALL_POS = -30;
	private final int SONIC_SENSOR_GROUND_POS = -100;
	private SampleProvider distanceProvider;
	
	/*
	 * The distance between the two walls of the final spurt (measured from sonic sensor,
	 * when robot is at the left wall).
	 */
	private static final float DISTANCE_TO_TURN_RIGHT = 0.05f;
	
	
	
	public int standLeft;
	public int standRight;
	public int standRightCorrection;
	private boolean running = true;
	private boolean runStartBridge = true;
	private int standLeftCorrection;
	
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public Bridge(Drive drive) {
		this.drive = drive;
	}
	
	public Bridge(Drive drive, EV3MediumRegulatedMotor sonicMotor, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, EV3UltrasonicSensor sonicSensor){
		this.drive = drive;
		this.sonicMotor = sonicMotor;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.sonicSensor = sonicSensor;
		this.distanceProvider = sonicSensor.getDistanceMode();
		this.standLeftCorrection = (int) drive.maxSpeed() / 5;
		this.standRightCorrection = (int) drive.maxSpeed();
		this.standLeft = (int) (drive.maxSpeed());
		this.standRight = (int) (drive.maxSpeed() / 5);
	}
	



	private void resetSonicMotor() {
		sonicMotor.rotate(-SONIC_SENSOR_GROUND_POS,true);
		Delay.msDelay(1000);
		sonicMotor.rotate(-SONIC_SENSOR_WALL_POS, true);
		Delay.msDelay(1000);
		sonicMotor.stop();		
	}

	private void bridgeRoutine() {
		float curPos = 0;
		int count = 0;
		drive.moveForward(standLeft, standRight);
		LCD.clear();
		while(count < 10){
			drive.stop();
			count++;
			float [] samples = new float[distanceProvider.sampleSize()];
			 distanceProvider.fetchSample(samples, 0);
			 
			 curPos = samples[0];
			 LCD.drawString(String.valueOf(curPos),0,0);
			 LCD.clear();
			 Delay.msDelay(1000);
			 if(curPos > DISTANCE_TO_GROUND) {
				 drive.moveForward(standLeftCorrection, standRightCorrection);
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
		sonicMotor.stop();
	//	sonicMotor.rotate(-SONIC_SENSOR_GROUND_POS, true);
	}

	@Override
	public void run() {
	//	initSonicMotor();
	//	test();

		
	//	resetSonicMotor();
	//	startBridgeRoutine();
		bridgeRoutine();
	}

	private void test() {
		float [] samples = new float[distanceProvider.sampleSize()];
		for(int i = 0; i <= 10; i++) {
			 distanceProvider.fetchSample(samples, 0);
			 LCD.drawString(String.valueOf(samples[0]), 0, 0);
			 Delay.msDelay(1000);
			 LCD.clear();
		}		
	}

	private void startBridgeRoutine() {
		while (runStartBridge) {
			float [] sonicSensorResults = new float[distanceProvider.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			
			if (sonicSensorResults[0] > 1) {
				runStartBridge = false;
			}
			
			if (sonicSensorResults[0] > DISTANCE_TO_TURN_RIGHT) {
				LCD.drawString("Right", 0, 0);
				Delay.msDelay(1000);
				
			} else {
				LCD.drawString("LEFT", 0, 0);
				Delay.msDelay(1000);
			}
		}
		
	}
}
