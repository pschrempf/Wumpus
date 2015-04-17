package game;

import java.util.*;

/**
 * @author wumpus
 */
public class Game implements IConstants {

    static boolean[][] graph;
    static Cave[] caveSystem;
    static ArrayList<Player> players;

    public static void main(String[] args) {
        init();

        /*System.out.println("Adjacency matrix:");
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph.length; j++) {
                if (graph[i][j]) System.out.print("X");
                else System.out.print("O");
                System.out.print(" ");
            }
            System.out.println();
        }*/

        gameFlow:
        while (true) {
            for (Player player : players) {
                System.out.println();
                if (players.size() > 1)
                    System.out.println("It is " + player.toString() + "'s turn");       // only print player's name if more than single player
                turn(player);
                player.incrementMovesMade();
                if (player.isGameOver()) break gameFlow;
            }
        }

        printGameSummary();
    }

    private static void printGameSummary() {
        System.out.println("Game statistics: ");

        for (Player player : players) {
            System.out.println(player.getGameStatisics());
        }
    }

    private static void turn(Player player) {
        // check if player is in a specific cave
        Cave currentCave = caveSystem[player.getLocation()];
        if (!currentCave.hasNoActions()) {
            if (currentCave.executeEvents(player)) return;
        }

        // print possible directions
        player.feedBack("You are in cave number " + (player.getLocation() + 1) + "!");

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
            if (action == CaveAction.TREASURE) player.feedBack("There is a sense of glittering...");
            if (action == CaveAction.PIT) player.feedBack("You can feel a light breeze...");
            if (action == CaveAction.WUMPUS) player.feedBack("There is a strong stench...");
        }

        String feedback = "You can move to caves: ";
        for (int i = 0; i < possibilities.size(); i++) {
            feedback += (possibilities.get(i) + 1);
            if (i < possibilities.size() - 1) feedback += ", ";
        }
        player.feedBack(feedback);

        Scanner in = new Scanner(System.in);

        // check shooting
        if (player.getArrows() > 0) {
            player.feedBack("Would you like to shoot an arrow? (y/n)");
            while (true) {
                try {
                    String line = player.getInput("");
                    if (line.equals("n")) break;
                    if (line.equals("y")) {
                        player.feedBack("Which cave would you like to shoot into? If you know what I mean.");
                        int aim;
                        while (!possibilities.contains(aim = Integer.parseInt(in.nextLine()) - 1)) {
                            player.feedBack("Please enter a valid cave number!");
                        }
                        if (!caveSystem[aim].hasNoActions() && caveSystem[aim].contains(CaveAction.WUMPUS)) {
                            player.feedBack("Congratulations, you killed the Wumpus!");
                            player.setWumpusSlain(true);
                            caveSystem[aim].removeAction(CaveAction.WUMPUS);
                        } else {
                            player.feedBack("You missed the Wumpus... It has been disturbed and has moved location.");
                            // delete the wumpus from actions
                            for (int i = 0; i < NUMBER_OF_CAVES; i++) {
                                if (caveSystem[i].contains(CaveAction.WUMPUS)) {
                                    caveSystem[i].removeAction(CaveAction.WUMPUS);
                                    if (i == player.getLocation()) {
                                        player.feedBack("He has moved into your cave!");
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
                    player.feedBack("You fucking stupid person put in a fucking number.");
                }
            }
        }

        // check movement
        player.feedBack("Please enter the number of the cave you would like to move to: ");
        int nextCave = -1;
        do {
            try {
                nextCave = Integer.parseInt(player.getInput("")) - 1;
            } catch (NumberFormatException e) {
                player.feedBack("Please enter a valid number!");
            }
        } while (!possibilities.contains(nextCave));


        player.setLocation(nextCave);
    }

    private static void init() {
        System.out.println("************************************" +
                "\n*********WUMPUS SLAYER 2015*********" +
                "\n************************************");
        System.out.println();
        System.out.println("Initialising game...");

        graph = new boolean[NUMBER_OF_CAVES][NUMBER_OF_CAVES];
        caveSystem = new Cave[NUMBER_OF_CAVES];
        players = new ArrayList<>();

        generateRandomGraph();
        generateRandomCaves();
        addExtraEdges();

        Scanner in = new Scanner(System.in);

        String answer;
        do {
            System.out.println("What is your name?");
            String name = in.nextLine();
            if (name.equalsIgnoreCase("AI")) {
                players.add(new AIPlayer(name + (players.size() + 1)));
            } else {
                players.add(new KeyboardPlayer(name));
            }
            System.out.println("Would you like to add more players? (y/n)");
            answer = in.nextLine();
            while (!answer.equals("y") && !answer.equals("n")) {
                System.out.println("Please enter a valid answer.");
                answer = in.nextLine();
            }
        } while (!answer.equals("n"));

        System.out.println("Game initialised!");
    }

    private static void addExtraEdges() {
        for (int i = 0; i < caveSystem.length; i++) {
            if (caveSystem[i].contains(CaveAction.PIT)) {
                ArrayList<Integer> connectedCaves = new ArrayList<>();

                for (int j = 0; j < graph[i].length; j++) {
                    if (graph[i][j]) connectedCaves.add(j);
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
            if (caveSystem[index].hasNoActions() || caveSystem[index].contains(CaveAction.WUMPUS)) {
                if (caveSystem[index].addAction(CaveAction.PIT)) pitsPlaced++;
            }
        }

        // placing the bats
        System.out.println("The superbats are engaged.");
        int superBatsPlaced = 0;
        while (superBatsPlaced < NUMBER_OF_BATS) {
            int index = r.nextInt(NUMBER_OF_CAVES);
            if (caveSystem[index].hasNoActions()) {
                if (caveSystem[index].addAction(CaveAction.SUPERBAT)) superBatsPlaced++;
            }
        }

        // place treasure
        System.out.println("The treasure is hidden deep inside the cave system...");
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

        // generate maximum number of connections for each cave
        int[] maxConnections = new int[NUMBER_OF_CAVES];
        for (int i = 0; i < maxConnections.length; i++) {
            maxConnections[i] = r.nextInt(MAX_ADJACENT_CAVES) + 1;
        }
    }
}

