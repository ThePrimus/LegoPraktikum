package parkour;

import logic.Drive;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3TouchSensor;

/**
 * Implements the logic to beat the maze obstacle.
 * 
 * @author Group 1
 */
public class Maze implements Runnable {

	// The navigation class.
	private Drive drive;
	private EV3UltrasonicSensor sonicSensor;
	private EV3MediumRegulatedMotor sonicMotor;
	private EV3TouchSensor touchSensorLeft;
	private EV3TouchSensor touchSensorRight;
	
	
	/**
	 * Constructor:
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public Maze(Drive drive, EV3UltrasonicSensor sonicSensor, EV3MediumRegulatedMotor sonicMotor,
					EV3TouchSensor touchLeftSensor, EV3TouchSensor touchRightSensor) {
		this.drive = drive;
		this.sonicMotor = sonicMotor;
		this.sonicSensor = sonicSensor;
		this.touchSensorLeft = touchLeftSensor;
		this.touchSensorRight = touchRightSensor;
	}


	@Override
	public void run() {
	}

}
