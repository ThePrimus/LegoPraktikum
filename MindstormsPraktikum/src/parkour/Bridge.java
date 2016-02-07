package parkour;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import logic.Drive;
import logic.GUI;

/**
 * Implements the logic to beat the bridge obstacle.
 * 
 * @author Group 1
 */
public class Bridge {

	private static final float ABYSS_THRESHOLD = 0.15f; // in m
	// The navigation class.
	private Drive drive;
	private SampleProvider distanceProvider;

	private SampleProvider colorProvider;
	private boolean runEnterElevator = false;

	public static boolean runBridge = true;

	/**
	 * Constructor:
	 * 
	 * @param drive
	 *            the drive class for navigation and motor control.
	 */
	public Bridge(Drive drive) {
		this.drive = drive;
	}

	public Bridge(Drive drive, EV3MediumRegulatedMotor sonicMotor,
			EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			EV3UltrasonicSensor sonicSensor, EV3ColorSensor colorSensor) {
		this.drive = drive;
		this.distanceProvider = sonicSensor.getDistanceMode();
		this.colorProvider = colorSensor.getRGBMode();
	}

	private void followBridge() {
		float curPos = 0;
		while (!runBridge) {

			// get color
			float[] colorResults = new float[colorProvider.sampleSize()];
			colorProvider.fetchSample(colorResults, 0);
			float curRed = colorResults[0];
			float curGreen = colorResults[1];
			float curBlue = colorResults[2];

			// exit if reached lift which shows color red
			// TODO find correct threshold
			if (curRed > 0.8) {
				drive.stop();
				// TODO Handle start of elevator program
				// MOVE SONIC MOTOR TO INITAL POS
				break;
			}

			// exit if reached lift which shows color green
			// TODO find correct threshold
			if (curGreen > 0.8) {
				drive.stop();
				// TODO Handle start of elevator program
				// MOVE SONIC MOTOR TO INITAL POS
				break;
			}

			// exit if reached lift which shows color blue
			// TODO find correct threshold
			if (curBlue > 0.8) {
				drive.stop();
				// TODO Handle start of elevator program
				// MOVE SONIC MOTOR TO INITAL POS
				break;
			}

			// get distance to ground
			float[] samples = new float[distanceProvider.sampleSize()];
			distanceProvider.fetchSample(samples, 0);
			curPos = samples[0];

			if (curPos > ABYSS_THRESHOLD) { // Driving towards abyss therefor
											// turn left
				drive.setSpeedLeftMotor(drive.maxSpeed() * 0.3f);
				drive.setSpeedRightMotor(drive.maxSpeed());
			} else { // on the bridge so turn right to follow right side of the
						// bridge
				drive.setSpeedLeftMotor(drive.maxSpeed());
				drive.setSpeedRightMotor(drive.maxSpeed() * 0.8f);
			}
		}
		drive.stop();
		
		// Activate elevator program
		GUI.PROGRAM_CHANGED = true;
		GUI.PROGRAM_STATUS = GUI.PROGRAM_ELEVATOR;
	}

	public void run() {
		runBridge = true;
		followBridge();
	}

	public void end() {
		runBridge = false;
	}

}
