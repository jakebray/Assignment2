package bananabank.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BananaBankServer {
	// constants
	private static final int PORT = 2000;
	private static final String ACCOUNT_FILE = "accounts.txt";

	// private members
	private static BananaBank bank;
	private static Socket shutDownSocket;
	private static ServerSocket ss;
	private static ArrayList<BananaBankServerThread> threads;

	public static void main(String[] args) throws IOException {
		// keep track of all created threads
		threads = new ArrayList<BananaBankServerThread>();

		// open the server socket
		ss = new ServerSocket(PORT);
		System.out.println("BananaBankServer Started");

		System.out.println("Loading accounts");
		// load accounts
		bank = new BananaBank(ACCOUNT_FILE);

		System.out.println("Serving clients");
		try {
			while (true) {
				Socket cs = ss.accept();
				BananaBankServerThread t = new BananaBankServerThread(cs, bank);
				threads.add(t);
				t.start();
			}
		} catch (IOException ie) {
			// ss has been closed (Shutdown was called)
			System.out.println("shutting down");
		}
		
		// wait for threads to finish;
		for (BananaBankServerThread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		System.out.println("Saving accounts");
		// store account info back to file
		bank.save(ACCOUNT_FILE);

		// TODO send total money amount back to shutdown client
		PrintStream out = new PrintStream(shutDownSocket.getOutputStream());
		int total = 0;
		for (Account a : bank.getAllAccounts()) {
			total += a.getBalance();
		}
		out.println(total);
		out.close();
		System.out.println("Finished");
	}

	public static void shutDown(Socket S) throws IOException {
		shutDownSocket = S;
		ss.close();
	}
}
