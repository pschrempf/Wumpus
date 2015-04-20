package game;

import java.io.IOException;
import java.util.*;

/**
 * @author wumpus
 */
public class Game implements IConstants {

    static boolean[][] graph;
    static Cave[] caveSystem;
    static ArrayList<Player> players;
    public static boolean isFirstPlayer;
    static Scanner consoleReader;

    public static void main(String[] args) {
        try {
            init();

            gameFlow:
            while (true) {
                for (Player player : players) {
                    System.out.println();
                    if (players.size() > 1) {
                        System.out.println("It is " + player.toString() + "'s turn"); // only print player's name if more than single player
                    }
                    turn(player);

                    player.incrementMovesMade();
                    if (player.isGameOver())
                        break gameFlow;
                }
            }

            printGameSummary();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private static void printGameSummary() {
        System.out.println("Game statistics: ");

        for (Player player : players) {
            System.out.println(player.getGameStatisics());
        }
    }

    private static void turn(Player player) throws IOException {
        // check if player is in a specific cave
        Cave currentCave = caveSystem[player.getLocation()];
        if (!currentCave.hasNoActions()) {
            if (currentCave.executeEvents(player))
                return;
        }

        player.feedBack(PRINT_CODE + PARAMETER_SEPARATOR
                + "You are in cave number " + (player.getLocation() + 1) + "!");

        ArrayList<Integer> possibilities = new ArrayList<>();
        Set<CaveAction> adjacent = new HashSet<>();

        for (int i = 0; i < NUMBER_OF_CAVES; i++) {
            if ((graph[player.getLocation()][i])) {
                possibilities.add(i);
                if (!caveSystem[i].hasNoActions()) {
                    adjacent.addAll(caveSystem[i].getActions());
                }
            }
        }

        for (CaveAction action : adjacent) {
            if (action == CaveAction.TREASURE)
                player.feedBack(GLISTEN_CODE);
            if (action == CaveAction.PIT)
                player.feedBack(BREEZE_CODE);
            if (action == CaveAction.WUMPUS)
                player.feedBack(STENCH_CODE);
        }

        String feedback = "";
        for (int i = 0; i < possibilities.size(); i++) {
            feedback += (possibilities.get(i) + 1);
            if (i < possibilities.size() - 1)
                feedback += ",";
        }
        player.feedBack(CONNECTIONS_CODE + PARAMETER_SEPARATOR + feedback);

        // check shooting
        if (player.getArrows() > 0) {
            while (true) {
                try {
                    String line = player.getInput(SHOOTSELECT_CODE);
                    if (line.equals("n"))
                        break;
                    if (line.equals("y")) {
                        int aim;
                        while (!possibilities.contains(aim = Integer
                                .parseInt(player.getInput(SHOOT_CODE)) - 1)) {
                            player.feedBack(PRINT_CODE + PARAMETER_SEPARATOR
                                    + "Please enter a valid cave number!");
                        }
                        if (!caveSystem[aim].hasNoActions()
                                && caveSystem[aim].contains(CaveAction.WUMPUS)) {
                            player.feedBack(PRINT_CODE + PARAMETER_SEPARATOR
                                    + "Congratulations, you killed the Wumpus!");
                            player.setWumpusSlain(true);
                            caveSystem[aim].removeAction(CaveAction.WUMPUS);
                        } else {
                            player.feedBack(PRINT_CODE
                                    + PARAMETER_SEPARATOR
                                    + "You missed the Wumpus... It has been disturbed and has moved location.");
                            // delete the wumpus from actions
                            for (int i = 0; i < NUMBER_OF_CAVES; i++) {
                                if (caveSystem[i].contains(CaveAction.WUMPUS)) {
                                    caveSystem[i]
                                            .removeAction(CaveAction.WUMPUS);
                                    if (i == player.getLocation()) {
                                        player.feedBack(PRINT_CODE
                                                + PARAMETER_SEPARATOR
                                                + "He has moved into your cave!");
                                    }
                                }
                            }
                            // place the wumpus in a random cave
                            Random r = new Random();
                            int index = r.nextInt(NUMBER_OF_CAVES);
                            caveSystem[index].addAction(CaveAction.WUMPUS);
                        }
                        player.shootArrow();
                        return;
                    }
                } catch (NumberFormatException e) {
                    player.feedBack(PRINT_CODE
                            + PARAMETER_SEPARATOR
                            + "You fucking stupid person put in a fucking number.");
                }
            }
        }

        // check movement
        int nextCave = -1;
        do {
            try {
                nextCave = Integer.parseInt(player.getInput(MOVE_CODE)) - 1;
            } catch (NumberFormatException e) {
                player.feedBack(PRINT_CODE + PARAMETER_SEPARATOR
                        + "Please enter a valid number!");
            }
        } while (!possibilities.contains(nextCave));

        player.setLocation(nextCave);
    }

    private static void init() throws IOException {
        System.out.println("************************************"
                + "\n*********WUMPUS SLAYER 2015*********"
                + "\n************************************");
        System.out.println();
        System.out.println("Initialising game...");

        graph = new boolean[NUMBER_OF_CAVES][NUMBER_OF_CAVES];
        caveSystem = new Cave[NUMBER_OF_CAVES];
        players = new ArrayList<>();
        consoleReader = new Scanner(System.in);

        boolean isOverNetwork = false;

        // Adding players

        String answer;
        // Initialising first player
        System.out.println("What is your name?");
        String name = consoleReader.nextLine();
        if (name.equalsIgnoreCase("AI")) {
            players.add(new AIPlayer(name + (players.size() + 1)));
        } else {
            players.add(new KeyboardPlayer(name));
        }

        System.out.println("Would you like to play over the network (y/n)?");

        answer = consoleReader.nextLine();
        while (!answer.equals("y") && !answer.equals("n")) {
            System.out.println("Please enter a valid answer.");
            answer = consoleReader.nextLine();
        }
        if (answer.equalsIgnoreCase("y")) {
            try {
                NetworkPlayer newNetPlayer = (NetworkPlayer) NetworkPlayer
                        .createNetworkPlayer(false);
                players.add(newNetPlayer);

                isOverNetwork = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            System.out.println("Would you like to add another player? (y/n)");
            answer = consoleReader.nextLine();
            while (!answer.equalsIgnoreCase("y")
                    && !answer.equalsIgnoreCase("n")) {
                System.out.println("Please enter a valid answer.");
                answer = consoleReader.nextLine();
            }
            if (answer.equalsIgnoreCase("y")) {
                System.out.println("What is your name?");
                name = consoleReader.nextLine();
                if (name.equalsIgnoreCase("AI")) {
                    players.add(new AIPlayer(name + (players.size() + 1)));
                } else {
                    players.add(new KeyboardPlayer(name));
                }
            }
        }

        if (!isFirstPlayer && isOverNetwork) {
            String[] parts = players.get(1).getInput("graphSetup").split("-");
            String[] graphSetup = parts[0].split(";");
            for (int i = 0; i < graphSetup.length; i++) {
                String[] setupParts = graphSetup[i].split(",");
                graph[Integer.parseInt(setupParts[0])][Integer.parseInt(setupParts[1])] = true;
            }

            String[] caveSetup = parts[1].split(";");

            for (int i = 0; i < caveSetup.length; i++) {
                caveSystem[i] = new Cave();
                String[] caveActions = caveSetup[i].split(",");
                for (int j = 0; j < caveActions.length; j++) {
                    switch (caveActions[j].toUpperCase()) {
                        case "PIT":
                            caveSystem[i].addAction(CaveAction.PIT);
                            break;
                        case "SUPERBAT":
                            caveSystem[i].addAction(CaveAction.SUPERBAT);
                            break;
                        case "WUMPUS":
                            caveSystem[i].addAction(CaveAction.WUMPUS);
                            break;
                        case "EXIT":
                            caveSystem[i].addAction(CaveAction.EXIT);
                            break;
                        case "TREASURE":
                            caveSystem[i].addAction(CaveAction.TREASURE);
                            break;

                    }
                }
            }

        } else {
            generateRandomGraph();
            generateRandomCaves();
            addExtraEdges();
            if (isOverNetwork) {
                String matrixFeedback = "";
                for (int i = 0; i < NUMBER_OF_CAVES; i++) {
                    for (int j = 0; j < NUMBER_OF_CAVES; j++) {
                        if (graph[i][j]) {
                            matrixFeedback += i + "," + j + ";";
                        }
                    }
                }
                matrixFeedback = matrixFeedback.substring(0, matrixFeedback.length() - 1);
                players.get(1).feedBack(matrixFeedback + "-");

                String caveFeedback = "";
                for (int i = 0; i < caveSystem.length; i++) {
                    for (int j = 0; j < caveSystem[i].getActions().size(); j++) {
                        caveFeedback += caveSystem[i].getActions().get(j) + ",";
                    }
                    if (caveSystem[i].getActions().size() > 0) {
                        caveFeedback = caveFeedback.substring(0, caveFeedback.length() - 1);
                    }
                    caveFeedback += ";";
                    

                }
                caveFeedback = caveFeedback.substring(0, caveFeedback.length() - 1);

                players.get(1).feedBack(caveFeedback);
            }

        }
        for (Player player : players) {
            dropPlayer(player);
        }

        System.out.println("Game initialised!");
    }

    private static void addExtraEdges() {
        for (int i = 0; i < caveSystem.length; i++) {
            if (caveSystem[i].contains(CaveAction.PIT)) {
                ArrayList<Integer> connectedCaves = new ArrayList<>();

                for (int j = 0; j < graph[i].length; j++) {
                    if (graph[i][j])
                        connectedCaves.add(j);
                }

                for (int j = 0; j < connectedCaves.size(); j++) {
                    for (int k = 0; k < connectedCaves.size(); k++) {
                        int x = connectedCaves.get(j);
                        int y = connectedCaves.get(k);
                        if (x != y) {
                            graph[x][y] = true;
                            graph[y][x] = true;
                        }
                    }
                }
            }
        }
    }

    public static void dropPlayer(Player player) {
        Random r = new Random();
        while (true) {
            int index = r.nextInt(NUMBER_OF_CAVES);
            if (caveSystem[index].hasNoActions()) {
                player.setLocation(index);
                break;
            }
        }
    }

    private static void generateRandomCaves() {
        Random r = new Random();

        // placing the wumpus
        System.out.println("The Wumpus is ready to fight!");
        caveSystem[r.nextInt(NUMBER_OF_CAVES)].addAction(CaveAction.WUMPUS);

        // placing the pits
        System.out.println("The pits have been formed.");
        int pitsPlaced = 0;
        while (pitsPlaced < NUMBER_OF_PITS) {
            int index = r.nextInt(NUMBER_OF_CAVES);
            if (caveSystem[index].hasNoActions()
                    || caveSystem[index].contains(CaveAction.WUMPUS)) {
                if (caveSystem[index].addAction(CaveAction.PIT))
                    pitsPlaced++;
            }
        }

        // placing the bats
        System.out.println("The superbats are engaged.");
        int superBatsPlaced = 0;
        while (superBatsPlaced < NUMBER_OF_BATS) {
            int index = r.nextInt(NUMBER_OF_CAVES);
            if (caveSystem[index].hasNoActions()) {
                if (caveSystem[index].addAction(CaveAction.SUPERBAT))
                    superBatsPlaced++;
            }
        }

        // place treasure
        System.out
                .println("The treasure is hidden deep inside the cave system...");
        while (true) {
            int index = r.nextInt(NUMBER_OF_CAVES);
            if (caveSystem[index].hasNoActions()) {
                caveSystem[index].addAction(CaveAction.TREASURE);
                break;
            }
        }

        // place exit
        System.out.println("The cave has one exit only...");
        while (true) {
            int index = r.nextInt(NUMBER_OF_CAVES);
            if (caveSystem[index].hasNoActions()) {
                caveSystem[index].addAction(CaveAction.EXIT);
                break;
            }
        }
    }

    private static void generateRandomGraph() {
        Random r = new Random();

        // create cave system
        for (int i = 0; i < NUMBER_OF_CAVES; i++) {
            caveSystem[i] = new Cave();
            int index;
            do {
                index = r.nextInt(NUMBER_OF_CAVES);
            } while (index == i);
            graph[i][index] = true;
            graph[index][i] = true;
        }

		/*
         * // generate maximum number of connections for each cave int[]
		 * maxConnections = new int[NUMBER_OF_CAVES]; for (int i = 0; i <
		 * maxConnections.length; i++) { maxConnections[i] =
		 * r.nextInt(MAX_ADJACENT_CAVES) + 1; }
		 */
    }
}
