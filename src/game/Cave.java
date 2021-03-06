package game;

import java.util.ArrayList;

/**
 * Class representing a cave, containing the specific actions that occur in it.
 */
public class Cave implements IConstants {
    private ArrayList<CaveAction> actions;

    public Cave() {
        actions = new ArrayList<>();
    }

    public boolean executeEvents(Player player) {
        if (actions.contains(CaveAction.PIT)) {
            player.feedBack(PIT_CODE);
        }
        if (actions.contains(CaveAction.WUMPUS)) {
            player.feedBack(WUMPUS_CODE);
        }
        if (actions.contains(CaveAction.TREASURE)) {
            player.feedBack(TREASURE_CODE);
            actions.remove(CaveAction.TREASURE);
        }
        if (actions.contains(CaveAction.EXIT)) {
            player.feedBack(EXIT_CODE);
        }
        if (actions.contains(CaveAction.SUPERBAT)) {
            player.feedBack(PRINT_CODE + PARAMETER_SEPARATOR + "There is a flap of wings and you are transported to a random cave!");
            Game.replaceSuperBats(player.getLocation());
            Game.dropPlayer(player);
        }
        if (player.isGameOver()) return true;
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
