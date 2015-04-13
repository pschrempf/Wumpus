package game;

import java.util.*;

public class Main {

    static int MAX_ADJACENT_CAVES = 3;
    static int NUMBER_OF_CAVES = 10;
    static int NUMBER_OF_BATS = 1;
    static int NUMBER_OF_PITS = 1;
    static int INITIAL_ARROWS = 1;
    static int NUMBER_OF_PLAYERS = 1;

    static boolean[][] graph;
    static Cave[] caveSystem;
    static boolean gameOver;
    static int playerCave;
    static boolean treasureCollected;
    static int arrows;
    static boolean wumpusAlive;

    public static void main(String[] args) {
        init();

        /*System.out.println("Adjacency matrix:");
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph.length; j++) {
                System.out.print(graph[i][j] + " ");
            }
            System.out.print("\n");
        }*/

        while (!gameOver) {
            play();
        }
    }

    private static void play() {
        // check if player is in a specific cave
        Cave currentCave = caveSystem[playerCave];
        if (currentCave != null) {
            if (currentCave.contains(CaveAction.PIT)) {
                gameOver = true;
                System.out.println("You fell into a pit!");
                return;
            } else if (currentCave.contains(CaveAction.EXIT)) {
                System.out.println("You have reached the exit.");
                if (treasureCollected && !wumpusAlive) {
                    System.out.println("As you have collected the treasure and slain the Wumpus, you have exited the cave! Well Done!");
                    gameOver = true;
                    return;
                } else {
                    System.out.println("Please collect the treasure and kill the Wumpus before exiting...");
                }
            } else if (currentCave.contains(CaveAction.TREASURE)) {
                treasureCollected = true;
                caveSystem[playerCave] = null;
                System.out.println("You have collected the treasure!");
            } else if (currentCave.contains(CaveAction.SUPERBAT)) {
                System.out.println("There is a flap of wings and you are transported to a random cave!");
                while (true) {
                    Random r = new Random();
                    int index = r.nextInt(NUMBER_OF_CAVES);
                    if (caveSystem[index] == null || (!caveSystem[index].contains(CaveAction.WUMPUS) && !caveSystem[index].contains(CaveAction.PIT))) {
                        playerCave = index;
                        return;
                    }
                }
            } else if (currentCave.contains(CaveAction.WUMPUS)) {
                System.out.println("The Wumpus has killed you before you could even make the slightest noise... Nice try.");
                gameOver = true;
                return;
            }
        }

        // print possible directions
        System.out.println("You are in cave number " + playerCave + "!");

        ArrayList<Integer> possibilities = new ArrayList<>();
        Set<CaveAction> adjacent = new HashSet<>();

        for (int i = 0; i < NUMBER_OF_CAVES; i++) {
            if ((graph[playerCave][i]) == true) {
                possibilities.add(i);
                if (caveSystem[i] != null) {
                    adjacent.addAll(caveSystem[i].getActions());
                }
            }
        }

        for (CaveAction action : adjacent) {
            if (action == CaveAction.TREASURE) System.out.println("There is a sense of glittering...");
            if (action == CaveAction.PIT) System.out.println("You can feel a light breeze...");
            if (action == CaveAction.WUMPUS) System.out.println("There is a strong stench...");
        }

        System.out.print("You can move to caves: ");
        for (int i = 0; i < possibilities.size(); i++) {
            System.out.print(possibilities.get(i));
            if (i < possibilities.size() - 1) System.out.print(", ");
        }
        System.out.print("\n");

        Scanner in = new Scanner(System.in);

        // check shooting
        if (arrows > 0) {
            System.out.println("Would you like to shoot an arrow?");
            while (true) {
                String line = in.nextLine();
                if (line.equals("n")) break;
                if (line.equals("y")) {
                    System.out.println("Which cave would you like to shoot into?");
                    int aim;
                    while (!possibilities.contains(aim = Integer.parseInt(in.nextLine()))) {
                        System.out.println("Please enter a valid cave number!");
                    }
                    if (caveSystem[aim] != null && caveSystem[aim].contains(CaveAction.WUMPUS)) {
                        System.out.println("Congratulations, you killed the Wumpus!");
                        caveSystem[aim] = null;
                    } else {
                        System.out.println("You missed the Wumpus...");
                    }
                    arrows--;
                    return;
                }
            }
        }

        // check movement
        System.out.println("Please enter the number of the cave you would like to move to: ");
        int nextCave;
        while (!possibilities.contains(nextCave = Integer.parseInt(in.nextLine()))) {
            System.out.println("Please enter a valid cave number!");
        }
        playerCave = nextCave;
    }

    private static void move() {
        System.out.println("Which cave would you like to move to?");
    }

    private static void init() {
        graph = new boolean[NUMBER_OF_CAVES][NUMBER_OF_CAVES];
        caveSystem = new Cave[NUMBER_OF_CAVES];
        arrows = INITIAL_ARROWS;
        gameOver = false;
        treasureCollected = false;
        wumpusAlive = true;

        generateRandomGraph();
        generateRandomCaves();

        dropPlayer();
    }

    private static void dropPlayer() {
        Random r = new Random();
        while (true) {
            int index = r.nextInt(NUMBER_OF_CAVES);
            if (caveSystem[index] == null) {
                playerCave = index;
                break;
            }
        }
    }

    private static void generateRandomCaves() {
        Random r = new Random();

        // placing the wumpus
        caveSystem[r.nextInt(NUMBER_OF_CAVES)] = new Cave(CaveAction.WUMPUS);

        // placing the pits
        int pitsPlaced = 0;
        while (pitsPlaced < NUMBER_OF_PITS) {
            int index = r.nextInt(NUMBER_OF_CAVES);
            if (caveSystem[index] == null) {
                caveSystem[index] = new Cave(CaveAction.PIT);
                pitsPlaced++;
            }
        }

        // placing the bats
        int superBatsPlaced = 0;
        while (superBatsPlaced < NUMBER_OF_BATS) {
            int index = r.nextInt(NUMBER_OF_CAVES);
            if (caveSystem[index] == null) {
                caveSystem[index] = new Cave(CaveAction.SUPERBAT);
                superBatsPlaced++;
            }
        }

        // place treasure
        while (true) {
            int index = r.nextInt(NUMBER_OF_CAVES);
            if (caveSystem[index] == null) {
                caveSystem[index] = new Cave(CaveAction.TREASURE);
                break;
            }
        }

        // place exit
        while (true) {
            int index = r.nextInt(NUMBER_OF_CAVES);
            if (caveSystem[index] == null) {
                caveSystem[index] = new Cave(CaveAction.EXIT);
                break;
            }
        }
    }

    private static void generateRandomGraph() {
        Random r = new Random();

        ArrayList<Integer> connected = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_CAVES; i++) {
            for (int j = 0; j < MAX_ADJACENT_CAVES; j++) {
                if (r.nextBoolean()) {
                    int y = r.nextInt(NUMBER_OF_CAVES);
                    if (i != y) {
                        graph[i][y] = true;
                        graph[y][i] = true;
                        connected.add(y);
                    }
                }
            }
        }

        return;
    }


}

