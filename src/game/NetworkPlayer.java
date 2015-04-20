package game;

// saleem, Nov1998

// saleem, Jan 2015, update and compile check for CS1006 with javac 1.8.0_31

// A simple TCP text chat program.

// Note that this program is written to demonstrate the use of the
// various i/o calls and does not necessarily have the best structure
// for extending to a full application!

import java.io.*;
import java.net.*;

enum ChatState {
	idle, // wait for session to be established
	chatting // chat session in progress
}

/**
 * Class representing a player playing on the network.
 * Slightly modified code of Prof. Saleem Bhatti to fit the needs of the current program.
 * @author Prof. Saleem Bhatti
 */
public class NetworkPlayer extends Player implements IConstants {

	static String _quit = "quit"; // user command
	static String _prompt = ">";
	static String _busy = "::busy::";

	static String _me; // identity to be used for chat
	static String _otherPerson = "Opponent"; // Opponent

	static ServerSocket _listen; // to receive from network
	static BufferedReader _keyboard; // to receive from keyboard

	static ChatState _state;
	static Socket _call; // for chat

	InputStream _in; // network input stream
	OutputStream _out; // network output stream

	NetworkPlayer(boolean isWhite, String name, Socket call, String otherPerson)
			throws SocketException {
		super(name);
		_call = call;
		_otherPerson = otherPerson;

		try {
			_in = call.getInputStream();
			_out = call.getOutputStream();
		} catch (java.io.IOException e) {
			error("TcpTextChat(): io problem " + e.getClass().getName());
			System.exit(-1);
		}

		report("new call");
		report("local: " + _call.getLocalAddress().getHostName() + " "
				+ _call.getLocalAddress().getHostAddress() + " port: "
				+ _call.getLocalPort());
		report("remote: " + _call.getInetAddress().getHostName() + " "
				+ _call.getInetAddress().getHostAddress() + " port: "
				+ _call.getPort());
		report("playing with: " + _otherPerson);
	}

	static boolean checkQuit(String s) {
		return s != null ? s.compareTo(_quit) == 0 : false;
	}

	static boolean checkBusy(String s) {
		return s != null ? s.compareTo(_busy) == 0 : false;
	}

	static void error(String s) {
		System.err.println("-!- " + s);
	}

	static void report(String s) {
		System.out.println("-*- " + s);
	}

	static void chatting(String s) {
		System.out.println("--> " + s);
	}

	static void startup() {
		// create listening socket
		try {
			_listen = new ServerSocket(chatPort);
			_listen.setSoTimeout(soTimeout);
		} catch (java.io.IOException e) {
			error("server failed! " + e.getClass().getName());
			System.exit(-1);
		}
		// local input
		_keyboard = new BufferedReader(new InputStreamReader(System.in),
				bufferSize); // restrict line length

		_me = System.getProperty("user.name");
		report("greetings " + _me);
		report("started server");
		InetAddress h;
		String s = "(unknown)";
		try {
			h = InetAddress.getLocalHost();
			s = h.getCanonicalHostName();
			report("host: " + h.getByName(s));
		} catch (java.net.UnknownHostException e) {
			s = "(unknown)";
			error("startup(): cannot get hostname!");
		}
		report("port: " + _listen.getLocalPort());
		report("ready ...");

		_state = ChatState.idle;
	}

	static void shutdown() {
		try {
			if (_call != null) {
				_call.close();
			}
			_listen.close();
			report("shutdown ... bye ...");
		} catch (IOException e) {
			error("io problem() " + e.getClass().getName());
			System.exit(-1);
		}
	}

