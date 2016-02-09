package parkour;

import lejos.hardware.BrickFinder;
import lejos.hardware.Key;
import lejos.hardware.Keys;
import lejos.hardware.Sound;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;
import lejos.utility.Delay;
import lejos.utility.SensorSelectorException;
import logic.Drive;
import logic.GUI;
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
	 * The distance between the two walls of the final spurt (measured from
	 * sonic sensor, when robot is at the left wall).
	 */
	private static final float DISTANCE_TO_WALL = 0.12f;

	private SampleProvider distanceProvider;

	private final int SONIC_SENSOR_WALL_POS = -30;
	private final int SONIC_SENSOR_GROUND_POS = -100;
	private final int GAP_TIME_DIFF = 4;
	private EV3MediumRegulatedMotor sonicMotor;
	private boolean runStartBridge = true;
	private boolean runColorFollow = true;
	private EV3ColorSensor colorSensor;
	private boolean runBridgeRoutine2 = true;
	private boolean runBridgeRoutine3 = true;
	private boolean runEndBridge = true;
	public static boolean runBridgeRoutine1 = true;
	
	private float gapFound = 0;
	private SensorMode colorProvider;

	/**
	 * Constructor:
	 * 
	 * @param drive
	 *            the drive class for navigation and motor control.
	 */
	public ChainBridge(Drive drive, EV3UltrasonicSensor sonicSensor,
			EV3MediumRegulatedMotor sonicMotor, EV3ColorSensor sensor) {
		this.drive = drive;
		this.sonicMotor = sonicMotor;
		this.distanceProvider = sonicSensor.getDistanceMode();
		this.colorSensor = sensor;
		this.colorProvider = sensor.getRedMode();
	}

	/**
	 * Solution for the chained bridge.
	 *
	 * Idea: move forward until the bar code is scanned, that informs about the
	 * end of the obstacle. Correct movement to left if the sonic sensor
	 * measures a high distance (abbys detected).
	 */
	public void run() {

		idea3();

		// TODO: Missing: move sonic sensor back to initial position
	}

	/*
	 * Klappe Sensor auf waagrechte Höhe, folge dann der Wand in einem
	 * bestimmten Abstand bis die Wand nicht mehr erkannt wird. Klappe dann den
	 * Sensor schräg zum Boden/Wand, damit er einen Abstand zur übrigen Wand als
	 * auch (hoffentlich zum Abgrund der Brücke erkennt) Folgt dann nach auch am
	 * Ende weiterhin der Wand bis er am ende (falls keine wand mehr da ist)
	 * entweder darüber es erkennt stehen zu bleiben oder beim Anfang des
	 * Barcodes (ersteres ist sicher fehleranfälliger)
	 */
	private void idea3() {
		sonicMotor.setAcceleration(100);
		sonicMotor.rotate(SONIC_SENSOR_WALL_POS - 2, true);
		sonicMotor.waitComplete(); // short wait to make
							// sure that it's in the
							// right position
		startBridgeRoutine();
	
		sonicMotor.setAcceleration(100);

		bridgeRoutine3();

		sonicMotor.setAcceleration(100);
		sonicMotor.rotate(-SONIC_SENSOR_WALL_POS + 2, true);
		GUI.PROGRAM_FINISHED_START_BARCODE = true;
	}

	private void startBridgeRoutine() {
		while (runStartBridge) {
			// get current distance
			float[] sonicSensorResults = new float[distanceProvider
					.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			float curPos = sonicSensorResults[0];
	
			// wall can't be detected therefore it's lower then the sensor
			if (curPos > 0.2) {
				drive.stop();
				break;
			} else if (curPos > 0.10) { // move right if robot is to far
												// from wall
				drive.moveForward((drive.maxSpeed() * 0.3f),
						(drive.maxSpeed() * 0.2f));
	
			} else {// move left if if to close to wall
				drive.moveForward((drive.maxSpeed() * 0.2f),
						(drive.maxSpeed() * 0.3f));
			}
		}
	}

	private void bridgeRoutine3() {
		// It should follow remaining wall and the chainbridge
		// I hope it will follow also the start of the end part of the wall
		// should be tested
		
		drive.setAcceleration(1000);
	//	drive.moveForward(500,650);
		
		drive.moveForward(300, 302);
		Delay.msDelay(2000);
		runBridgeRoutine3 = true;
		runColorFollow = true;
		
		while(runColorFollow){
			float[] sonicSensorResults = new float[distanceProvider
			                   					.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			                   			float curPos = sonicSensorResults[0];
	
			  // wall can't be detected therefore it's lower then the sensor
			    if (curPos < 0.2) {
			   	//	drive.stop();
			    	drive.moveForward(280, 700);
			    	//Delay.msDelay(1000);
			    	runColorFollow = false;
			       	break;
				}
		}
	
		MedianFilter filter = new MedianFilter(distanceProvider, 5);
		while (runBridgeRoutine3) {		
			
			// get current distance
			float[] sonicSensorResults = new float[filter
					.sampleSize()];
			filter.fetchSample(sonicSensorResults, 0);
			float curPos = sonicSensorResults[0];
			
			
			if(curPos > 0.20) {
				drive.stopSynchronized();
				break;
			}
	
			if (curPos > 0.12) { // move right if robot is to far
												// from wall
				drive.moveForward((drive.maxSpeed() * 0.3f),
						(drive.maxSpeed() * 0.2f));
	
			} else {// move left if if to close to wall
				drive.moveForward((drive.maxSpeed() * 0.2f),
						(drive.maxSpeed() * 0.3f));
			}
		}
		drive.turnRight(15, false);
		
	//	drive.moveDistance(300, -7);
	
	}



	private void linefollowing() {
		runColorFollow = true;
		colorSensor.setCurrentMode("Red");
		while (runColorFollow) {
			float[] colorResults = new float[colorSensor.sampleSize()];
			colorSensor.fetchSample(colorResults, 0);
			float curColor = colorResults[0];

			float[] sonicSensorResults = new float[distanceProvider
					.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			float curPos = sonicSensorResults[0];

			if (curPos < 0.2) {
				drive.stop();
				runColorFollow = false;
				break;
			}

			if (curColor > 0.15) {
				drive.moveForward(drive.maxSpeed() * 0.7f,
						drive.maxSpeed() * 0.1f);
			} else  if (curColor < 0.1){
				drive.moveForward(drive.maxSpeed() * 0.1f,
						drive.maxSpeed() * 0.5f);
			} else {
				drive.moveForward(drive.maxSpeed() * 0.3f,
						drive.maxSpeed() * 0.1f);
			}

		}

	}

	public void end() {
		runBridgeRoutine1 = false;
		runStartBridge = false;
		runBridgeRoutine2 = false;
		runBridgeRoutine3 = false;
		runEndBridge = false;
		runColorFollow = false;
		drive.stop();
	}
}
