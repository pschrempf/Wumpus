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
        System.out.println(feedback);

    }

    @Override
    public String getInput(String prompt) {
        return null;
    }
}
