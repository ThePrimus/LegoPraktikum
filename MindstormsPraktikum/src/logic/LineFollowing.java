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
	private float redMax;
	
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
		redMax = 0;
		
		sensor.setCurrentMode(0);
		sensor.setFloodlight(lejos.robotics.Color.RED);
	}
	
	private void FindMaxIntensity(boolean rightMoves){
		float maxIntensity = 0;
		boolean movment = true;
		float [] samples = new float[sensor.sampleSize()];
		
		if(rightMoves){
			drive.moveForward(0, drive.maxSpeed());
		}
		else
		{
			drive.moveForward(drive.maxSpeed(), 0);
		}
		
		while(movment){
			if(rightMoves)
			{
				movment = drive.isRightMoving();
			}else{
				movment = drive.isLeftMoving();
			}
			sensor.fetchSample(samples, 0);
			
			redMax = redMax < samples[0] ? samples[0] : redMax;
		}
		
	}
	private void Calibrate(){
		
		filter = new OffsetCorrectionFilter(sensor);
		float[] sample = new float[filter.sampleSize()];
	}
		
}
