package com.elbisopmi.chatbot.Utilities;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.elbisopmi.chatbot.Parser.HttpMessage;

public class Utils {
	public static HttpMessage SendRequestHttpToServer(String host, int port, HttpMessage request) {
		Socket client = null;
		DataOutputStream output = null;
        DataInputStream input = null;
        StringBuffer buffer = new StringBuffer("");
        byte[] readBuff = new byte[128 * 1024];
        int length, body_index = -1, body_length = -1, start_index, end_index;
        HttpMessage response = null;
        String name, value, method, url, body;
        byte[] requestBuffer;
        
        if (request != null) {
        	// auto correct host
        	request.Host(host + ":" + port);
        	
        	requestBuffer = request.toString().getBytes();
        	
        	try {
    			client = new Socket(host, port);
    			
    			output = new DataOutputStream(client.getOutputStream());
    			input = new DataInputStream(client.getInputStream());
    			
    			output.write(requestBuffer);
    		} catch (UnknownHostException e) {
                System.err.println("Don't know about host: " + host);
                return null;
            } catch (IOException e) {
    			e.printStackTrace();
    			return null;
    		}
    		
    		buffer = new StringBuffer("");
    		while (true) {
    			try {
    				length = input.read(readBuff,  0, 128 * 1024);
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
    							break;
    						}
    					} else {
    						//System.out.println("Content-Length not found.");
    						body_length = 0;
    					}
    				}
    			}
    			
    			if (body_length >=0) {
    				if (buffer.length() >= (body_index + body_length)) {
    					if ((end_index = buffer.indexOf(" ")) > 0) {
    						method = buffer.substring(0, end_index);
    						
    						start_index = end_index + 1;
    						
    						if ((end_index = buffer.indexOf(" ", start_index)) > 0) {
    							url = buffer.substring(start_index, end_index);
    							
    							body = buffer.substring(body_index, body_index + body_length);
    							
    							response = new HttpMessage(method, url, body);
    							
    							if ((start_index = buffer.indexOf("\r\n")) > 0) {
    								start_index += 2;
    								while (buffer.charAt(start_index) == '\r' || buffer.charAt(start_index) == '\n') start_index++;
    								
    								while (start_index < body_index) {
    									if ((end_index = buffer.indexOf(":", start_index)) > 0) {
    										name = buffer.substring(start_index, end_index);
    										
    										start_index = end_index + 1;
    										while (buffer.charAt(start_index) == ' ') start_index++;
    										
    										if ((end_index = buffer.indexOf("\r\n", start_index)) > 0 && (end_index - start_index) > 0) {
    											value = buffer.substring(start_index, end_index);
    											
    											response.AddHeader(name, value);
    											
    											start_index = end_index + 2;
    											while (buffer.charAt(start_index) == '\r' || buffer.charAt(start_index) == '\n') start_index++;
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
    						}
    					}
    					
    					break;	// sucess http receive
    				}
    			}
    		}
    		
    		try {
    			input.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		try {
    			output.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		try {
    			client.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }
		
		return response;
	}
}
