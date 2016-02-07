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
	private float speed;
	private String collision;
	private boolean terminateCollisionEstimation;
	
	Collision(Drive drive, EV3TouchSensor leftSensor, EV3TouchSensor rightSensor, EV3MediumRegulatedMotor sonicMotor, EV3UltrasonicSensor sonicSensor) {
		this.drive = drive;
		this.leftSensor = leftSensor;
		this.rightSensor = rightSensor;
		speed = drive.maxSpeed();
		collision = "";
		terminateCollisionEstimation = false;
	}

	/**
	 * Kills other robots
	 * @param programm Current program running
	 */
	public void killThemAll(String programm) {
		
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
		
		while(!terminateCollisionEstimation)
		{
			leftSensor.getTouchMode().fetchSample(leftSample, 0);
			leftSensor.getTouchMode().fetchSample(leftSample, 0);
			leftTouch = (int) leftSample[0];
			rightTouch = (int) rightSample[0];

			//for Bridge, ChainBridge, Elevator, Rolls, Seesaw
			//Elevator dürfte nicht vorkommen
			if(leftTouch != 0 && rightTouch != 0) {
				drive.stop();
				Delay.msDelay(delay);
				touchCount++;
				if(touchCount == 2)
				{
					collision = "Wall";
				}
			}
			else if (touchCount == 2){
				touchCount = 0;
				collision = "Robot";
			}
			if((program == "Bridge" || program == "ChainBridge" || program == "LineFollowing" ||
			    program == "Rolls" || program == "Seesaw") ) {
				
			}
		}


		return collision;
	}
}
