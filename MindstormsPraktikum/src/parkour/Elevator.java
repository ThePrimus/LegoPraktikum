package parkour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import connection.ComModule;
import connection.Communication;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
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
	}

	public void run() {
		callElevator();
		initPosition();
		waitForElevator();
		enterElevator();
		goDownAndLeaveElevator();
	}

	private void initPosition() {
		drive.turnRight(45, false);
		drive.stop();
		sonicMotor.rotate(-SONIC_SENSOR_GROUND_POS, true);
	}

	private void callElevator() {

		// check if free
		boolean isFree = false;
		while (runWaitForFree) {
			try {
				isFree = communication.requestStatus();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (isFree) {
				break;
			}
		}

		// is free then call for it

		boolean isOk = false;
		while (runCallElevator) {
			try {
				isOk = communication.requestElevator();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (isOk) {
				break;
			}
		}
	}

	private void waitForElevator() {
		while (runWaitForElevator) {
			// get color
			float[] colorResults = new float[colorProvider.sampleSize()];
			colorProvider.fetchSample(colorResults, 0);
			float curColor = colorResults[0];

			// Elevator is ready, could also be down with http
			if (curColor > 0.2) {
				break;
			}
		}

	}

	private void enterElevator() {
		drive.moveDistance(300, 25);

		////
		drive.moveForward(300, 250);
		while (runEnterElevator) {
			float[] sampleL = new float[touchLeftSensor.sampleSize()];
			touchLeftSensor.fetchSample(sampleL, 0);

			float[] sampleR = new float[touchRightSensor.sampleSize()];
			touchRightSensor.fetchSample(sampleR, 0);
			if (sampleL[0] == 1 && sampleR[0] == 1) {
				drive.stop();
				break;
			} else if (sampleL[0] == 1 && sampleR[0] == 0) {
				drive.moveDistance(300, -5);
				drive.moveForward(300, 250);
			} else if (sampleL[0] == 1 && sampleR[0] == 0) {
				drive.moveDistance(300, -5);
				drive.moveForward(250, 300);
			}

		}
	}

	private void goDownAndLeaveElevator() {

		// keep asking elevator if it's okay to go down
		boolean isOk = false;
		while (runGoDown) {
			try {
				isOk = communication.moveElevatorDown();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			if (isOk) {
				break;
			}
		}
		// wait for 10 seconds until elevator is down
		Delay.msDelay(10000);
		GUI.PROGRAM_FINISHED_START_BARCODE = true;

	}

	public static String httpGet(String request) throws IOException {
		URL url = new URL("192.168.0.5" + request);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}

		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();

		conn.disconnect();
		return sb.toString();
	}

	public void end() {
		runCallElevator = false;
		runWaitForElevator = false;
		runEnterElevator = false;
		runWaitForFree = false;
		runGoDown = false;
	}
}
