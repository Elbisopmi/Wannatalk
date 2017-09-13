package com.elbsopmi.chatbot.Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

abstract class SocketServer extends ITCPServer {
	private MultithreadServerSocket server = null;
	
	@Override
	public boolean Start(int bind_port) {
		if (this.started) {
			System.out.println("Server already started port [" + this.bind_port + "]");
			return false;
		}
		
		this.bind_port = bind_port;
		
		this.server = new MultithreadServerSocket(bind_port);
		new Thread(this.server).start();
		
		this.started = true;
		
		System.out.println("Server Started.");
		
		return true;
	}

	@Override
	public boolean Stop() {
		if (!this.started) {
			System.out.println("Server socket not start yet.");
			return false;
		}
		
		this.server.stop();
		
		this.started = false;
		
		return true;
	}
	
	abstract void ProtocolProcess(Socket clientSocket);

	public class WorkerThread implements Runnable {
		private Socket clientSocket = null;
		
		WorkerThread(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		
		public void run() {
			System.out.println("Client ip " + this.clientSocket.getInetAddress().getHostAddress() + " port " + this.clientSocket.getPort() + " connected.");
			ProtocolProcess(this.clientSocket);
			System.out.println("Client ip " + this.clientSocket.getInetAddress().getHostAddress() + " port " + this.clientSocket.getPort() + " process done.");
			try {
				this.clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class MultithreadServerSocket implements Runnable
	{
		private int bind_port = 80;
		ServerSocket serverSocket = null;
		private boolean stopped = false;
		
		MultithreadServerSocket(int bind_port) {
			this.bind_port = bind_port;
		}
		
		public void run() {
	        openServerSocket();
	        
	        while (!isStopped())  {
	            Socket clientSocket = null;
	            try {
	                clientSocket = this.serverSocket.accept();
	            } catch (IOException e) {
	                if(isStopped()) {
	                    System.out.println("Server Stopped.") ;
	                    return;
	                }
	                throw new RuntimeException("Error accepting client connection", e);
	            }
	            
	            new Thread(new WorkerThread(clientSocket)).start();
	        }
	        
	        System.out.println("Server Stopped.");
		}
		
		private synchronized boolean isStopped() {
	        return this.stopped;
	    }
		
		public synchronized void stop() {
			this.stopped = true;
			
	        try {
	            this.serverSocket.close();
	        } catch (IOException e) {
	            throw new RuntimeException("Error closing server", e);
	        }
	    }

	    private void openServerSocket() {
	        try {
	            this.serverSocket = new ServerSocket(this.bind_port, 256);
	        } catch (IOException e) {
	            throw new RuntimeException("Cannot open port " + this.bind_port, e);
	        }
	    }
	}

}
