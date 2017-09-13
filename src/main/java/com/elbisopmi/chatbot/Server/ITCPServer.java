package com.elbisopmi.chatbot.Server;

public abstract class ITCPServer {
	
	protected boolean started = false;
	protected int bind_port;
	
	public boolean Started()
	{
		return this.started;
	}
	
	public abstract boolean Start(int bind_port);
	public abstract boolean Stop();
}
