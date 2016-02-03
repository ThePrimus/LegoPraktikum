package parkour;

import logic.Drive;

/**
 * Implements the logic to beat the final enemy of the parkour.
 * 
 * @author Group 1
 */
public class EndBoss {

	// The navigation class.
	private Drive drive;
	
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public EndBoss(Drive drive) {
		this.drive = drive;
	}
}
