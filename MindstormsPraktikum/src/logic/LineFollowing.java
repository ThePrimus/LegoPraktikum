package logic;

import lejos.hardware.sensor.EV3ColorSensor;

/**
 * Lets the robot follow a path (line on ground).
 * 
 * @author Group 1
 */
public class LineFollowing {
	
	
	/*
	 * The color sensor.
	 */
	private  EV3ColorSensor sensor;
	
	/*
	 *  The navigation class.
	 */
	private Drive drive;
	
	
	private float redMax = 1;
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
	public void run(){
		int count = 0;
		char lastState = 'f';
		float speed = 100;
		float[] sample = new float[sensor.sampleSize()];
		/*EV3 ev3 = (EV3) BrickFinder.getLocal();
		Keys key = ev3.getKeys();*/
		
		drive.moveForward(drive.maxSpeed() * 0.4f, drive.maxSpeed() * 0.4f);
		LCD.clear();
		
		while(!terminate){	
			
			sensor.fetchSample(sample, 0);
			/*if(count%100 == 0)
			{
				LCD.drawString("Value: " + String.valueOf(sample[0]), 0, 0);
				LCD.drawString("State: " + String.valueOf(lastState), 0, 1);
			}*/
			//Delay.msDelay(100);
			if(sample[0] > redMax * 0.55 && lastState == 'f') {
				drive.moveForward(drive.maxSpeed() *0.6f, 0);
				lastState = 'r';
			}
			if(sample[0] < redMax * 0.45  && lastState == 'f') {
				drive.moveForward(0, drive.maxSpeed()*0.6f);
				lastState = 'l';
			}
			else if(sample[0] < redMax * 0.55 && sample[0] > redMax * 0.45)
			{	
				drive.moveForward(drive.maxSpeed() * 0.4f, drive.maxSpeed() * 0.4f);
				lastState = 'f';
			}
			
		}
		
		drive.stop();
		LCD.clear();
	}
	
	
	public void end() {
		this.terminate = true;
	}
}
