package logic;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.utility.TextMenu;
import parkour.Bridge;
import parkour.ChainBridge;
import parkour.Elevator;
import parkour.EndBoss;
import parkour.FinalSpurt;
import parkour.Maze;
import parkour.Rolls;
import parkour.Seesaw;

/**
 * Main class. Handles the program logic. Allows to start a certain obstacle mode 
 * for the robot by a key press. Each mode/obstacle is handled in a different class.
 * 
 * @author Group 1
 */
public class main implements Runnable {
	
	// Current mode/state of the robot
	public static int PROGRAM_STATUS = -1;
	public static boolean PROGRAM_STOP = false;
	
	// Constants for the certain modes/obstacles
	public static final int PROGRAM_FOLLOW_LINE = 0;
	public static final int PROGRAM_MAZE = 1;
	public static final int PROGRAM_BRIDGE = 2;
	public static final int PROGRAM_CHAIN_BRDIGE = 3;
	public static final int PROGRAM_ROLLS = 4;
	public static final int PROGRAM_SEESAW = 5;
	public static final int PROGRAM_ELEVATOR = 6;
	public static final int PROGRAM_FINAL_SPURT = 7;
	public static final int PROGRAM_FINAL_BOSS = 8;
	
	// All sensors of the robot
	private static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
	private static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.B);
	private static EV3MediumRegulatedMotor sonicMotor = new EV3MediumRegulatedMotor(MotorPort.D);
	private static EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S1);
	private static EV3UltrasonicSensor sonicSensor = new EV3UltrasonicSensor(SensorPort.S2);
	private static EV3TouchSensor touchLeftSensor = new EV3TouchSensor(SensorPort.S3);
	private static EV3TouchSensor touchRightSensor = new EV3TouchSensor(SensorPort.S4);
	
	
	// The class that handles the movement and navigation of the robot.
	private static Drive drive = new Drive(leftMotor, rightMotor);
	
	
	/**
	 * Main class. Initializes the main menu that enables the user to select
	 * a certain obstacle mode.
	 * 
	 * @param args program arguments
	 */
	public static void main(String[] args) {	
		
		LCD.clear();	// Make sure display is clear before the menu is displayed
		
		/*
		EV3 ev3 = (EV3) BrickFinder.getLocal();
		Keys keys = ev3.getKeys();
		
		keys.waitForAnyPress();
		*/
		/*
		for (int i = 0; i < 10000; i++) {
			leftMotor.forward();
			rightMotor.forward();
		}*/

		
		// Stop program when the escape button is pressed on the ev3 brick
		Button.ESCAPE.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(Key k) {
				PROGRAM_STOP = true;
			}

			@Override
			public void keyReleased(Key k) {
			}
		});
		
		// Start GUI in separate thread.
		Thread GUIThread = new Thread(new main());
		GUIThread.start();		
	}
	
	
	/**
	 * Lets the GUI run in a separate thread.
	 */
	@Override
	public void run() {
		
		/*
		 * Creating the menu to select certain robot states/obstacles.
		 * Run GUI/menu in new thread.
		 */
		while (true) {
		
			// The elements of the menu to display on the ev3 brick
			String[] viewItems = {	"Labyrinth", 
									"Linie folgen", 
									"Bruecke",
									"Haengebruecke",
									"Rollen",
									"Wippe",
									"Aufzug",
									"Endspurt",
									"Endgegner"};
				
			TextMenu menu = new TextMenu(viewItems, 1);
			//LCD.clear();
			int selection = menu.select();
			
			/*
			 * Menu selection. Selection number is the index of the element in the viewItems array.
			 */
			if (selection == -1) {
				// ESCAPE button pressed. End loop
				break;
			} else if (selection == 0) {
				// Maze
				LCD.clear();
				LCD.drawString("Mode: Labyrinth", 0, 0);
				PROGRAM_STATUS = PROGRAM_MAZE;
				maze();
			} else if (selection == 1) {
				// Follow line
				LCD.clear();
				LCD.drawString("Mode: Linie folgen", 0, 0);
				PROGRAM_STATUS = PROGRAM_FOLLOW_LINE;
				followLine();
			} else if (selection == 2) {
				// Bridge
				LCD.clear();
				LCD.drawString("Mode: Bruecke", 0, 0);
				PROGRAM_STATUS = PROGRAM_BRIDGE;
				bridge();
			} else if (selection == 3) {
				// Chain bridge
				LCD.clear();
				LCD.drawString("Mode: Haengebruecke", 0, 0);
				PROGRAM_STATUS = PROGRAM_CHAIN_BRDIGE;
				chainBridge();
			} else if (selection == 4) {
				// Rolls
				LCD.clear();
				LCD.drawString("Mode: Rollen", 0, 0);
				PROGRAM_STATUS = PROGRAM_ROLLS;
				rolls();
			} else if (selection == 5) {
				// Seesaw
				LCD.clear();
				LCD.drawString("Mode: Wippe", 0, 0);
				PROGRAM_STATUS = PROGRAM_SEESAW;
				seesaw();
			} else if (selection == 6) {
				// Elevator
				LCD.clear();
				LCD.drawString("Mode: Aufzug", 0, 0);
				PROGRAM_STATUS = PROGRAM_ELEVATOR;
				elevator();
			} else if (selection == 7) {
				// Final spurt
				LCD.clear();
				LCD.drawString("Mode: Endspurt", 0, 0);
				PROGRAM_STATUS = PROGRAM_FINAL_SPURT;
				finalSpurt();
			} else if (selection == 8) {
				// Final boss
				LCD.clear();
				LCD.drawString("Mode: Endgegner", 0, 0);
				PROGRAM_STATUS = PROGRAM_FINAL_BOSS;
				finalBoss();
			} 
			
			PROGRAM_STOP = false;
			PROGRAM_STATUS = -1;
		}
	}
	
	
	/**
	 * Initializing the maze mode.
	 */
	public static void maze() {
		Maze maze = new Maze(drive);
	}
	
	/**
	 * Initializing the follow line mode.
	 */
	public static void followLine() {
		LineFollowing lineFollowing = new LineFollowing(drive, colorSensor);
	}
	
	/**
	 * Initializing the bridge mode.
	 */
	public static void bridge() {
		Bridge bridge = new Bridge(drive, sonicMotor, leftMotor, rightMotor, sonicSensor);
		bridge.run();
	}
	
	/**
	 * Initializing the chain bridge mode.
	 */
	public static void chainBridge() {
		ChainBridge chainBridge = new ChainBridge(drive);
	}
	
	/**
	 * Initializing the roll mode.
	 */
	public static void rolls() {
		Rolls rolls = new Rolls(drive);
	}
	
	/**
	 * Initializing the seesaw mode.
	 */
	public static void seesaw() {
		Seesaw seesaw = new Seesaw(drive);
	}
	
	/**
	 * Initializing the elevator mode.
	 */
	public static void elevator() {
		Elevator elevator = new Elevator(drive);
	}
	
	/**
	 * Initializing the final spurt.
	 */
	public static void finalSpurt() {
		FinalSpurt finalSpurt = new FinalSpurt(drive, sonicSensor, touchLeftSensor);
		Thread endspurtThread = new Thread(finalSpurt);
		endspurtThread.start();
	}
	
	/**
	 * Initializing the final boss mode.
	 */
	public static void finalBoss() {
		EndBoss endBoss = new EndBoss(drive);
	}

}
