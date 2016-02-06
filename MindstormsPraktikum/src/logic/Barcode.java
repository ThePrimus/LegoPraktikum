package logic;

import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3ColorSensor;

/**
 * Scans a barcode and returns the result.
 * 
 * @author Group 1
 */
public class Barcode implements Runnable {

	// Sensors and robot control.
	private Drive drive;
	private EV3ColorSensor colorSensor;
	private boolean moveRobot;				// If this class/program should move the robot with 30% speed
	
	// The length of a wheel of the robot (in mm).
	private final float WHEEL_LENGTH = 106.8142f;
	
	// The detected barcode = id of obstacle
	private int barcode = 0;
	
	// Variables indicate the black and white line of a barcode.
	private final int BLACK = 0;
	private final int WHITE = 1;
	
	// The minimum color value of a white/silver element of the barcode.
	private final float THRESHOLD_WHITE = 0.7f;
	
	// The width of an element pair of the barcode (in millimeter: one white line 
	// (= 25mm), one black ground (= 25mm)).
	private final float WIDTH_BARCODE_ELEMENT = 50.0f; 
	
	
	
	/**
	 * Constructor.
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
	@Override
	public void run() {
		
		int position = BLACK;	// Robot starts on black ground
		long startTime = 0;		// Measures the time so that the length of the moved way can be calculated
		
		boolean programRunning = true;
		float currentColorValue = 0;
		
		while (programRunning) {
			
			// Getting the current color value from the sensor
			float[] sample = new float[colorSensor.sampleSize()];
			colorSensor.fetchSample(sample, 0);
			currentColorValue = sample[0];
			
			// Displaying the current detected bar code
			LCD.clear();
			//LCD.drawString("Barcode:", 0, 7);
			//LCD.drawInt(barcode, 3, "Barcode:".length(), 7);
			//System.out.println("Barcode = " + barcode);
			
			// If barcode thread running parallel to obstacle, the obstacle thread handles the movement of the robot
			if (moveRobot) {
				drive.moveForward(drive.maxSpeed() * 0.3f);		// Start robot movement with 30% speed
			}
			
				
			if (currentColorValue > THRESHOLD_WHITE) {
				
				// Robot is on a white/silver line
				if (position == BLACK) {
					// Robot was previously on a black line/ground
					position = WHITE;
					startTime = System.nanoTime();
					barcode++;
				}
			} else {
				
				// Robot is on black ground
				
				// Calculating the moved distance since the last white line
				float elapsedTime = System.nanoTime() - startTime;
				float currentSpeed = drive.getSpeed();
				float traveledDistanceDegree = currentSpeed * (elapsedTime / 1000000000.0f);	
				float traveledDistanceMM = traveledDistanceDegree / 360.0f * WHEEL_LENGTH;
				
				//LCD.drawString("Distance", 0, 7);
				//LCD.drawInttraveledDistanceMM, 3, "Distance:".length(), 7);
				//System.out.println("Distance = " + traveledDistanceMM);
				
				if (position != BLACK) {
					// Robot has previously detected a white line
					position = BLACK;
				} else if (barcode > 1 && traveledDistanceMM > WIDTH_BARCODE_ELEMENT) {
					// Barcode completed and detected. ToDo: inform about result
					programRunning = false;
					System.out.println("Detected barcode = " + barcode);
				}
			}
			
					
			// Reset barcode
			if (barcode > 6) {
				barcode = 0;
				programRunning = false;
			}
		}	
	}
	
}
