package game;

import java.util.ArrayList;

/**
 * Created by Patrick on 12/04/2015.
 */
public class Cave {
    private ArrayList<CaveAction> actions;

    public Cave() {
        actions = new ArrayList<>();
    }

    public Cave(CaveAction action) {
        actions = new ArrayList<>();
        addAction(action);
    }

    public ArrayList<CaveAction> getActions() {
        return actions;
    }

    public boolean contains(CaveAction action) {
        return actions.contains(action);
    }

    public void addAction(CaveAction action) {
        actions.add(action);
    }

}
