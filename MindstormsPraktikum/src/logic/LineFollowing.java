package logic;

import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3ColorSensor;

import lejos.robotics.filter.SampleThread;
import lejos.utility.Delay;

/**
 * Lets the robot follow a path (line on ground).
 * 
 * @author Group 1
 */
public class LineFollowing {
	
	/*
	 * The color sensor.
	 */
	private EV3ColorSensor sensor;
	
	/*
	 *  The navigation class.
	 */
	private Drive drive;
	
	
	private float redMax = 1;
	private boolean terminate = false;
	private char lastState = 's';
	
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
	 * Searches the line in a 180 degree range by 5 degree steps.
	 * If line is found, save from the side from which the line was approached(Left or right turn).
	 */
	private void searchLine() {
		int deg = 0;
		int inc = 10;
		float [] samples = new float[sensor.getRedMode().sampleSize()];
		
		while(deg < 180) {
			if(deg < 90) {
				drive.turnLeft(inc, false);
				deg += inc;
				sensor.getRedMode().fetchSample(samples, 0);
				if(samples[0] > 0.9)
				{
					lastState = 'l';
					//Sound.beep();
					break;
				}
					
			}
			
			if(deg == 90)
			{
				drive.turnRight(90, false);
			}
			
			if(deg >= 90)
			{
				drive.turnRight(inc, false);
				deg += inc;
				sensor.getRedMode().fetchSample(samples, 0);
				if(samples[0] > 0.9)
				{
					lastState = 'r';
					//Sound.buzz();
					break;
				}
			}
			
			LCD.drawString("Deg: " + String.valueOf(deg), 0, 2);
			LCD.drawString("State: " + lastState, 0, 4);
		}
		LCD.clear();
		LCD.drawString("Last state: " + lastState, 0, 5);
		Delay.msDelay(2000);
		LCD.clear();
	}
	
	/**
	 * Executes an algorithm so that the robot follows a silver/white line.
	 * Idea: Stay on line and adjust depending on last adjustment.
	 * Problem: Parameterization of states and state switching depends on sample fetching frequency
	 */
	public void runt(){
		LCD.clear();

		char state = 'f';
				
		SampleThread sample = new SampleThread(sensor, 2);
		float[] samples = new float[sample.sampleSize()];
		
		while(!terminate) {
			if(sample.isNewSampleAvailable())
			{
				sample.fetchSample(samples, 0);
				
				LCD.drawString(String.valueOf("Sample: " + String.valueOf(samples[0])), 0, 0);
				LCD.drawString("Last State: " + lastState, 0, 2);
				LCD.drawString("State: " + state, 0, 3);
				
				if(samples[0] < 0.2)
				{
					switch(lastState) {
					case 'r':
						//sample.fetchSample(samples, 0);
						//LCD.drawString("Sample: " + String.valueOf(samples[0]), 0, 4);
						if(state == 'f') {
							drive.moveForward(0, drive.maxSpeed() * 0.3f);
							lastState = 'l';
						}
						//LCD.drawString("State: " + lastState, 0, 3);
						//drive.setSpeedRightMotor(drive.maxSpeed());
						
						break;
					case 'l':
						//Sound.beep();
						//sensor.fetchSample(samples, 0);
						//LCD.drawString("Sample: " + String.valueOf(samples[0]), 0, 4);
						if(state == 'f') {
							lastState = 'r';
							drive.moveForward(drive.maxSpeed() * 0.3f, 0);
						}
						//LCD.drawString("State: " + lastState, 0, 3);
						
						break;
					case 's':
						searchLine();
						break;
					}
					//lastSample = samples[0];
				
				} 
				
				state = 'n';
				
				if(samples[0] > 0.8) {
					state = 'f';
					drive.moveForward(drive.maxSpeed()* 0.2f, drive.maxSpeed()* 0.2f);
				}
				
			}
		}
		drive.stop();
	}
	
	/**
	 * Executes an algorithm so that the robot follows a silver/white line.
	 * Idea: Adjust the whole time to reach a red intensity between 0.5 - 0.4
	 * ToDo: handling of special cases like 90 degree turns and reflections.
	 */
	public void run(){
		int count = 0;
		float[] sample = new float[sensor.sampleSize()];

		drive.moveForward(drive.maxSpeed() * 0.4f, drive.maxSpeed() * 0.4f);
		LCD.clear();
		
		while(!terminate){	
			if(count > 20000) {
				terminate = true;
				break;
			}
			sensor.fetchSample(sample, 0);
			
			if(sample[0] >= redMax * 0.5) {
			
				drive.moveForward(drive.maxSpeed() * 0.6f,0);
				drive.rightBackward(drive.maxSpeed() * 0.3f);
			
			} else if(sample[0] < redMax * 0.4) {
				
				drive.moveForward(0, drive.maxSpeed() * 0.6f);
				drive.leftBackward(drive.maxSpeed() * 0.3f);
	
			} else {
				drive.moveForward(300, 200);
			}
			count++;
		}
		
		drive.stop();
		LCD.clear();
	}
	
	
	public void end() {
		this.terminate = true;
	}
}
