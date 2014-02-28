package bananabank.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class BananaBankClient {

	// constants
	private static final String HOST = "localhost";
	private static final int PORT = 2000;

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		Socket s = new Socket(HOST, PORT);
		System.out.println("Client is connected to the server");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		PrintStream out = new PrintStream(s.getOutputStream());

		out.println("10 33333 44444");
		System.out.println(in.readLine());
		// invalid src
		out.println("1 99999 44444");
		System.out.println(in.readLine());
		out.println("1 55555 44444");
		System.out.println(in.readLine());
		// invalid dest
		out.println("1 55555 99999");
		System.out.println(in.readLine());
		// invalid input
		out.println("1 55555 44444 33333");
		System.out.println(in.readLine());
		out.println("SHUTDOWN");
		System.out.print("Total in bank: ");
		System.out.println(in.readLine());
		out.close();

		System.out.println("Client disconnected");
	}
}