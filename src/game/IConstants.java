package game;

/**
 * @author wumpus
 */
public interface IConstants {
    int MAX_ADJACENT_CAVES = 5;
    int NUMBER_OF_CAVES = 10;
    int NUMBER_OF_BATS = 1;
    int NUMBER_OF_PITS = 1;
    int INITIAL_ARROWS = 5;
    
	// network constants
    final static int chatPort = 48320; // the port number to be used
    final static int soTimeout = 10; // ms to wait for socket read
    final static int readRetry = 10; // # re-try of handshake
    final static int sleepTime = 200; // ms to sleep - 200 is fine
    final static int bufferSize = 128; // # chars in line
    
	// network messages
	String COMMAND_FORFEIT = "you-win";
	String ERROR_FEEDBACK = "error";
	String BYE_MESSAGE = "bye";
	String NEW_GAME_MESSAGE = "new-game";
	String HANDSHAKE_MESSAGE = "hello";
	String READY_MESSAGE = "ready";
	String REJECT_MESSAGE = "reject";
}
