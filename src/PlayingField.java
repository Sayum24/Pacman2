import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.TimerTask;

import javax.swing.*;

/**
 * das ganze Spielfeld/GUI wird hier verwaltet
 */
public class PlayingField extends JPanel {

    private int width;
    private int height;
    private int scalingFactor;
    private int wallWidth;

    private WonLostInformationWindow informationWindow;

    private GameObject player1; //pacMan / also Spieler

    private GameObject[] powerPellet; //kraftpillen

    private GameObject[] foodPoints;//Fresspunkte

    private GameObject blackBackGround; //schwarzer Hintergrund

    private int pacManAnzahlLeben;

    private int pacManPunkteStand;

    private GameObject wall[]; //wände

    private int erstenTicks = 0; //sodass am Anfang nicht alle Geister anfangen sich zu bewegen

    private Timer vulnerableTimer; //Timer für die Zeit wie lange PacMan unverwundbar ist
    private int vulnerableTimeLeft = 0; //sekunden bis PacMan wieder verwundbar ist

    private final static int VULNERABLE_TIME = 30;//wie lange PacMan unverwundbar ist

    private static final int NO_GHOST_IS_TOUCHING = 5;

    private GameObject ghost[];//geister

    private Timer moveTimer; //timer für die Ticks wenn sich alles bewegt

    private Boolean gameStarted;

    private int moveStep; //schrittweite

    private MusicLoader musicLoader; //sounds

