package logic;

import lejos.hardware.sensor.EV3ColorSensor;

/**
 * Lets the robot follow a path (line on ground).
 * 
 * @author Group 1
 */
public class LineFollowing {
	
	private boolean programRunning = true;
	
	/*
	 *  The minimum color value of a white/silver line.
	 */
	private final float THRESHOLD_WHITE = 0.7f;
	
	/*
	 * The color sensor.
	 */
	private  EV3ColorSensor colorSensor;
	
	/*
	 *  The navigation class.
	 */
	private Drive drive;
	
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public LineFollowing(Drive drive, EV3ColorSensor colorSensor) {
		this.drive = drive;
		this.colorSensor = colorSensor;
		
		colorSensor.setCurrentMode("Red");
	}
	

	
	/**
	 * Executes an algorithm so that the robot follows a silver/white line.
	 * Idea:
	 */
	public void run(){
		
	}

	
	public void end() {
		programRunning = false;
	}
		
}
