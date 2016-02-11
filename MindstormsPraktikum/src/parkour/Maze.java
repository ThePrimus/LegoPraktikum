package parkour;

import logic.Drive;
import logic.GUI;
import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;

/**
 * Implements the logic to beat the maze obstacle.
 * 
 * @author Group 1
 */
public class Maze {

	/*
	 * The minimum color value of a white/silver element of the barcode.
	 */
	private final float THRESHOLD_WHITE = 0.7f;
	
	/*
	 * The left touch sensor.
	 */
	private EV3TouchSensor touchSensorLeft;
	
	/*
	 * The right touch sensor.
	 */
	private EV3TouchSensor touchSensorRight;
	
	// The navigation class.
	private Drive drive;
	private EV3UltrasonicSensor sonicSensor;
	private EV3MediumRegulatedMotor sonicMotor;	
	private EV3ColorSensor colorSensor;
	
	// If the program is running
	private boolean programRunning = true;
	
	
	/**
	 * Constructor
	 */
	public Maze(Drive drive, EV3UltrasonicSensor sonicSensor, EV3MediumRegulatedMotor sonicMotor, 
					EV3TouchSensor touchLeftSensor, EV3TouchSensor touchRightSensor,
					EV3ColorSensor colorSensor) {
		
		this.drive = drive;
		this.sonicSensor = sonicSensor;
		this.sonicMotor = sonicMotor;
		this.touchSensorLeft = touchLeftSensor;
		this.touchSensorRight = touchRightSensor;
		this.colorSensor = colorSensor;
		
		this.colorSensor.setCurrentMode("Red");
	}

	
	/**
	 * Idea: Drive along right walls and turn left if both touch sensors are pressed
	 * to always follow the right walls until the end of the maze.
	 */
	public void run() {
		
		LCD.clear();
		// Make sure the sonic sensor is facing sideways
		sonicMotor.setAcceleration(100);
		sonicMotor.rotate(-31);
		sonicMotor.waitComplete();
		
		EV3 ev3 = (EV3) BrickFinder.getLocal();
		Keys keys = ev3.getKeys();
		
		keys.waitForAnyPress();
		
		SampleProvider distanceProvider = sonicSensor.getDistanceMode();
				
		long algorithmStart = System.nanoTime(); 	// Stores when the algorithm starts
		float currentColorValue = 0;
		
		// Start moving
		this.drive.moveForward(400, 300);
				
		while (programRunning) {
				
			// Check the two touch sensors while the program is running
			float[] touchSensorResultsLeft = new float[touchSensorLeft.sampleSize()];
			touchSensorLeft.fetchSample(touchSensorResultsLeft, 0);
			
			float[] touchSensorResultsRight = new float[touchSensorRight.sampleSize()];
			touchSensorRight.fetchSample(touchSensorResultsRight, 0);
					
			if (touchSensorResultsLeft[0] == 1 && touchSensorResultsRight[0] == 1) {
				// Touch sensors pressed, drive back a bit and turn left
				drive.moveDistance(400, -15);
				drive.turnLeft(70);
			}
						
			float[] sonicSensorResults = new float [distanceProvider.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
						
			// Sonic sensor encounters a needed movement correction
			if (sonicSensorResults[0] < 0.165) {
				drive.moveForward(300, 400);
			} else {
				drive.moveForward(600, 300);
			}
			
			// Stop algorithm if the color sensor detects the white/silver line of the 
			// barcode
			// Getting the current color value from the sensor
			float[] sample = new float[this.colorSensor.sampleSize()];
			this.colorSensor.fetchSample(sample, 0);
			currentColorValue = sample[0] * 1.25f;
			
			if (currentColorValue > THRESHOLD_WHITE) {
				end();
				sonicMotor.setAcceleration(100);
				sonicMotor.rotate(31, true);
				sonicMotor.waitComplete();
				drive.stopSynchronized();
				drive.moveDistance(300, -12);
				drive.turnRight(10);
				
				programRunning = false;
				GUI.PROGRAM_FINISHED_START_BARCODE = true;
			}
			
			// Terminate program automatically after 240seconds
			/*if (((System.nanoTime() - algorithmStart) / 1000000000.0f) > 240.0f) {
				end();
			}*/
		}
	}
	
	
	/**
	 * Ends the maze obstacle algorithm.
	 */
	public void end() {
		drive.stopSynchronized();
		programRunning = false;
	}
}