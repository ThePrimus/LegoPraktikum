package logic;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.filter.OffsetCorrectionFilter;
import lejos.robotics.navigation.LineFollowingMoveController;

/**
 * Lets the robot follow a path (line on ground).
 * 
 * @author Group 1
 */
public class LineFollowing {

	/**
	 * Constructor: 
	 */
	public LineFollowing(EV3ColorSensor sensor) {
		this.sensor = sensor;
		RedOffset = 0;
		BlackOffset = 0;
		
	}
	private  EV3ColorSensor sensor;
	private OffsetCorrectionFilter filter;
	private float RedOffset;
	private float BlackOffset;
	
	private void Calibrate(){
		filter = new OffsetCorrectionFilter(sensor);
		float[] sample = new float[filter.sampleSize()];
	}
		
}
