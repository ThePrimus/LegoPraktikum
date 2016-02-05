package parkour;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import logic.Drive;


import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.Keys;
/**
 * Implements the logic to beat the chain bridge obstacle.
 * 
 * @author Group 1
 */
public class ChainBridge implements Runnable {


	private static final float DISTANCE_TO_GROUND = 0.08f; // in m 
	
	private static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
	private static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.B);
	private static EV3MediumRegulatedMotor sonicMotor = new EV3MediumRegulatedMotor(MotorPort.D);
	private static EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S1);
	private static EV3UltrasonicSensor sonicSensor = new EV3UltrasonicSensor(SensorPort.S2);
	private static EV3TouchSensor touchLeftSensor = new EV3TouchSensor(SensorPort.S3);
	private static EV3TouchSensor touchRightSensor = new EV3TouchSensor(SensorPort.S4);
	
	
	/*
	 * The minimum distance that defines an abbys.
	 */
	private static final float DISTANCE_ABBYS = 0.08f;
	

	/*
	 * The navigation class.
	 */
	private Drive drive;
	
	

	public static boolean PROGRAM_STOP = false;
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public ChainBridge(Drive drive, EV3UltrasonicSensor sonicSensor, EV3MediumRegulatedMotor sonicMotor) {
		this.drive = drive;
		this.sonicSensor = sonicSensor;
		this.sonicMotor = sonicMotor;
		Button.ESCAPE.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(Key k) {
				PROGRAM_STOP = true;
			}

			@Override
			public void keyReleased(Key k) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	
	/**
	 * Solution for the chained bridge.
	 *
	 * Idea: move forward until the bar code is scanned, that informs about the end of the
	 * obstacle. Correct movement to left if the sonic sensor measures a high distance (abbys detected).
	 */
	@Override
	public void run() {
		
		SampleProvider distanceProvider = sonicSensor.getDistanceMode();
		int standLeftCorrection = (int) (leftMotor.getMaxSpeed() * 0.3);
		int standRightCorrection = (int) rightMotor.getMaxSpeed();

		int standLeft = (int) (leftMotor.getMaxSpeed());
		int standRight = (int) (rightMotor.getMaxSpeed()*0.9);
		
		// Make sure the sonic sensor is facing downwards
		//sonicMotor.rotateTo(SONIC_POSITION_SIDEWAYS, true);
		//sonicMotor.waitComplete();
		
		this.drive.moveForward((int) (drive.maxSpeed() * 0.97), drive.maxSpeed());
		
				

		float curPos = 0;
		leftMotor.setSpeed(standLeft);
		leftMotor.forward();
		

		rightMotor.setSpeed(standRight);
		rightMotor.forward();
		while(!PROGRAM_STOP){
			float [] samples = new float[distanceProvider.sampleSize()];
			 distanceProvider.fetchSample(samples, 0);
			 curPos = samples[0];
			 
			 /*
			 float [] colors = new float[distanceProvider.sampleSize()];
			 colorSensor.fetchSample(colors, 0);
			 colorSensor.setCurrentMode("Rgb");
			 float curColor = colors[0];
			 
			 if(curColor > COLOR_YELLOW) { // Lift is red
				 
			 } else if (curColor >=  COLOR_GREEN){ // lift is Yellow
				 
			} else { // lift is green
				
			}
			 */ 
			 if(curPos > DISTANCE_TO_GROUND) {
					leftMotor.setSpeed(standLeftCorrection);					
					rightMotor.setSpeed(standRightCorrection);
					rightMotor.forward();
					leftMotor.forward();
			 } else {	
					leftMotor.setSpeed(standLeft);
					rightMotor.setSpeed(standRight);
					rightMotor.forward();
					leftMotor.forward();
			 }
	}
	
}
}
