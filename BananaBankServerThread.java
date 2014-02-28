package bananabank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class BananaBankServerThread extends Thread {

	private Socket cs;
	private BananaBank bank;

	public BananaBankServerThread(Socket cs, BananaBank bank) {
		this.cs = cs;
		this.bank = bank;
	}

	@Override
	public void run() {
		BufferedReader in;
		PrintStream out = null;
		try {
			in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			out = new PrintStream(cs.getOutputStream());
			String line;
			while ((line = in.readLine()) != null) {
				// separate the request into amount, src, and dest
				String[] request = line.split(" ");

				if (request.length == 1 && request[0].equals("SHUTDOWN")) {
					// only allow SHUTDOWN from local client
					if (cs.getInetAddress().toString().equals("/127.0.0.1")) {

						BananaBankServer.shutDown(cs);
						return;

					} else {
						out.println("SHUTDOWN command is only available to local clients!");
					}
				} else if (request.length == 3) {
					int amount = Integer.parseInt(request[0]);
					int src = Integer.parseInt(request[1]);
					int dest = Integer.parseInt(request[2]);

					Account srcAccount, destAccount;
					if ((srcAccount = bank.getAccount(src)) != null) {
						if ((destAccount = bank.getAccount(dest)) != null) {
							// acquire locks in increasing account number
							// order
							Account lesserID;
							Account greaterID;
							if (srcAccount.getAccountNumber() < destAccount
									.getAccountNumber()) {
								lesserID = srcAccount;
								greaterID = destAccount;
							} else {
								lesserID = destAccount;
								greaterID = srcAccount;
							}
							synchronized (lesserID) {
								synchronized (greaterID) {
									srcAccount.transferTo(amount,
											destAccount);
									out.println(amount
											+ " transferred from account "
											+ src + " to account "
											+ dest);
								}
							}
						} else {
							out.println("Invalid destination account!");
						}
					} else {
						out.println("Invalid source account!");
					}
				} else {
					out.println("Invalid input!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.close();
	}
}