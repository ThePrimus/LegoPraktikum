package logic;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
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
 * Main class. Handles the program logic. Allows to start a certain obstacle
 * mode for the robot by a key press. Each mode/obstacle is handled in a
 * different class.
 * 
 * @author Group 1
 */
public class GUI {

	/*
	 * If the program should be executed in the race-mode: obstacles/their
	 * corresponding classes are executed one after another from the selected
	 * obstacle in the main menu until the end. If false, only the selected
	 * obstacle algorithm is executed, then the robot stops without switching to
	 * the next one.
	 */
	public static final boolean RACE_MODE = true;

	/*
	 * Thread that executes the solution algorithm for the obstacles.
	 */
	private Thread obstacleThread;

	// Current mode/state of the robot
	public static int PROGRAM_STATUS = -1; // The id of the currently running
											// obstacle program
	public static boolean PROGRAM_STOP = false;

	// If an obstacle program finished completion and the next one should be
	// loaded.
	public static boolean PROGRAM_CHANGED = false;

	// If an obstacle program finished completion and the search for a barcode
	// should be started.
	public static boolean PROGRAM_FINISHED_START_BARCODE = false;

	// Constant/Id that defines the different obstacles/programs

	// Ids equal to barcode
	public static final int PROGRAM_MAZE = 0;
	public static final int PROGRAM_FINAL_SPURT = 1;
	public static final int PROGRAM_FOLLOW_LINE = 2;
	public static final int PROGRAM_BRIDGE = 3;
	public static final int PROGRAM_SEESAW = 4;
	public static final int PROGRAM_CHAIN_BRDIGE = 5;
	public static final int PROGRAM_ROLLS = 6;

	// Other parkour elements: id not equal to any barcode
	public static final int PROGRAM_FINAL_BOSS = 8;

	public static final int PROGRAM_ELEVATOR = 9;
	public static final int PROGRAM_EXIT = 10;
	public static final int PROGRAM_BARCODE = 11;

