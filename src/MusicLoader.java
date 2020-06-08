import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.io.File;

public class MusicLoader {

    public static final int PAC_MAN_DEATH = 1;
    public static final int PAC_MAN_WON = 2;

    private static File deathSound1;
    private static File deathSound2;

    private static File gameWonSound;

    private static float volume;

    Clip clip;

    public MusicLoader(){
        volume = 0;
    }

    public void load(){

        deathSound1 = new File("res/Pac-ManDeathSound1.wav");
        deathSound2 = new File("res/Pac-ManDeathSound2.wav");

        gameWonSound = new File("res/PacManwon_sound.wav");


    }

    public void play(int soundMode) {

        try {
            clip = AudioSystem.getClip();


            switch (soundMode){
                case PAC_MAN_DEATH:

                    clip.open(AudioSystem.getAudioInputStream(deathSound1));


                    break;
                case PAC_MAN_WON:
                    clip.open(AudioSystem.getAudioInputStream(gameWonSound));

                    break;
            }
            clip.start();



        } catch (Exception e){
            e.printStackTrace();
        }



    }

}
