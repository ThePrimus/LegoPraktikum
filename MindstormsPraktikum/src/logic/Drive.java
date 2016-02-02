package logic;

import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Implements the logic to drive and navigate the robot.
 * 
 * @author Group 1
 */
public class Drive {

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
	 * Moves the robot backward until "stop" of this class is called.
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
	 * Turns the robot left by the assigned degree.
	 * 
	 * @param degree how far the robot should turn left.
	 */
	public void turnLeft(int degree) {
		
	}
	
	/**
	 * Turns the robot right by the assigned degree.
	 * 
	 * @param degree how far the robot should turn right.
	 */
	public void turnRight(int degree) {
		
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
	public void setSpeed(int speed) {
		this.speedLeft = speed;
		this.speedRight = speed;
	}
	
	/**
	 * Sets the acceleration for both motors (left and right).
	 * 
	 * @param the acceleration to set for both motors.
	 */
	public void setAcceleration(int acceleration) {
		this.accelerationLeft = acceleration;
		this.accelerationRight = acceleration;
	}
	
	/**
	 * The maximum speed of both motors.
	 * 
	 * @return the maximum speed of both motors.
	 */
	public float maxSpeed() {
		return this.maxSpeed;
	}
}
