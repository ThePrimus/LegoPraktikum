package parkour;

import lejos.hardware.BrickFinder;
import lejos.hardware.Key;
import lejos.hardware.Keys;
import lejos.hardware.Sound;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import lejos.utility.SensorSelectorException;
import logic.Drive;
/**
 * Implements the logic to beat the chain bridge obstacle.
 * 
 * @author Group 1
 */
public class ChainBridge {

	private static final float DISTANCE_TO_GROUND = 0.15f; // in m
	/*
	 * The navigation class.
	 */
	private Drive drive;

	/*
	 * The distance between the two walls of the final spurt (measured from
	 * sonic sensor, when robot is at the left wall).
	 */
	private static final float DISTANCE_TO_WALL = 0.12f;

	private SampleProvider distanceProvider;

	private final int SONIC_SENSOR_WALL_POS = -30;
	private final int SONIC_SENSOR_GROUND_POS = -100;
	private final int GAP_TIME_DIFF = 4;
	private EV3MediumRegulatedMotor sonicMotor;
	private boolean runStartBridge = true;
	private boolean runColorFollow = true;
	private EV3ColorSensor colorSensor;
	private boolean runBridgeRoutine2 = true;
	private boolean runBridgeRoutine3 = true;
	private boolean runEndBridge = true;
	public static boolean runBridgeRoutine1 = true;
	
	private float gapFound = 0;
	private SensorMode colorProvider;

	/**
	 * Constructor:
	 * 
	 * @param drive
	 *            the drive class for navigation and motor control.
	 */
	public ChainBridge(Drive drive, EV3UltrasonicSensor sonicSensor,
			EV3MediumRegulatedMotor sonicMotor, EV3ColorSensor sensor) {
		this.drive = drive;
		this.sonicMotor = sonicMotor;
		this.distanceProvider = sonicSensor.getDistanceMode();
		this.colorSensor = sensor;
		this.colorProvider = sensor.getRedMode();
	}

	/**
	 * Solution for the chained bridge.
	 *
	 * Idea: move forward until the bar code is scanned, that informs about the
	 * end of the obstacle. Correct movement to left if the sonic sensor
	 * measures a high distance (abbys detected).
	 */
	public void run() {

		// Grundsätzlich müsste anfangs noch ein kurzer Linefollow, der beendet
		// sobald vllt die Wand
		// erkannt wird oder die Linie zu ende ist
	//	linefollowing(); // <- war mein kurzer Test am vormittag

	//	idea1();

	// idea2();

		idea3();

		// TODO: Missing: move sonic sensor back to initial position
	}

	/*
	 * Klappe Sensor auf waagrechte Höhe, folge dann der Wand in einem
	 * bestimmten Abstand bis die Wand nicht mehr erkannt wird. Klappe dann den
	 * Sensor schräg zum Boden/Wand, damit er einen Abstand zur übrigen Wand als
	 * auch (hoffentlich zum Abgrund der Brücke erkennt) Folgt dann nach auch am
	 * Ende weiterhin der Wand bis er am ende (falls keine wand mehr da ist)
	 * entweder darüber es erkennt stehen zu bleiben oder beim Anfang des
	 * Barcodes (ersteres ist sicher fehleranfälliger)
	 */
	private void idea3() {
		sonicMotor.setAcceleration(1000);
		sonicMotor.rotate(SONIC_SENSOR_WALL_POS - 2, true);
		sonicMotor.waitComplete(); // short wait to make
							// sure that it's in the
							// right position
		startBridgeRoutine();
	
		sonicMotor.setAcceleration(1000);
	
		int correctPosition = 40; // 45° to position and wall
	
	//	sonicMotor.rotate(SONIC_SENSOR_GROUND_POS + correctPosition, true);
	//	sonicMotor.waitComplete(); // short wait to make sure that it's in the right
							// position
		bridgeRoutine3();
	}

