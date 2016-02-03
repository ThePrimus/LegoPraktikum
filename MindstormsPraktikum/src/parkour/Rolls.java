package parkour;

import logic.Drive;

/**
 * Implements the logic to beat the rolls obstacle.
 * 
 * @author Group 1
 */
public class Rolls {

	// The navigation class.
	private Drive drive;
	
	
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public Rolls(Drive drive) {
		this.drive = drive;
	}
}
