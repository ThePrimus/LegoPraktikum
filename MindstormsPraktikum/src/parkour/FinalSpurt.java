package parkour;

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
	private static final float DISTANCE_TO_TURN_RIGHT = 0.50f;
	
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
	
	
	
	/**
	 * Constructor.
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public FinalSpurt(Drive drive, EV3UltrasonicSensor sonicSensor, EV3TouchSensor touchLeftSensor) {
		this.drive = drive;
		this.sonicSensor = sonicSensor;
		this.touchSensorLeft = touchLeftSensor;
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
		
		this.drive.moveForward((int) (drive.maxSpeed() * 0.95), drive.maxSpeed());
		
		boolean programRunning = true;
		
		while (programRunning) {
		
			// Check the touch sensor while the program is running
			float[] touchSensorResults = new float[touchSensorLeft.sampleSize()];
			touchSensorLeft.fetchSample(touchSensorResults, 0);
			
			if (touchSensorResults[0] == 1) {
				
				// Left touch sensor pressed: check the sonic sensor when to turn right
				float[] sonicSensorResults = new float [sonicSensor.sampleSize()];
				sonicSensor.fetchSample(sonicSensorResults, 0);
				
				if (sonicSensorResults[0] > DISTANCE_TO_TURN_RIGHT) {
					
					// Sonic sensor measures a high distance, turn right to the next obstacle.
					drive.turnRight(90);
					drive.moveForward();
					Delay.msDelay(1000);
					drive.stop();
					programRunning = false;
				}
			}	
		}
	}
	
}
