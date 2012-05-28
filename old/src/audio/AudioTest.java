package audio;

import java.io.File;

import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecWav;
import settings.Settings;

public class AudioTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Instantiate the SoundSystem:

		SoundSystem mySoundSystem = null;
		try {
			//mySoundSystem = new SoundSystem(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.setCodec("wav",CodecWav.class);
		} catch (SoundSystemException sse) {
			// Shouldn’t happen, but it is best to prepare for anything
			sse.printStackTrace();
			return;
		}
		for (int i = 0; i < 10; i++) {
			mySoundSystem.quickPlay(true, "explosion.wav", false, 0,
					i * 10 - 50, 0, SoundSystemConfig.ATTENUATION_ROLLOFF,
					SoundSystemConfig.getDefaultRolloff());
			sleep(300);
		}
		sleep(3000);
		// Shut down:
		mySoundSystem.cleanup();
	}

	public static void sleep(long seconds) {
		try {
			Thread.sleep(seconds);
		} catch (InterruptedException e) {
		}
	}

}
