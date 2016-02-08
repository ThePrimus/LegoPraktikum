package parkour;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import logic.Drive;

/**
 * Implements the logic for the final spurt (drive as fast as possible).
 * 
 * @author Group 1
 */
public class FinalSpurt {
	
	/*
	 * The distance between the two walls of the final spurt (measured from sonic sensor,
	 * when robot is at the left wall).
	 */
	private static final float DISTANCE_TO_TURN_RIGHT = 5.0f;
	
	/*
	 * Distance to the right wall when the robot movement needs to be corrected to the left.
	 */
	private static final float DISTANCE_TO_CORRECT_LEFT = 0.15f;
	
	/*
	 * Distance to the right wall when the robot movement needs to be corrected to the right.
	 */
	private static final float DISTANCE_TO_CORRECT_RIGHT = 0.20f;
	
	/*
	 *  The maximum time that the barcode algorithm has time to search for a barccode (in seconds).
	 */
	private static final float MAXIMUM_TIME_TO_ENDBOSS = 30.0f;
	
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
	
	
	
	/**
	 * Constructor.
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public FinalSpurt(Drive drive, EV3UltrasonicSensor sonicSensor, EV3TouchSensor touchLeftSensor, 
						EV3TouchSensor touchRightSensor,
						EV3MediumRegulatedMotor sonicMotor) {
		this.drive = drive;
		this.sonicSensor = sonicSensor;
		this.touchSensorLeft = touchLeftSensor;
		this.touchSensorRight = touchRightSensor;
		this.sonicMotor = sonicMotor;
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
		/*sonicMotor.setAcceleration(4000);
		sonicMotor.rotate(-31);
		sonicMotor.waitComplete();*/
		
		SampleProvider distanceProvider = sonicSensor.getDistanceMode();
		
		long algorithmStart = System.nanoTime(); 	// Stores when the algorithm starts
		
		this.drive.moveForward(drive.maxSpeed() * 0.97f, drive.maxSpeed());
		
		while (programRunning) {
		
			// Check the two touch sensors while the program is running
			float[] touchSensorResultsLeft = new float[touchSensorLeft.sampleSize()];
			touchSensorLeft.fetchSample(touchSensorResultsLeft, 0);
			
			float[] touchSensorResultsRight = new float[touchSensorRight.sampleSize()];
			touchSensorRight.fetchSample(touchSensorResultsRight, 0);
			
			if (touchSensorResultsLeft[0] == 1 && touchSensorResultsRight[0] == 1) {
				// Touch sensors pressed, drive back a bit and turn right
				drive.moveDistance(400, -15);
				drive.turnRight(70);
			}
				
			float[] sonicSensorResults = new float [distanceProvider.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			
			// Sonic sensor encounters a needed movement correction
			if (sonicSensorResults[0] < DISTANCE_TO_CORRECT_LEFT) {
				drive.moveForward(drive.maxSpeed() * 0.85f, drive.maxSpeed() * 1.0f);
			} else if (sonicSensorResults[0] > DISTANCE_TO_CORRECT_RIGHT) {
				drive.moveForward(drive.maxSpeed() * 1.0f, drive.maxSpeed() * 0.85f);
			}
			
			if (((System.nanoTime() - algorithmStart) / 1000000000.0f) > MAXIMUM_TIME_TO_ENDBOSS) {
				end();
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
