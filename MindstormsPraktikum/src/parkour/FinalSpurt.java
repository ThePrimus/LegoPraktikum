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
	 * The position of the sonic motor, so that the sonic sensor faces sideways.
	 */
	private static final int SONIC_POSITION_SIDEWAYS = 50;
	
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
	 * The motor that controls the ultrasonic sensor.
	 */
	private EV3MediumRegulatedMotor sonicMotor;
	
	
	
	/**
	 * Constructor.
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public FinalSpurt(Drive drive, EV3UltrasonicSensor sonicSensor, EV3TouchSensor touchLeftSensor,
						EV3MediumRegulatedMotor sonicMotor) {
		this.drive = drive;
		this.sonicSensor = sonicSensor;
		this.touchSensorLeft = touchLeftSensor;
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
		Delay.msDelay(1000);
		
		boolean programRunning = true;
		
		while (programRunning) {
		
			// Check the touch sensor while the program is running
			/*float[] touchSensorResults = new float[touchSensorLeft.sampleSize()];
			touchSensorLeft.fetchSample(touchSensorResults, 0);
			
			if (touchSensorResults[0] == 1) {*/
				
				// Left touch sensor pressed: check the sonic sensor when to turn right
				float[] sonicSensorResults = new float [sonicSensor.sampleSize()];
				sonicSensor.fetchSample(sonicSensorResults, 0);
				
				if (sonicSensorResults[0] < 0.15f) {
					drive.turnLeft(20);
					//drive.stop();
					//drive.moveForward((int) (drive.maxSpeed() * 0.7), (int) (drive.maxSpeed() * 1.0));
					//Delay.msDelay(1500);
					drive.moveForward((int) (drive.maxSpeed() * 0.97), drive.maxSpeed());
				} else if (sonicSensorResults[0] > DISTANCE_TO_TURN_RIGHT) {
					
					// Sonic sensor measures a high distance, turn right to the next obstacle.
					drive.turnRight(90);
					drive.moveForward();
					Delay.msDelay(1000);
					drive.stop();
					programRunning = false;
				}
			//}	
		}
	}
	
}
