package parkour;

import java.io.IOException;

import connection.ComModule;
import connection.Communication;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;
import lejos.utility.Delay;
import logic.Collision;
import logic.Drive;
import logic.GUI;

/**
 * Implements the logic to beat the elevator obstacle.
 * 
 * @author Group 1
 */
public class Elevator {

	// The navigation class.
	private Drive drive;
	private EV3ColorSensor colorSensor;
	private boolean runCallElevator = true;
	private boolean runWaitForElevator = true;
	private boolean runWaitForFree = true;
	private boolean runEnterElevator = true;
	private boolean runGoDown = true;
	private SensorMode colorProvider;
	private EV3TouchSensor touchRightSensor;
	private EV3TouchSensor touchLeftSensor;
	private Collision collisionDetection;

	private final int SONIC_SENSOR_WALL_POS = -30;
	private final int SONIC_SENSOR_GROUND_POS = -90;
	private EV3MediumRegulatedMotor sonicMotor;
	private ComModule communication;
	private SampleProvider distanceProvider;
	private boolean runMoveForwardUntilTouch = true;
	private static final float ABYSS_THRESHOLD = 0.10f; // in m

	/**
	 * Constructor:
	 * 
	 * @param drive
	 *            the drive class for navigation and motor control.
	 */
	public Elevator(Drive drive) {
		this.drive = drive;
	}

	public Elevator(Drive drive, EV3ColorSensor colorSensor,
			EV3TouchSensor touchLeftSensor, EV3TouchSensor touchRightSensor,
			EV3UltrasonicSensor sonicSensor,
			EV3MediumRegulatedMotor sonicMotor) {
		this.drive = drive;
		this.colorSensor = colorSensor;
		this.colorProvider = colorSensor.getAmbientMode();
		this.touchLeftSensor = touchLeftSensor;
		this.sonicMotor = sonicMotor;
		this.touchRightSensor = touchRightSensor;
		this.collisionDetection = new Collision(false, drive, touchLeftSensor,
				touchRightSensor, sonicSensor);
		this.communication = Communication.getModule();
		this.distanceProvider = sonicSensor.getDistanceMode();
	}

	public void run() {
		callElevator();
		initPosition();
		waitForElevator();
		enterElevator();
		goDownAndLeaveElevator();
	}

	private void initPosition() {
		drive.moveDistance(300, 5);
		drive.stopSynchronized();
	}

	private void callElevator() {

		// check if free
		boolean isFree = false;
		while (runWaitForFree) {
			try {
				isFree = communication.requestStatus();
			} catch (IOException e1) {
			//	e1.printStackTrace();
			}
			if (isFree) {
				break;
			}
			Delay.msDelay(100);
		}

		// is free then call for it

		boolean isOk = false;
		while (runCallElevator) {
			try {
				isOk = communication.requestElevator();
			} catch (IOException e1) {
				//e1.printStackTrace();
			}
			if (isOk) {
				break;
			}
			Delay.msDelay(100);
		}
	}

	private void waitForElevator() {
		MedianFilter filter = new MedianFilter(colorProvider, 5);
		while (runWaitForElevator) {
			// get ambient
			float[] colorResults = new float[filter.sampleSize()];
			filter.fetchSample(colorResults, 0);
			float curColor = colorResults[0];

			// Elevator is ready, could also be down with http
			if (curColor > 0.20) {
				Sound.beepSequence();
				break;
			}
		}

	}

	private void enterElevator() {
		float curPos = 0;
		drive.moveForward(drive.maxSpeed() * 0.3f,drive.maxSpeed() * 0.3f);
		while(runMoveForwardUntilTouch ){		

			float[] sampleR = new float[touchRightSensor.sampleSize()];
			touchRightSensor.fetchSample(sampleR, 0);
			if (sampleR[0] == 1) {
				drive.stopSynchronized();
				Sound.buzz();
				break;
			}
			
			
			// get distance to ground
			float[] samples = new float[distanceProvider.sampleSize()];
			distanceProvider.fetchSample(samples, 0);
			curPos = samples[0];

			if (curPos > ABYSS_THRESHOLD) { // Driving towards abyss therefor
											// turn left
				drive.setSpeedLeftMotor(drive.maxSpeed() * 0.1f);
				drive.setSpeedRightMotor(drive.maxSpeed()*0.5f);
			} else { // on the bridge so turn right to follow right side of the
						// bridge
				drive.setSpeedLeftMotor(drive.maxSpeed()*0.5f);
				drive.setSpeedRightMotor(drive.maxSpeed() * 0.1f);
			}
			
		}

		sonicMotor.setAcceleration(100);
		sonicMotor.rotate(-(SONIC_SENSOR_GROUND_POS + SONIC_SENSOR_WALL_POS), true);
		sonicMotor.waitComplete();
		drive.moveDistance(300, -10);
		drive.stopSynchronized();
		Delay.msDelay(1000);
		drive.turnLeft(15,true);
		drive.stopSynchronized();
		

		Sound.beepSequenceUp();

		drive.moveForward(300, 300);
		while (runEnterElevator) {
			float[] sampleL = new float[touchLeftSensor.sampleSize()];
			touchLeftSensor.fetchSample(sampleL, 0);

			float[] sampleR = new float[touchRightSensor.sampleSize()];
			touchRightSensor.fetchSample(sampleR, 0);
			if (sampleL[0] == 1 && sampleR[0] == 1) {
				drive.stopSynchronized();
				Sound.buzz();
				break;
			} else if (sampleL[0] == 1 && sampleR[0] == 0) {
				Delay.msDelay(200);

				sampleR = new float[touchRightSensor.sampleSize()];
				touchRightSensor.fetchSample(sampleR, 0);
				
				if(sampleR[0] == 1){
					break;
				}
				
				drive.moveDistance(300, -10);
				drive.turnRight(15);
				drive.moveForward(300, 300);
			} else if (sampleL[0] == 0 && sampleR[0] == 1) {
				Delay.msDelay(200);
				sampleL = new float[touchLeftSensor.sampleSize()];
				touchLeftSensor.fetchSample(sampleL, 0);
				if(sampleL[0] == 1){
					break;
				}
				drive.moveDistance(300, -10);
				drive.turnLeft(15);
				drive.moveForward(300, 300);
			}

		}
		drive.moveDistance(300, -5);
	}

	private void goDownAndLeaveElevator() {

		// keep asking elevator if it's okay to go down
		boolean isOk = false;
		while (runGoDown) {
			try {
				isOk = communication.moveElevatorDown();
			} catch (IOException e1) {
			//	e1.printStackTrace();
			}

			if (isOk) {
				Sound.playTone(440, 2000);
				break;
			}
			Delay.msDelay(100);
		}

		// wait for 10 seconds until elevator is down
		Delay.msDelay(5000);
		Sound.buzz();
		Sound.buzz();
		//drive.moveDistance(500, 20);
		
		GUI.PROGRAM_FINISHED_START_BARCODE = true;

	}

	public void end() {
		runCallElevator = false;
		runWaitForElevator = false;
		runEnterElevator = false;
		runWaitForFree = false;
		runGoDown = false;
		runMoveForwardUntilTouch = false;
	}
}
