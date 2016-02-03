package parkour;

import logic.Drive;

/**
 * Implements the logic for the final spurt (drive as fast as possible).
 * 
 * @author Group 1
 */
public class FinalSpurt {
	
	// The navigation class.
	private Drive drive;
	
	
	/**
	 * Constructor: 
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public FinalSpurt(Drive drive) {
		this.drive = drive;
		
		this.drive.moveForward(drive.maxSpeed(), drive.maxSpeed());
	}
}
