package com.elbisopmi.chatbot.Launcher;
import java.util.Scanner;

import com.elbisopmi.chatbot.Server.HttpSocketServer;
import com.elbisopmi.chatbot.Server.ITCPServer;

public class Server {
	
	public static void main(String[] args) {
		ITCPServer server = new HttpSocketServer();
		Scanner input = new Scanner(System.in);
		int port = 80;
		if( args.length == 1) {
			port = Integer.parseInt(args[0]);
		}
		System.out.println("Listen on " + port);
		if (!server.Start(port)) {
			System.out.println("Server start failed.");
			input.close();
			return;
		}
		
		while (true) {
			if (input.nextLine().equals("quit")) {
				break;
			}
		}
		
		server.Stop();
		input.close();
	}
	

}
