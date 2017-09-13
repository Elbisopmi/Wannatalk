package com.elbsopmi.chatbot.Launcher;
import java.util.Scanner;

import com.elbsopmi.chatbot.Server.HttpSocketServer;
import com.elbsopmi.chatbot.Server.ITCPServer;

public class Server {
	
	public static void main(String[] args) {
		ITCPServer server = new HttpSocketServer();
		Scanner input = new Scanner(System.in);
		
		if (!server.Start(8080)) {
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