	private void startBridgeRoutine() {
		while (runStartBridge) {
			// get current distance
			float[] sonicSensorResults = new float[distanceProvider
					.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			float curPos = sonicSensorResults[0];
	
			// wall can't be detected therefore it's lower then the sensor
			if (curPos > 0.2) {
				drive.stop();
				break;
			} else if (curPos > 0.10) { // move right if robot is to far
												// from wall
				drive.moveForward((drive.maxSpeed() * 0.3f),
						(drive.maxSpeed() * 0.2f));
	
			} else {// move left if if to close to wall
				drive.moveForward((drive.maxSpeed() * 0.2f),
						(drive.maxSpeed() * 0.3f));
			}
		}
	}

	private void bridgeRoutine3() {
		// It should follow remaining wall and the chainbridge
		// I hope it will follow also the start of the end part of the wall
		// should be tested
		
		drive.setAcceleration(1000);
		//drive.moveForward(300,302);
		drive.moveForward(700, 830);
		Delay.msDelay(1000);
		runBridgeRoutine3 = true;
		runColorFollow = true;
		
		while(runColorFollow){
			
			float[] sonicSensorResults = new float[distanceProvider
			                   					.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			                   			float curPos = sonicSensorResults[0];
	
			  // wall can't be detected therefore it's lower then the sensor
			    if (curPos < 0.2) {
			   	//	drive.stop();
			    	runColorFollow = false;
			       	break;
				}
		}
	
		while (runBridgeRoutine3) {
			float[] colorResults = new float[colorProvider.sampleSize()];
			colorProvider.fetchSample(colorResults, 0);
			float curColor = colorResults[0];
			
			
			
			// get current distance
			float[] sonicSensorResults = new float[distanceProvider
					.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			float curPos = sonicSensorResults[0];
			
			if(curColor > 0.9) {
				Sound.beep();
				drive.stop();
				break;
			}
	
			// wall can't be detected therefore it's lower then the sensor
		//	if (curPos > 0.2) {
		//		drive.stop();
		//		break;
		//	} else 
			if (curPos > 0.12) { // move right if robot is to far
												// from wall
				drive.moveForward((drive.maxSpeed() * 0.3f),
						(drive.maxSpeed() * 0.2f));
	
			} else {// move left if if to close to wall
				drive.moveForward((drive.maxSpeed() * 0.2f),
						(drive.maxSpeed() * 0.3f));
			}
		}
		
		drive.moveDistance(300, -7);
	
	}

	/*
	 * Klappe Sensor auf waagrechte Höhe, folge dann der Wand in einem bis die
	 * Wand nicht mehr erkannt wird. Klappe nach unten Folge Brücke an der Kante
	 * (ähnlich Bridge) Problem: Wie erkennen dass er nicht beim Ende der Brücke
	 * weiterhin am Rand fahren soll, da sonst er sich aufhängt, da beim
	 * hinunterfahren die Wand größer wird
	 */
	private void idea1() {
		sonicMotor.setAcceleration(1000);
		sonicMotor.rotate(SONIC_SENSOR_WALL_POS, true);
		Delay.msDelay(500); // short wait to make sure that it's in the right
							// position
		startBridgeRoutine();

		sonicMotor.setAcceleration(1000);
		sonicMotor.rotate(SONIC_SENSOR_GROUND_POS, true);
		Delay.msDelay(500); // short wait to make sure that it's in the right
							// position
		bridgeRoutine1();

		// can't come up with a good end routine, see bridgeROutine1();
	}
	/**
	 * Detects if the chain bridge ended.
	 * Idea: Check if a gap was encountered in a given time
	 * @return
	 */
	private boolean detectEndOfBridge() {
		boolean end = false;
		float []samples = new float[colorSensor.sampleSize()];
		float diff = 0;
		float curTime = 0 ;
		
		if(samples[0] < 0.2) {
			curTime = (float) System.currentTimeMillis();
			diff = Math.abs(gapFound - curTime);
			
			if(diff > GAP_TIME_DIFF) {
				end = true;
			}
		}
		return end;
	}

	/*
	 * Klappe Sensor auf waagrechte Höhe, folge dann der Wand in einem
	 * bestimmten Abstand bis die Wand nicht mehr erkannt wird. Fahre einfach
	 * gerade aus und hoffe, dass man es über das Ziel schafft Ende erkennen:
	 * Moglichkeit 1: Über den Sonic Sensor in dem er wieder eine Wand erkennt
	 * (Fehleranfällig) sich neu ausrichtet und bei Linie stehen bleibt für
	 * Barcode scan Möglichkeit 2: Einfach weiter fahren, bis Linie
	 */
	private void idea2() {
		sonicMotor.setAcceleration(1000);
		sonicMotor.rotate(SONIC_SENSOR_WALL_POS, true);
		Delay.msDelay(500); // short wait to make
							// sure that it's in the
							// right position

		startBridgeRoutine();
		bridgeRoutine2();

		// if the idea for detecting the wall after the chain bridge
		// then follow the wall until the barcode
		endBridgeRoutine();

	}

	private void bridgeRoutine1() {
		// move for a given time forward to make sure its on the chain bridge
		// prevents it drives towards the remaining wall
		drive.moveForward((int) (drive.maxSpeed() * 1),
				(int) (drive.maxSpeed() * 1));
		Delay.msDelay(1000);

		float curPos = 0;

		// TODO: Don't know how I can detect that the chain bridge has ended if
		// I am looking down it can happen that the robot drives on the wall
		while (runBridgeRoutine1) {
			float[] samples = new float[distanceProvider.sampleSize()];
			distanceProvider.fetchSample(samples, 0);
			curPos = samples[0];

			// see Bridge class, works well for it
			if (curPos > DISTANCE_TO_GROUND) { // Driving towards abyss therefor
												// turn left
				drive.setSpeedLeftMotor(drive.maxSpeed() * 0.3f);
				drive.setSpeedRightMotor(drive.maxSpeed());

			} else {// on the bridge so turn right to follow right side of the
					// bridge
				drive.setSpeedLeftMotor(drive.maxSpeed() * 0.3f);
				drive.setSpeedRightMotor(drive.maxSpeed());
			}

		}
		drive.stop();
	}

	private void bridgeRoutine2() {
		// just move forward
		drive.setAcceleration(2000);
		drive.moveForward((int) (drive.maxSpeed() * 0.6),
				(int) (drive.maxSpeed() * 0.6));

		while (runBridgeRoutine2) {
			float[] sonicSensorResults = new float[distanceProvider
					.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			float curPos = sonicSensorResults[0];
			// Missing detect chain end function
			// see two possibilites in the idea2 explanation

			drive.moveForward((int) (drive.maxSpeed() * 0.6),
					(int) (drive.maxSpeed() * 0.6));
		}

	}

	private void endBridgeRoutine() {
		while (runEndBridge) {
			// get current position
			float[] sonicSensorResults = new float[distanceProvider
					.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			float curPos = sonicSensorResults[0];

			// Missing: Detect end of wall
			// Maybe use sonic sensor (curPos > 0.8) which maybe
			// fail if the sonic sensor makes mistakes after the chain bridge
			// or use the color sensor to detect a white line for a barcode

			// follow wall
			if (curPos > DISTANCE_TO_WALL) {
				drive.moveForward((int) (drive.maxSpeed() * 0.3),
						(int) (drive.maxSpeed() * 0.2));
			} else {
				drive.moveForward((int) (drive.maxSpeed() * 0.2),
						(int) (drive.maxSpeed() * 0.3));
			}
		}
	}

	private void linefollowing() {
		runColorFollow = true;
		colorSensor.setCurrentMode("Red");
		while (runColorFollow) {
			float[] colorResults = new float[colorSensor.sampleSize()];
			colorSensor.fetchSample(colorResults, 0);
			float curColor = colorResults[0];

			float[] sonicSensorResults = new float[distanceProvider
					.sampleSize()];
			distanceProvider.fetchSample(sonicSensorResults, 0);
			float curPos = sonicSensorResults[0];

			if (curPos < 0.3) {
				drive.stop();
			//	runColorFollow = false;
			}

			if (curColor > 0.15) {
				drive.moveForward(drive.maxSpeed() * 0.7f,
						drive.maxSpeed() * 0.1f);
			} else  if (curColor < 0.1){
				drive.moveForward(drive.maxSpeed() * 0.1f,
						drive.maxSpeed() * 0.5f);
			} else {
				drive.moveForward(drive.maxSpeed() * 0.3f,
						drive.maxSpeed() * 0.1f);
			}

		}

	}

	public void end() {
		runBridgeRoutine1 = false;
		runStartBridge = false;
		runBridgeRoutine2 = false;
		runBridgeRoutine3 = false;
		runEndBridge = false;
		runColorFollow = false;
		drive.stop();
	}
}
