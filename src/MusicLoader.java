import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import java.io.File;

/**
 * alle Sounds werden hier verwaltet
 */
public class MusicLoader {

    public static final int PAC_MAN_DEATH = 1;
    public static final int PAC_GHOST = 3;
    public static final int PAC_MAN_WON = 2;

    private static File deathSound1;
    private static File deathSound2;

    private static File gameWonSound;

    Clip clip;


    public MusicLoader() {

    }

    //sounds werden geladen
    public void load() {
        deathSound2 = new File("res/Pac-ManDeathSound1.wav");
        deathSound1 = new File("res/Pac-ManDeathSound2.wav");
        gameWonSound = new File("res/PacManwon_sound.wav");
    }

    /**
     * sounds werden abgespielt
     *
     * @param soundMode welcher Sound abgespielt werden soll (Konstante)
     */
    public void play(int soundMode) {

        try {
            clip = AudioSystem.getClip();

            switch (soundMode) {
                case PAC_MAN_DEATH:
                    clip.open(AudioSystem.getAudioInputStream(deathSound1));
                    break;
                case PAC_MAN_WON:
                    clip.open(AudioSystem.getAudioInputStream(gameWonSound));
                    break;
                case PAC_GHOST:
                    clip.open(AudioSystem.getAudioInputStream(deathSound2));
                    break;
                default:
                    System.out.println("Fehler | darf nicht auftreten | MusicLoader: keine richtige Zahl zum Musik abspielen");
                    break;
            }
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
