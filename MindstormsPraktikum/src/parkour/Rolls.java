package parkour;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import logic.Drive;
import logic.GUI;

/**
 * Implements the logic to beat the rolls obstacle.
 * 
 * @author Group 1
 */
public class Rolls {
	
	// The minimum color value of a white/silver element of the barcode.
	private final float THRESHOLD_WHITE = 0.7f;
	
	/*
	 * Distance to the right wall until the robot movement needs to be corrected.
	 */
	private static final float DISTANCE_TO_TURN_LEFT = 0.07f;
	
	/*
	 * Distance to the right wall until the robot movement needs to be corrected.
	 */
	private static final float DISTANCE_TO_TURN_RIGHT = 0.09f;
	
	// It the loop to solve the obstacle should run or not. Can be used to terminate
	// the algorithm.
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
	 * The motor that controls the ultrasonic sensor.
	 */
	private EV3MediumRegulatedMotor sonicMotor;
	
	/*
	 * The color sensor to detect the end of the rolls obstacle.
	 */
	private EV3ColorSensor colorSensor;
	
	
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public Rolls(Drive drive, EV3UltrasonicSensor sonicSensor, EV3MediumRegulatedMotor sonicMotor, 
						EV3ColorSensor colorSensor) {
		this.drive = drive;
		this.sonicSensor = sonicSensor;
		this.sonicMotor = sonicMotor;
		this.colorSensor = colorSensor;
		
		this.colorSensor.setCurrentMode("Red");
	}
	
	
	/**
	 * Solution for the rolls/swamp obstacle.
	 *
	 * Idea: move forward until the bar code is scanned, that informs about the end of the
	 * obstacle. Correct movement to left if a low distance to the right wall is measured.
	 */
	public void run() {
		
		float currentColorValue = 0;
		
		// Make sure the sonic sensor is facing sideways
		/*sonicMotor.setAcceleration(2000);
		sonicMotor.rotate(-31);
		sonicMotor.waitComplete();*/
		
		this.drive.moveForward(drive.maxSpeed() * 0.5f, drive.maxSpeed() * 0.5f);
		
		while (programRunning) {
			
			float[] sonicSensorResults = new float [sonicSensor.sampleSize()];
			sonicSensor.fetchSample(sonicSensorResults, 0);
				
			if (sonicSensorResults[0] < DISTANCE_TO_TURN_LEFT) {
				// Sonic sensor encounters a needed movement correction
				//drive.turnLeft(7);
				drive.moveForward(drive.maxSpeed() * 0.5f, drive.maxSpeed() * 0.7f);
			} else if (sonicSensorResults[0] > DISTANCE_TO_TURN_RIGHT) {
				drive.moveForward(drive.maxSpeed() * 0.7f, drive.maxSpeed() * 0.5f);
			} else if (sonicSensorResults[0] > DISTANCE_TO_TURN_RIGHT + 0.02f) {
				drive.stopSynchronized();
			}
			
			// Getting the current color value from the sensor
			float[] sample = new float[this.colorSensor.sampleSize()];
			this.colorSensor.fetchSample(sample, 0);
			currentColorValue = sample[0] * 1.25f;
			
			if (currentColorValue > THRESHOLD_WHITE) {
				drive.stopSynchronized();
				
				// White/silver line detected => rolls obstacle finished, move a few cm back,
				// because distance rolls obstacle to barcode is very short. Then switch to barcode
				// to scan it.
				drive.moveDistance(300, -15);
				programRunning = false;
				GUI.PROGRAM_FINISHED_START_BARCODE = true;
			}
		}
	}
	
	
	/**
	 * Ends the rolls program.
	 */
	public void end() {
		drive.stopSynchronized();
		programRunning = false;
	}
}
