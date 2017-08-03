package com.fuyao.example.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class NIOClient {
	//通道管理器
		private Selector selector;

		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss.SSS");
		/**
		 * 获得一个socket通道，并对该通道做一些初始化的工作
		 * @param ip 链接的服务器的ip
		 * @param port 连接的服务器的端口号
		 * @throws IOException
		 */
		public void initClient(String ip,int port)throws IOException{
			//获得一个socket通道
			SocketChannel channel = SocketChannel.open();
			//设置通道为非阻塞
			channel.configureBlocking(false);
			//获得一个通道管理器
			this.selector = Selector.open();
			/**
			 * 客户端连接服务器，其实方法执行并没有实现连接，需要listen()方法中调用
			 * channel.finishConnect();才能完成连接
			 */
			channel.connect(new InetSocketAddress(ip,port));
			//将通道管理器和该通道绑定，并为该通道注册Selectionkey.OP_CONNECT事件
			System.out.println("client ------register---:"+sdf.format(new Date()));
			channel.register(selector, SelectionKey.OP_CONNECT);

		}

		/**
		 * 采用轮询的方式监听selector上是否有需要处理的事件,如果有,则进行处理
		 * @throws IOException
		 */
		public void listen()throws IOException{

			//轮询访问selector
			while(true){

				System.out.println("client ------select---1:"+sdf.format(new Date()));
				selector.select();
				//获得selector中选中的想的迭代器
				Iterator ite = this.selector.selectedKeys().iterator();
				while(ite.hasNext()){
					System.out.println("client ------next---2:"+sdf.format(new Date()));
					SelectionKey key = (SelectionKey)ite.next();
					//删除已选的key,以防重复处理
					ite.remove();
					if(key.isConnectable()){
						System.out.println("client ------Connect---3:"+sdf.format(new Date()));
						SocketChannel channel = (SocketChannel)key.channel();
						//如果正在连接,则完成连接
						if(channel.isConnectionPending()){
							channel.finishConnect();
							//System.out.println("-----------connect-----------"+sdf.format(new Date()));
						}
						channel.configureBlocking(false);
						channel.write(ByteBuffer.wrap(new String("向服务端发送了一条信息").getBytes("utf-8")));
						channel.register(this.selector, SelectionKey.OP_READ);
					}else if(key.isReadable()){
						System.out.println("client ------read---3"+sdf.format(new Date()));
						read(key);
					}
				}
			}
		}

		/**
		 * 处理读取服务端发来的信息的事件
		 * @param key
		 * @throws IOException
		 */
		public void read(SelectionKey key)throws IOException{
			//客户端可读取消息:得到事件发生的socket通道
			SocketChannel channel = (SocketChannel)key.channel();
			//创建读取的缓冲区
			ByteBuffer buffer = ByteBuffer.allocate(10);
			String msg = "";
			int len;
			while((len = channel.read(buffer)) > 0){
				byte[] data = buffer.array();
				msg += new String(data,0,len,"utf-8").trim();
				buffer.clear();
			}
			System.out.println("客户端收到信息:"+msg);
			//channel.write(ByteBuffer.wrap(data));
		}

		public static void main(String[] args) throws IOException {
			NIOClient client = new NIOClient();
			client.initClient("127.0.0.1", 8000);
			client.listen();
		}
}
