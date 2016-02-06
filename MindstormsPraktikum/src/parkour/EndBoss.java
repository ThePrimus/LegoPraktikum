package parkour;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import logic.Drive;

/**
 * Implements the logic to beat the final enemy of the parkour.
 * 
 * @author Group 1
 */
public class EndBoss {

	// The navigation class.
	private Drive drive;
	private EV3MediumRegulatedMotor sonicMotor;
	private SampleProvider distanceProvider;
	private EV3TouchSensor touchLeftSensor;
	private EV3TouchSensor touchRightSensor;

	/**
	 * Constructor:
	 * 
	 * @param drive
	 *            the drive class for navigation and motor control.
	 */
	public EndBoss(Drive drive) {
		this.drive = drive;
	}

	public EndBoss(Drive drive2, EV3TouchSensor touchLeftSensor,
			EV3TouchSensor touchRightSensor, EV3MediumRegulatedMotor sonicMotor,
			EV3UltrasonicSensor sonicSensor) {
		this.drive = drive;
		this.sonicMotor = sonicMotor;
		this.distanceProvider = sonicSensor.getDistanceMode();
		this.touchLeftSensor = touchLeftSensor;
		this.touchRightSensor = touchRightSensor;
	}

	public void end() {
		// TODO Auto-generated method stub

	}

	/*
	 * Idea: Drive forward if touch sensor detects something, stop, drive
	 * backwards, make a right turn to drive to right side, follow it. If it
	 * again touches something, stop, go backwards, drive a curve. if it touches
	 * again start again
	 */
	public void run() {

	}

}
