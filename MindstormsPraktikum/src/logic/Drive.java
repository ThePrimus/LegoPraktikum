package logic;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Implements the logic to drive and navigate the robot.
 * 
 * @author Group 1
 */
public class Drive {

	/*
	 * The diameter of the tire in mm.
	 */
	private static final float TIRE_DIAMETER = 34;
	
	/*
	 * The distance between the left and right tire that are mounted on the
	 * two motors.
	 */
	private static final float DISTANCE_TIRES = 130;
	
	/*
	 * Pi
	 */
	private static final float PI = 3.14159265359f; 
	
	/*
	 * The left motor of the robot.
	 */
	private EV3LargeRegulatedMotor leftMotor;
	
	/*
	 * The right motor of the robot.
	 */
	private EV3LargeRegulatedMotor rightMotor;
	
	/*
	 * The maximum speed. Equal for left and right motor.
	 */
	private float maxSpeed;
	
	/*
	 * The speed of the left motor.
	 */
	private float speedLeft;
	
	/*
	 * The speed of the right motor.
	 */
	private float speedRight;
	
	/*
	 * The acceleration of the left motor.
	 */
	private int accelerationLeft;
	
	/*
	 * The acceleration of the right motor.
	 */
	private int accelerationRight;
	
	
	/**
	 * Initializes the drive class. On default a speed of 50% is set.
	 */
	public Drive (EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		maxSpeed = leftMotor.getMaxSpeed();
		
		speedLeft = leftMotor.getMaxSpeed() / 2;
		speedRight = rightMotor.getMaxSpeed() / 2;
	}
	
	
	/**
	 * Moves the robot forward until "stop" of this class is called.
	 * 
	 * @param speed the speed of the movement.
	 */
	public void moveForward(float speed) {
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	/**
	 * Moves the robot forward until "stop" of this class is called.
	 * 
	 * @param speedLeftMotor the speed for the left motor
	 * @param speedLeftMotor the speed for the right motor
	 */
	public void moveForward(float speedLeftMotor, float speedRightMotor) {
		leftMotor.setSpeed(speedLeftMotor);
		rightMotor.setSpeed(speedRightMotor);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	/**
	 * Moves the robot forward until "stop" of this class is called.
	 * Uses the speed that is currently set in this class.
	 */
	public void moveForward() {
		leftMotor.forward();
		rightMotor.forward();
	}
	
	/**
	 * Moves the robot forward until "stop" of this class is called.
	 * 
	 * @param speed the speed of the movement.
	 */
	public void moveBackward(float speed) {
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		leftMotor.backward();
		rightMotor.backward();
	}
	
	/**
	 * Moves the robot backward until "stop" of this class is called.
	 * 
	 * @param speedLeftMotor the speed for the left motor
	 * @param speedLeftMotor the speed for the right motor
	 */
	public void moveBackward(float speedLeftMotor, float speedRightMotor) {
		leftMotor.setSpeed(speedLeftMotor);
		rightMotor.setSpeed(speedRightMotor);
		leftMotor.backward();
		rightMotor.backward();
	}
	
	/**
	 * Moves the robot backward until "stop" of this class is called.
	 * Uses the speed that is currently set in this class.
	 */
	public void moveBackward() {
		leftMotor.backward();
		rightMotor.backward();
	}
	
	
	/**
	 * Moves the left motor backwards with the assigned speed.
	 * 
	 * @param speed the speed for the left motor.
	 */
	public void leftB(float speed){
		leftMotor.setSpeed(speed);
		leftMotor.backward();
	}
	
	
	/**
	 * Moves the right motor backwards with the assigned speed.
	 * 
	 * @param speed the speed for the right motor.
	 */
	public void rightB(float speed) {
		rightMotor.setSpeed(speed);
		rightMotor.backward();
	}
	
	
	/**
	 * Turns the robot right by the assigned degree.
	 * 
	 * @param degree how far the robot should turn right (degree between 0 and 360).
	 */
	public void turnRight(int degree) {		
		float distanceFullCircle = DISTANCE_TIRES * PI;
		float distanceToMove = distanceFullCircle / 360.0f * degree;
		
		float distanceOneRotation = TIRE_DIAMETER * PI;
		
		float amountRotations = distanceToMove / distanceOneRotation;
		int degreesToRotate = (int) (amountRotations * 360.0f);
		
		leftMotor.rotate(degreesToRotate, true);
		rightMotor.rotate((-1) * degreesToRotate, true);
		leftMotor.waitComplete();		// Wait for completion of turn
		rightMotor.waitComplete();
	}
	
	
	/**
	 * Turns the robot right by the assigned degree, but allows to define
	 * if both motors should rotate. Turn happens with maximum speed.
	 * 
	 * @param degree how far the robot should turn right (degree between 0 and 360).
	 * @param both if both motors should rotate.
	 */
	public void turnRight(int degree, boolean both) {		
		float distanceFullCircle = DISTANCE_TIRES * PI;
		float distanceToMove = distanceFullCircle / 360.0f * degree;
		
		float distanceOneRotation = TIRE_DIAMETER * PI;
		
		float amountRotations = distanceToMove / distanceOneRotation;
		int degreesToRotate = (int) (amountRotations * 360.0f);
		
		leftMotor.setSpeed(maxSpeed);
		leftMotor.rotate(degreesToRotate, true);
		if(both)
		{
			rightMotor.setSpeed(maxSpeed);
			rightMotor.rotate((-1) * degreesToRotate, true);
		}
		leftMotor.waitComplete();
		if(both)
		{// Wait for completion of turn
			rightMotor.waitComplete();
		}
	}
	
	/**
	 * Turns the robot left by the assigned degree.
	 * 
	 * @param degree how far the robot should turn left (degree between 0 and 360).
	 */
	public void turnLeft(int degree) {
		float distanceFullCircle = DISTANCE_TIRES * PI;
		float distanceToMove = distanceFullCircle / 360.0f * degree;
		
		float distanceOneRotation = TIRE_DIAMETER * PI;
		
		float amountRotations = distanceToMove / distanceOneRotation;
		int degreesToRotate = (int) (amountRotations * 360.0f);
		
		leftMotor.rotate((-1) * degreesToRotate, true);
		rightMotor.rotate(degreesToRotate, true);
		leftMotor.waitComplete();		// Wait for completion of turn
		rightMotor.waitComplete();
	}
	
	
	/**
	 * Turns the robot left by the assigned degree, but allows to define
	 * if both motors should rotate. Turn happens with maximum speed.
	 * 
	 * @param degree how far the robot should turn left (degree between 0 and 360).
	 * @param both if both motors should rotate.
	 */
	public void turnLeft(int degree, boolean both) {
		float distanceFullCircle = DISTANCE_TIRES * PI;
		float distanceToMove = distanceFullCircle / 360.0f * degree;
		
		float distanceOneRotation = TIRE_DIAMETER * PI;
		
		float amountRotations = distanceToMove / distanceOneRotation;
		int degreesToRotate = (int) (amountRotations * 360.0f);
		
		rightMotor.setSpeed(maxSpeed);
		if(both)
		{
			leftMotor.setSpeed(maxSpeed);
			leftMotor.rotate((-1) * degreesToRotate, true);
		}
		rightMotor.rotate(degreesToRotate, true);
		if(both)
		{// Wait for completion of turn
			leftMotor.waitComplete();
		}// Wait for completion of turn
		rightMotor.waitComplete();
	}
	
	/**
	 * Stops the current movement of the robot immediately.
	 */
	public void stop() {
		leftMotor.stop();
		rightMotor.stop();
	}
	
	/**
	 * Sets the speed for both motors (left and right).
	 * 
	 * @param the speed to set for both motors.
	 */
	public void setSpeed(float speed) {
		this.speedLeft = speed;
		this.speedRight = speed;
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	/**
	 * Sets the speed for both motors (left and right).
	 * 
	 * @param the speed to set for both motors.
	 */
	public void setSpeedLeftMotor(float speed) {
		this.speedLeft = speed;
		leftMotor.setSpeed(speed);
		leftMotor.forward();
	}
	
	/**
	 * Sets the speed for both motors (left and right).
	 * 
	 * @param the speed to set for both motors.
	 */
	public void setSpeedRightMotor(float speed) {
		this.speedRight = speed;
		rightMotor.setSpeed(speed);
		rightMotor.forward();
	}
	
	/**
	 * Sets the acceleration for both motors (left and right).
	 * 
	 * @param the acceleration to set for both motors.
	 */
	public void setAcceleration(int acceleration) {
		this.accelerationLeft = acceleration;
		this.accelerationRight = acceleration;
		leftMotor.setAcceleration(acceleration);
		rightMotor.setAcceleration(acceleration);
	}
	
	/**
	 * Returns the maximum speed of both motors.
	 * 
	 * @return the maximum speed of both motors.
	 */
	public float maxSpeed() {
		return this.maxSpeed;
	}
	
	/**
	 * Returns the average speed of both motors (degree/s).
	 * 
	 * @return the average speed of both motors (degree/s).
	 */
	public float getSpeed() {
		return (leftMotor.getRotationSpeed() + rightMotor.getRotationSpeed()) / 2.0f;
	}
	
}
