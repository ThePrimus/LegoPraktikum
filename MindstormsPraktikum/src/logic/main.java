package logic;

import java.awt.Button;

import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.TextMenu;
import parkour.Bridge;
import parkour.ChainBridge;
import parkour.Elevator;
import parkour.EndBoss;
import parkour.Maze;
import parkour.Rolls;
import parkour.Seesaw;

/**
 * Main class. Handles the program logic. Allows to start a certain obstacle mode 
 * for the robot by a key press. Each mode/obstacle is handled in a different class.
 * 
 * @author Group 1
 */
public class main {
	
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
	public static final int PROGRAM_FINAL_BOSS = 7;
	
	// All sensors of the robot
	
	
	
	
	
	public static void main(String[] args) {		
		EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
		leftMotor.setAcceleration(1000);
		leftMotor.setSpeed(1000);
		
		EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		rightMotor.setAcceleration(1000);
		rightMotor.setSpeed(1000);
		
		LCD.clear();	// Make sure display is clear before the menu is displayed
	
		
		/*
		EV3 ev3 = (EV3) BrickFinder.getLocal();
		Keys keys = ev3.getKeys();
		
		keys.waitForAnyPress();
		*/
		/*for (int i = 0; i < 10000; i++) {
			leftMotor.backward();
			rightMotor.backward();
		}*/
		
		
		
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
		
		// stop program when pressing ESCAPE
		/*Button.ESCAPE.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button arg0) {
				PROGRAM_STOP = true;
			}

			public void buttonReleased(Button arg0) {
			}
		});*/
		
		
		/*
		 * Creating the menu to select certain robot states/obstacles.
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
									"Endgegner" };
				
			TextMenu menu = new TextMenu(viewItems, 1);
			//LCD.clear();
			int selection = menu.select();
			
			/*
			 * Menu selection. Selection number is the index of the element in the viewItems array.
			 */
			if (selection == -1) {
				// ESCAPE. End loop
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
			} else if (selection == 2) {
				// Bridge
				LCD.clear();
				LCD.drawString("Mode: Bruecke", 0, 0);
				PROGRAM_STATUS = PROGRAM_BRIDGE;
			} else if (selection == 3) {
				// Bridge
				LCD.clear();
				LCD.drawString("Mode: Haengebruecke", 0, 0);
				PROGRAM_STATUS = PROGRAM_CHAIN_BRDIGE;
			} else if (selection == 4) {
				// Bridge
				LCD.clear();
				LCD.drawString("Mode: Rollen", 0, 0);
				PROGRAM_STATUS = PROGRAM_ROLLS;
			} else if (selection == 5) {
				// Bridge
				LCD.clear();
				LCD.drawString("Mode: Wippe", 0, 0);
				PROGRAM_STATUS = PROGRAM_SEESAW;
			} else if (selection == 6) {
				// Bridge
				LCD.clear();
				LCD.drawString("Mode: Aufzug", 0, 0);
				PROGRAM_STATUS = PROGRAM_ELEVATOR;
			} else if (selection == 7) {
				// Bridge
				LCD.clear();
				LCD.drawString("Mode: Endgegner", 0, 0);
				PROGRAM_STATUS = PROGRAM_FINAL_BOSS;
			} 
			
			PROGRAM_STOP = false;
			PROGRAM_STATUS = -1;
		}
	}
	
	/**
	 * Initializing the maze mode.
	 */
	public static void maze() {
		Maze maze = new Maze();
	}
	
	/**
	 * Initializing the follow line mode.
	 */
	public static void followLine() {
		LineFollowing lineFollowing = new LineFollowing();
	}
	
	/**
	 * Initializing the bridge mode.
	 */
	public static void bridge() {
		Bridge bridge = new Bridge();
	}
	
	/**
	 * Initializing the chain bridge mode.
	 */
	public static void chainBridge() {
		ChainBridge chainBridge = new ChainBridge();
	}
	
	/**
	 * Initializing the roll mode.
	 */
	public static void rolls() {
		Rolls rolls = new Rolls();
	}
	
	/**
	 * Initializing the seesaw mode.
	 */
	public static void seesaw() {
		Seesaw seesaw = new Seesaw();
	}
	
	/**
	 * Initializing the elevator mode.
	 */
	public static void elevator() {
		Elevator elevator = new Elevator();
	}
	
	/**
	 * Initializing the final boss mode.
	 */
	public static void finalBoss() {
		EndBoss endBoss = new EndBoss();
	}
}
