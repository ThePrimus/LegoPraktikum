package parkour;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;
import logic.Drive;
import logic.GUI;

/**
 * Implements the logic to beat the bridge obstacle.
 * 
 * @author Group 1
 */
public class Bridge {

	private static final float ABYSS_THRESHOLD = 0.10f; // in m
	// The navigation class.
	private Drive drive;
	private SampleProvider distanceProvider;
	

	private final int SONIC_SENSOR_WALL_POS = -30;
	private final int SONIC_SENSOR_GROUND_POS = -90;

	private SampleProvider colorProvider;
	private boolean runEnterElevator = false;
	private EV3MediumRegulatedMotor sonicMotor;

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
		this.colorProvider = colorSensor.getRedMode();
		this.sonicMotor = sonicMotor;
	}

	private void followBridge() {
		float curPos = 0;
		//drive.moveDistance(300, 10);
		MedianFilter filter = new MedianFilter(distanceProvider, 5);
		
		while (runBridge) {

			// get color
			float[] colorResults = new float[colorProvider.sampleSize()];
			colorProvider.fetchSample(colorResults, 0);
			float curColor = colorResults[0];

			// exit if reached lift which shows color red
			// TODO find correct threshold
			if (curColor > 0.5) {
				drive.stopSynchronized();
				runBridge = false;
				break;
			}

			// get distance to ground
			float[] samples = new float[filter.sampleSize()];
			filter.fetchSample(samples, 0);
			curPos = samples[0];

			if (curPos > ABYSS_THRESHOLD) { // Driving towards abyss therefor
											// turn left
				drive.setSpeedLeftMotor(200);
				drive.setSpeedRightMotor(800);
			} else { // on the bridge so turn right to follow right side of the
						// bridge
				drive.setSpeedLeftMotor(800);
				drive.setSpeedRightMotor(700); // vorher 500
			}
		}
		// Activate elevator program
		GUI.PROGRAM_CHANGED = true;
	 	GUI.PROGRAM_STATUS = GUI.PROGRAM_ELEVATOR;
	}

	public void run() {
		sonicMotor.setAcceleration(100);
		sonicMotor.rotate(SONIC_SENSOR_WALL_POS + SONIC_SENSOR_GROUND_POS, true);
		sonicMotor.waitComplete(); // short wait to make sure that it's in the right
							// position
		
	
		followBridge();
	}

	public void end() {
		runBridge = false;
	}

}
