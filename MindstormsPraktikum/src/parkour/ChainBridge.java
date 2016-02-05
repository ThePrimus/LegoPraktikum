package parkour;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import logic.Drive;

/**
 * Implements the logic to beat the chain bridge obstacle.
 * 
 * @author Group 1
 */
public class ChainBridge implements Runnable {

	/*
	 * The minimum distance that defines an abbys.
	 */
	private static final float DISTANCE_ABBYS = 0.2f;
	
	/*
	 * The navigation class.
	 */
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
	public ChainBridge(Drive drive, EV3UltrasonicSensor sonicSensor, EV3MediumRegulatedMotor sonicMotor) {
		this.drive = drive;
		this.sonicSensor = sonicSensor;
		this.sonicMotor = sonicMotor;
	}
	
	
	/**
	 * Solution for the chained bridge.
	 *
	 * Idea: move forward until the bar code is scanned, that informs about the end of the
	 * obstacle. Correct movement to left if the sonic sensor measures a high distance (abbys detected).
	 */
	@Override
	public void run() {
		
		// Make sure the sonic sensor is facing downwards
		//sonicMotor.rotateTo(SONIC_POSITION_SIDEWAYS, true);
		//sonicMotor.waitComplete();
		
		this.drive.moveForward((int) (drive.maxSpeed() * 0.97), drive.maxSpeed());
		
		boolean programRunning = true;
				
		while (programRunning) {
			
			float[] sonicSensorResults = new float [sonicSensor.sampleSize()];
			sonicSensor.fetchSample(sonicSensorResults, 0);
			
			if (sonicSensorResults[0] < DISTANCE_ABBYS) {
				// Sonic sensor encounters an abbys, correct movement
				drive.turnLeft(20);
				drive.moveForward((int) (drive.maxSpeed() * 0.97), drive.maxSpeed());
			} /*else if (// TODO: Rolls obstacle finished, when bar code is scanned) {
			}*/
		}
	}
	
}
