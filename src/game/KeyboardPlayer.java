package game;

/**
 * @author wumpus
 */
public class KeyboardPlayer extends Player {

    public KeyboardPlayer(String name) {
        super(name);
    }

    @Override
    public void feedBack(String feedback) {
        String identifier = feedback.split(PARAMETER_SEPARATOR)[0];
        String msg = "";
        switch (identifier) {
            case GLISTEN_CODE:
                msg = "There is a sense of glittering...";
                break;
            case BREEZE_CODE:
                msg = "You can feel a light breeze...";
                break;
            case STENCH_CODE:
                msg = "There is a strong stench...";
                break;
            case EXIT_CODE:
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
                msg += "You can move to the caves: " + feedback.split(PARAMETER_SEPARATOR)[1].replaceAll(",", ", ");
                break;
            case WUMPUS_CODE:
                msg = "The Wumpus has killed you before you could even make the slightest noise... Nice try.";
                setGameOver(true);
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
                msg = "You missed the Wumpus! It has moved location...";
                break;
            case HITWUMPUS_CODE:
                msg = "Congratulations! You hit the Wumpus and it died a horrible death, screaming and rolling on the floor for hours and hours on end! I hope you enjoyed it...";
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
        String msg;
        switch (identifier) {
            case SHOOTSELECT_CODE:
                msg = "Would you like to shoot an arrow? (y/n)";
                break;
            case SHOOT_CODE:
                msg = "Which cave would you like to shoot into?";
                break;
            case MOVE_CODE:
                msg = "Please enter the number of the cave you would like to move to: ";
                break;
            default:
                msg = "An error has occurred, please press enter:";
        }

        System.out.println(msg);
        return Game.consoleReader.nextLine();
    }
}
