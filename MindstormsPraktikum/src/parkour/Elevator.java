package parkour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.utility.Delay;
import logic.Drive;

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
	private boolean runEnterElevator = true;
	private boolean runGoDown = true;
	private SensorMode colorProvider;
	private EV3TouchSensor touchRightSensor;
	private EV3TouchSensor touchLeftSensor;

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
			EV3TouchSensor touchLeftSensor, EV3TouchSensor touchRightSensor) {
		this.drive = drive;
		this.colorSensor = colorSensor;
		this.colorProvider = colorSensor.getRGBMode();
		this.touchLeftSensor = touchLeftSensor;
		this.touchRightSensor = touchRightSensor;
	}

	public void run() {
		initPosition();
		callElevator();
		waitForElevator();
		enterElevator();
		goDownAndLeaveElevator();
	}

	private void initPosition() {
		// drive left, so that the robot is in front of the elevator
		drive.moveForward(drive.maxSpeed() * 0.3f, drive.maxSpeed() * 0.5f);
		Delay.msDelay(2000);
		drive.stop();

		// make a right turn so its perpendicular to the elevator
		drive.moveForward(drive.maxSpeed() * 0.3f, drive.maxSpeed() * 0.3f);
		drive.rightBackward(drive.maxSpeed() * 0.3f);
		Delay.msDelay(2000);
		drive.stop();

	}

	private void callElevator() {

		while (runCallElevator) {
			// get color
			float[] colorResults = new float[colorProvider.sampleSize()];
			colorProvider.fetchSample(colorResults, 0);
			float curBlue = colorResults[2];

			// call elevator if blue (free)
			String response;
			if (curBlue > 0.8) {
				try {
					response = httpGet("/go_up/");
					if (response.equals("OK")) {
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	private void waitForElevator() {
		while (runWaitForElevator) {
			// get color
			float[] colorResults = new float[colorProvider.sampleSize()];
			colorProvider.fetchSample(colorResults, 0);
			float curGreen = colorResults[1];

			// Elevator is ready, could also be down with http
			if (curGreen > 0.8) {
				break;
			}
		}

	}

	private void enterElevator() {
		drive.moveForward(drive.maxSpeed() * 0.3f, drive.maxSpeed() * 0.3f);

		while (runEnterElevator) {
			SensorMode leftSensor = touchLeftSensor.getTouchMode();
			float[] left = new float[leftSensor.sampleSize()];
			leftSensor.fetchSample(left, 0);

			SensorMode rightSensor = touchLeftSensor.getTouchMode();
			float[] right = new float[rightSensor.sampleSize()];
			leftSensor.fetchSample(right, 0);

			// if both sensors are pressed then robot is inside the elevator
			if (left[0] == 1 && right[0] == 1) {
				drive.stop();
				break;
			}
		}

	}

	private void goDownAndLeaveElevator() {
		// call elevator
		String response;

		// keep asking elevator if it's okay to go down
		while (runGoDown) {
			try {
				response = httpGet("/go_down/");
				if (response.equals("OK")) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// wait for 10 seconds until elevator is down
		Delay.msDelay(10000);

		// TODO Start BarCode

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
		runGoDown = false;
	}
}
