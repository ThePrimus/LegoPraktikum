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
public class LineFollowing {
	
	private EV3ColorSensor sensor;
	private float redMax = 1;
	// The navigation class.
	private Drive drive;
	private boolean terminate = false;
	
	
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
	public void end()
	{
		this.terminate = true;
	}
	
	public void run(){
		
		int count = 0;
		char lastState = '\0';
		float speed = 100;
		float[] sample = new float[sensor.sampleSize()];
		/*EV3 ev3 = (EV3) BrickFinder.getLocal();
		Keys key = ev3.getKeys();*/
		
		drive.moveForward(drive.maxSpeed() * 0.8f, drive.maxSpeed() * 0.8f);
		LCD.clear();
		
		while(!terminate){	
			
			sensor.fetchSample(sample, 0);
			/*if(count%100 == 0)
			{
				LCD.drawString("Value: " + String.valueOf(sample[0]), 0, 0);
				LCD.drawString("State: " + String.valueOf(lastState), 0, 1);
			}*/
			//Delay.msDelay(100);
			if(sample[0] > redMax * 0.6 && lastState == 'f') {
				drive.moveForward(drive.maxSpeed(), 0);
				lastState = 'r';
			}
			if(sample[0] < redMax * 0.4  && lastState == 'f') {
				drive.moveForward(0, drive.maxSpeed());
				lastState = 'l';
			}
			else if(sample[0] < redMax * 0.6 && sample[0] > redMax * 0.4 && lastState != 'f')
			{	
				drive.moveForward(drive.maxSpeed() * 0.8f, drive.maxSpeed() * 0.8f);
				lastState = 'f';
			}
			
		}
		
		drive.stop();
		LCD.clear();
	}
		
}
