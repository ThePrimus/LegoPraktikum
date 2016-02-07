package logic;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.Delay;
import logic.Drive;

public class Collision {
	
	private Drive drive;
	private EV3TouchSensor leftSensor;
	private EV3TouchSensor rightSensor;
	private EV3UltrasonicSensor sonicSensor;
	private float speed;
	private String collision;
	private boolean terminateCollisionEstimation;
	private boolean destructionMode;
	
	private final int TIME_TO_WAIT = 2000;
	private final int TIME_TO_DESTRUCT = 2000;
	private final float DISTANCE_TO_WALL = 0.01f;
	
	public Collision(boolean destructionMode, Drive drive, EV3TouchSensor leftSensor, EV3TouchSensor rightSensor, EV3UltrasonicSensor sonicSensor) {
		this.drive = drive;
		this.leftSensor = leftSensor;
		this.rightSensor = rightSensor;
		this.sonicSensor = sonicSensor;
		speed = drive.maxSpeed();
		collision = "none";
		terminateCollisionEstimation = false;
		this.destructionMode = destructionMode;
	}

	public void end()
	{
		terminateCollisionEstimation = true;
	}
	/**
	 * Kills other robots
	 * @param programm Current program running
	 */
	public void killThemAll(String program) {
		
		if(program == "")
		{
			drive.moveForward(drive.maxSpeed());
			Delay.msDelay(TIME_TO_DESTRUCT);
		}
		
	}
	private void elevatorCollision()
	{
		float[] dist = new float[sonicSensor.getDistanceMode().sampleSize()];
		sonicSensor.getDistanceMode().fetchSample(dist, 0);
		
		if((dist[0] - DISTANCE_TO_WALL) > 0.005)
		{
			drive.turnRight(5, false);
		} else {
			drive.turnLeft(5, false);
		}
		if(collision == "Wall")
		{
			drive.stop();
		} else if(collision == "Left Wall") {
			drive.turnRight(5, false);
		} else if(collision == "Right Wall") {
			drive.moveDistance(drive.maxSpeed(), -2);
			drive.turnLeft(20, false);
		}
	}
	/**
	 * Estimates the collision object depending on current program
	 * @param program Current program running
	 * @param delay Time to wait in ms
	 * @param destructionMode If collision object is a robot KILL IT ^^
	 * @return collision Estimated collision object
	 */
	public String estimateCollision(String program, int delay) {
		
		float [] leftSample = new float[leftSensor.getTouchMode().sampleSize()];
		float [] rightSample= new float[rightSensor.getTouchMode().sampleSize()];
		int leftTouch = 0;
		int rightTouch = 0;
		int touchCount = 0;
		boolean leftTouched = false;
		boolean rightTouched = false;
		collision = "none";
		
		while(!terminateCollisionEstimation)
		{
			leftSensor.getTouchMode().fetchSample(leftSample, 0);
			leftSensor.getTouchMode().fetchSample(leftSample, 0);
			leftTouch = (int) leftSample[0];
			rightTouch = (int) rightSample[0];

			//for Bridge, ChainBridge, Elevator, Rolls, Seesaw
			//Elevator dürfte nicht vorkommen
			//TODO: Collision mit einem Sensor Roboter
			if(leftTouch != 0 && rightTouch != 0) {
				drive.stop();
				Delay.msDelay(delay);
				touchCount++;
				if(touchCount == 2)
				{
					collision = "Wall";
					break;
				}
			} else if(leftTouch != 0) {
				leftTouched = true;
				drive.stop();
				Delay.msDelay(delay);
				if(!rightTouched)
				{
					touchCount++;
				}
				else
				{
					rightTouched = false;
				}
				if(touchCount == 2)
				{
					collision = "Left Wall";
					break;
				}
			} else if(rightTouch != 0) {
				rightTouched = true;
				drive.stop();
				Delay.msDelay(delay);
				if(!leftTouched)
				{
					touchCount++;
				}
				else
				{
					leftTouched = false;
				}
				if(touchCount == 2)
				{
					collision = "Right Wall";
					break;
				} 
			} else if (touchCount < 2 && touchCount != 0){
				touchCount = 0;
				collision = "Robot";
				break;
			}
				
		}
		if((program == "ChainBridge" || program == "LineFollowing" ||
				program == "Rolls" || program == "Seesaw" || program == "Maze") && collision == "Robot") {
			if(destructionMode)
			{
				killThemAll("");
			} else {
				drive.stop();
				Delay.msDelay(TIME_TO_WAIT);
			}
		} else if(program == "Elevator") {
			elevatorCollision();
		} else if(program == "Bridge") {
						
		} else if(program == "EndBoss") {
			
		}	
		return collision;
	}
}