    /**
     * Spielfeld wird initialisiert
     *
     * @param pWidth         breite des Spielfelds
     * @param pHeight        h�he des Spielfelds
     * @param pScalingFactor Skalierungsfaktor
     */
    public PlayingField(int pWidth, int pHeight, int pScalingFactor, WonLostInformationWindow pInformationWindow) {



        informationWindow = pInformationWindow;

        gameStarted = false;

        width = pWidth;
        height = pHeight;
        scalingFactor = pScalingFactor;

        pacManAnzahlLeben = 3;

        pacManPunkteStand = 0;

        wallWidth = scalingFactor;

        blackBackGround = new GameObject(getWidth(), getHeight()); //schwarzer Hintergrund

        player1 = new GameObject(11 * scalingFactor, 15 * scalingFactor, scalingFactor, scalingFactor, GameObject.PACMAN); //spieler wird auf Spielfeld gesetzt

        // mover1 = new GameObject(50, 40, 30, 10, GameObject.TEST_OBJECT);
        // mover1.setColor(Color.BLACK);

        // hier werden die powerPellets gesetzt
        setPowerPellets();



        initializeInformationWindow(); //informationsLeiste wird initialisiert

        //sounds
        musicLoader = new MusicLoader();
        musicLoader.load();

        // hier werden die Mauern gesetzt
        setWallOnField();

        moveStep = scalingFactor / 2; /// 2; //die weite die er pro Schritt macht
        initialiseGhosts();

        //Punkte werden auf dem Spielfeld platziert
        setPointsOnField();


        // TIME!!
        moveTimer = new Timer(100, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // getNextDirection PACMAN | ob man direkt das machen kann was der Spieler auf der Tastatur gedrückt hat
                switch (player1.getNextDirection()) {
                    case GameObject.WAIT:

                        break;

                    case GameObject.RIGHT_DIRECTION:
                        if (!player1.isTouchingRight(wall, moveStep)) {
                            setCurrentDirectionToGameObject(player1, GameObject.RIGHT_DIRECTION);
                        }
                        break;

                    case GameObject.LEFT_DIRECTION:
                        if (!player1.isTouchingLeft(wall, moveStep)) {
                            setCurrentDirectionToGameObject(player1, GameObject.LEFT_DIRECTION);
                        }
                        break;

                    case GameObject.UP_DIRECTION:
                        if (!player1.isTouchingAbove(wall, moveStep)) {
                            setCurrentDirectionToGameObject(player1, GameObject.UP_DIRECTION);
                        }
                        break;

                    case GameObject.DOWN_DIRECTION:
                        if (!player1.isTouchingBelow(wall, moveStep)) {
                            setCurrentDirectionToGameObject(player1, GameObject.DOWN_DIRECTION);
                        }
                        break;

                    default:
                        System.out.println("There is no defined nextDirection!  " + player1.getNextDirection());
                        break;
                }


                //GHOSTS
                for (int i = 0; i < ghost.length; i++) {

                    Boolean[] possibleDirections = ghost[i].getPossibleDirections(wall, moveStep, ghost, i);

                    //Lokalisiere PacMan
                    int pacManX = player1.getX();
                    int pacManY = player1.getY();

                    int ghostX = ghost[i].getX();
                    int ghostY = ghost[i].getY();

                    int xDistanceToPacMan = ghostX - pacManX;

                    int yDistanceToPacMan = ghostY - pacManY;

                    if (xDistanceToPacMan < 0) {
                        xDistanceToPacMan = -xDistanceToPacMan;
                    }
                    if (yDistanceToPacMan < 0) {
                        yDistanceToPacMan = -yDistanceToPacMan;
                    }
                    /*
                    Es wird berechnet ob sich der Geist x oder y von PacMan weiter weg befindet
                    Je nach dem welches versucht er näher an PacMan heran zu kommen
                    er kann sich aber nicht umdrehen
                     */

                    Boolean xDistanceFirst = false;

                    if (ghost[i].getOnline()) {
                        if (xDistanceToPacMan > yDistanceToPacMan) {
                            xDistanceFirst = true;
                        }
                    }

                    if (!ghost[i].getOnline()) {
                        if (xDistanceToPacMan < yDistanceToPacMan) {
                            xDistanceFirst = true;
                        }
                    }


                    Boolean alreadyDirectionChanged = false;

                    int mindestAbstandZuPacMan; //ab wie weit sich ein Geist nicht mehr zu PacMan hin gezogen fühlt

                    if (ghost[i].getOnline()) {
                        mindestAbstandZuPacMan = 4;
                    } else {
                        mindestAbstandZuPacMan = 2;
                    }
//TODO https://youtu.be/ataGotQ7ir8 wie sich Geister bei PacMan verhalten

                    //finde herraus in welche Richtung man gehen sollte um in PacMans naehe zu kommen
                    if (xDistanceFirst) {
                        if (ghostCheckMoveX(i, ghostX, pacManX, possibleDirections, mindestAbstandZuPacMan)) {
                            alreadyDirectionChanged = true;
                        }
                    } else {
                        if (ghostCheckMoveY(i, ghostY, pacManY, possibleDirections, mindestAbstandZuPacMan)) {
                            alreadyDirectionChanged = true;
                        }
                    }

                    if (!alreadyDirectionChanged) {
                        if (xDistanceFirst) {
                            if (ghostCheckMoveY(i, ghostY, pacManY, possibleDirections, mindestAbstandZuPacMan)) {
                                alreadyDirectionChanged = true;
                            }
                        } else {
                            if (ghostCheckMoveX(i, ghostX, pacManX, possibleDirections, mindestAbstandZuPacMan)) {
                                alreadyDirectionChanged = true;
                            }
                        }
                    }

                    //finde herraus in welche Richtung man zufällig geht


                    System.out.println("Status: geaendert durch PacMan? - " + alreadyDirectionChanged);

                    if (!alreadyDirectionChanged) {
                        switch (ghost[i].getCurrentDirection()) {
                            case GameObject.UP_DIRECTION:
                                if (!possibleDirections[GameObject.UP_DIRECTION]) {
                                    System.out.println("Changing dir-A");
                                    findNewDirectionForGhost(i);
                                }
                                break;
                            case GameObject.DOWN_DIRECTION:
                                if (!possibleDirections[GameObject.DOWN_DIRECTION]) {
                                    System.out.println("Changing dir-D");
                                    findNewDirectionForGhost(i);
                                }
                                break;
                            case GameObject.LEFT_DIRECTION:
                                if (!possibleDirections[GameObject.LEFT_DIRECTION]) {
                                    System.out.println("Changing dir-L");
                                    findNewDirectionForGhost(i);
                                }
                                break;
                            case GameObject.RIGHT_DIRECTION:
                                if (!possibleDirections[GameObject.RIGHT_DIRECTION]) {
                                    System.out.println("Changing dir-R");
                                    findNewDirectionForGhost(i);
                                }
                                break;
                            case GameObject.WAIT:
                                System.out.println("Ghost " + i + " no direction set, now finding a new direction.");
                                findNewDirectionForGhost(i);
                                break;

                            default:
                                System.out.println("Fehler  | No defined direction of Ghost." + ghost[i].getCurrentDirection());
                                break;
                        }
                    }
                }
                doAction();
                //Aktionen werden nun Ausgeführt
            }
        });

        //Eingaben vom Spieler werden hier verarbeitet
        /*
        zuerst wird die Richtung in PacMans nextDirection gespeichert - sobald pacMan in diese Richtung kann geht er in die Richtung
         */
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {

                    case KeyEvent.VK_D: // RIGHT
                        player1.setNextDirection(GameObject.RIGHT_DIRECTION);
                        startTimer();
                        break;

                    case KeyEvent.VK_A: // LEFT
                        player1.setNextDirection(GameObject.LEFT_DIRECTION);
                        startTimer();
                        break;

                    case KeyEvent.VK_W: // UP
                        player1.setNextDirection(GameObject.UP_DIRECTION);
                        startTimer();
                        break;

                    case KeyEvent.VK_S:// DOWN
                        player1.setNextDirection(GameObject.DOWN_DIRECTION);
                        startTimer();
                        break;

                }
            }
        });

        //timer wie lange PacMan unverwundbar ist
        vulnerableTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vulnerableTimeLeft--;

                //wenn die Zeit vorüber ist...
                if (vulnerableTimeLeft < 1) {
                    vulnerableTimer.stop();
                    System.out.println("Time is up!");
                    for (GameObject ghost : ghost) {
                        ghost.invertCurrentDirection();
                        ghost.setOnline(true);
                    }
                }
            }
        });

        setFocusable(true);

        requestFocusInWindow();

    }

    /**
     * wenn eine zufällige neue Richtung für einen Geist gewählt werden muss
     *
     * @param ghostNumber nummer im Geist Array
     */
    private void findNewDirectionForGhost(int ghostNumber) {

        ghost[ghostNumber].setCurrentDirection(ghost[ghostNumber].getRandomDirectionForGhost(wall, moveStep, ghost, ghostNumber));

    }

    public Dimension getPreferredSize() {
        //Dimension d = new Dimension(width, height);
        //return d;

        return new Dimension(width, height);
    }

    /**
     * currentDirection wird gesetzt
     *
     * @param pGO        GameObject
     * @param pDirection Richtung
     */
    private void setCurrentDirectionToGameObject(GameObject pGO, int pDirection) {
        pGO.setCurrentDirection(pDirection);

        pGO.setNextDirection(GameObject.WAIT);

    }

    /**
     * spielstart wird hier überprüft
     */
    private void startTimer() {
        if (!gameStarted) {
            moveTimer.start();
            gameStarted = true;

            ghost[0].setCurrentDirection(GameObject.LEFT_DIRECTION);
            System.out.println("aktuelleRichtung Geist 1: " + ghost[0].getCurrentDirection());
            ghost[1].setCurrentDirection(GameObject.LEFT_DIRECTION);
            ghost[2].setCurrentDirection(GameObject.RIGHT_DIRECTION);

        }
    }

    /**
     * Timer wird gestartet (/verlängert) wie lange PacMan noch unverwundbar ist
     */
    private void startTimerGhostVulnerable() {
        if (vulnerableTimeLeft == 0) {
            vulnerableTimeLeft = vulnerableTimeLeft + VULNERABLE_TIME;
            for (GameObject ghost : ghost) {
                ghost.invertCurrentDirection();
            }
            vulnerableTimer.start();
            System.out.println("Timer started | 27736");
        } else {
            vulnerableTimeLeft = vulnerableTimeLeft + VULNERABLE_TIME;
        }
    }

    /**
     * @param ghostNumber
     * @param ghostX
     * @param pacManX
     * @param possibleDirections
     * @param mindestAbstandZuPacMan
     * @return es wird überprüft in welche Richtung sich ein Geist am besten bewegt auf X
     */
    private Boolean ghostCheckMoveX(int ghostNumber, int ghostX, int pacManX, Boolean[] possibleDirections, int mindestAbstandZuPacMan) {

        Boolean alreadyDirectionChanged = false;

        if (ghost[ghostNumber].getOnline()) { //Geister sind auf der Jagt
            if ((ghostX - pacManX) < -moveStep * mindestAbstandZuPacMan) {//geist geht nach rechts
                if (possibleDirections[GameObject.RIGHT_DIRECTION]) {
                    ghost[ghostNumber].setCurrentDirection(GameObject.RIGHT_DIRECTION);
                    System.out.println("Changed dir-R");
                    alreadyDirectionChanged = true;
                }
            } else if ((ghostX - pacManX) > moveStep * mindestAbstandZuPacMan) {//geist geht nach links
                if (possibleDirections[GameObject.LEFT_DIRECTION]) {
                    ghost[ghostNumber].setCurrentDirection(GameObject.LEFT_DIRECTION);
                    System.out.println("Changed dir-L");
                    alreadyDirectionChanged = true;
                }

            }
        } else { //geister sind auf der Flucht
            if ((ghostX - pacManX) < -moveStep * mindestAbstandZuPacMan) {//geist geht nach rechts

                if (possibleDirections[GameObject.LEFT_DIRECTION]) {
                    ghost[ghostNumber].setCurrentDirection(GameObject.LEFT_DIRECTION);
                    System.out.println("Changed dir-L");
                    alreadyDirectionChanged = true;
                }


            } else if ((ghostX - pacManX) > moveStep * mindestAbstandZuPacMan) {//geist geht nach links

                if (possibleDirections[GameObject.RIGHT_DIRECTION]) {
                    ghost[ghostNumber].setCurrentDirection(GameObject.RIGHT_DIRECTION);
                    System.out.println("Changed dir-R");
                    alreadyDirectionChanged = true;
                }
            }
        }


        return alreadyDirectionChanged;
    }

    /**
     * @param ghostNumber
     * @param ghostY
     * @param pacManY
     * @param possibleDirections
     * @param mindestAbstandZuPacMan
     * @return es wird überprüft in welche Richtung sich ein Geist am besten bewegt auf Y
     */
    private Boolean ghostCheckMoveY(int ghostNumber, int ghostY, int pacManY, Boolean[] possibleDirections, int mindestAbstandZuPacMan) {

        Boolean alreadyDirectionChanged = false;

        if (ghost[ghostNumber].getOnline()) {//wenn Geist hinter PacMan hinterher ist
            if ((ghostY - pacManY) > moveStep * mindestAbstandZuPacMan) {//Geist geht nach oben
                if (possibleDirections[GameObject.UP_DIRECTION]) {
                    ghost[ghostNumber].setCurrentDirection(GameObject.UP_DIRECTION);
                    System.out.println("Changed dir-U");
                    alreadyDirectionChanged = true;
                }
            } else if ((ghostY - pacManY) < -moveStep * mindestAbstandZuPacMan) {//Geist geht nach unten
                if (possibleDirections[GameObject.DOWN_DIRECTION]) {
                    ghost[ghostNumber].setCurrentDirection(GameObject.DOWN_DIRECTION);
                    System.out.println("Changed dir-D");
                    alreadyDirectionChanged = true;
                }
            }


        } else { //wenn geist vor PacMan flüchten muss
            if (ghost[ghostNumber].getOnline()) {//wenn Geist hinter PacMan hinterher ist
                if ((ghostY - pacManY) > moveStep * mindestAbstandZuPacMan) {//Geist geht nach unten

                    if (possibleDirections[GameObject.DOWN_DIRECTION]) {
                        ghost[ghostNumber].setCurrentDirection(GameObject.DOWN_DIRECTION);
                        System.out.println("Changed dir-D");
                        alreadyDirectionChanged = true;
                    }
                } else if ((ghostY - pacManY) < -moveStep * mindestAbstandZuPacMan) {//Geist geht nach oben
                    if (possibleDirections[GameObject.UP_DIRECTION]) {
                        ghost[ghostNumber].setCurrentDirection(GameObject.UP_DIRECTION);
                        System.out.println("Changed dir-U");
                        alreadyDirectionChanged = true;
                    }
                }
            }
        }
        return alreadyDirectionChanged;
    }

    /**
     * informationen leiste wird initialisiert
     */
    private void initializeInformationWindow() {
        informationWindow.setLifeCounter(pacManAnzahlLeben);
        informationWindow.setPoint(0);

    }

    /**
     * Aktionen werden ausgeführt
     */
    private void doAction() {


        //überprüfen ob sich PacMan in die Richtung weiterbewegen kann.
        // getCurrentDirection
        switch (player1.getCurrentDirection()) {
            case GameObject.RIGHT_DIRECTION:

                if (player1.isTouchingRight(wall, moveStep)) {
                    player1.setCurrentDirection(GameObject.WAIT);
                    break;
                }

                // f�r das Teleportieren wenn man durch das Loch geht
                if (player1.getX() == 23 * scalingFactor && player1.getY() == 9 * scalingFactor) {
                    player1.moveTo(-scalingFactor, 9 * scalingFactor);
                }
                player1.move(moveStep, 0);
                break;

            case GameObject.LEFT_DIRECTION:

                if (player1.isTouchingLeft(wall, moveStep)) {

                    player1.setCurrentDirection(GameObject.WAIT);
                    break;
                }

                // f�r das Teleportieren wenn man durch das Loch geht
                if (player1.getX() == -scalingFactor && player1.getY() == 9 * scalingFactor) {
                    player1.moveTo(23 * scalingFactor, 9 * scalingFactor);
                }

                player1.move(-moveStep, 0);
                System.out.println("8856995");
                break;

            case GameObject.UP_DIRECTION:

                if (player1.isTouchingAbove(wall, moveStep)) {
                    player1.setCurrentDirection(GameObject.WAIT);
                    break;
                }

                player1.move(0, -moveStep);
                break;

            case GameObject.DOWN_DIRECTION:

                if (player1.isTouchingBelow(wall, moveStep)) {
                    player1.setCurrentDirection(GameObject.WAIT);
                    break;
                }

                player1.move(0, moveStep);
                break;

            case GameObject.WAIT:
                System.out.println("WAIT");
                break;

            default:
                System.out.println("Fehler | There is no direction! Class: PlayingField");

        }


        for (int i = 0; i < erstenTicks; i++) {

            //Geister werden weitergesteuert
            Boolean[] possibleDirections = ghost[i].getPossibleDirections(wall, moveStep, ghost, i);
            switch (ghost[i].getCurrentDirection()) {
                case GameObject.WAIT:
                    System.out.println("Wait position von ghost");
                    break;
                case GameObject.LEFT_DIRECTION:
                    // f�r das Teleportieren wenn man durch das Loch geht
                    if (ghost[i].getX() == -scalingFactor && ghost[i].getY() == 9 * scalingFactor) {
                        System.out.println("Geist Ist am teleportierenL");
                        ghost[i].moveTo(23 * scalingFactor, 9 * scalingFactor);
                    }
                    if (ghost[i].isTouchingLeft(wall, moveStep)) {
                        System.out.println("Da ist ein Fehler!L");
                        ghost[i].setCurrentDirection(GameObject.WAIT);
                        break;
                    }
                    if (!possibleDirections[GameObject.LEFT_DIRECTION]) {
                        System.out.println("Da ist ein Fehler!NL");
                        ghost[i].setCurrentDirection(GameObject.WAIT);
                        break;
                    }

                    ghost[i].move(-moveStep, 0);
                    break;
                case GameObject.RIGHT_DIRECTION:

                    // f�r das Teleportieren wenn man durch das Loch geht
                    if (ghost[i].getX() == 23 * scalingFactor && ghost[i].getY() == 9 * scalingFactor) {
                        System.out.println("Geist Ist am teleportierenR");
                        ghost[i].moveTo(-scalingFactor, 9 * scalingFactor);
                        break;
                    }

                    if (ghost[i].isTouchingRight(wall, moveStep)) {
                        System.out.println("Da ist ein Fehler!R");
                        ghost[i].setCurrentDirection(GameObject.WAIT);
                        break;
                    }
                    if (!possibleDirections[GameObject.RIGHT_DIRECTION]) {
                        System.out.println("Da ist ein Fehler!NR");
                        ghost[i].setCurrentDirection(GameObject.WAIT);
                        break;
                    }

                    ghost[i].move(moveStep, 0);
                    break;
                case GameObject.UP_DIRECTION:
                    if (ghost[i].isTouchingAbove(wall, moveStep)) {
                        System.out.println("Da ist ein Fehler!U");
                        ghost[i].setCurrentDirection(GameObject.WAIT);
                        break;
                    }
                    if (!possibleDirections[GameObject.UP_DIRECTION]) {
                        System.out.println("Da ist ein Fehler!NU");
                        ghost[i].setCurrentDirection(GameObject.WAIT);
                        break;
                    }
                    ghost[i].move(0, -moveStep);
                    break;
                case GameObject.DOWN_DIRECTION:
                    if (ghost[i].isTouchingBelow(wall, moveStep)) {
                        System.out.println("Da ist ein Fehler!B");
                        ghost[i].setCurrentDirection(GameObject.WAIT);
                        break;
                    }

                    if (!possibleDirections[GameObject.DOWN_DIRECTION]) {
                        System.out.println("Da ist ein Fehler!ND");
                        ghost[i].setCurrentDirection(GameObject.WAIT);
                        break;
                    }

                    ghost[i].move(0, moveStep);
                    break;

                default:
                    System.out.println(" Fehler | Sollte nicht passieren: Ghostnummer: " + i + " Ghost directio: " + ghost[i].getCurrentDirection());
                    break;
            }

        }
        if (erstenTicks < 3) {
            //sodass nicht alle Geister auf einmal losgehen
            erstenTicks++;
        }

        //überprüfen ob sich PacMan in einem Fresspunkt/Kraftpille befindet
        if (pacManIsNowInPoint()) {
            informationWindow.setPoint(pacManPunkteStand);
            checkGameWon();
        } else if (pacManIsNowInPowerPellet()) {
            checkGameWon();
            informationWindow.setPoint(pacManPunkteStand);
            setGhostsVulnerable();
            startTimerGhostVulnerable();
        }


        //checked ob PacMan einen Geist berührt
        if (pacManIsNowInGhostIsOnline()) {
            //Leben werden verringert
            pacManAnzahlLeben--;
            if (pacManAnzahlLeben < 1) {
                informationWindow.setWonLostText(informationWindow.GAME_LOST);
                musicLoader.play(MusicLoader.PAC_MAN_DEATH); //Sound wird ausgegeben
                //TODO wenn Spiel verloren, dann Option, neues Spiel zu spielen
            }

            informationWindow.setLifeCounter(pacManAnzahlLeben);
            //pacMan wird wieder in die Mitte gesetzt
            player1.setXY(11 * scalingFactor, 15 * scalingFactor);

            moveTimer.stop();
            gameStarted = false;

            //Geist wird in die Mitte des Spielfeldes gesetzt.
            setGhosts();
        }

        //ob PacMan mit PowerPille einen Geist berührt
        int ghostStatus = pacManIsNowInGhostIsOffline();

        if (ghostStatus != NO_GHOST_IS_TOUCHING) {
            ghost[ghostStatus].setGameObjectToTheBeginningPlace(); //Geist wird wieder auf anfangsPosition gesetzt
        }

        repaint();
    }

    /**
     * überprüft ob das Spiel gewonnen ist (höchstpunktzahl errreicht ist)
     */
    private void checkGameWon() {
        if (pacManPunkteStand > 262) {
            informationWindow.setWonLostText(WonLostInformationWindow.GAME_WON);
            moveTimer.stop();
            musicLoader.play(MusicLoader.PAC_MAN_WON);
        }
    }


    /**
     * fragt ob sich pacMan in einem Geist befindet
     *
     */
    private Boolean pacManIsNowInGhostIsOnline() {
        for (GameObject gameObject : ghost) {
            if (player1.isTouching(gameObject)) {
                if (gameObject.getOnline()) {
                    System.out.println("PacMan touches Ghost!!");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @return ob der unverwundbare PacMan einen Geist berührt
     */
    private int pacManIsNowInGhostIsOffline() {
        for (int i = 0; i < ghost.length; i++) {
            if (player1.isTouching(ghost[i]) && !ghost[i].getOnline()) {
                return i;
            }
        }
        return NO_GHOST_IS_TOUCHING;
    }

    /**
     * ob sich Pacman in einem footPoint befindet sodass man diesen ausblenden kann
     *
     * @return ob sich pacman in einem Punkt befindet (true/false)
     */
    private Boolean pacManIsNowInPoint() {

        for (GameObject foodPoint : foodPoints) {

            if (player1.isTouching(foodPoint)) {
                if (foodPoint.getOnline()) { // fragt nach ob es �berhaupt noch sichtbar ist
                    pacManPunkteStand++;

                    System.out.println("PacMan Touches Point!");
                    foodPoint.setOnline(false);
                    return true;
                }
            }

        }

        return false;
    }

    /**
     *
     * @return ob sich PacMan in Kraftpille befindet
     */
    private Boolean pacManIsNowInPowerPellet() {
        // wenn PacMan ein PowerPellet ber�hrt
        for (GameObject gameObject : powerPellet) {

            if (player1.isTouching(gameObject)) {
                if (gameObject.getOnline()) {
                    pacManPunkteStand = pacManPunkteStand + 10;
                    System.out.println("PacMan Touches PowerPellet huhuhu");
                    gameObject.setOnline(false);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Geister werden verwundbar gemacht (pacman ist unverwundbar)
     */
    private void setGhostsVulnerable() {
        System.out.println("Ghosts are vulnerable");

        for (GameObject ghost : ghost) {
            ghost.setOnline(false);
        }
    }

    public void paintComponent(Graphics pG) {
        clearBackground(pG);

        blackBackGround.paintTo(pG);

        player1.paintTo(pG);

        // mover1.paintTo(pG);

        // w�nde werden neu gezeichnet
        for (GameObject value : wall) {
            value.paintTo(pG);
        }

        for (GameObject object : powerPellet) {
            if (object.getOnline()) {
                object.paintTo(pG);
            }

        }

        for (GameObject gameObject : ghost) {
            gameObject.paintTo(pG);

        }

        for (GameObject foodPoint : foodPoints) {
            if (foodPoint.getOnline()) {
                foodPoint.paintTo(pG);
            }

        }
    }

    private void clearBackground(Graphics pG) {
        pG.clearRect(0, 0, width, height);
    }

    /**
     * Method sets walls on the field
     */
    private void setWallOnField() {

        wall = new GameObject[40];

        wall[0] = new GameObject(0, 0, scalingFactor * 23, wallWidth, GameObject.WALL);

        wall[1] = new GameObject(0, scalingFactor, wallWidth, scalingFactor * 8, GameObject.WALL);

        wall[2] = new GameObject(0, 10 * scalingFactor, wallWidth, 9 * scalingFactor, GameObject.WALL);

        wall[3] = new GameObject(scalingFactor, scalingFactor * 18, scalingFactor * 22, wallWidth, GameObject.WALL);

        wall[4] = new GameObject(scalingFactor, 8 * scalingFactor, 4 * scalingFactor, wallWidth, GameObject.WALL);

        wall[5] = new GameObject(scalingFactor, 10 * scalingFactor, 4 * scalingFactor, wallWidth, GameObject.WALL);

        wall[6] = new GameObject(2 * scalingFactor, 2 * scalingFactor, wallWidth, 5 * scalingFactor, GameObject.WALL);

        wall[7] = new GameObject(2 * scalingFactor, 12 * scalingFactor, wallWidth, 5 * scalingFactor, GameObject.WALL);

        wall[8] = new GameObject(3 * scalingFactor, 2 * scalingFactor, 8 * scalingFactor, wallWidth, GameObject.WALL);

        wall[9] = new GameObject(3 * scalingFactor, 12 * scalingFactor, 2 * scalingFactor, wallWidth, GameObject.WALL);

        wall[10] = new GameObject(3 * scalingFactor, 6 * scalingFactor, 2 * scalingFactor, wallWidth, GameObject.WALL);

        wall[11] = new GameObject(4 * scalingFactor, 14 * scalingFactor, wallWidth, 4 * scalingFactor, GameObject.WALL);

        wall[12] = new GameObject(4 * scalingFactor, 4 * scalingFactor, 3 * scalingFactor, wallWidth, GameObject.WALL);

        wall[13] = new GameObject(6 * scalingFactor, 5 * scalingFactor, wallWidth, 2 * scalingFactor, GameObject.WALL);

        wall[14] = new GameObject(7 * scalingFactor, 6 * scalingFactor, 4 * scalingFactor, wallWidth, GameObject.WALL);

        wall[15] = new GameObject(6 * scalingFactor, 8 * scalingFactor, wallWidth, 7 * scalingFactor, GameObject.WALL);

        wall[16] = new GameObject(6 * scalingFactor, 16 * scalingFactor, 11 * scalingFactor, wallWidth,
                GameObject.WALL);

        wall[17] = new GameObject(8 * scalingFactor, 12 * scalingFactor, 3 * scalingFactor, wallWidth, GameObject.WALL);

        wall[18] = new GameObject(8 * scalingFactor, 9 * scalingFactor, wallWidth, 3 * scalingFactor, GameObject.WALL);

        wall[19] = new GameObject(8 * scalingFactor, 8 * scalingFactor, 3 * scalingFactor, wallWidth, GameObject.WALL);

        wall[20] = new GameObject(8 * scalingFactor, 4 * scalingFactor, 7 * scalingFactor, wallWidth, GameObject.WALL);

        wall[21] = new GameObject(8 * scalingFactor, 14 * scalingFactor, 7 * scalingFactor, wallWidth, GameObject.WALL);

        wall[22] = new GameObject(10 * scalingFactor, 10 * scalingFactor, 3 * scalingFactor, wallWidth,
                GameObject.WALL);

        // jetzt die andere Seite

        wall[23] = new GameObject(22 * scalingFactor, scalingFactor, wallWidth, scalingFactor * 8, GameObject.WALL);

        wall[24] = new GameObject(22 * scalingFactor, 10 * scalingFactor, wallWidth, 9 * scalingFactor,
                GameObject.WALL);

        wall[25] = new GameObject(18 * scalingFactor, 8 * scalingFactor, 4 * scalingFactor, wallWidth, GameObject.WALL);

        wall[26] = new GameObject(18 * scalingFactor, 10 * scalingFactor, 4 * scalingFactor, wallWidth,
                GameObject.WALL);

        wall[27] = new GameObject(20 * scalingFactor, 2 * scalingFactor, wallWidth, 5 * scalingFactor, GameObject.WALL);

        wall[28] = new GameObject(20 * scalingFactor, 12 * scalingFactor, wallWidth, 5 * scalingFactor,
                GameObject.WALL);

        wall[29] = new GameObject(12 * scalingFactor, 2 * scalingFactor, 8 * scalingFactor, wallWidth, GameObject.WALL);

        wall[30] = new GameObject(18 * scalingFactor, 12 * scalingFactor, 2 * scalingFactor, wallWidth,
                GameObject.WALL);

        wall[31] = new GameObject(18 * scalingFactor, 6 * scalingFactor, 2 * scalingFactor, wallWidth, GameObject.WALL);

        wall[32] = new GameObject(18 * scalingFactor, 14 * scalingFactor, wallWidth, 4 * scalingFactor,
                GameObject.WALL);

        wall[33] = new GameObject(16 * scalingFactor, 4 * scalingFactor, 3 * scalingFactor, wallWidth, GameObject.WALL);

        wall[34] = new GameObject(16 * scalingFactor, 5 * scalingFactor, wallWidth, 2 * scalingFactor, GameObject.WALL);

        wall[35] = new GameObject(12 * scalingFactor, 6 * scalingFactor, 4 * scalingFactor, wallWidth, GameObject.WALL);

        wall[36] = new GameObject(16 * scalingFactor, 8 * scalingFactor, wallWidth, 7 * scalingFactor, GameObject.WALL);

        wall[37] = new GameObject(12 * scalingFactor, 12 * scalingFactor, 3 * scalingFactor, wallWidth,
                GameObject.WALL);

        wall[38] = new GameObject(14 * scalingFactor, 9 * scalingFactor, wallWidth, 3 * scalingFactor, GameObject.WALL);

        wall[39] = new GameObject(12 * scalingFactor, 8 * scalingFactor, 3 * scalingFactor, wallWidth, GameObject.WALL);

        // http://www.onlinespiele-sammlung.de/pacman/about-pacman.php

    }

    /**
     * Method sets Points of the field
     */
    private void setPointsOnField() {

        foodPoints = new GameObject[203];

        int i = 0;
        int w = 0;

        for (int xAchse = 0; xAchse < 22 * scalingFactor + 2; xAchse = xAchse + scalingFactor) {
            for (int yAchse = 0; yAchse < 17 * scalingFactor + 2; yAchse = yAchse + scalingFactor) {

                Boolean n = true;// ob es in einem Objekt ist

                for (GameObject gameObject : wall) {
                    if (gameObject.isInside(xAchse, yAchse)) {

                        n = false;
                    }
                }

                for (GameObject gameObject : ghost) {
                    if (gameObject.isInside(xAchse, yAchse)) {
                        n = false;

                    }
                }

                for (GameObject gameObject : powerPellet) {
                    if (gameObject.isInside(xAchse, yAchse)) {
                        // System.out.println("power");
                        n = false;
                    }
                }

                if (player1.isInside(xAchse, yAchse)) {
                    n = false;
                    // System.out.println("player");
                }

                if (n) {
                    foodPoints[i] = new GameObject(xAchse - 1, yAchse - 1, scalingFactor, GameObject.POINT);
                    i++;
                    w++;
                }

            }
        }

        System.out.println(w + " Foodpoints");

        for (int j = 0; j < foodPoints.length; j++) {
            if (foodPoints[j] == null) {
                System.out.println(j + " gebrauchte slotz");
                return;
            }
        }

    }

    /**
     * Method sets PowerPellets of the field (Kraftpillen)
     */
    private void setPowerPellets() {


        powerPellet = new GameObject[6];

        powerPellet[0] = new GameObject(1 * scalingFactor, 1 * scalingFactor, scalingFactor, scalingFactor,
                GameObject.POWER_PELLET);
        powerPellet[1] = new GameObject(21 * scalingFactor, 1 * scalingFactor, scalingFactor, scalingFactor,
                GameObject.POWER_PELLET);
        powerPellet[2] = new GameObject(11 * scalingFactor, 12 * scalingFactor, scalingFactor, scalingFactor,
                GameObject.POWER_PELLET);
        powerPellet[3] = new GameObject(11 * scalingFactor, 8 * scalingFactor, scalingFactor, scalingFactor,
                GameObject.POWER_PELLET);
        powerPellet[4] = new GameObject(1 * scalingFactor, 17 * scalingFactor, scalingFactor, scalingFactor,
                GameObject.POWER_PELLET);
        powerPellet[5] = new GameObject(21 * scalingFactor, 17 * scalingFactor, scalingFactor, scalingFactor,
                GameObject.POWER_PELLET);

    }

    /**
     * setzt Geister auf ihre Anfangsposition
     */
    private void initialiseGhosts() {
        ghost = new GameObject[3];
        ghost[0] = new GameObject(10 * scalingFactor, 9 * scalingFactor, scalingFactor, scalingFactor,
                GameObject.GHOST);
        ghost[1] = new GameObject(11 * scalingFactor, 9 * scalingFactor, scalingFactor, scalingFactor,
                GameObject.GHOST);
        ghost[2] = new GameObject(12 * scalingFactor, 9 * scalingFactor, scalingFactor, scalingFactor,
                GameObject.GHOST);
    }

    //setzt Geister auf ihre Anfangspositionen
    private void setGhosts() {

        ghost[0].setGameObjectToTheBeginningPlace();
        ghost[1].setGameObjectToTheBeginningPlace();
        ghost[2].setGameObjectToTheBeginningPlace();

        erstenTicks = 0;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
