package parkour;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MeanFilter;
import lejos.utility.Delay;
import logic.Drive;
import logic.GUI;

/**
 * Implements the logic to beat the seesaw obstacle.
 * 
 * @author Group 1
 */
public class Seesaw {

	// The navigation class.
	private Drive drive;
	private boolean LineFollowing = true;
	private SampleProvider colorProvider;
	private float mSpeed;
	private final float diffSpeed = 200;
	private float initSpeed; //= mSpeed - 2 * diffSpeed;
	private long timestamp = 0;
	
	private int deg = 0;

	/**
	 * Constructor:
	 * 
	 * @param drive
	 *            the drive class for navigation and motor control.
	 * @param colorSensor
	 * @param sonicMotor
	 * @param sonicSensor
	 */
	public Seesaw(Drive drive, EV3ColorSensor colorSensor) {
		this.drive = drive;
		this.colorProvider = colorSensor.getRedMode();
		mSpeed = 600;
		initSpeed = mSpeed - 2 * diffSpeed;
	}
	
	private boolean lineFound()
	{
		boolean found = false;
		float[] sample = new float[colorProvider.sampleSize()];
		drive.turnRight(45);
		//drive.setSpeed(drive.maxSpeed());
		
		//while(deg < 90) {
			colorProvider.fetchSample(sample, 0);
			LCD.drawString("Sample: " + String.valueOf(sample[0]), 0, 6);
			if(deg <= 180) {
				if(sample[0] > 0.8) {
					deg = 0;
					found = true;
					//break;
				} else {
					drive.turnRight(5);
					deg += 5;
				}	
			}/* else if(deg == 95) {
				if(sample[0] > 0.8) {
					found = true;
					deg = 0;
					//break;
				} else {
					drive.turnLeft(95);
					deg++;
				}	
			} else if(deg <= 181) {
				if(sample[0] > 0.9) {
					found = true;
					deg = 0;
					//break;
				} else {
					drive.turnLeft(5);
					deg += 5;
				}
			}*/
		//}
		return found;
	}
	// Idea:Follow right side of the line
	// TODO: Adjust values
	// see : https://www.youtube.com/watch?v=tViA21Y08cU
	public void run() {
		float counter = 0;
		float search = 0;
		//MeanFilter filter = new MeanFilter(colorProvider, 10);
		float[] colorResults = new float[colorProvider.sampleSize()];
		while (LineFollowing) {
			// get color of line
			if(!LineFollowing) {
				drive.stop();
				LineFollowing = false;
				break;
			}
			if(counter > 20000) {
				LineFollowing = false;
				drive.stop();
				break;
			}
			
			colorProvider.fetchSample(colorResults, 0);
			float curColor = colorResults[0];

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
					timestamp = System.currentTimeMillis();
					//Sound.beep();
					
				} else if(Math.abs(timestamp - System.currentTimeMillis()) > 600 ) {
					//Sound.buzz();
					LCD.drawString("Break!", 0, 5);
					timestamp = 0;
					drive.stop();
					//Delay.msDelay(000);
					while(!lineFound()) {
						if(search > 18) {
							LCD.drawString("Line not Found!", 0, 1);
							deg = 0;
							break;
						}
						LCD.drawString("Looking for Line...", 0, 1);
						search++;
					}
				}
				drive.setSpeedRightMotor(rSpeed);
			}
			counter++;
			LCD.drawString("Timestamp: " + String.valueOf(timestamp), 0, 2);
			LCD.drawString("Dif: " + String.valueOf(timestamp - System.currentTimeMillis()), 0, 4);
			/*System.out.println();
			System.out.println("Dif: " + String.valueOf(timestamp - System.currentTimeMillis()));*/
		}
		
		//Delay.msDelay(1000);
		//searchLine();
		
		// ToDo: Move code to correct position: seesaw obstacle finished, inform GUI to start
		// the search for a barcode
		GUI.PROGRAM_FINISHED_START_BARCODE = false;
	}

	public void end() {
		drive.stop();
		LineFollowing = false;
	}
}
