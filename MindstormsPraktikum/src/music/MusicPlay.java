package music;

public class MusicPlay {

	private boolean playMusic = true;

	private static String[] gameOfThrones = {"A4", "D4", "F4", "G4", "A4", "A4",
			"D4", "F4", "G4", "A4", "D4", "F#4", "G4", "A4", "D4", "F4", "G4",
			"A4", "D4", "F4", "G4"};
	private static int[] gameOfThronesDuration = {1000, 1000, 500, 500, 1000,
			1000, 1000, 500, 500, 1000, 1000, 500, 500, 2000, 2000, 500, 500,
			2000, 2000};

	public void playGameOfThrones() {
		playMusic(gameOfThrones, gameOfThronesDuration);
	}

	private void playMusic(String[] melody, int[] duration) {
		Music music = new Music();
		while (playMusic) {
			for (int i = 0; i < melody.length; i++) {
				music.musicPiano(melody[i], duration[i]);
			}
		}
	}

	public void end() {
		playMusic = false;
	}
}