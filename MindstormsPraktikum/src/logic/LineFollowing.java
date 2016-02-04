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
	private float redMax;
	
	// The navigation class.
	private Drive drive;
	
	public static boolean terminate;
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public LineFollowing(Drive drive, EV3ColorSensor sensor) {
		this.drive = drive;
		this.sensor = sensor;
		terminate = false;
		redMax = 1;
		
		sensor.setCurrentMode("Red");
	}
	
	/**
	 * Finds the max value for red intesity by rotating to 90° and -90°
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
		
		//LCD.clear();
		LCD.drawString(String.valueOf(redMax), 0, 10);
		
		Delay.msDelay(5000);
	}
	
	private void FollowLine(){
		//FindMaxIntensity();
		float[] sample = new float[sensor.sampleSize()];
		float lastSample = 0;
		int count = 0;
		char lastState = '\0';
		int speed = 100;
		EV3 ev3 = (EV3) BrickFinder.getLocal();
		Keys key = ev3.getKeys();
		drive.moveForward(speed/2, speed/2);
		
		
		
		while(count <= 50000){	
			sensor.fetchSample(sample, 0);
		
			
			if(sample[0] > redMax * 0.6) {
				drive.moveForward(0, speed);
			}
			if(sample[0] < redMax * 0.4) {
				drive.moveForward(speed, 0);
			}
			else
			{
				drive.moveForward(speed/2, speed/2);
			}
			
			count++;
			/*if(sample[0] < (redMax * 0.8) || !rightTurn){
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
			} */
		}
		drive.stop();
		
	}
	@Override
	public void run(){
		FollowLine();
	}
		
}
