package parkour;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import logic.Drive;
import logic.GUI;

/**
 * Implements the logic for the final spurt (drive as fast as possible).
 * 
 * @author Group 1
 */
public class FinalSpurt {
		
	/*
	 * Distance to the right wall when the robot movement needs to be corrected to the left.
	 */
	private static final float DISTANCE_TO_CORRECT_LEFT = 0.15f;
	
	/*
	 * Distance to the right wall when the robot movement needs to be corrected to the right.
	 */
	private static final float DISTANCE_TO_CORRECT_RIGHT = 0.20f;
	
	/*
	 *  The maximum time to reach the endboss obstacle (in seconds).
	 */
	private static final float MAXIMUM_TIME_TO_ENDBOSS = 30.0f;
	
	/*
	 * The endboss obstacle is separated from this end run obstacle by a red line.
	 */
	private static final float THRESHOLD_RED_LINE = 0.20f;
	
	/*
	 * If the program is running. 
	 */
	boolean programRunning = true;
	
	/*
	 * The navigation class.
	 */
	private Drive drive;
	
	/*
	 * The ultrasonic distance sensor.
	 */
	private EV3UltrasonicSensor sonicSensor;
	
	/*
	 * The left touch sensor.
	 */
	private EV3TouchSensor touchSensorLeft;
	
	/*
	 * The right touch sensor.
	 */
	private EV3TouchSensor touchSensorRight;
	
	/*
	 * The motor that controls the ultrasonic sensor.
	 */
	private EV3MediumRegulatedMotor sonicMotor;
	
	/*
	 * The color sensor to detect the red line.
	 */
	private EV3ColorSensor colorSensor; 
	
	
	/**
	 * Constructor.
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public FinalSpurt(Drive drive, EV3UltrasonicSensor sonicSensor, EV3TouchSensor touchLeftSensor, 
						EV3TouchSensor touchRightSensor,
						EV3MediumRegulatedMotor sonicMotor,
						EV3ColorSensor colorSensor) {
		this.drive = drive;
		this.sonicSensor = sonicSensor;
		this.touchSensorLeft = touchLeftSensor;
		this.touchSensorRight = touchRightSensor;
		this.sonicMotor = sonicMotor;
		this.colorSensor = colorSensor;
	}
	

	/**
	 * Solution for final spurt obstacle.
	 *
	 * Idea: move to the left wall and move along that as fast as possible, until
	 * the sonic sensor measures a higher distance than the width of the path. Then
	 * turn right to the next obstacle.
	 */
	public void run() {
		
		// Make sure the sonic sensor is facing sideways
		sonicMotor.setAcceleration(100);
		sonicMotor.rotate(-30);
		sonicMotor.waitComplete();
		
		SampleProvider distanceProvider = sonicSensor.getDistanceMode();
		
		long algorithmStart = System.nanoTime(); 	// Stores when the algorithm starts
		float currentColorValue = 0;
		
		this.drive.moveForward(drive.maxSpeed() * 0.97f, drive.maxSpeed());
		
		while (programRunning) {
		
			// Check the two touch sensors while the program is running
			float[] touchSensorResultsLeft = new float[touchSensorLeft.sampleSize()];
			touchSensorLeft.fetchSample(touchSensorResultsLeft, 0);
			
			float[] touchSensorResultsRight = new float[touchSensorRight.sampleSize()];
			touchSensorRight.fetchSample(touchSensorResultsRight, 0);
			
			if (touchSensorResultsLeft[0] == 1 && touchSensorResultsRight[0] == 1) {
				// Touch sensors pressed, drive back a bit and turn right
				drive.moveDistance(400, -13);
				drive.turnRight(70);
			}
				
			float[] sonicSensorResults = new float [distanceProvider.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			
			// Sonic sensor encounters a needed movement correction
			if (sonicSensorResults[0] < DISTANCE_TO_CORRECT_LEFT) {
				drive.moveForward(drive.maxSpeed() * 0.8f, drive.maxSpeed() * 1.0f);
			} else if (sonicSensorResults[0] > DISTANCE_TO_CORRECT_RIGHT) {
				drive.moveForward(drive.maxSpeed() * 1.0f, drive.maxSpeed() * 0.8f);
			}
			
			/*if (((System.nanoTime() - algorithmStart) / 1000000000.0f) > MAXIMUM_TIME_TO_ENDBOSS) {
				end();
			}*/
			
			float[] sample = new float[this.colorSensor.sampleSize()];
			this.colorSensor.fetchSample(sample, 0);
			currentColorValue = sample[0];
			
			if (currentColorValue > THRESHOLD_RED_LINE) {
				drive.stopSynchronized();
				
				// Red line detected, final spurt obstacle finished, switch to endboss
				programRunning = false;
				GUI.PROGRAM_CHANGED = true;
			 	GUI.PROGRAM_STATUS = GUI.PROGRAM_FINAL_BOSS;
			}
		}
	}
	
	
	/**
	 * Ends the algorithm.
	 */
	public void end() {
		drive.stop();
		programRunning = false;
	}
}
