package com.fuyao.example.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.util.StringUtils;

public class SocketServer {

	public static void main(String[] args) throws IOException {

	/** 
	
	 * 基于TCP协议的Socket通信，实现用户登录，服务端 
	
	*/

	ServerSocket serverSocket;
	Socket socket = null;
	InputStream is = null;
	InputStreamReader isr = null;
	BufferedReader br = null;
	//OutputStream os = null;
	PrintStream pw = null;
	try {
		serverSocket = new ServerSocket(10086);
		
		//2、调用accept()方法开始监听，等待客户端的连接 
		socket = serverSocket.accept(); 
		
		//3、获取输入流，并读取客户端信息 
		
		is = socket.getInputStream(); 
		
		isr = new InputStreamReader(is); 
		
		br = new BufferedReader(isr); 
		
		String info =null; 
		
		//os = socket.getOutputStream(); 
		pw = new PrintStream(socket.getOutputStream());
		//PrintWriter pw = new PrintWriter(os); 
		while(!StringUtils.isEmpty(info=br.readLine())){ 
		
			System.out.println("Hello,我是服务器，客户端说："+info);
			pw.println(info); 
			pw.flush(); 
			//info=br.readLine();
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally{
		socket.close();
		pw.close();

		try {
			is.close();
			isr.close();
			br.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	//socket.shutdownInput();//关闭输入流 
	
	//4、获取输出流，响应客户端的请求 
	

	}

}
