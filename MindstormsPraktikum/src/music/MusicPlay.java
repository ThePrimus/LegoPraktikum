package music;

/**
 * Plays the Game of Thrones theme, e.g. during the final boss fight.
 * This music class runs in a separate thread, so that the robot can still
 * be controlled while the music is playing.
 * 
 * By Erik Nauman, feel free to use with attribution see:
 * http://www.lejos.org/forum/viewtopic.php?t=4719
 * 
 * Game of Thrones theme added by gruppe 1.
 */
public class MusicPlay implements Runnable {

	// If the music should be played.
	private boolean playMusic = true;

	// Sheet music and duration of the Game of Thrones theme.
	private static String[] gameOfThrones = {"A4", "D4", "F4", "G4", "A4", "A4",
			"D4", "F4", "G4", "A4", "D4", "F4", "G4", "A4", "D4", "F4", "G4",
			"A4", "D4", "F4", "G4"};
	private static int[] gameOfThronesDuration = {1000, 1000, 500, 500, 1000,
			1000, 1000, 500, 500, 1000, 1000, 500, 500, 2000, 2000, 500, 500,
			2000, 2000, 1000, 500};

	/**
	 * Constructor. Doesn't actually starts the music, music playback is handled
	 * by MusicPlay.run().
	 */
	public void playGameOfThrones() {
	}
	
	/**
	 * Plays the Game of Thrones theme in a separate thread.
	 */
	@Override
	public void run() {
		if (playMusic) {
			playMusic(gameOfThrones, gameOfThronesDuration);
		}
	}

	
	/*
	 * Plays the assigned melody with the assigned duration of the sheet music.
	 */
	private void playMusic(String[] melody, int[] duration) {
		Music music = new Music();
		while (playMusic) {
			for (int i = 0; i < melody.length; i++) {
				music.musicPiano(melody[i], duration[i]);
			}
		}
	}
	

	/**
	 * Ends the music.
	 */
	public void end() {
		playMusic = false;
	}
}