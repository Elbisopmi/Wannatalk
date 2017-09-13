package com.elbisopmi.chatbot.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.elbisopmi.chatbot.Application.ApplicationProcess;
import com.elbisopmi.chatbot.Parser.HttpMessage;
import org.alicebot.ab.*;

public final class HttpSocketServer extends SocketServer {

	public static Bot masterBot;
	
	public HttpSocketServer() {
		MagicStrings.setRootPath();
		AIMLProcessor.extension = new PCAIMLProcessorExtension();
		SetupBot();
	}

	public boolean SetupBot() {
		String botName = "gbot";
		MagicBooleans.jp_tokenize = false;
		MagicBooleans.trace_mode = true;
		String action = "chat";

		System.out.println(MagicStrings.program_name_version);
		Bot masterBot = new Bot(botName, MagicStrings.root_path, action);

		if (masterBot.brain.getCategories().size() < MagicNumbers.brain_print_size)
			masterBot.brain.printgraph();
		return true;
	}
	
	public String ask(Bot bot, String question, boolean doWrites, boolean traceMode) {
		
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

	@Override
	public void ProtocolProcess(Socket clientSocket) {
		StringBuffer buffer = new StringBuffer("");
		char[] readBuff = new char[1024 * 128];
		int length, body_index = -1, body_length = -1, start_index, end_index;
		HttpMessage request = null;
		String method, url, body, name, value;

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e1) {
			System.out.println("Get input reader failed.");
			return;
		}

		while (true) {
			try {
				length = reader.read(readBuff, 0, 1024 * 128);
				if (length <= 0)
					break;
				readBuff[length] = 0;
			} catch (IOException e) {
				break;
			}
			buffer.append(new String(readBuff));

			if (body_length < 0) {
				if ((body_index = buffer.indexOf("\r\n\r\n")) > 0) {
					body_index += 4;

					if ((start_index = buffer.indexOf("Content-Length: ")) > 0) {
						start_index += 16;

						if ((end_index = buffer.indexOf("\r\n", start_index)) > 0) {
							body_length = Integer.parseInt(new String(buffer.substring(start_index, end_index)));
						} else {
							System.out.println("Content-Length is invalid format.");
							try {
								reader.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							return;
						}
					} else {
						// System.out.println("Content-Length not found.");
						body_length = 0;
					}
				}
			}

			if (body_length >= 0) {
				if (buffer.length() >= (body_index + body_length)) {
					if ((end_index = buffer.indexOf(" ")) > 0) {
						method = buffer.substring(0, end_index);

						start_index = end_index + 1;

						if ((end_index = buffer.indexOf(" ", start_index)) > 0) {
							url = buffer.substring(start_index, end_index);

							body = buffer.substring(body_index, body_index + body_length);

							request = new HttpMessage(method, url, body);

							if ((start_index = buffer.indexOf("\r\n")) > 0) {
								start_index += 2;
								while (buffer.charAt(start_index) == '\r' || buffer.charAt(start_index) == '\n')
									start_index++;

								while (start_index < body_index) {
									if ((end_index = buffer.indexOf(":", start_index)) > 0) {
										name = buffer.substring(start_index, end_index);

										start_index = end_index + 1;
										while (buffer.charAt(start_index) == ' ')
											start_index++;

										if ((end_index = buffer.indexOf("\r\n", start_index)) > 0
												&& (end_index - start_index) > 0) {
											value = buffer.substring(start_index, end_index);

											request.AddHeader(name, value);

											start_index = end_index + 2;
											while (buffer.charAt(start_index) == '\r'
													|| buffer.charAt(start_index) == '\n')
												start_index++;
											// next header
										} else {
											System.out.println("Header value not found.");
											break;
										}
									} else {
										break;
									}
								}
							}

							ApplicationProcess.actionProcess(clientSocket, buffer.toString(), request);
						}
					}

					break;
				}
			}
		}

		try {
			reader.close();
		} catch (IOException e) {
			// e.printStackTrace();
		}

	}

}
