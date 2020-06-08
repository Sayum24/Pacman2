import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;

public class GUI {

    private JFrame gameWindow;
    private PlayingField playingField;
    private WonLostInformationWindow informationWindow;

    /**
     * Fenster werden geladen
     * @param pTitle
     */
    public GUI(String pTitle) {

        int scalingFactor = 40;
        gameWindow = new JFrame(pTitle);

        informationWindow = new WonLostInformationWindow(23 * scalingFactor, 1 * scalingFactor);

        playingField = new PlayingField(23 * scalingFactor, 19 * scalingFactor, scalingFactor, informationWindow);
        gameWindow.setResizable(false);


        gameWindow.add(BorderLayout.NORTH, informationWindow);

        gameWindow.add(BorderLayout.SOUTH, playingField);


        gameWindow.pack();

        gameWindow.setVisible(true);
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}