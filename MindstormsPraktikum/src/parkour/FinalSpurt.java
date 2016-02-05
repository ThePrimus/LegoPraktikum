package parkour;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.Delay;
import logic.Drive;

/**
 * Implements the logic for the final spurt (drive as fast as possible).
 * 
 * @author Group 1
 */
public class FinalSpurt implements Runnable {
	
	/*
	 * The distance between the two walls of the final spurt (measured from sonic sensor,
	 * when robot is at the left wall).
	 */
	private static final float DISTANCE_TO_TURN_RIGHT = 5.0f;
	
	/*
	 * Distance to the right wall until the robot movement needs to be corrected.
	 */
	private static final float DISTANCE_TO_CORRECT_MOVEMENT = 0.15f;
	
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
	public FinalSpurt(Drive drive, EV3UltrasonicSensor sonicSensor, EV3TouchSensor touchRightSensor,
						EV3MediumRegulatedMotor sonicMotor) {
		this.drive = drive;
		this.sonicSensor = sonicSensor;
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
	@Override
	public void run() {
		
		// Make sure the sonic sensor is facing sideways
		sonicMotor.setAcceleration(4000);
		sonicMotor.rotate(-31);
		sonicMotor.waitComplete();
		
		this.drive.moveForward(drive.maxSpeed() * 0.97f, drive.maxSpeed());
		
		boolean programRunning = true;
		
		while (programRunning) {
		
			// Check the touch sensor while the program is running
			float[] touchSensorResults = new float[touchSensorRight.sampleSize()];
			touchSensorRight.fetchSample(touchSensorResults, 0);
			
			if (touchSensorResults[0] == 1) {
				// Touch sensor pressed, drive back a bit and turn right
				drive.moveBackward(drive.maxSpeed() * 0.97f, drive.maxSpeed());
				Delay.msDelay(1500);
				drive.turnLeft(80);
			}
				
			float[] sonicSensorResults = new float [sonicSensor.sampleSize()];
			sonicSensor.fetchSample(sonicSensorResults, 0);
				
			if (sonicSensorResults[0] < DISTANCE_TO_CORRECT_MOVEMENT) {
				// Sonic sensor encounters a needed movement correction
				drive.turnLeft(20);
				//drive.stop();
				//drive.moveForward((int) (drive.maxSpeed() * 0.7), (int) (drive.maxSpeed() * 1.0));
				//Delay.msDelay(1500);
				drive.moveForward(drive.maxSpeed() * 0.97f, drive.maxSpeed());
			} else if (sonicSensorResults[0] > DISTANCE_TO_TURN_RIGHT) {
					
				Delay.msDelay(1500);
				// Sonic sensor measures a high distance, turn right to the next obstacle.
				drive.turnRight(70);
				drive.moveForward();
				Delay.msDelay(3000);
				drive.stop();
				programRunning = false;
			}
		}
	}
	
}
