package logic;

import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class main {

	public static void main(String[] args) throws InterruptedException {
		LCD.drawString("Test Team 1", 0, 4);
		
		EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
		leftMotor.setAcceleration(1000);
		leftMotor.setSpeed(1000);
		
		EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		rightMotor.setAcceleration(1000);
		rightMotor.setSpeed(1000);
		
		
		EV3 ev3 = (EV3) BrickFinder.getLocal();
		Keys keys = ev3.getKeys();
		
		keys.waitForAnyPress();
	
		
		for (int i = 0; i < 100000; i++) {
			leftMotor.backward();
			rightMotor.backward();
		}
		
		
		
/*
		while(true){
			leftMotor.backward();
			rightMotor.backward();
			if(keys.waitForAnyEvent() > 0) {
				rightMotor.stop();
				leftMotor.stop();
				break;
			}
		}

		*/
	}

}
