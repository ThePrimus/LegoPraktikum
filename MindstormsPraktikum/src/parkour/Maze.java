package parkour;

import logic.Drive;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.port.MotorPort;

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
	private EV3TouchSensor touchSensorLeft;
	private EV3TouchSensor touchSensorRight;
	private SampleProvider leftTouchProvider;
	private SampleProvider rightTouchProvider;
	private SampleProvider distanceProvider;
	
	private final float DISTANCE_TO_WALL = 5;
	private float speed;
	private final float lengthOfCar = 23; 
	private final float eps = 0.5f;
	


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
		touchSensorLeft = touchLeftSensor;
		touchSensorRight = touchRightSensor;
		leftTouchProvider = touchLeftSensor.getTouchMode();
		rightTouchProvider = touchRightSensor.getTouchMode();
		speed = drive.maxSpeed();
	}
	
	/*public void goForward(float distance) { // go forward for a certain distance
	    this.drive.leftMotor.rotate(distance * 2000 / this.TIRE_DIAMETER, true);
	    this.drive.rightMotor.rotate(distance * 2000 / this.TIRE_DIAMETER, true);
	    leftMotor.waitComplete();		
	    rightMotor.waitComplete();
	}*/
	
	
	public void PositionAdjust() { // adjust the distance to wall
	    drive.turnRight(90);
	    drive.moveForward(speed);
	    float [] leftSample = new float[leftTouchProvider.sampleSize()];
	    float [] rightSample = new float[rightTouchProvider.sampleSize()];
	    int leftTouched = 0;
	    int rightTouched = 0;
	    
	    boolean ProgramRunning = true;
	    while (ProgramRunning) {

	    	leftTouchProvider.fetchSample(leftSample, 0);
	    	leftTouched = (int) leftSample[0];

	    	rightTouchProvider.fetchSample(rightSample, 0);
	    	rightTouched = (int) rightSample[0];

	    	if (leftTouched == 1 && rightTouched == 1) {
	    		ProgramRunning = false;
	    	}
	    }
	    
	    drive.moveDistance(speed, -(DISTANCE_TO_WALL + eps));        
	    drive.turnLeft(90);
	    drive.moveForward(speed);
	}

	
	public void MazeRoutine() {
	   boolean ProgramRunning = true;
	   float [] curDistance  = new float[distanceProvider.sampleSize()];
	   float [] leftSample = new float[leftTouchProvider.sampleSize()];
	   float [] rightSample = new float[rightTouchProvider.sampleSize()];
	   
	   PositionAdjust(); //why??
	   
	   while (ProgramRunning) {
		   distanceProvider.fetchSample(curDistance, 0);
		   leftTouchProvider.fetchSample(leftSample, 0);
		   rightTouchProvider.fetchSample(rightSample, 0);
		   int leftTouched = (int) leftSample[0];
		   int rightTouched = (int) rightSample[0];
		  
		   if (curDistance[0] > DISTANCE_TO_WALL) { // sonic sensor measured a higher distance than the width of the path
			   drive.moveDistance(speed, lengthOfCar + DISTANCE_TO_WALL - eps);
			   drive.turnRight(90);
			   drive.moveDistance(speed, DISTANCE_TO_WALL); //curDista
			   drive.moveForward(speed);
		   } else if (leftTouched == 1 && rightTouched == 1) { //not for all cases
			   drive.moveDistance(speed,-(DISTANCE_TO_WALL + eps));
			   drive.turnLeft(90);
			  drive.moveForward(speed);
		   } /*else if (// TODO: Maze obstacle finished, when bar code is scanned) {
		 }*/
	   }

	    
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