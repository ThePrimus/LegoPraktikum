package logic;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;
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
	private SampleProvider colorProvider;
	
	/*
	 *  The navigation class.
	 */
	private Drive drive;
	private float mSpeed;
	private final float diffSpeed = 140; //140 oder 100 mit emplified
	private float initSpeed; //= mSpeed - 2 * diffSpeed;
	private float timestamp = 0;
	
	private int tachoCount = 0;
	
	private boolean terminate = false;
	//private char lastState = 's';
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public LineFollowing(Drive drive, EV3ColorSensor sensor) {
		this.drive = drive;
		this.colorProvider = sensor.getRedMode();
		
		
		mSpeed = 600; //600 oder 500
		initSpeed = mSpeed - 2 * diffSpeed;
	}
	
	/**
	 * Searches the line in a 180 degree range by 5 degree steps.
	 * If line is found, save from the side from which the line was approached(Left or right turn).
	 */
	private boolean searchLine() {
		boolean found = false;
		int deg = 0;
		int inc = 10;
		float [] samples = new float[colorProvider.sampleSize()];
		float curVal  = 0;
		while(deg < 120) {
				drive.turnLeft(-inc, false);
				deg += inc;
				colorProvider.fetchSample(samples, 0);
				curVal = samples[0] * 1.25f;
				if(curVal > 0.7)
				{
					Sound.beepSequenceUp();
					found = true;
					break;
				}			
			}
			
	return found;
	}
	
	/**
	 * Executes an algorithm so that the robot follows a silver/white line.
	 * Idea: Stay on line and adjust depending on last adjustment.
	 * Problem: Parameterization of states and state switching depends on sample fetching frequency
	 */
	/*
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
	*/
	/**
	 * Executes an algorithm so that the robot follows a silver/white line.
	 * Idea: Adjust the whole time to reach a red intensity between 0.5 - 0.4
	 * ToDo: handling of special cases like 90 degree turns and reflections.
	 */
	public void run(){
		int counter = 0;
		float[] colorResults = new float[colorProvider.sampleSize()];
		Sound.twoBeeps();
		while (!terminate) {
			LCD.drawString("Counter: " +  String.valueOf(counter) ,0, 2);
			// get color of line
			if(terminate) {
				Sound.beep();
				drive.stop();
				//terminate = true;
				break;
			}
			if(counter > 100000) {
				Sound.twoBeeps();
				terminate = true;
				drive.stop();
				break;
			}
			
			colorProvider.fetchSample(colorResults, 0);
			float curColor = colorResults[0] * 1.25f;

			// correct movement according to the youtube video
			float lSpeed = curColor * mSpeed - diffSpeed;
			float rSpeed = initSpeed - lSpeed;
			

			if (lSpeed < 0) {
				drive.leftBackward(lSpeed);
			} else {
				timestamp = 0;
				tachoCount = 0;
				drive.setSpeedLeftMotor(lSpeed);
			}

			if (rSpeed < 0) {
				drive.rightBackward(rSpeed);
			} else {
				if(timestamp == 0)
				{
					timestamp = System.nanoTime();
					//Sound.beep();
					
				} else if(((System.nanoTime() - timestamp) / 1000000000.0f) > 1.4) {
					drive.stop();
					Sound.beep();
					//LCD.drawString("Break!", 0, 5);
					drive.turnLeft(-90, false);
					if(!searchLine())
					{
						LCD.drawString("Line not Found!", 0, 5);
						break;
					}
					
					//Delay.msDelay(000);
					/*while(!lineFound()) {
						if(search > 18) {
							LCD.drawString("Line not Found!", 0, 1);
							deg = 0;
							break;
						}
						LCD.drawString("Looking for Line...", 0, 1);
						search++;
					}*/
				}
				drive.setSpeedRightMotor(rSpeed);
			}
			
			//LCD.drawString("Dif: " + String.valueOf(Math.abs(lastSample - curColor)), 0, 2);
			//LCD.drawString("Left: " + String.valueOf(lSpeed), 0, 3);
			//LCD.drawString( "Right: " + String.valueOf(lSpeed), 0, 2);
			counter++;
			//lastSample = curColor;
			//LCD.drawString("Timestamp: " + String.valueOf(timestamp), 0, 2);
			//LCD.drawString("Dif: " + String.valueOf(timestamp - System.currentTimeMillis()), 0, 4);
			/*System.out.println();
			System.out.println("Dif: " + String.valueOf(timestamp - System.currentTimeMillis()));*/
		}
		//}
		
		//Delay.msDelay(1000);
		//searchLine();
		
		// ToDo: Move code to correct position: seesaw obstacle finished, inform GUI to start
		// the search for a barcode
		GUI.PROGRAM_FINISHED_START_BARCODE = false;
		terminate = false;
		timestamp = 0;
	}
	
	
	public void end() {
		drive.stop();
		this.terminate = true;
	}
	
}
