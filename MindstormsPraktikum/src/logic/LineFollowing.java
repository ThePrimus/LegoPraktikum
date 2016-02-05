package logic;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.utility.Delay;

import java.lang.Math;

/**
 * Lets the robot follow a path (line on ground).
 * 
 * @author Group 1
 */
public class LineFollowing implements Runnable {
	
	private  EV3ColorSensor sensor;
	
	// The navigation class.
	private Drive drive;
	
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public LineFollowing(Drive drive, EV3ColorSensor sensor) {
		this.drive = drive;
		this.sensor = sensor;
		
		sensor.setCurrentMode("Red");
	}
	

	
	/**
	 * Executes an algorithm so that the robot follows a silver/white line.
	 * Idea:
	 */
	@Override
	public void run(){
	}
		
}
