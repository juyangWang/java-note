package com.fuyao.example.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import org.springframework.util.StringUtils;

public class SocketClient1 {

	public static void main(String[] args) throws IOException {
		// 发送消息
				Socket socket = null;
				PrintStream ps = null;
				BufferedReader br = null;
				BufferedReader br2 = null;
				try {
					socket = new Socket("127.0.0.1", 10086);
					ps = new PrintStream(socket.getOutputStream());// 流与套接字之间建立联系微笑

					br = new BufferedReader(new InputStreamReader(System.in));// 从控制台输入要发送到服务器端的消息吐舌头

					br2 = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));// 从服务器端接收到的消息大笑
					String input = "";
					while (!"quit".equals(input)) { // 一直循环，一直到从客户端输入quit为止
						input = br.readLine();
						if(StringUtils.isEmpty(input))continue;
						ps.println("s1:"+input); // 向服务器中发送消息
						ps.flush();
						if (input.trim().equals("quit"))// 退出
						{
							break;
						}

						// 从服务器端得到消息
						String serverString = br2.readLine();
						System.out.println(serverString);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					socket.close();
					ps.close();

					try {
						br.close();
						br2.close();

					} catch (IOException e) {

						e.printStackTrace();
					}
				}

	}

}
