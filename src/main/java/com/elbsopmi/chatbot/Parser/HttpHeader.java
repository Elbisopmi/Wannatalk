package com.elbsopmi.chatbot.Parser;

public class HttpHeader {
	private String name = "";
	private String value = "";
	
	HttpHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String Name() {
		return this.name;
	}
	
	public void Name(String name) {
		this.name = name;
	}
	
	public String Value() {
		return this.value;
	}
	
	public void Value(String value) {
		this.value = value;
	}
}
