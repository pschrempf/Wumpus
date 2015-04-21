package game;

/**
 * Interface containing the crucial constants for the game.
 */
public interface IConstants {
    int NUMBER_OF_CAVES = 20;
    int NUMBER_OF_BATS = 1;
    int NUMBER_OF_PITS = 5;
    int INITIAL_ARROWS = 3;
    
	// network constants
    final static int chatPort = 48320; // the port number to be used
    final static int soTimeout = 1000; // ms to wait for socket read
    final static int readRetry = 10; // # re-try of handshake
    final static int sleepTime = 200; // ms to sleep - 200 is fine
    final static int bufferSize = 8192; // # chars in line
    
	// network messages
	String COMMAND_FORFEIT = "you-win";
	String ERROR_FEEDBACK = "error";
	String BYE_MESSAGE = "bye";
	String NEW_GAME_MESSAGE = "new-game";
	String HANDSHAKE_MESSAGE = "hello";
	String READY_MESSAGE = "ready";
	String REJECT_MESSAGE = "reject";

    // player messages
    String GLISTEN_CODE = "GLISTEN";
    String BREEZE_CODE = "BREEZE";
    String STENCH_CODE = "STENCH";
    String EXIT_CODE = "EXIT";
    String WUMPUS_CODE = "WUMPUS";
    String PIT_CODE = "PIT";
    String TREASURE_CODE = "TREASURE";
    String CONNECTIONS_CODE = "CONNECTIONS";
    String HITWUMPUS_CODE = "HITWUMPUS";
    String MISSEDWUMPUS_CODE = "MISSEDWUMPUS";

    String PRINT_CODE = "PRINT";

    String SHOOT_CODE = "SHOOT";
    String MOVE_CODE = "MOVE";
    String SHOOTSELECT_CODE = "SHOOTSELECT";

    String PARAMETER_SEPARATOR = ":";
}
