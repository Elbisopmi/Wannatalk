package com.elbsopmi.chatbot.Application;
import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicNumbers;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Verbs;


import  com.elbsopmi.chatbot.Parser.HttpMessage;

public class ApplicationProcess {
	
	public static Bot masterBot = null;
	
	public static void SetupBot() {
		MagicStrings.setRootPath();
		AIMLProcessor.extension = new PCAIMLProcessorExtension();
		
		String botName = "gbot";
		MagicBooleans.jp_tokenize = false;
		MagicBooleans.trace_mode = true;
		String action = "chat";
		Graphmaster.enableShortCuts = true;
		
		System.out.println("Working Directory = " + MagicStrings.root_path);
		System.out.println(MagicStrings.program_name_version);
		masterBot = new Bot(botName, MagicStrings.root_path, action);

		if (masterBot.brain.getCategories().size() < MagicNumbers.brain_print_size)
			masterBot.brain.printgraph();
		if (MagicBooleans.make_verbs_sets_maps)
			Verbs.makeVerbSetsMaps(masterBot);
	}
	
	public static String ask(Bot bot, String question, boolean doWrites, boolean traceMode) {
		
        Chat chatSession = new Chat(bot, doWrites);
        bot.brain.nodeStats();
        MagicBooleans.trace_mode = traceMode;
        
        String request = "";
        bot.tokenizer.wordInstance(question);
    	int beginIndex = bot.tokenizer.first();
        while(bot.tokenizer.hasNext()) {
        	int endIndex = bot.tokenizer.next();
        	request += 	question.substring(beginIndex, endIndex)+" ";
        	beginIndex = endIndex;
        }
        
        if (MagicBooleans.trace_mode) System.out.println("STATE="+request+":THAT="+chatSession.thatHistory.get(0).get(0)+":TOPIC="+chatSession.predicates.get("topic"));
        String response = chatSession.multisentenceRespond(request);
        while (response.contains("&lt;")) response = response.replace("&lt;","<");
        while (response.contains("&gt;")) response = response.replace("&gt;",">");
        
        return response;
	}
	

	public static void actionProcess(Socket clientSocket, String rawData, HttpMessage request) {
		
		HttpMessage response;
		
		if( masterBot == null) {
			SetupBot();
		}
		
		try {
			String question[] = request.URL().split("\\?");
			System.out.println("Question: " + question[1].split("=")[1].trim());
			String q = question[1].split("=")[1];
			q = URLDecoder.decode(q, "UTF-8");
			String ans = ask(masterBot, q, false, false);
			System.out.println("Answer  : " + ans);
			response = new HttpMessage(200, "OK", ans);
			response.AddHeader("Content-Type", "text/html; charset=UTF-8");
			
			
		}catch(Exception ex) {
			System.out.println("Invalid parameter");
			ex.printStackTrace();
			response = new HttpMessage(400, "Bad Request", "Invalid parameter");
			response.AddHeader("Content-Type", "text/plain");
		}

		try {
			clientSocket.getOutputStream().write(response.toString().getBytes());
		} catch (IOException e) {
			System.out.println("Write client error.");
			e.printStackTrace();
		}
	}
}
