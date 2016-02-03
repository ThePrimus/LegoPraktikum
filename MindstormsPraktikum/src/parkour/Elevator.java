package parkour;

import logic.Drive;

/**
 * Implements the logic to beat the elevator obstacle.
 * 
 * @author Group 1
 */
public class Elevator {

	// The navigation class.
	private Drive drive;
	
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public Elevator(Drive drive) {
		this.drive = drive;
	}
}
