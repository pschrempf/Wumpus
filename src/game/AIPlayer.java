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
                break;
            case PIT_CODE:
                msg = "You fell into a pit!";
                setGameOver(true);
                break;
            case TREASURE_CODE:
                msg = "You have collected the treasure!";
                collectTreasure();
                break;
            case MISSEDWUMPUS_CODE:
                msg = "You missed the wumpus and it moved location!";
                for (int i = 0; i < caves.length; i++) {
                    caves[i].addAction(CaveAction.WUMPUS);
                }
                break;
            case HITWUMPUS_CODE:
                msg = "Hit the Wumpus!";
                for (int i = 0; i < caves.length; i++) {
                    caves[i].removeAction(CaveAction.WUMPUS);
                }
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

        // current location is safe
        caves[getLocation()].removeAction(CaveAction.TREASURE);
        caves[getLocation()].removeAction(CaveAction.PIT);
        caves[getLocation()].removeAction(CaveAction.WUMPUS);

        // remove actions from other caves
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
        Random r = new Random();

        switch (identifier) {
            case SHOOTSELECT_CODE:
                if (hasSlainWumpus()) return "n";

                // checks if the location of the Wumpus is known
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

                // if there is a stench and at least a 50/50 chance of hitting the Wumpus then shoot
                if (stench) {
                    ArrayList<Integer> safeCaves = new ArrayList<>();
                    for (int adjacent : connections) {
                        if (!caves[adjacent].contains(CaveAction.WUMPUS) && !caves[adjacent].contains(CaveAction.PIT)) safeCaves.add(adjacent);
                    }
                    if ((connections.size() - safeCaves.size()) < 3) {
                        wumpusLocation = connections.get(r.nextInt(connections.size()));
                        return "y";
                    }
                }
                System.out.println("n");
                return "n";
            case SHOOT_CODE:
                System.out.println(wumpusLocation + 1);
                return String.valueOf(wumpusLocation + 1);
            case MOVE_CODE:


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
                int move;

                // if there are new safe connections move into them
                if (newSafeConnections.size() > 0) {
                    move = newSafeConnections.get(r.nextInt(newSafeConnections.size()));
                }
                // if there is a safe connection choose it
                else if (safeConnections.size() > 0 && r.nextInt(100) > 10) {
                    move = safeConnections.get(r.nextInt(safeConnections.size()));
                } else {
                    move = connections.get(r.nextInt(connections.size()));
                }
                System.out.println(move + 1);
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