	public static NetworkPlayer checkIncomingCall(boolean isWhite)
			throws IOException {
		NetworkPlayer chat = null;
		Socket connection = null;

		try {
			// wait for a connection request
			connection = _listen.accept();
			connection.setSoTimeout(soTimeout);
			connection.setTcpNoDelay(true);
			if (!handshake(connection, false))
				return null;

			switch (_state) {

			case idle:
				chat = new NetworkPlayer(isWhite, _otherPerson, connection,
						_otherPerson);
				_state = ChatState.chatting;
				break; // idle

			// already chatting so send "busy" signal
			case chatting:
				// handshake() does the following:
				// - sends a "busy" signal
				// closes the connection
				break;

			} // switch
		} // try

		catch (java.net.SocketTimeoutException e) {
			// ignore
		} catch (java.net.UnknownHostException e) {
			report("checkIncomingCall(): cannot get hostname of remote host");
		} catch (java.io.IOException e) {
			error("checkIncomingCall() problem: " + e.getClass().getName());
			throw e;
		}

		return chat;
	}

	static NetworkPlayer makeCall(boolean isWhite, String host) {
		NetworkPlayer chat = null;
		Socket call = null;

		if (host != null) {
			
			try {
				call = new Socket(host, chatPort);
				call.setSoTimeout(soTimeout);
				call.setTcpNoDelay(true);
				report("Game request sent to: " + host + ". Waiting for response...");
				if (!handshake(call, true))
					return null;
				chat = new NetworkPlayer(isWhite, _otherPerson, call,
						_otherPerson);
			} // try

			catch (java.net.UnknownHostException e) {
				error("makeCall(): unknown host " + host);
				return null;
			} catch (java.io.IOException e) {
				error("makeCall(): i/o problem " + e.getClass().getName());
				return null;
			}

		} // if

		if (chat != null) {
			report("New game!");
		}

		return chat;
	}

	static String readNetwork(Socket connection) throws IOException {
		return connection == null ? null
				: recvLine(connection.getInputStream());
	}

	static void writeNetwork(Socket connection, String line) throws IOException {
		sendLine(connection.getOutputStream(), line);
	}

	static String readKeyboard() {
		String line = null;

		try {
			if (_keyboard.ready())
				line = _keyboard.readLine();
		} catch (java.io.IOException e) {
			error("readKeyboard(): problem reading! " + e.getClass().getName());
			System.exit(-1);
		}

		return line;
	}

	static void sendLine(OutputStream out, String line) {
		if (out == null || line == null || line.length() < 0)
			return;

		try {
			int l = line.length();
			if (l > bufferSize) {
				report("line too long (" + l + ") truncated to "
						+ bufferSize);
				l = bufferSize;
			}
			out.write(line.getBytes(), 0, l);
		} catch (java.io.IOException e) {
			error("sendLine() problem " + e.getClass().getName());
		}
	}

	static String recvLine(InputStream in) throws IOException {
		if (in == null) {
			return null;
		}

		String line = null;

		do {
			try {
				byte buffer[] = new byte[bufferSize];
				int l = in.read(buffer);
				if (l > 0)
					line = new String(buffer, 0, l);
			} catch (java.net.SocketTimeoutException e) {
				// ignore
			} catch (java.io.IOException e) {
				String eName = e.getClass().getName();
				if (eName != "java.io.InterruptedIOException") {
					error("recvLine() problem " + eName);
					throw e;
				}
			}
		} while (line == null);

		return line;
	}

