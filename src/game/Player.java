package game;

/**
 * @author wumpus
 */
public abstract class Player implements IConstants {
    private String name;
    private int location;
    private int arrows;
    private boolean hasTreasure;
    private boolean wumpusSlain;
    private boolean gameOver;
    private boolean exited;
    private int movesMade;

    public Player(String name) {
        this.name = name;
        hasTreasure = false;
        gameOver = false;
        arrows = INITIAL_ARROWS;
        wumpusSlain = false;
        exited = false;
        movesMade = 0;
        Game.dropPlayer(this);
    }

    public abstract void feedBack(Code code, String feedback);

    public abstract String getInput(Code code, String prompt);

    public String getGameStatisics() {
        StringBuilder stats = new StringBuilder();
        stats.append(name + ":\n");
        stats.append("\tExited: " + exited + "\n");
        stats.append("\tTreasure collected: " + hasTreasure + "\n");
        stats.append("\tWumpus slain: " + wumpusSlain + "\n");
        stats.append("\tArrows left: " + arrows + "\n");
        stats.append("\tMoves made: " + movesMade + "\n");
        return stats.toString();
    }

    public void incrementMovesMade() {
        movesMade++;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean hasSlainWumpus() {
        return wumpusSlain;
    }

    public void setWumpusSlain(boolean wumpusSlain) {
        this.wumpusSlain = wumpusSlain;
    }

    public boolean hasTreasure() {
        return hasTreasure;
    }

    public void shootArrow() {
        arrows--;
    }

    public int getArrows() {
        return arrows;
    }

    public void collectTreasure() {
        hasTreasure = true;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setExited(boolean exited) {
        this.exited = exited;
    }
}
