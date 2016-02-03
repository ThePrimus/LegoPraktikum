package parkour;

import logic.Drive;

/**
 * Implements the logic to beat the maze obstacle.
 * 
 * @author Group 1
 */
public class Maze {

	// The navigation class.
	private Drive drive;
	
	
	/**
	 * Constructor:
	 * 
	 * @param drive the drive class for navigation and motor control.
	 */
	public Maze(Drive drive) {
		this.drive = drive;
	}
	
}
