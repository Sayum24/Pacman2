import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
//import java.io.File;
//import java.io.IOException;
//import java.net.URL;
//
//import javax.imageio.ImageIO;


public class GameObject {

    private int x;
    private int y;
    private int width;
    private int height;
    private Color color;

    private int beginningX;
    private int beginningY;

    private Boolean online;

    private BufferedImage img;

    public final static int POWER_PELLET = 0;

    public final static int WALL = 1;

    public final static int GHOST = 2;

    public final static int PACMAN = 3;

    public final static int TEST_OBJECT = 4;

    public final static int POINT = 5;

    public static final int RIGHT_DIRECTION = 0;

    public final static int LEFT_DIRECTION = 1;

    public final static int UP_DIRECTION = 2;

    public final static int DOWN_DIRECTION = 3;

    public final static int WAIT = 4;

    public final static int SCALING_FACTOR = 40;

    public final static int BACKGROUND = 10;

    private static int ghostNumber = 0;

    private int currentDirection;

    private int type;

    private int nextDirection;


    public GameObject(int pX, int pY, int pWidth, int pHeight, int pType) {
        x = pX;
        y = pY;
        beginningX = pX;
        beginningY = pY;

        if (pWidth < 0) {
            pWidth = -pWidth;
        }
        width = pWidth;

        if (pHeight < 0) {
            pHeight = -pHeight;
        }
        height = pHeight;

        currentDirection = WAIT;

        nextDirection = WAIT;

        type = pType;

        switch (pType) {// hier wird der Typ schon abgefragt
            // für die Typen
            case POWER_PELLET:

                color = Color.GRAY;
                online = true;
                break;

            case WALL:
                color = Color.blue;
                break;

            case GHOST:
                color = Color.RED;
                online = true;

                switch (ghostNumber) {
                    case 0:
                        color = Color.RED;
                        break;

                    case 1:
                        color = Color.PINK;
                        break;

                    case 2:
                        color = Color.orange;
                        ghostNumber = 0;
                        break;

                    default:
                        System.out.println("Fehler | too many ghosts lol");
                        break;
                }

                ghostNumber++;

                break;

            case PACMAN:
                color = Color.YELLOW;
                break;

            default:
                System.out.println("Fehler | GameObject Konstruktor| Typ: " + pType);
                break;
        }

    }

    public GameObject(int pX, int pY, int pScale, int pType) {
        x = pX + 15;
        y = pY + 15;// TODO 10 ist hier 1/4tel vom scaling factor

        beginningX = pX;
        beginningY = pY;
//		currentDirection = WAIT;
//		
//		nextDirection = WAIT;

        switch (pType) {
            case POINT:
                type = POINT;
                color = Color.white;

                // ein viertel des Scaling factors
                width = 10;
                height = 10;

                online = true;

                break;

            default:
                break;
        }
    }

    public GameObject(int pWidth, int pHeight) {// für den schwarzen Hintergrund

        x = 0;
        y = 0;

        height = pHeight + 5;
        width = pWidth + 5;

        color = Color.BLACK;

    }

//	public GameObject(int pWidth, int pHeight){
//		this(0, 0, pWidth, pHeight);
//	}

    public int getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(int pCurrentDirection) {

        currentDirection = pCurrentDirection;

    }

    public Boolean getOnline() {
        return online;
    }

    public void setGameObjectToTheBeginningPlace() {
        x = beginningX;
        y = beginningY;
    }

    public void setOnline(Boolean pOnline) {

        online = pOnline;

        if (type == GHOST) {
            if (!pOnline) {
                color = new Color(105, 175, 201);
                System.out.println("bis hier hin läufts 299348");
            } else {
                switch (ghostNumber) {
                    case 0:
                        color = Color.RED;
                        ghostNumber++;
                        break;

                    case 1:
                        color = Color.PINK;
                        ghostNumber++;
                        break;

                    case 2:
                        color = Color.orange;
                        ghostNumber = 0;
                        break;

                    default:
                        System.out.println("Fehler | too many ghosts lol");
                        break;
                }
            }

        }
    }

    public int getNextDirection() {
        return nextDirection;
    }

    public void setNextDirection(int pNextDirection) {

        if (pNextDirection >= 0 || pNextDirection < 5) {
            nextDirection = pNextDirection;
        } else {
            System.out.println("Fehler | The direction is not right! [Place: setNextDirection() {GameObject}]");
        }

    }

    // setter & getter für Variablen
    public int getX() {
        return x;
    }

    public void setX(int pX) {
        this.x = pX;
    }

    public int getY() {
        return y;
    }

    public void setY(int pY) {
        this.y = pY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int pWidth) {
        this.width = pWidth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int pHeight) {
        this.height = pHeight;
    }

