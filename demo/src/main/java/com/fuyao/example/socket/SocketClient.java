package com.fuyao.example.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.springframework.util.StringUtils;

public class SocketClient {
	
	Socket socket = null;
	PrintStream ps = null;
	BufferedReader br = null;
	BufferedReader br2 = null;
	public SocketClient(){
		try {
			socket = new Socket("127.0.0.1", 10086);
			ps = new PrintStream(socket.getOutputStream());// 流与套接字之间建立联系微笑

			br = new BufferedReader(new InputStreamReader(System.in));// 从控制台输入要发送到服务器端的消息吐舌头

			br2 = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
			close();
		}
	}
	
	public void send() throws IOException{
		new Thread(new ClientSendThread(br)).start();
	}
	
	public void revice(){
		new Thread(new ClientReviceThread(br2)).start();
	}
	
	public void close(){
		try {
			socket.close();
			ps.close();
			br.close();
			br2.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException {
		SocketClient cc = new SocketClient();
	    cc.send();
		cc.revice();
	}
	

class ClientReviceThread implements Runnable{
		
		BufferedReader br;
		public ClientReviceThread(BufferedReader br){
			this.br = br;
		}
		@Override
		public void run() {
			// 从服务器端得到消息
		try {
			while(true){
				String serverString = br.readLine();
					System.out.println(serverString);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
			
		}
	}

class ClientSendThread implements Runnable{
	
	BufferedReader br;
	public ClientSendThread(BufferedReader br){
		this.br = br;
	}
	@Override
	public void run() {
		// 从服务器端得到消息
		try {
			String input = "";
			while (!"quit".equals(input)) { // 一直循环，一直到从客户端输入quit为止
				
					input = br.readLine();
					if(StringUtils.isEmpty(input))continue;
					ps.println("s0:"+input); // 向服务器中发送消息
					ps.flush();
					if (input.trim().equals("quit"))// 退出
					{
						break;
					}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
		
	}
}
}
