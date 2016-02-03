package logic;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.filter.OffsetCorrectionFilter;

/**
 * Lets the robot follow a path (line on ground).
 * 
 * @author Group 1
 */
public class LineFollowing {
	
	private  EV3ColorSensor sensor;
	private OffsetCorrectionFilter filter;
	private float RedOffset;
	private float BlackOffset;
	
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
		RedOffset = 0;
		BlackOffset = 0;
	}
	
	private void Calibrate(){
		filter = new OffsetCorrectionFilter(sensor);
		float[] sample = new float[filter.sampleSize()];
	}
		
}
