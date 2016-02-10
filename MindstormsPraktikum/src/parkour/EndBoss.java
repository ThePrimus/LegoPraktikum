package parkour;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import logic.Drive;
import music.MusicPlay;

/**
 * Implements the logic to beat the final enemy of the parkour.
 * 
 * @author Group 1
 */
public class EndBoss {
	
	// The maximum time that the algorithm has time to reach the end wall (in seconds).
	private final float MAXIMUM_ALGORITHM_TIME = 50.0f;
	
	/*
	 * Distance to the right wall when the robot movement needs to be corrected.
	 */
	private static final float DISTANCE_TO_CORRECT_MOVEMENT = 0.16f;
	
	/*
	 * The finish is marked by a red line.
	 */
	private static final float THRESHOLD_RED_LINE = 0.20f;
	
	// The classes that control the robot and the sensors.
	private Drive drive;
	private EV3MediumRegulatedMotor sonicMotor;
	private EV3UltrasonicSensor sonicSensor;
	private EV3TouchSensor touchSensorLeft;
	private EV3TouchSensor touchSensorRight;
	private EV3ColorSensor colorSensor; 
	
	
	// The class/thread for playing the Game of Thrones theme.
	MusicPlay gameOfThrones;
	
	// If the program should run.
	boolean programRunning = true;

	
	/**
	 * Constructor:
	 */
	public EndBoss(Drive drive, EV3TouchSensor touchLeftSensor,
			EV3TouchSensor touchRightSensor, EV3MediumRegulatedMotor sonicMotor,
			EV3UltrasonicSensor sonicSensor, EV3ColorSensor colorSensor) {
		this.drive = drive;
		this.sonicMotor = sonicMotor;
		this.sonicSensor = sonicSensor;
		this.touchSensorLeft = touchLeftSensor;
		this.touchSensorRight = touchRightSensor;
		this.colorSensor = colorSensor;
		
		this.colorSensor.setCurrentMode("Red");
	}


	/**
	 * Idea: Drive forward, if touch sensor detects something, stop, drive
	 * backwards, make a right turn to drive to right side, follow it. Keep a 
	 * minimum distance to the right side.
	 * While this program is running the Game of Thrones theme is played.
	 * If this program is running for a predefined time stop it automatically,
	 * because it can't be differentiated if the touch sensors have been pressed
	 * by the enemy robot or the end wall.
	 */
	public void run() {
		
		// Make sure the sonic sensor is facing sideways
		sonicMotor.setAcceleration(100);
		sonicMotor.rotate(-30);
		sonicMotor.waitComplete();
		
		long algorithmStart = System.nanoTime(); 			// Stores when the algorithm starts
		float currentColorValue = 0;
		
		// Start moving forward and begin playing music
		this.drive.moveForward(drive.maxSpeed() * 0.9f, drive.maxSpeed() * 0.75f);
		
		gameOfThrones = new MusicPlay();
		Thread musicThread = new Thread(gameOfThrones);
		musicThread.start();
		
		while (programRunning) {
		
			// Check the two touch sensors while the program is running
			float[] touchSensorResultsLeft = new float[touchSensorLeft.sampleSize()];
			touchSensorLeft.fetchSample(touchSensorResultsLeft, 0);
			
			float[] touchSensorResultsRight = new float[touchSensorRight.sampleSize()];
			touchSensorRight.fetchSample(touchSensorResultsRight, 0);
			
			if (touchSensorResultsLeft[0] == 1 && touchSensorResultsRight[0] == 1) {
				// Touch sensor pressed, drive back a bit and turn right
				drive.moveDistance(500, -15);
				drive.turnLeft(70);
				drive.moveForward(drive.maxSpeed() * 0.9f, drive.maxSpeed());
			}
				
			// Check the sonic sensor to keep a minimum distance to the right wall.
			// Right wall is beveled.
			float[] sonicSensorResults = new float [sonicSensor.sampleSize()];
			sonicSensor.fetchSample(sonicSensorResults, 0);
				
			if (sonicSensorResults[0] < DISTANCE_TO_CORRECT_MOVEMENT) {
				// Sonic sensor encounters a needed movement correction
				drive.turnLeft(45);
				drive.moveForward(drive.maxSpeed() * 0.9f, drive.maxSpeed());
			} else if (sonicSensorResults[0] > 0.2f) {
				drive.moveForward(730, 700);
			}
			
			// Checking if the red line is detected, if so the end of the parkour has been reached.
			float[] sample = new float[this.colorSensor.sampleSize()];
			this.colorSensor.fetchSample(sample, 0);
			currentColorValue = sample[0];
			
			if (currentColorValue > THRESHOLD_RED_LINE) {
				end();
			}
			
			
			// End program if it is running for a longer period of time, because it can't be 
			// differentiated if the touch sensors have been pressed by the enemy robot or the end wall
			/*if (((System.nanoTime() - algorithmStart) / 1000000000.0f) > MAXIMUM_ALGORITHM_TIME) {
				end();
			}*/
		}
	}
	
	
	/**
	 * Ends this obstacle program.
	 */
	public void end() {
		drive.stopSynchronized();
		
		if (gameOfThrones != null) {
			gameOfThrones.end();
		}
		
		programRunning = false;
	}

}