    public int getType() {
        return type;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isInside(int pX, int pY) {

        return pX < x + width && pX >= x && pY < y + height && pY >= y;
    }

    private boolean isInside(int meX, int meY, int pX, int pY) {

        return pX < meX + width && pX >= meX && pY < meY + height && pY >= meY;
    }

    public void moveTo(int pX, int pY) {
        x = pX;
        y = pY;
    }

    public void move(int pDX, int pDY) {
        x = pDX + x;
        y = pDY + y;
    }

    public Boolean isTouching(GameObject pGO) {
        int objX = pGO.getX();
        int objYUP = pGO.getY();

        int objYDown = pGO.getY() + pGO.getHeight() - SCALING_FACTOR;// eigentlich -1

        return isInside(objX, objYUP) || isInside(objX, objYDown) || pGO.isInside(x, y)
                || pGO.isInside(x, y + height - SCALING_FACTOR);

    }

    private Boolean isTouching(int meX, int meY, GameObject pGO) {

        int objX = pGO.getX();
        int objYUP = pGO.getY();

        int objYDown = pGO.getY() + pGO.getHeight() - 1;

        if (isInside(meX, meY, objX, objYUP) || isInside(meX, meY, objX, objYDown) || pGO.isInside(meX, meY)
                || pGO.isInside(meX, meY + height - 1) || pGO.isInside(meX + width - 1, meY)) {

            if (isInside(meX, meY, objX, objYUP)) {
                System.out.println("E1");
            }

            if (isInside(meX, meY, objX, objYDown)) {
                System.out.println("E2");
            }

            if (pGO.isInside(meX, meY)) {
                System.out.println("E3");
            }

            if (pGO.isInside(meX, meY + height - 1)) {
                System.out.println("E4");
            }

            return true;
        } else {
            return false;
        }

    }

    public Boolean isTouchingAbove(GameObject[] pGO, int pStepWide) {

        int space = pStepWide / 2;

        int yValueForCheck = y - space;

        for (GameObject go : pGO) {
            if (isTouching(x, yValueForCheck, go)) {
                System.out.println("is Touching Above");
                return true;
            }
        }
        return false;
    }

    public Boolean isTouchingBelow(GameObject[] pGO, int pStepWide) {

        int space = pStepWide / 2;

        int yValueForCheck = y + space;

        for (GameObject go : pGO) {
            if (isTouching(x, yValueForCheck, go)) {
                System.out.println("is Touching Below");
                return true;
            }
        }
        return false;
    }

    public Boolean isTouchingRight(GameObject[] pGO, int pStepWide) {

        int space = pStepWide / 2;

        int xValueForCheck = x + space;
        //System.out.println("check4Right");

        for (int i = 0; i < pGO.length; i++) {
            if (isTouching(xValueForCheck, y, pGO[i])) {
                System.out.println("is Touching Right");
                return true;
            }
        }
        return false;
    }


    public Boolean isTouchingLeft(GameObject[] pGO, int pStepWide) {

        int space = pStepWide / 2;

        int xValueForCheck = x - space;

        for (int i = 0; i < pGO.length; i++) {
            if (isTouching(xValueForCheck, y, pGO[i])) {
                System.out.println("is Touching Left");
                return true;
            }
        }
        return false;
    }

    public void paintTo(Graphics pG) {

//		if (type == PACMAN) {
//			pG.drawImage(img, 0, 0, this);
//			
//		}

        pG.setColor(color);
        pG.fillRect(x, y, width, height);

    }


    /**
     * gibt mögliche Richtungen in einem Array zurück
     *
     * @param pWall
     * @param pStepWide
     * @param pGhosts
     * @param pCurrentGhostNumber um zwischen den Geistern die noch überprüft werden müssen, zu unterscheiden
     * @return Boolean, in welche Richtung man gehen könnte
     */
    public Boolean[] getPossibleDirections(GameObject[] pWall, int pStepWide, GameObject[] pGhosts, int pCurrentGhostNumber) {

        Boolean[] possibleDirections = new Boolean[4];

        //hole die anderen Geister in einen eigenen Array
        GameObject[] ghostsToCheck = new GameObject[2];
        int counter = 0;
        for (int i = 0; i < pGhosts.length; i++) {
            if (i != pCurrentGhostNumber) {
                ghostsToCheck[counter] = pGhosts[i];
                counter++;
            }
        }

        //fülle den Array mit den Booleans
        possibleDirections[RIGHT_DIRECTION] = !isTouchingRight(pWall, pStepWide) && !isTouchingRight(ghostsToCheck, pStepWide);
        possibleDirections[LEFT_DIRECTION] = !isTouchingLeft(pWall, pStepWide) && !isTouchingLeft(ghostsToCheck, pStepWide);
        possibleDirections[UP_DIRECTION] = !isTouchingAbove(pWall, pStepWide) && !isTouchingAbove(ghostsToCheck, pStepWide);
        possibleDirections[DOWN_DIRECTION] = !isTouchingBelow(pWall, pStepWide) && !isTouchingBelow(ghostsToCheck, pStepWide);

        //das die Geister nicht die ganze Zeit hin- und her gehen
        switch (currentDirection) {
            case UP_DIRECTION:
                possibleDirections[DOWN_DIRECTION] = false;
                break;
            case DOWN_DIRECTION:
                possibleDirections[UP_DIRECTION] = false;
                break;
            case LEFT_DIRECTION:
                possibleDirections[RIGHT_DIRECTION] = false;
                break;
            case RIGHT_DIRECTION:
                possibleDirections[LEFT_DIRECTION] = false;
                break;
            case WAIT:
                break;
            default:
                System.out.println("Fehler | keine richtige CurrentDirection 276");
                break;
        }

        int counterOfPossibleDirections = 0;
        for (Boolean possibleDirection : possibleDirections) {
            if (possibleDirection) {
                counterOfPossibleDirections++;
            }
        }
        System.out.println("Possible Directions: " + counterOfPossibleDirections);
        return possibleDirections;
    }

    public int getRandomDirectionForGhost(GameObject[] pWall, int pStepWide, GameObject[] pGhosts, int pCurrentGhostNumber) {

        Boolean[] possibleDirections = getPossibleDirections(pWall, pStepWide, pGhosts, pCurrentGhostNumber);

        int possibleDirectionsCounter = 0;
        for (int i = 0; i < possibleDirections.length; i++) {
            if (possibleDirections[i]) {
                possibleDirectionsCounter++;
            }
            System.out.println("23474: " + i + " _ enthält: " + possibleDirections[i]);
        }
        System.out.println("counter: hu " + possibleDirectionsCounter);


        if (possibleDirectionsCounter == 0) {

            System.out.println("leichterFehlerb | sollte nicht zu oft vorkommen! [class: GameObject, getRandomDirectionForGhost]");
            return WAIT;
        }

        Random random = new Random();
        int randomNumber = random.nextInt(possibleDirectionsCounter);

        int counterWith = 0;
        for (int j = 0; j < possibleDirections.length; j++) {
            if (possibleDirections[j]) {

                if (counterWith == randomNumber) {


                    return j;
                }
                counterWith++;
            }
        }
        System.out.println("Fehler | Bis hier hin sollte es nicht gehen! 233");//TODO hier ist der Fehler
        return WAIT;
    }

    public void invertCurrentDirection() {
        switch (currentDirection) {
            case UP_DIRECTION:
                currentDirection = DOWN_DIRECTION;
                break;
            case DOWN_DIRECTION:
                currentDirection = UP_DIRECTION;
                break;
            case LEFT_DIRECTION:
                currentDirection = RIGHT_DIRECTION;
                break;
            case RIGHT_DIRECTION:
                currentDirection = LEFT_DIRECTION;
                break;
            case WAIT:
                break;
            default:
                System.out.println("Fehler | Sollte nicht passieren | Keine richtige Direction");
        }
    }


    public Boolean isThere(int directionCode, GameObject[] walls, int stepwide) {
        switch (directionCode) {
            case UP_DIRECTION:
                return isTouchingAbove(walls, stepwide);

            case DOWN_DIRECTION:
                return isTouchingBelow(walls, stepwide);

            case LEFT_DIRECTION:
                return isTouchingLeft(walls, stepwide);

            case RIGHT_DIRECTION:
                return isTouchingRight(walls, stepwide);

            default:
                System.out.println("Fehler | no real directionCode! [Class: GameObject, isthere()]");
                return true;
        }
    }

//	private int getAutoNewDirection(GameObject [] walls, int stepwide) {
//		
//		Boolean [] noDirections = getNotDirections(walls, stepwide);
//		
//		for (int i = 0; i < noDirections.length; i++) {
//			if(noDirections[i] = false) {
//				
//			}
//		}
//		
//		int [] possibleDirections = new int[4];
//		
//		
//		return null;
//	}


//	switch(randomNumber) {
//	case UP_DIRECTION:
//		
//		break;
//	case DOWN_DIRECTION:
//		
//		break;
//	case GameObject.LEFT_DIRECTION:
//		
//		break;
//	case GameObject.RIGHT_DIRECTION:
//		
//		break;
//
//	default:
//		
//		break;
//	}

}
