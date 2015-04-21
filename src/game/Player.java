package game;

import java.io.IOException;

/**
 * Superclass of all different players used by the game.
 */
public abstract class Player implements IConstants {
	protected String name;
	private int location;
	protected int arrows;
	protected boolean hasTreasure;
	protected boolean wumpusSlain;
	private boolean gameOver;
	protected boolean exited;
	protected int movesMade;

	public Player(String name) {
		this.name = name;
		hasTreasure = false;
		gameOver = false;
		arrows = INITIAL_ARROWS;
		wumpusSlain = false;
		exited = false;
		movesMade = 0;
	}

	public abstract void feedBack(String feedback);

	public abstract String getInput(String prompt) throws IOException;

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

	public int getMovesMade() {
		return movesMade;
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

	public String getExited() {
		return exited ? "yes" : "no";
	}
}
