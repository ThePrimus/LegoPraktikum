package parkour;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MeanFilter;
import lejos.robotics.filter.SampleThread;
import lejos.utility.Delay;
import logic.Drive;
import logic.GUI;

/**
 * Implements the logic to beat the seesaw obstacle.
 * 
 * @author Group 1
 */
public class Seesaw {

	// The navigation class.
	private Drive drive;
	private SampleProvider colorProvider;
	private boolean runColorFollow;


	/**
	 * Constructor:
	 * 
	 * @param drive
	 *            the drive class for navigation and motor control.
	 * @param colorSensor
	 * @param sonicMotor
	 * @param sonicSensor
	 */
	public Seesaw(Drive drive, EV3ColorSensor colorSensor) {
		this.drive = drive;
		this.colorProvider = colorSensor.getRedMode();
	}
	
	
	// Idea:Follow right side of the line
	// TODO: Adjust values
	// see : https://www.youtube.com/watch?v=tViA21Y08cU
	public void run() {
		runColorFollow = true;
		float algorithmStart = System.nanoTime();
		drive.moveDistance(500, 10);
		drive.stopSynchronized();
		while (runColorFollow) {
			float[] colorResults = new float[colorProvider.sampleSize()];
			colorProvider.fetchSample(colorResults, 0);
			float curColor = colorResults[0] * 1.25f;

			if (curColor > 0.6) {
				drive.moveForward(600, 300);
			} else  if (curColor < 0.4){
				drive.moveForward(300,
						400);
			} else {
				drive.moveForward(300, 300);
			}
			if (((System.nanoTime() - algorithmStart) / 1000000000.0f) > 6.5) {
				end();
				break;
			}
		}
		
		drive.moveDistance(500, 10);
		drive.stopSynchronized();
		Sound.buzz();
		GUI.PROGRAM_FINISHED_START_BARCODE = false;
	}

	public void end() {
		drive.stopSynchronized();
		runColorFollow = false;
	}
}
