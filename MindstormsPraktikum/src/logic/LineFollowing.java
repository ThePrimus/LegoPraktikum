package logic;

import lejos.hardware.sensor.EV3ColorSensor;


import java.lang.Math;

/**
 * Lets the robot follow a path (line on ground).
 * 
 * @author Group 1
 */
public class LineFollowing implements Runnable {
	
	private  EV3ColorSensor sensor;
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

		redMax = 0;
		
		sensor.setCurrentMode("Red");
	}
	
	/**
	 * Finds the max value for red intesity by rotating to 90� and -90�
	 */
	private void FindMaxIntensity(){
		float redMaxR = 0;
		float redMaxL = 0;
		boolean movment = true;
		float [] samples = new float[sensor.sampleSize()];
		
		drive.turnRight(90);
				
		while(movment){
			movment = drive.isLeftMoving() && drive.isRightMoving();
			sensor.fetchSample(samples, 0);
			
			redMaxR = redMaxR < samples[0] ? samples[0] : redMaxR;
		}
		movment = true;
		drive.turnLeft(180);
		
		while(movment){
			movment = drive.isLeftMoving() && drive.isRightMoving();
			sensor.fetchSample(samples, 0);
			
			redMaxL = redMaxL < samples[0] ? samples[0] : redMaxL;
		}
		drive.turnRight(90);
		
		redMax = (redMaxR + redMaxL)/2;
	}
	
	private void FollowLine(){
		FindMaxIntensity();
		float[] sample = new float[sensor.sampleSize()];
		float lastSample = 0;
		boolean rightTurn = false;
		int count = 0;
		
		drive.moveForward(drive.maxSpeed(), drive.maxSpeed());
		
		while(drive.isLeftMoving() && drive.isRightMoving())
		{	
			sensor.fetchSample(sample, 0);
			
			if(sample[0] < (redMax * 0.8) || !rightTurn){
				while(sample[0] < redMax * 0.8)
				{
					sensor.fetchSample(sample, 0);
					drive.turnRight(5);
				}
				rightTurn = true;
			}
			
			if(sample[0] < (redMax * 0.8) || rightTurn){
				while(sample[0] < (redMax * 0.8))
				{
					sensor.fetchSample(sample, 0);
					drive.turnLeft(5);
					rightTurn = false;
				}
			}
			if(((sample[0] - lastSample) <= 0.0f ? 0.0f - (sample[0] - lastSample) : (sample[0] - lastSample)) < 0.2 && sample[0] < redMax * 0.2){
				count++;
			}
			if(count == 100)
			{
				drive.stop();
			}
		}
		
	}
	public void run(){
		FollowLine();
	}
		
}
