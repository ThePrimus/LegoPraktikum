package parkour;

import logic.Drive;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.port.MotorPort;

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
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private EV3TouchSensor touchSensorLeft;
	private EV3TouchSensor touchSensorRight;
	private SampleProvider leftTouchProvider;
	private SampleProvider rightTouchProvider;
	private SampleProvider distanceProvider;
	
	private final static float DISTANCE_TO_WALL = 0.05f;
	private final static float standSpeed;
	private final static float lengthOfCar = 0.23f; 
	private final static float eps = 0.005f;
	


	/*
	 * The diameter of the tire in mm.
	 */
	private static final float TIRE_DIAMETER = 34;
	
	/**
	 * Constructor:
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public Maze(Drive drive,EV3UltrasonicSensor sonicSensor, EV3MediumRegulatedMotor sonicMotor,EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,EV3TouchSensor touchLeftSensor,EV3TouchSensor touchRightSensor,) {
		this.drive = drive;
		this.sonicMotor = sonicMotor;
		this.sonicSensor = sonicSensor;
		this.distanceProvider = sonicSensor.getDistanceMode();
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.touchSensorLeft = touchLeftSensor;
		this.touchSensorRight = touchRightSensor;
		this.leftTouchProvider = touchLeftSensor.getTouchMode();
		this.rightTouchProvider = touchRightSensor.getTouchMode();
		this.standSpeed = drive.maxSpeed();
	}
	
	public void goForward(float distance) { // go forward for a certain distance
	    this.drive.leftMotor.rotate(distance * 2000 / this.TIRE_DIAMETER, true);
	    this.drive.rightMotor.rotate(distance * 2000 / this.TIRE_DIAMETER, true);
	    leftMotor.waitComplete();		
	    rightMotor.waitComplete();
	}
	
	
	public void PositionAdjust() { // adjust the distance to wall
	    this.drive.turnRight(90);
	    this.drive.forward(this.standSpeed);
	    
	    boolean ProgramRunning = true;
	    while (ProgramRunning) {
		
		float [] samples2 = new float[leftTouchProvider.sampleSize()];
		leftTouchProvider.fetchSample(samples2, 0);
		int leftTouched = (int) samples2[0];
		float [] samples3 = new float[rightTouchProvider.sampleSize()];
		rightTouchProvider.fetchSample(samples3, 0);
		int rightTouched = (int) samples3[0];
		    
		if (leftTouched == 1 && rightTouched == 1) ProgramRunning = false;
		
	    }
	    
	    this.goForward(-this.DISTANCE_TO_WALL + eps);
	    this.drive.turnLeft(90);
	    this.drive.forward(this.standSpeed);
	    
	}

	
	public void MazeRoutine() {
	   boolean ProgramRunning = true;
	   this.PositionAdjust();
	   while (ProgramRunning) {

		float [] samples1 = new float[distanceProvider.sampleSize()];
		 distanceProvider.fetchSample(samples1, 0);
		 float curDis = samples1[0];
		 
		 float [] samples2 = new float[leftTouchProvider.sampleSize()];
		 leftTouchProvider.fetchSample(samples2, 0);
		 int leftTouched = (int) samples2[0];
		 float [] samples3 = new float[rightTouchProvider.sampleSize()];
		 rightTouchProvider.fetchSample(samples3, 0);
		 int rightTouched = (int) samples3[0];

		 
		 if (curDis > this.DISTANCE_TO_WALL) { // sonic sensor measured a higher distance than the width of the path
		     this.goForward(this.lengthOfCar + this.DISTANCE_TO_WALL);
		     this.drive.turnRight(90);
		     this.goForward(eps + this.DISTANCE_TO_WALL);
		     this.drive.forward(this.standSpeed);
		 } else if (leftTouched == 1 && rightTouched == 1) { 
		     this.goForward(-this.DISTANCE_TO_WALL);
		     this.drive.turnLeft(90);
		     this.drive.forward(this.standSpeed);
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
       @Override
       public void run() {
	   this.MazeRoutine();
       }
}