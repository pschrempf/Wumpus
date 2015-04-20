package game;

import java.util.Scanner;

/**
 * @author wumpus
 */
public class AIPlayer extends Player {

    private Boolean[][] graph;
    private Boolean[] visited;
    private Boolean[] safe;
    private Cave[] caves;
    private int exitLocation;

    public AIPlayer(String name) {
        super(name);

        graph = new Boolean[NUMBER_OF_CAVES][NUMBER_OF_CAVES];
        visited = new Boolean[NUMBER_OF_CAVES];
        safe = new Boolean[NUMBER_OF_CAVES];
        caves = new Cave[NUMBER_OF_CAVES];
        for (int i = 0; i < NUMBER_OF_CAVES; i++) {
            caves[i].addAction(CaveAction.PIT);
            caves[i].addAction(CaveAction.WUMPUS);
            caves[i].addAction(CaveAction.TREASURE);
        }
        exitLocation = -1;
    }

    @Override
    public void feedBack(String feedback) {
        String identifier = feedback.split(PARAMETER_SEPARATOR)[0];
        String msg = "";
        switch (identifier) {
            case GLISTEN_CODE:
                msg = "There is a sense of glittering...";
                break;
            case BREEZE_CODE:
                msg = "You can feel a light breeze...";
                break;
            case STENCH_CODE:
                msg = "There is a strong stench...";
                break;
            case EXIT_CODE:
               // exitLocation
               // msg = "You have reached the exit.";
                if (hasTreasure()) {
                    msg += "\nAs you have collected the treasure you have exited the cave! Well Done!";
                    setGameOver(true);
                    setExited(true);
                } else {
                    msg += "\nPlease collect the treasure before exiting...";
                }
                break;
            case CONNECTIONS_CODE:
                msg += "You can move to the caves: " + feedback.split(PARAMETER_SEPARATOR)[1].replaceAll(",", ", ");
                break;
            case WUMPUS_CODE:
                msg = "The Wumpus has killed you before you could even make the slightest noise... Nice try.";
                setGameOver(true);
                setGameOver(true);
                break;
            case PIT_CODE:
                msg = "You fell into a pit!";
                setGameOver(true);
                break;
            case TREASURE_CODE:
                msg = "You have collected the treasure!";
                collectTreasure();
                break;
            case PRINT_CODE:
                msg = feedback.split(PARAMETER_SEPARATOR)[1];
                break;
        }
        System.out.println(feedback);

    }

    @Override
    public String getInput(String prompt) {
        return null;
    }
}
