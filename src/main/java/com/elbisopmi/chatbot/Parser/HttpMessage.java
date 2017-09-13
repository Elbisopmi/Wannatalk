package com.elbisopmi.chatbot.Parser;

import java.io.UnsupportedEncodingException;

public class HttpMessage {
	private int reponseCode = 200;
	private String responseDescription = "OK";
	private String method = "";
	private String host = "";
	private String url = "";
	private int contentLength = 0;
	private String body = "";
	private HttpHeaderList headerList = new HttpHeaderList();
	private HttpMessageType type = HttpMessageType.HTTP_MESSAGE_TYPE_UNKNOWN;
	
	public enum HttpMessageType {
		HTTP_MESSAGE_TYPE_UNKNOWN,
		HTTP_MESSAGE_TYPE_REQUEST,
		HTTP_MESSGAE_TYPE_RESPONSE
	};
	
	HttpMessage() {}		// disable default constructor
	
	public HttpMessage(String method, String url, String body) {
		this.method = method;
		this.url = url;
		this.contentLength = body.length();
		this.body = body;
		
		this.type = HttpMessageType.HTTP_MESSAGE_TYPE_REQUEST;
	}
	
	public HttpMessage(int responseCode, String responseDescription, String body) {
		this.reponseCode = responseCode;
		this.responseDescription = responseDescription;
		try {
			this.contentLength = body.getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.body = body;
		
		this.type = HttpMessageType.HTTP_MESSGAE_TYPE_RESPONSE;
	}
	
	public HttpMessageType Type() {
		return this.type;
	}
	
	public int ResponseCode() {
		return this.reponseCode;
	}
	
	public void ResponseCode(int responseCode) {
		this.reponseCode = responseCode;
	}
	
	public String ResponseDescription() {
		return this.responseDescription;
	}
	
	public void ResponseDescription(String responseDescription) {
		this.responseDescription = responseDescription;
	}
	
	public String Method() {
		return this.method;
	}
	
	public String Host() {
		return this.host;
	}
	
	public void Host(String host) {
		this.host = host;
	}
	
	public String URL() {
		return this.url;
	}
	
	public int ContentLength() {
		return this.contentLength;
	}
	
	public String Body() {
		return this.body;
	}
	
	public void AddHeader(String name, String value) {
		if (name.equals("Host") || name.equals("host")) {
			this.host = value;
			return;
		}
		
		this.headerList.add(name, value);
	}
	
	public void RemoveHeader(String name) {
		this.headerList.remove(name);
	}
	
	public Object[] GetHeaderArray() {
		return this.headerList.getArray();
	}
	
	public HttpHeader GetHeader(String name) {
		return this.headerList.getHeader(name);
	}
	
	public String GetHeaderValue(String name) {
		HttpHeader header = this.GetHeader(name);
		
		if (header != null) return header.Value();
		
		return "";
	}
	
	public String toString() {
		StringBuffer buffer;
		Object[] headerList;
		
		if (this.type == HttpMessageType.HTTP_MESSAGE_TYPE_REQUEST) {
			headerList = this.headerList.getArray();
			buffer = new StringBuffer("");
			
	        for (int i = 0; i < headerList.length; i++) {
	        	buffer.append(((HttpHeader)headerList[i]).Name() + ": " + ((HttpHeader)headerList[i]).Value() + "\r\n");
	        }
	        
	        if (this.host.length() > 0) buffer.append("Host: " + this.host + "\r\n");
			
	        return this.method + " " + this.url + " HTTP/1.1\r\nUser-Agent: Custom-Client Pepsi\r\nContent-Length: " + this.contentLength + "\r\nConnection: close\r\n" + buffer.toString() + "\r\n" + this.body;
		} else if (this.type == HttpMessageType.HTTP_MESSGAE_TYPE_RESPONSE) {
			headerList = this.headerList.getArray();
			buffer = new StringBuffer("");
			
	        for (int i = 0; i < headerList.length; i++) {
	        	buffer.append(((HttpHeader)headerList[i]).Name() + ": " + ((HttpHeader)headerList[i]).Value() + "\r\n");
	        }
	        
	        return "HTTP/1.1 " + this.reponseCode + " " + this.responseDescription + "\r\nServer: Custom-Server Pepsi\r\nContent-Length: " + this.contentLength + "\r\nConnection: close\r\n" + buffer.toString() + "\r\n" + this.body;
		} else {
			return "";
		}
	}
}
