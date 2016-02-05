package parkour;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import logic.Drive;

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
	}

	// Idea:Follow right side of the line
	// TODO: Adjust values
	// see : https://www.youtube.com/watch?v=tViA21Y08cU
	public void run() {
		while (LineFollowing) {
			// get color of line
			float[] colorResults = new float[colorProvider.sampleSize()];
			colorProvider.fetchSample(colorResults, 0);
			float curColor = colorResults[0];

			// correct movement according to the youtube video
			float lSpeed = curColor * 800 - 250;
			float rSpeed = 300 - lSpeed;

			if (lSpeed < 0) {
				drive.leftBackward(lSpeed);
			} else {
				drive.setSpeedLeftMotor(lSpeed);
			}

			if (rSpeed < 0) {
				drive.rightBackward(rSpeed);
			} else {
				drive.setSpeedRightMotor(rSpeed);
			}
		}

	}

	public void end() {
		LineFollowing = false;
	}
}
