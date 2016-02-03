package parkour;

import logic.Drive;

/**
 * Implements the logic to beat the chain bridge obstacle.
 * 
 * @author Group 1
 */
public class ChainBridge {

	// The navigation class.
	private Drive drive;
	
	
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public ChainBridge(Drive drive) {
		this.drive = drive;
	}
}
