package parkour;

import logic.Drive;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.EV3TouchSensor;
import logic.Collision;

/**
 * Implements the logic to beat the maze obstacle.
 * 
 * @author Group 1
 */
public class Maze {

	// The navigation class.
	private Drive drive;
	private EV3UltrasonicSensor sonicSensor;
	private EV3MediumRegulatedMotor sonicMotor;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private SampleProvider leftTouchProvider;
	private SampleProvider rightTouchProvider;
	private SampleProvider distanceProvider;
	private int leftCounter = 0;

	private final float DISTANCE_TO_WALL = 5;
	private final float WALL_WIDTH = 5;
	private final float DISTANCE_TO_TURN = 2;
	private final float TURN_THRESHOLD = 20;
	private float speed;
	private final float lengthOfCar = 23; 
	private final float eps = 0.5f;

	private boolean ProgramRunning;
	
	private Collision collisionDetection;

	/*
	 * The diameter of the tire in mm.
	 */
	private final float TIRE_DIAMETER = 34;

	/**
	 * Constructor:
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public Maze(Drive drive,EV3UltrasonicSensor sonicSensor, 
			EV3MediumRegulatedMotor sonicMotor, EV3TouchSensor touchLeftSensor, EV3TouchSensor touchRightSensor) {
		this.drive = drive;
		this.sonicMotor = sonicMotor;
		this.sonicSensor = sonicSensor;
		distanceProvider = sonicSensor.getDistanceMode();
		leftTouchProvider = touchLeftSensor.getTouchMode();
		rightTouchProvider = touchRightSensor.getTouchMode();
		speed = drive.maxSpeed();
		ProgramRunning = true;
		collisionDetection = new Collision(false, drive, touchLeftSensor, touchRightSensor, sonicSensor);
	}

	/*public void goForward(float distance) { // go forward for a certain distance
	    this.drive.leftMotor.rotate(distance * 2000 / this.TIRE_DIAMETER, true);
	    this.drive.rightMotor.rotate(distance * 2000 / this.TIRE_DIAMETER, true);
	    leftMotor.waitComplete();		
	    rightMotor.waitComplete();
	}*/


	public void PositionAdjust() { // adjust the distance to wall
		drive.moveDistance(speed, -DISTANCE_TO_TURN);
		drive.turnRight(90);
		leftCounter = 0;
		drive.moveForward(speed);
		float [] leftSample = new float[leftTouchProvider.sampleSize()];
		float [] rightSample = new float[rightTouchProvider.sampleSize()];
		int leftTouched = 0;
		int rightTouched = 0;

		while (ProgramRunning) {
			if(!ProgramRunning)
			{
				ProgramRunning = false;
				break;
			}
			leftTouchProvider.fetchSample(leftSample, 0);
			leftTouched = (int) leftSample[0];

			rightTouchProvider.fetchSample(rightSample, 0);
			rightTouched = (int) rightSample[0];

			if (leftTouched == 1 && rightTouched == 1) {
				ProgramRunning = false;
				break;
			}
		}

		drive.moveDistance(speed, -(DISTANCE_TO_WALL + eps));        
		drive.turnLeft(90);
		drive.moveForward(speed);
	}

	//TODO: Sensor Daten wie beim Wand Kollision erst überprüfen(Änderung)

	public void MazeRoutine() {
		float [] curDistance  = new float[distanceProvider.sampleSize()];
		float [] leftSample = new float[leftTouchProvider.sampleSize()];
		float [] rightSample = new float[rightTouchProvider.sampleSize()];
		int leftTouched = 0;
		int rightTouched = 0;
		int touchCount = 0;
		String collision = "";

		PositionAdjust(); //why?? if you want to set distance to wall do it while driving

		while (ProgramRunning) {
			if(!ProgramRunning)
			{
				ProgramRunning = false;
				break;
			}
			collision = collisionDetection.estimateCollision("Maze", 2000);
			
			distanceProvider.fetchSample(curDistance, 0);
			leftTouchProvider.fetchSample(leftSample, 0);
			rightTouchProvider.fetchSample(rightSample, 0);
			leftTouched = (int) leftSample[0];
			rightTouched = (int) rightSample[0];
				
			if (curDistance[0] * 100 > DISTANCE_TO_WALL && curDistance[0] * 100 > TURN_THRESHOLD ) { // sonic sensor measured a higher distance than the width of the path
				drive.stop();
				drive.moveDistance(speed, DISTANCE_TO_TURN);
				drive.turnRight(90);
				leftCounter = 0;
				drive.moveDistance(speed, 2 * DISTANCE_TO_WALL + WALL_WIDTH); //
				drive.moveForward(speed);
			} else if (curDistance[0] * 100 > DISTANCE_TO_WALL && curDistance[0] * 100 < TURN_THRESHOLD) {
				drive.stop();
				//PositionAdjust(); 
				//adjust on the fly
				//check speed parameters so that sonic sensor is nearly orthogonal to wall
				drive.moveForward(speed, speed * 0.6f);
			} else if (leftTouched == 1 && rightTouched == 1 && leftCounter < 4 && collision == "Wall") { //not for all cases
				drive.stop();
				drive.moveDistance(speed,-(DISTANCE_TO_WALL + eps));
				drive.turnLeft(90);
				drive.moveForward(speed);
				leftCounter++;
			} else if(curDistance[0] * 100 < DISTANCE_TO_WALL) {
				//check speed parameters so that sonic sensor is nearly orthogonal to wall
				drive.moveForward(speed * 0.6f, speed);
			}
			else if((curDistance[0] * 100 - DISTANCE_TO_WALL) < 0.5) {
				drive.moveForward(speed, speed);
			}
			/*else if (// TODO: Maze obstacle finished, when bar code is scanned) {
		 }*/
		}


	}

	public void end()
	{
		ProgramRunning = false;
	}

	/**
	 * Solution for the maze obstacle.
	 *
	 * Idea: move forward along the right wand,until the sonic sensor measures a higher distance 
	 * than the width of the path. Then turn right and balance the distance and move forward along the right wand.
	 * move forward until the bar code is scanned, that informs about the end of the obstacle.
	 */

	public void run() {
		MazeRoutine();
	}
}