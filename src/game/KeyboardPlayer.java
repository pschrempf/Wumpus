package game;

import java.util.Scanner;

/**
 * @author wumpus
 */
public class KeyboardPlayer extends Player {

    public KeyboardPlayer(String name) {
        super(name);
    }

    @Override
    public void feedBack(String feedback) {
        System.out.println(feedback);
    }

    @Override
    public String getInput(String prompt) {
        //System.out.println(prompt);
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }
}
