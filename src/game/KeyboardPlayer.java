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
    public void feedBack(Code code, String feedback) {
        System.out.println(feedback);
    }

    @Override
    public String getInput(Code code, String prompt) {
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }
}
