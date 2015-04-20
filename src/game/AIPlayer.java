package game;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author wumpus
 */
public class AIPlayer extends Player {

    private boolean[][] graph;
    private Cave[] caves;
    private int exitLocation;
    private int wumpusLocation;
    private boolean[] visited;

    private boolean glistening;
    private boolean breeze;
    private boolean stench;

    public AIPlayer(String name) {
        super(name);

        graph = new boolean[NUMBER_OF_CAVES][NUMBER_OF_CAVES];
        caves = new Cave[NUMBER_OF_CAVES];
        for (int i = 0; i < NUMBER_OF_CAVES; i++) {
            caves[i] = new Cave();
            caves[i].addAction(CaveAction.PIT);
            caves[i].addAction(CaveAction.WUMPUS);
            caves[i].addAction(CaveAction.TREASURE);
        }
        visited = new boolean[NUMBER_OF_CAVES];
        glistening = false;
        breeze = false;
        stench = false;
        exitLocation = -1;
        wumpusLocation = -1;
    }

    @Override
    public void feedBack(String feedback) {
        String identifier = feedback.split(PARAMETER_SEPARATOR)[0];
        String msg = "";
        switch (identifier) {
            case GLISTEN_CODE:
                glistening = true;
                msg = "There is a sense of glittering...";
                break;
            case BREEZE_CODE:
                breeze = true;
                msg = "You can feel a light breeze...";
                break;
            case STENCH_CODE:
                stench = true;
                msg = "There is a strong stench...";
                break;
            case EXIT_CODE:
               // exitLocation
               // msg = "You have reached the exit.";
                exitLocation = getLocation();
                msg = "You have reached the exit.";
                if (hasTreasure()) {
                    msg += "\nAs you have collected the treasure you have exited the cave! Well Done!";
                    setGameOver(true);
                    setExited(true);
                } else {
                    msg += "\nPlease collect the treasure before exiting...";
                }
                break;
            case CONNECTIONS_CODE:
                String temp = feedback.split(PARAMETER_SEPARATOR)[1];
                msg += "You can move to the caves: " + feedback.split(PARAMETER_SEPARATOR)[1].replaceAll(",", ", ");
                ArrayList<Integer> connections = new ArrayList<>();

                String[] conn = temp.split(",");
                for (int i = 0; i < conn.length; i++) {
                    connections.add(Integer.parseInt(conn[i]) - 1);
                }

                for (int index : connections) {
                    graph[getLocation()][index] = true;
                    graph[index][getLocation()] = true;
                }

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
        System.out.println(msg);
    }

    @Override
    public String getInput(String prompt) {
        String identifier = prompt.split(PARAMETER_SEPARATOR)[0];
        ArrayList<Integer> connections = getConnections(getLocation());

        switch (identifier) {
            case SHOOT_CODE:
                System.out.println(wumpusLocation);
                return String.valueOf(wumpusLocation);
            case SHOOTSELECT_CODE:
                int wumpusCounter = 0;
                for (int i = 0; i < caves.length; i++) {
                    if (caves[i].contains(CaveAction.WUMPUS)) {
                        wumpusCounter++;
                        wumpusLocation = i;
                    }
                }
                if (wumpusCounter == 1 && connections.contains(wumpusLocation)) {
                    System.out.println("y");
                    return "y";
                }
                wumpusLocation = -1;
                System.out.println("n");
                return "n";
            case MOVE_CODE:
                caves[getLocation()].removeAction(CaveAction.TREASURE);
                caves[getLocation()].removeAction(CaveAction.PIT);
                caves[getLocation()].removeAction(CaveAction.WUMPUS);

                for (int i = 0; i < caves.length; i++) {
                    if ((!glistening && connections.contains(i)) || (glistening && !connections.contains(i))) {
                        caves[i].removeAction(CaveAction.TREASURE);
                    }
                    if (!breeze && connections.contains(i)) {
                        caves[i].removeAction(CaveAction.PIT);
                    }
                    if ((!stench && connections.contains(i)) || (stench && !connections.contains(i))) {
                        caves[i].removeAction(CaveAction.WUMPUS);
                    }
                }

                glistening = false;
                breeze = false;
                stench = false;
                visited[getLocation()] = true;

                ArrayList<Integer> newSafeConnections = new ArrayList<>();
                ArrayList<Integer> safeConnections = new ArrayList<>();
                for (int index : connections) {
                    if (!caves[index].contains(CaveAction.WUMPUS) && !caves[index].contains(CaveAction.PIT)) {
                        if (!visited[index]) newSafeConnections.add(index);
                        safeConnections.add(index);
                    }
                }
                Random r = new Random();
                int move;
                if (newSafeConnections.size() == 0) {
                    // all safe caves have been visited
                    move = connections.get(r.nextInt(connections.size()));
                } else if (safeConnections.size() > 0) {
                    move = safeConnections.get(r.nextInt(safeConnections.size()));
                } else {
                    move = connections.get(r.nextInt(connections.size()));
                }
                System.out.println(move+1);
                return String.valueOf(move + 1);
        }
        return null;
    }

    private ArrayList<Integer> getConnections(int index) {
        ArrayList<Integer> connections = new ArrayList<>();
        for (int i = 0; i < graph.length; i++) {
            if (graph[index][i]) connections.add(i);
        }
        return connections;
    }
}
