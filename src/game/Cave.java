package game;

import java.util.ArrayList;

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
            player.feedBack(Code.PIT, "You fell into a pit! You asshole.");
            player.setGameOver(true);
            return true;
        }
        if (actions.contains(CaveAction.WUMPUS)) {
            player.feedBack(Code.WUMPUS, "The Wumpus has killed you before you could even make the slightest noise... Nice try.");
            player.setGameOver(true);
            return true;
        }
        if (actions.contains(CaveAction.TREASURE)) {
            player.collectTreasure();
            player.feedBack(Code.TREASURE, "You have collected the treasure!");
            actions.remove(CaveAction.TREASURE);
        }
        if (actions.contains(CaveAction.EXIT)) {
            player.feedBack(Code.EXIT, "You have reached the exit.");
            if (player.hasTreasure()) {
                player.feedBack(Code.IGNORE, "As you have collected the treasure you have exited the cave! Well Done!");
                player.setGameOver(true);
                player.setExited(true);
                return true;
            } else {
                player.feedBack(Code.IGNORE, "Please collect the treasure before exiting...");
            }
        }
        if (actions.contains(CaveAction.SUPERBAT)) {
            player.feedBack(Code.IGNORE, "There is a flap of wings and you are transported to a random cave!");
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
