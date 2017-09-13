package com.elbisopmi.chatbot.Parser;
import java.util.LinkedList;

public class HttpHeaderList {
	private LinkedList<HttpHeader> headerList = new LinkedList<HttpHeader>();
	
	public void add(String name, String value) {
		if (name.equals("Content-Length") || name.equals("content-length") || name.equals("Connection") || name.equals("connection")) {
			return;
		}
		
		Object[] header = this.headerList.toArray();
		for (int i = 0; i < header.length; i++) {
			if (((HttpHeader)header[i]).Name().equals(name)) {
				((HttpHeader)header[i]).Value(value);
				return;
			}
		}
		this.headerList.add(new HttpHeader(name, value));
	}
	
	public void remove(String name) {
		Object[] header = this.headerList.toArray();
		for (int i = 0; i < header.length; i++) {
			if (((HttpHeader)header[i]).Name().equals(name)) {
				this.headerList.remove(i);
				return;
			}
		}
	}
	
	public Object[] getArray() {
		return this.headerList.toArray();
	}
	
	public HttpHeader getHeader(String name) {
		Object[] header = this.headerList.toArray();
		for (int i = 0; i < header.length; i++) {
			if (((HttpHeader)header[i]).Name().equals(name)) {
				return ((HttpHeader)header[i]);
			}
		}
		
		return null;
	}
}
