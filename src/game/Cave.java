package game;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author wumpus
 */
public class Cave implements IConstants {
    private ArrayList<CaveAction> actions;

    public Cave() {
        actions = new ArrayList<>();
    }

    public boolean executeEvents(Player player) {
        if (actions.contains(CaveAction.PIT)) {
            System.out.println("You fell into a pit! You asshole.");
            player.setGameOver(true);
            return true;
        }
        if (actions.contains(CaveAction.WUMPUS)) {
            System.out.println("The Wumpus has killed you before you could even make the slightest noise... Nice try.");
            player.setGameOver(true);
            return true;
        }
        if (actions.contains(CaveAction.TREASURE)) {
            player.collectTreasure();
            System.out.println("You have collected the treasure!");
            actions.remove(CaveAction.TREASURE);
        }
        if (actions.contains(CaveAction.EXIT)) {
            System.out.println("You have reached the exit.");
            if (player.hasTreasure()) {
                System.out.println("As you have collected the treasure you have exited the cave! Well Done!");
                player.setGameOver(true);
                player.setExited(true);
                return true;
            } else {
                System.out.println("Please collect the treasure and kill the Wumpus before exiting...");
            }
        }
        if (actions.contains(CaveAction.SUPERBAT)) {
            System.out.println("There is a flap of wings and you are transported to a random cave!");
            Game.dropPlayer(player);
        }
        return false;
    }

    public ArrayList<CaveAction> getActions() {
        return actions;
    }

    public boolean contains(CaveAction action) {
        return actions.contains(action);
    }

    public boolean hasNoActions() {
        return actions.size() == 0;
    }

    public void removeAction(CaveAction action) {
        actions.remove(action);
    }

    public boolean addAction(CaveAction action) {
        if (!actions.contains(action)) {
            actions.add(action);
            return true;
        } else {
            return false;
        }
    }
}
