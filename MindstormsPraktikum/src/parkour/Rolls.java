package parkour;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import logic.Drive;

/**
 * Implements the logic to beat the rolls obstacle.
 * 
 * @author Group 1
 */
public class Rolls implements Runnable {
	
	/*
	 * Distance to the right wall until the robot movement needs to be corrected.
	 */
	private static final float DISTANCE_TO_CORRECT_MOVEMENT = 0.10f;
	
	// The navigation class.
	private Drive drive;
	
	/*
	 * The ultrasonic distance sensor.
	 */
	private EV3UltrasonicSensor sonicSensor;
	
	/*
	 * The motor that controls the ultrasonic sensor.
	 */
	private EV3MediumRegulatedMotor sonicMotor;
	
	
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public Rolls(Drive drive, EV3UltrasonicSensor sonicSensor, EV3MediumRegulatedMotor sonicMotor) {
		this.drive = drive;
		this.sonicSensor = sonicSensor;
		this.sonicMotor = sonicMotor;
	}
	
	
	/**
	 * Solution for final spurt obstacle.
	 *
	 * Idea: move to the left wall and move along that as fast as possible, until
	 * the sonic sensor measures a higher distance than the width of the path. Then
	 * turn right to the next obstacle.
	 */
	@Override
	public void run() {
		
		// Make sure the sonic sensor is facing sideways
		//sonicMotor.rotateTo(SONIC_POSITION_SIDEWAYS, true);
		//sonicMotor.waitComplete();
		
		this.drive.moveForward((int) (drive.maxSpeed() * 0.97), drive.maxSpeed());
		
		boolean programRunning = true;
		
		while (programRunning) {
			
			float[] sonicSensorResults = new float [sonicSensor.sampleSize()];
			sonicSensor.fetchSample(sonicSensorResults, 0);
				
			if (sonicSensorResults[0] < DISTANCE_TO_CORRECT_MOVEMENT) {
				// Sonic sensor encounters a needed movement correction
				drive.turnLeft(20);
				drive.moveForward((int) (drive.maxSpeed() * 0.97), drive.maxSpeed());
			} /*else if (// TODO: Rolls obstacle finished, when bar code is scanned) {
			}*/
		}
	}
	
	
}