	static boolean handshake(Socket connection, boolean isMakingCall) {
		boolean receivedResponse = false;

		try {
			// already chatting ...
			if (_state == ChatState.chatting) {
				writeNetwork(connection, _busy);
				connection.close();
				return false;
			}
			if (!isMakingCall) {
				readNetwork(connection);
			}
			writeNetwork(connection, HANDSHAKE_MESSAGE);

			// For loop checking for a handshake, and any additional messages
			// sent by the server, and handling it.
			for (int i = 0; (i < readRetry) && !receivedResponse; ++i) {
				String line = readNetwork(connection);
				if (line != null) {
					if (checkBusy(line)) {
						report("other end is busy - try again later.");
						break;
					} else {
						if (line.equals(HANDSHAKE_MESSAGE)) {

							// If we receive an adequate handshake, we ask for a
							// new game.
							writeNetwork(connection, NEW_GAME_MESSAGE);
							receivedResponse = true;
						} else if (line.equals(NEW_GAME_MESSAGE)) {
							report("A new game has been requested from "
									+ connection.getInetAddress().getCanonicalHostName() + "!");
							report("Would you like to accept this request? (y/n)");

							boolean validResponse = false;
							do {
								String answer = Game.consoleReader
										.nextLine();
								if (answer.equals("y")) {
									writeNetwork(connection, READY_MESSAGE);
									return true;
								} else if (answer.equals("n")) {
									writeNetwork(connection, REJECT_MESSAGE);
									report("Game rejected.");
									return false;
								} else {
									error("Invalid input, please provide a valid input:");
								}
							} while (!validResponse);

						} else {
							error("There was an error with the handshake.");
							writeNetwork(connection, ERROR_FEEDBACK);
						}

					}
				} else
					Thread.sleep(sleepTime);
			}

			// Checking if we received an adequate response, and if yes, listen
			// for any additional messages.
			if (receivedResponse) {
				receivedResponse = false;
				for (int i = 0; (i < readRetry) && !receivedResponse; ++i) {
					String line = readNetwork(connection);
					if (line != null) {
						if (line.equals(READY_MESSAGE)) {
							receivedResponse = true;
						} else if (line.equals(REJECT_MESSAGE)) {
							report("Your request of a new game has been rejected!");
							return false;
						} else {
							error("There was an error whilst communicating with your partner.");
							writeNetwork(connection, ERROR_FEEDBACK);
							return false;
						}
					} else
						Thread.sleep(sleepTime);
				}
			}
		} // try

		catch (java.lang.InterruptedException e) {
			// ignore - just means that we are still waiting
		} catch (java.io.IOException e) {
			error("handshake(): io exception '" + e.getClass().getName());
		}

		return receivedResponse;
	}

	void print(String line) {
		if (line != null)
			chatting(_otherPerson + " " + _prompt + " " + line);
	}

	public void sendFeedback(String line) {
		sendLine(_out, line);
	}

	public String getInput() throws IOException {
		return recvLine(_in);
	}

	public void endCall() {
		try {
			if (_state == ChatState.chatting) {
				report("closing call with " + _otherPerson);
				_call.close();
				_call = null;
				_state = ChatState.idle;
			}
		} catch (java.io.IOException e) {
			error("endCall(): io problem " + e.getClass().getName());
			System.exit(-1);
		}
	}

	public static Player createNetworkPlayer(boolean firstPlayer)
			throws IOException, InterruptedException {
		startup();

		NetworkPlayer chat = null;

		boolean quit = false;

		while (!quit) {

			String lineKeyboard = null;

			// input from user?
			lineKeyboard = readKeyboard();

			try {
				switch (_state) {

				case idle:
					// wait for either:
					// - user typing the name for a remote host
					// - an incoming connection

					// assume user has given the name of a remote host
					if (NetworkPlayer.checkQuit(lineKeyboard)) {
						quit = true;
						NetworkPlayer.shutdown();
						System.exit(0);
					} else {
						chat = makeCall(firstPlayer, lineKeyboard);
						Game.isFirstPlayer = true;
					}

					if (chat == null) {
						chat = checkIncomingCall(firstPlayer);
						Game.isFirstPlayer = false;
					}
					if (chat != null)
						_state = ChatState.chatting;
					break;

				case chatting:
					quit = true;
					break;
				} // switch

			} // try

			catch (java.io.IOException e) {
				String eName = e.getClass().getName();
				if (eName != "java.net.SocketTimeoutException") {
					error("main() problem: " + eName);
					throw e;
				}
			}

			// avoid CPU overhead of continuous looping here
			Thread.sleep(sleepTime);

		} // while

		return chat;

	}

	@Override
	public void feedBack(String feedback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getInput(String prompt) {
		// TODO Auto-generated method stub
		return null;
	}

} // NetworkPlayer
