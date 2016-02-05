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
	private EV3ColorSensor colorSensor;
	private boolean LineFollowing = true;
	SampleProvider colorProvider;

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
		this.colorSensor = colorSensor;
		colorProvider = colorSensor.getRedMode();
	}

	public void run() {
		while (LineFollowing) {
			float[] colorResults = new float[colorProvider.sampleSize()];
			colorProvider.fetchSample(colorResults, 0);
			float curColor = colorResults[0];
			if(curColor > 1) {
				
			} else if () {
				
			} else

		}

	}

	public void end() {
		LineFollowing = false;

	}
}
