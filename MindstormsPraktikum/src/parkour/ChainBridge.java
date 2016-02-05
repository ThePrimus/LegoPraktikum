package parkour;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
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


	private static final float DISTANCE_TO_GROUND = 0.15f; // in m 
	/*
	 * The navigation class.
	 */
	private Drive drive;
	
	/*
	 * The distance between the two walls of the final spurt (measured from sonic sensor,
	 * when robot is at the left wall).
	 */
	private static final float DISTANCE_TO_TURN_RIGHT = 0.12f;

	private SampleProvider distanceProvider;

	private int standLeftCorrection;

	private int standRightCorrection;
	
	private final int SONIC_SENSOR_WALL_POS = -30;
	private final int SONIC_SENSOR_GROUND_POS = -90;

	private int standRight;

	private int standLeft;
	private EV3MediumRegulatedMotor sonicMotor;
	private boolean runStartBridge = false;
	private boolean runColorFollow = false;
	private EV3ColorSensor colorSensor;
	private boolean runStartBridge2 = false;
	private boolean runEndBridge =  false;
	
	

	public static boolean PROGRAM_STOP = false;
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public ChainBridge(Drive drive, EV3UltrasonicSensor sonicSensor, EV3MediumRegulatedMotor sonicMotor, EV3ColorSensor sensor) {
		this.drive = drive;
		this.sonicMotor = sonicMotor;
		this.distanceProvider = sonicSensor.getDistanceMode();
		this.colorSensor = sensor;
		this.standLeftCorrection = (int) (drive.maxSpeed()*0.3);
		this.standRightCorrection = (int) drive.maxSpeed();
		this.standLeft = (int) (drive.maxSpeed());
		this.standRight = (int) (drive.maxSpeed()*0.3);
		this.runStartBridge = true;
	}
	
	
	/**
	 * Solution for the chained bridge.
	 *
	 * Idea: move forward until the bar code is scanned, that informs about the end of the
	 * obstacle. Correct movement to left if the sonic sensor measures a high distance (abbys detected).
	 */
	public void run() {
		sonicMotor.setAcceleration(1000);
		sonicMotor.rotate(SONIC_SENSOR_WALL_POS,true);
		Delay.msDelay(500);
//		linefollowing();
		startBridgeRoutine();
		bridgeRoutine2();
	//	endBridgeRoutine();
	//	sonicMotor.rotate(SONIC_SENSOR_GROUND_POS,true);
	//	bridgeRoutine();
	//	sonicMotor.rotate(-(SONIC_SENSOR_GROUND_POS+SONIC_SENSOR_WALL_POS),true);
	//	Delay.msDelay(1000);
	}
	
	private void endBridgeRoutine() {
		drive.stop();
		Delay.msDelay(5000);
		while (runEndBridge) {
			float [] sonicSensorResults = new float[distanceProvider.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			float curPos = sonicSensorResults[0];
			
			
			if (curPos > DISTANCE_TO_TURN_RIGHT) {
				drive.moveForward((int)(drive.maxSpeed()*0.3), (int)(drive.maxSpeed()*0.2));
			} else {
				drive.moveForward((int)(drive.maxSpeed()*0.2), (int)(drive.maxSpeed()*0.3));
			}
		}
	}


	private void bridgeRoutine2() {
		runStartBridge2  = true;
		drive.setAcceleration(2000);
		drive.moveForward((int)(drive.maxSpeed()*0.6), (int)(drive.maxSpeed()*0.6));
		int count = 0;
		
		while (runStartBridge2) {
			
			if (count > 1000) {
				runStartBridge2 = false;
				break;
			} 
			float [] sonicSensorResults = new float[distanceProvider.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			float curPos = sonicSensorResults[0];
			if(curPos < 0.3) {
				count++;
			} else {
				drive.moveForward((int)(drive.maxSpeed()*0.6), (int)(drive.maxSpeed()*0.6));
			}
			
				
		}

		runEndBridge  = true;
	}


	private void linefollowing() {
		runColorFollow = true;
		colorSensor.setCurrentMode("Red");
		boolean lineFound = false;
		while(runColorFollow) {
			float [] colorResults = new float[colorSensor.sampleSize()];
			colorSensor.fetchSample(colorResults, 0);
			float curColor = colorResults[0]; 
			
			float [] sonicSensorResults = new float[distanceProvider.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			float curPos = sonicSensorResults[0];
			
			if (curPos < 0.3) {
				drive.stop();
				runColorFollow = false;
			}
			
			
			if(curColor > 0.5) {
				drive.moveForward((float) (drive.maxSpeed()*0.3), (float) (drive.maxSpeed()*0));
			} else {
				drive.moveForward((float) (drive.maxSpeed()*0), (float) (drive.maxSpeed()*0.3));
			}

			
		}
		
	}


	private void startBridgeRoutine() {
		runStartBridge  = true;
		while (runStartBridge) {
			float [] sonicSensorResults = new float[distanceProvider.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			float curPos = sonicSensorResults[0];
			
			if (curPos > 0.8) {
				drive.stop();
				Delay.msDelay(3000);
				runStartBridge = false;
				break;
			}
			
			
			if (curPos > DISTANCE_TO_TURN_RIGHT) {
				drive.moveForward((int)(drive.maxSpeed()*0.3), (int)(drive.maxSpeed()*0.2));
			} else {
				drive.moveForward((int)(drive.maxSpeed()*0.2), (int)(drive.maxSpeed()*0.3));
			}
		}

		runStartBridge2  = true;
		
	}

	
	private void bridgeRoutine() {
		runStartBridge = true;
		PROGRAM_STOP = false;
		
		drive.moveForward((int)(drive.maxSpeed()*1),(int)(drive.maxSpeed()*1));
		Delay.msDelay(1000);
		float curPos = 0;
		
		int count = 0;
		while(!PROGRAM_STOP ){
			count++;
			if(count > 40000) {
				PROGRAM_STOP = true;
			}
			float [] samples = new float[distanceProvider.sampleSize()];
			 distanceProvider.fetchSample(samples, 0);
			 curPos = samples[0];
			 

			 if(curPos > DISTANCE_TO_GROUND) {
				 drive.setSpeedLeftMotor((int)(drive.maxSpeed()*0.8));
				 drive.setSpeedRightMotor((int)(drive.maxSpeed()*1));
			 } else {
				 drive.setSpeedLeftMotor((int)(drive.maxSpeed()*0.3));
				 drive.setSpeedRightMotor((int)(drive.maxSpeed()*1));
			 }
		
		}
		drive.stop();
	}

	
	public void end() {
		PROGRAM_STOP = true;
		runStartBridge = false;
		runStartBridge2 = false;
		runEndBridge  = false;
		runColorFollow  = false;
		drive.stop();
	}
}
