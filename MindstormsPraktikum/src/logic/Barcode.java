package logic;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3ColorSensor;

/**
 * Scans a barcode and returns the result.
 * 
 * @author Group 1
 */
public class Barcode {

	// Sensors and robot control.
	private Drive drive;
	private EV3ColorSensor colorSensor;
	private boolean moveRobot;				
	
	// It the loop to search for a barcode should run or not. Can be used to terminate
	// the algorithm.
	boolean programRunning = true;
		
	// The detected barcode = id of obstacle
	private int barcode = 0;
	
	// Variables indicate the black and white line of a barcode.
	private final int BLACK = 0;
	private final int WHITE = 1;
	
	// The length of a wheel of the robot (in mm).
	private final float WHEEL_LENGTH = 106.8142f;
	
	// The minimum color value of a white/silver element of the barcode.
	private final float THRESHOLD_WHITE = 0.7f;
	
	// The width of an element pair of the barcode (in millimeter: one white line 
	// (= 25mm), one black ground (= 25mm)).
	private final float WIDTH_BARCODE_ELEMENT = 60.0f; 
	
	// The maximum time that the barcode algorithm has time to search for a barccode (in seconds).
	private final float MAXIMUM_ALGORITHM_TIME = 10.0f;
	
	
		
	/**
	 * Constructor.
	 * 
	 * @param moveRobot if this class/program should move the robot with 30% speed. Otherwise
	 * 			the class that called this constructor has to take care about the robot movement!
	 */
	public Barcode(Drive drive, EV3ColorSensor colorSensor, boolean moveRobot) {
		this.drive = drive;
		this.colorSensor = colorSensor;
		this.moveRobot = moveRobot;
		
		colorSensor.setCurrentMode("Red");
	}


	/**
	 * Every barcode consists of one or more single white lines that are horizontally to the
	 * movement direction of the robot. Between a line to follow and the beginning of the barcode
	 * are 5cm of black ground. 
	 */
	public void run() {
		
		int position = BLACK;	// Robot starts on black ground
		long startTime = 0;		// Measures the time so that the length of the moved way can be calculated
		long algorithmStart = System.nanoTime(); 	// Stores when the algorithm starts
		
		float currentColorValue = 0;
		
		while (programRunning) {
			
			// Getting the current color value from the sensor
			float[] sample = new float[colorSensor.sampleSize()];
			colorSensor.fetchSample(sample, 0);
			currentColorValue = sample[0] * 1.25f;
			
			// Displaying the current detected bar code
			LCD.clear();
			LCD.drawString("Barcode:", 0, 1);
			LCD.drawInt(barcode, 2, "Barcode:".length(), 1);
			//System.out.println("Barcode = " + barcode);
			
			// If barcode thread running parallel to obstacle, the obstacle thread handles the movement of the robot
			if (moveRobot) {
				drive.moveForward(drive.maxSpeed() * 0.35f);		// Start robot movement with 30% speed
			}
			
				
			if (currentColorValue > THRESHOLD_WHITE) {
				// Robot is on a white/silver line
				if (position == BLACK) {
					// Robot was previously on a black line/ground
					position = WHITE;
					startTime = System.nanoTime();
					Sound.beep();
					barcode++;
				}
			} else {
				
				// Robot is on black ground
				
				// Calculating the moved distance since the last white line
				long elapsedTime = System.nanoTime() - startTime;
				float currentSpeed = drive.getSpeed();
				float traveledDistanceDegree = currentSpeed * (elapsedTime / 1000000000.0f);	
				float traveledDistanceMM = traveledDistanceDegree / 360.0f * WHEEL_LENGTH;
				
				//LCD.drawString("Distance", 0, 7);
				//LCD.drawInttraveledDistanceMM, 3, "Distance:".length(), 7);
				//System.out.println("Distance = " + traveledDistanceMM);
				
				if (position != BLACK) {
					// Robot has previously detected a white line
					position = BLACK;
				} else if (barcode >= 1 && traveledDistanceMM > WIDTH_BARCODE_ELEMENT) {
					// Barcode completed and detected. Other classes can use Barcode.getBarcode() to
					// get the result
					drive.stopSynchronized();
					drive.moveDistance(300, 9);
					programRunning = false;
					System.out.println("Detected barcode = " + barcode);
				}
			}

			// Check if search for barcode is running for a long time or a not valid barcode
			// has been found. If so stop the loop.
			if (((System.nanoTime() - algorithmStart) / 1000000000.0f) > MAXIMUM_ALGORITHM_TIME
					|| barcode > 6) {
				barcode = 0;
				drive.stopSynchronized();
				programRunning = false;
			}
		}	
	}
	
	/**
	 * Ends this algorithm that searches for the barcode.
	 */
	public void end() {
		drive.stopSynchronized();
		programRunning = false;
	}
	
	
	/**
	 * Returns the found barcode if a valid one could be found.
	 * A valid barcode consists of at least two lines and has not more
	 * than six lines. If no valid barcode could be found -1 is returned.
	 * 
	 * @return the valid barcode, -1 otherwise.
	 */
	public int getBarcode() {
		if (barcode >= 1 && barcode <= 6) {
			return barcode;
		} else {
			return -1;
		}
	}
}
