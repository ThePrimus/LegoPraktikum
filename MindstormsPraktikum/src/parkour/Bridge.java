package parkour;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import logic.Drive;

/**
 * Implements the logic to beat the bridge obstacle.
 * 
 * @author Group 1
 */
public class Bridge {

	private static final float DISTANCE_TO_GROUND = 0.010f; // in m 
	// The navigation class.
	private Drive drive;
	private EV3MediumRegulatedMotor sonicMotor;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private EV3UltrasonicSensor sonicSensor;
	
	
	private final int SONICS_SENSOR_POS = 90;
	private SampleProvider distanceProvider;
	
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public Bridge(Drive drive) {
		this.drive = drive;
	}
	
	public Bridge(Drive drive, EV3MediumRegulatedMotor sonicMotor, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, EV3UltrasonicSensor sonicSensor ){
		this.drive = drive;
		this.sonicMotor = sonicMotor;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.sonicSensor = sonicSensor;
		this.distanceProvider = sonicSensor.getDistanceMode();
	}
	
	public void run(){
		initSonicMotor();
	//	sonicTest();
		
	//	drive.moveBackward(500, 400);
	//	float [] samples = new float[distanceProvider.sampleSize()];
		
	//	while(!(samples[0] > DISTANCE_TO_GROUND)){
			
	//	}
	}



	private void sonicTest() {
		float [] samples = new float[distanceProvider.sampleSize()];
		for(int i = 0; i <= 10; i++) {
			distanceProvider.fetchSample(samples, 0);
			LCD.drawString(String.valueOf(samples[0]), 0,0);
			Delay.msDelay(1000);
			LCD.clear();
		}
		
	}

	private void initSonicMotor() {

		
		//sonicMotor.rotateTo(SONICS_SENSOR_POS);
		/*
		if(Math.abs(distanceProvider.sampleSize() - INITIAL_POS) <= 5){
			return;
		} else {
			initSonicMotor();
		}
		*/
	}

	public Bridge(Drive drive2, EV3MediumRegulatedMotor sonicMotor) {
	//	float pos = sonicMotor.getPosition();
	//	for(int i = 0; i <= 1000; i++) {
	//		pos = sonicMotor.getPosition();
		//	LCD.drawString(String.valueOf(pos), 0, 0);
		//	Delay.msDelay(2000);
		//	LCD.clear();
	//	}
		
		
		sonicMotor.rotate(90);
		/*
		
		for(int i = 0; i <= 90; i++){
			sonicMotor.rotate(1);
			LCD.drawString(String.valueOf(sonicMotor.getPosition()), 0, 0);
			Delay.msDelay(1000);
			LCD.clear();
		}
	*/
	
	//	sonicMotor.setSpeed(200);
		//sonicMotor.rotateTo(0);
		//Delay.msDelay(1000);
		//sonicMotor.rotate(90);
/*
		Delay.msDelay(2000);
		sonicMotor.rotateTo(180);

		Delay.msDelay(2000);
		sonicMotor.rotate(90);
		*/
	}
}