	// All sensors of the robot
	private EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(
			MotorPort.A);
	private EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(
			MotorPort.B);
	private EV3MediumRegulatedMotor sonicMotor = new EV3MediumRegulatedMotor(
			MotorPort.D);
	private EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S1);
	private EV3UltrasonicSensor sonicSensor = new EV3UltrasonicSensor(
			SensorPort.S2);
	private EV3TouchSensor touchLeftSensor = new EV3TouchSensor(SensorPort.S3);
	private EV3TouchSensor touchRightSensor = new EV3TouchSensor(SensorPort.S4);

	// The class that handles the movement and navigation of the robot.
	private Drive drive = new Drive(leftMotor, rightMotor);

	// The obstacle programs
	Barcode barcode;
	private LineFollowing lineFollowing;
	private Maze maze;
	private Bridge bridge;
	private Elevator elevator;
	private Seesaw seesaw;
	private ChainBridge chainBridge;
	private Rolls rolls;
	private EndBoss endboss;


	/**
	 * Initializes the main menu that enables the user to select a certain
	 * obstacle mode.
	 */
	public GUI() {

		LCD.clear(); // Make sure display is clear before the menu is displayed

		// Stop current obstacle program if the left button is pressed
		Button.LEFT.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(Key k) {
				endAllPrograms();
			}

			@Override
			public void keyReleased(Key k) {
			}

		});

		// Stop program when the escape button is pressed on the ev3 brick
		Button.ESCAPE.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(Key k) {
				PROGRAM_STOP = true;
				drive.stop();

				if (obstacleThread != null) {
					obstacleThread.interrupt();
				}

				endAllPrograms();

				// Start the GUI again => main menu should be shown
				// when the obstacle program has been interrupted
				startGUI();
			}

			@Override
			public void keyReleased(Key k) {
			}
		});

		startGUI();
	}

	/*
	 * Helper method to initialize the GUI/creating the main menu.
	 */
	private void startGUI() {
		// Creating the menu to select certain robot states/obstacles.
		while (true) {

			// The elements of the menu to display on the ev3 brick
			String[] viewItems = {"Labyrinth", "Linie folgen", "Bruecke",
					"Haengebruecke", "Rollen", "Wippe", "Aufzug", "Endspurt",
					"Endgegner", "Exit", "Barcode"};

			TextMenu menu = new TextMenu(viewItems, 1);
			// LCD.clear();
			int selection = menu.select();

			/*
			 * Menu selection. Selection number is the index of the element in
			 * the viewItems array.
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
			} else if (selection == 9) {
				// Terminate whole program on ev3 brick
				LCD.clear();
				PROGRAM_STATUS = PROGRAM_EXIT;
				PROGRAM_STOP = true;
				System.exit(0);
			} else if (selection == 10) {
				// Barcode
				LCD.clear();
				LCD.drawString("Mode: Barcode", 0, 0);
				PROGRAM_STATUS = PROGRAM_BARCODE;
				barcode(true);
			}

			PROGRAM_STOP = false;
			PROGRAM_STATUS = -1;
		}
	}

	/*
	 * Terminates all programs that might currently run.
	 */
	private void endAllPrograms() {
		if (barcode != null) {
			barcode.end();
		}
		if (lineFollowing != null) {
			lineFollowing.end();
		}
		if (maze != null) {
			maze.end();
		}
		if (bridge != null) {
			bridge.end();
		}
		if (seesaw != null) {
			seesaw.end();
		}
		if (chainBridge != null) {
			chainBridge.end();
		}
		if (rolls != null) {
			rolls.end();
		}
		if (endboss != null) {
			endboss.end();
		}
		if (elevator != null) {
			elevator.end();
		}
	}

	/*
	 * Initializing the maze mode.
	 */
	private void maze() {

		this.maze = new Maze(drive, sonicSensor, sonicMotor, touchLeftSensor,
				touchRightSensor);
		maze.run();
		
		// Start search for barcode.
		if (RACE_MODE && PROGRAM_FINISHED_START_BARCODE) {
			PROGRAM_FINISHED_START_BARCODE = false;
			LCD.clear();
			System.out.println("Mode: Barcode");
			barcode(true);
		}
	}

	/*
	 * Initializing the follow line mode.
	 */

	private void followLine() {
		this.lineFollowing = new LineFollowing(drive, colorSensor);
		lineFollowing.run();

		// Start search for barcode.
		if (RACE_MODE && PROGRAM_FINISHED_START_BARCODE) {
			PROGRAM_FINISHED_START_BARCODE = false;
			LCD.clear();
			System.out.println("Mode: Barcode");
			barcode(true);
		}
	}

	/*
	 * Initializing the bridge mode.
	 */
	private void bridge() {
		this.bridge = new Bridge(drive, sonicMotor, leftMotor, rightMotor,
				sonicSensor, colorSensor);
		bridge.run();

		// Change bridge -> elevator
		if (RACE_MODE && PROGRAM_CHANGED
				&& PROGRAM_STATUS == PROGRAM_ELEVATOR) {
			PROGRAM_CHANGED = false;
			LCD.clear();
			System.out.println("Mode: Elevator");
			elevator();
		}
	}

	/*
	 * Initializing the chain bridge mode.
	 */
	private void chainBridge() {
		this.chainBridge = new ChainBridge(drive, sonicSensor, sonicMotor,
				colorSensor);
		chainBridge.run();

		// Start search for barcode.
		if (RACE_MODE && PROGRAM_FINISHED_START_BARCODE) {
			PROGRAM_FINISHED_START_BARCODE = false;
			LCD.clear();
			System.out.println("Mode: Barcode");
			barcode(true);
		}
	}

	/*
	 * Initializing the roll mode.
	 */
	private void rolls() {
		this.rolls = new Rolls(drive, sonicSensor, sonicMotor, colorSensor);
		rolls.run();

		// Start search for barcode.
		if (RACE_MODE && PROGRAM_FINISHED_START_BARCODE) {
			PROGRAM_FINISHED_START_BARCODE = false;
			LCD.clear();
			System.out.println("Mode: Barcode");
			barcode(true);
		}
	}

	/*
	 * Initializing the seesaw mode.
	 */
	private void seesaw() {
		this.seesaw = new Seesaw(drive, colorSensor);
		seesaw.run();

		// Start search for barcode.
		if (RACE_MODE && PROGRAM_FINISHED_START_BARCODE) {
			PROGRAM_FINISHED_START_BARCODE = false;
			LCD.clear();
			System.out.println("Mode: Barcode");
			barcode(true);
		}
	}

	/*
	 * Initializing the elevator mode.
	 */
	private void elevator() {
		this.elevator = new Elevator(drive, colorSensor, touchLeftSensor,
				touchRightSensor, sonicSensor, sonicMotor);
		elevator.run();

		// Start search for barcode.
		if (RACE_MODE && PROGRAM_FINISHED_START_BARCODE) {
			PROGRAM_FINISHED_START_BARCODE = false;
			LCD.clear();
			System.out.println("Mode: Barcode");
			barcode(true);
		}
	}

	/*
	 * Initializing the final spurt.
	 */
	private void finalSpurt() {
		FinalSpurt finalSpurt = new FinalSpurt(drive, sonicSensor,
				touchLeftSensor, sonicMotor);
		obstacleThread = new Thread(finalSpurt);
		obstacleThread.start();

		// Change final spurt -> final boss
		if (RACE_MODE && PROGRAM_CHANGED
				&& PROGRAM_STATUS == PROGRAM_FINAL_BOSS) {
			PROGRAM_CHANGED = false;
			LCD.clear();
			System.out.println("Mode: Final Boss");
			finalBoss();
		}
	}

	/*
	 * Initializing the final boss mode.
	 */
	private void finalBoss() {
		this.endboss = new EndBoss(drive, touchLeftSensor, touchRightSensor,
				sonicMotor, sonicSensor);
		endboss.run();
	}

	/*
	 * Start program to read a barcode. If a valid barcode could be found the
	 * next program will be loaded.
	 */
	private void barcode(boolean moveRobot) {
		barcode = new Barcode(drive, colorSensor, moveRobot);
		barcode.run();

		if (barcode != null) {
			int foundBarcode = barcode.getBarcode();
			LCD.clear();
			//System.out.println("Barcode: " + foundBarcode);

			if (foundBarcode != -1) {
				// Change the current program if a valid barcode has been found
				changeProgram(foundBarcode);
			}
		}
	}

	/*
	 * Changes the program, because a barcode has been detected.
	 * Make sure the previous program is ended/canceled properly before 
	 * 
	 * @param barcode the barcode that has been detected.
	 */
	private void changeProgram(final int barcode) {
		if (RACE_MODE) {
			if (barcode == PROGRAM_FOLLOW_LINE) {
				followLine();
			} else if (barcode == PROGRAM_FINAL_SPURT) {
				finalSpurt();
			} else if (barcode == PROGRAM_BRIDGE) {
				bridge();
			} else if (barcode == PROGRAM_SEESAW) {
				seesaw();
			} else if (barcode == PROGRAM_CHAIN_BRDIGE) {
				chainBridge();
			} else if (barcode == PROGRAM_ROLLS) {
				rolls();
			}
		}
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 *            program arguments
	 */
	public static void main(String[] args) {
		new GUI();
	}
}
