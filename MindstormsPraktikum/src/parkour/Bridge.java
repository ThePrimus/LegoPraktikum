package parkour;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.Delay;
import logic.Drive;

/**
 * Implements the logic to beat the bridge obstacle.
 * 
 * @author Group 1
 */
public class Bridge {

	// The navigation class.
	private Drive drive;
	
	
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public Bridge(Drive drive) {
		this.drive = drive;
	}



	public Bridge(Drive drive2, EV3MediumRegulatedMotor sonicMotor) {
	//	float pos = sonicMotor.getPosition();
	//	for(int i = 0; i <= 1000; i++) {
	//		pos = sonicMotor.getPosition();
		//	LCD.drawString(String.valueOf(pos), 0, 0);
		//	Delay.msDelay(2000);
		//	LCD.clear();
	//	}
		
		for(int i = 0; i <= 10; i++){
			sonicMotor.rotate(1);
			LCD.drawString(String.valueOf(sonicMotor.getPosition()), 0, 0);
			Delay.msDelay(1000);
			LCD.clear();
		}
	
	
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
