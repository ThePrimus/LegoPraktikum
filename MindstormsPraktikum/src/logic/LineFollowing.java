package logic;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
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
	
	private boolean terminate = false;
	
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
		while(deg <= 90) {
			drive.turnLeft(-inc, false);
			deg += inc;
			colorProvider.fetchSample(samples, 0);
			curVal = samples[0] * 1.25f;
			if(curVal > 0.7)
			{
				Sound.beep();
				found = true;
				break;
			}			
		}
		
		deg = 0;
		if(!found)
		{
			while(deg <= 90) {
				drive.turnRight(inc, false);
				deg += inc;
				colorProvider.fetchSample(samples, 0);
				curVal = samples[0] * 1.25f;
				if(curVal > 0.7)
				{
					Sound.buzz();
					found = true;
					break;
				}			
			}
			if(!found)
			{
				drive.turnRight(-90, false);
			}
			
		}
			
	return found;
	}
	

	/**
	 * Executes an algorithm so that the robot follows a silver/white line.
	 * Idea: Adjust the whole time to reach a red intensity between 0.5 - 0.4
	 * ToDo: handling of special cases like 90 degree turns and reflections.
	 * 
	 * @param startChainBridge if true the chain bridge program is started next if this follow
	 * 			line program doesn't find a line anymore, otherwise the
	 * 			barcode program is started
	 */
	public void run(boolean startChainBridge){
		int counter = 0;
		float[] colorResults = new float[colorProvider.sampleSize()];
		Sound.twoBeeps();
		while (!terminate) {
			LCD.drawString("Counter: " +  String.valueOf(counter) ,0, 2);
			// get color of line
			if(terminate) {
				Sound.beep();
				drive.stopSynchronized();
				//terminate = true;
				break;
			}
			if(counter > 20000) {
				Sound.twoBeeps();
				terminate = true;
				drive.stopSynchronized();
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
					drive.stopSynchronized();
					Sound.beep();
					//LCD.drawString("Break!", 0, 5);
					
					if (startChainBridge) {
						drive.turnLeft(-90, true);
						break;
					} else {
						drive.turnLeft(-90, false);
					}
					
					if(!searchLine())
					{
						LCD.drawString("Line not Found!", 0, 5);
						break;
					}
				}
				
				drive.setSpeedRightMotor(rSpeed);
			}
			
			counter++;
		}
		
		// No line found anymore, start bridge or barcode program next.
		if (startChainBridge) {
			GUI.PROGRAM_CHANGED = true;
		  	GUI.PROGRAM_STATUS = GUI.PROGRAM_CHAIN_BRDIGE;
		} else {
			GUI.PROGRAM_FINISHED_START_BARCODE = true;
		}
		terminate = false;
		timestamp = 0;
	}
	
	
	public void end() {
		drive.stopSynchronized();
		this.terminate = true;
	}
	
}
