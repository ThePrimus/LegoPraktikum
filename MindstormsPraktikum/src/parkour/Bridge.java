package parkour;

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
}
