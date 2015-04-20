package game;

import java.util.Scanner;

/**
 * @author wumpus
 */
public class AIPlayer extends Player {

    private Boolean[][] graph;
    private Boolean[] visited;
    private Boolean[] safe;
    private Boolean[] breeze;
    private Boolean[] stench;
    private Boolean[] glisten;
    private int exitLocation;

    public AIPlayer(String name) {
        super(name);

        graph = new Boolean[NUMBER_OF_CAVES][NUMBER_OF_CAVES];
        /*for (int i = 0; i < NUMBER_OF_CAVES; i++) {
            for (int j = 0; j < NUMBER_OF_CAVES; j++) {
                if (i == j) graph[i][j] = true;
            }
        }*/

        visited = new Boolean[NUMBER_OF_CAVES];
        safe = new Boolean[NUMBER_OF_CAVES];
        breeze = new Boolean[NUMBER_OF_CAVES];
        stench = new Boolean[NUMBER_OF_CAVES];
        glisten = new Boolean[NUMBER_OF_CAVES];
        exitLocation = -1;
    }

    @Override
    public void feedBack(Code code, String feedback) {
        System.out.println(feedback);
        if (code.equals(Code.IGNORE)) return;
        if (code.equals(Code.BREEZE)) breeze[getLocation()] = true;
        if (code.equals(Code.CONNECTIONS)) {
            String[] connections = feedback.replaceAll("[^\\d,]", "").split(",");
            for (int i = 0; i < connections.length; i++) {
                int index = Integer.parseInt(connections[i]) - 1;
                graph[getLocation()][index] = true;
            }
        }
        if (code.equals(Code.EXIT)) exitLocation = getLocation();
        if (code.equals(Code.GLISTEN)) glisten[getLocation()] = true;
        if (code.equals(Code.STENCH)) stench[getLocation()] = true;
    }

    @Override
    public String getInput(Code code, String prompt) {
        if (code.equals(Code.MOVE)) {
            visited[getLocation()] = true;
            safe[getLocation()] = true;

            int move = 0;
            // if no breeze and no stench then all adjacent are safe
            if (!breeze[getLocation()] && !stench[getLocation()]) {
                for (int i = 0; i < NUMBER_OF_CAVES; i++) {
                    if (graph[getLocation()][i]) {
                        safe[i] = true;
                    }
                }
            }



            return String.valueOf(move);
        }
        if (code.equals(Code.SHOOT)) {
            return "n";
        }
        if (code.equals(Code.SHOTSELECTION)) return "n";
        return "n";
    }
}
