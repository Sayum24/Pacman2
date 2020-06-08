import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;

import javax.swing.JPanel;

public class WonLostInformationWindow extends JPanel {

    private Label jLabelWonLost;
    private Label jLabelLifeCounter;
    private Label jLabelPoints;

    private int width;
    private int height;

    public static final int GAME_WON = 0;
    public static final int GAME_LOST = 1;


    private static final String GAME_WON_STRING = "Spiel gewonnen!";
    private static final String GAME_LOST_STRING = "Spiel verloren.";

    private final String pointInformationString = "Punktestand";


    public WonLostInformationWindow(int height, int width) {
        this.height = height * 300;
        this.width = width;


        jLabelPoints = new Label();
        jLabelWonLost = new Label();
        jLabelLifeCounter = new Label();

        jLabelPoints.setForeground(Color.white);

        jLabelWonLost.setForeground(Color.white);
        jLabelLifeCounter.setForeground(Color.white);


        this.add(BorderLayout.WEST, jLabelPoints);

        this.add(BorderLayout.CENTER, jLabelWonLost);


        this.add(BorderLayout.EAST, jLabelLifeCounter);

        this.setBackground(Color.BLACK);

        jLabelWonLost.setText("                HELLO               ");


    }


    public void setPoint(int pPointNumber) {
        jLabelPoints.setText(pointInformationString + ": " + String.valueOf(pPointNumber));

    }

    public void setLifeCounter(int pLifeNumber) {
        jLabelLifeCounter.setText(String.valueOf(pLifeNumber));
    }


    public void setWonLostText(int pGameWonLost) {

        switch (pGameWonLost) {
            case GAME_LOST:
                jLabelWonLost.setText(GAME_LOST_STRING);

                break;

            case GAME_WON:
                jLabelWonLost.setText(GAME_WON_STRING);

                break;

            default:
                System.out.println("Ein unbekannter Fehler ist aufgetreten! Klasse InformationWindow, setWonLostText()");
                break;
        }
    }


}