package parkour;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
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
		drive.moveDistance(300, 290, 30);
		
		while (runBridge) {

			// get color
			float[] colorResults = new float[colorProvider.sampleSize()];
			colorProvider.fetchSample(colorResults, 0);
			float curColor = colorResults[0];

			// exit if reached lift which shows color red
			// TODO find correct threshold
			if (curColor > 0.3) {
				drive.stop();
				runBridge = false;
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
				drive.setSpeedRightMotor(drive.maxSpeed() * 0.6f);
			}
		}
		// Activate elevator program
	//	GUI.PROGRAM_CHANGED = true;
	//  	GUI.PROGRAM_STATUS = GUI.PROGRAM_ELEVATOR;
	}

	public void run() {
		sonicMotor.setAcceleration(1000);
		sonicMotor.rotate(SONIC_SENSOR_WALL_POS, true);
		sonicMotor.waitComplete(); // short wait to make sure that it's in the right
							// position

		sonicMotor.setAcceleration(1000);
		sonicMotor.rotate(SONIC_SENSOR_GROUND_POS, true);
		sonicMotor.waitComplete(); // short wait to make sure that it's in the right
							// position
		
	
		followBridge();
	}

	public void end() {
		runBridge = false;
	}

}
