package com.fuyao.example.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * nio服务端
 * @author admin
 *
 */
public class NIOServer {

	//通道管理器
	private Selector selector;
	
	private Charset charset = Charset.forName("utf-8");// 
	/**
	 * 获得一个socket通道，并对该通道做一些初始化的工作
	 * @param port 端口号
	 * @throws IOException
	 */
	public void initServer(int port)throws IOException{
		//获得一个ServerSocket通道
		ServerSocketChannel serverchannel = ServerSocketChannel.open();
		//设置通道为非阻塞
		serverchannel.configureBlocking(false);
		//将该通道对应的ServerSocket绑定到port端口
		serverchannel.socket().bind(new InetSocketAddress(port));
		//获得一个通道管理器
		this.selector = Selector.open();
		/**
		 * 将通道管理器和该通道绑定,并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
		 * 当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
		 */
		serverchannel.register(selector, SelectionKey.OP_ACCEPT);
		
	}
	/**
	 * 采用轮询的方式监听selector上是否有需要处理的事件,如果有,则进行处理
	 * @throws IOException
	 */
	public void listen()throws IOException{
		System.out.println("服务端启动成功!!");
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss.SSS"); 
		//轮询访问selector
		while(true){
			//当注册的事件到达时,方法返回;否则,该方法会一直阻塞
			System.out.println("server ------select---1"+sdf.format(new Date()));
			selector.select();
			Iterator<SelectionKey> ite = this.selector.selectedKeys().iterator();
			while(ite.hasNext()){
				System.out.println("server ------next---2"+sdf.format(new Date()));
				SelectionKey key = ite.next();
				System.out.println("server ------acceptable:"+key.isAcceptable());
			    //删除已选的key,以防重复处理
				ite.remove();
				//客户端请求连接事件
				if(key.isAcceptable()){
					System.out.println("server ------accept---3"+sdf.format(new Date()));
					ServerSocketChannel server = (ServerSocketChannel)key.channel();
					//获得和客户端连接的通道
					SocketChannel channel = server.accept();
					channel.configureBlocking(false);
					channel.write(ByteBuffer.wrap(new String("向客户端发送了一条信息").getBytes("utf-8")));
					channel.register(this.selector, SelectionKey.OP_READ);
				}else if(key.isReadable()){
					System.out.println("server ------read----3"+sdf.format(new Date()));
					read(key);
				}
			}
		}
	}
	
	public void read(SelectionKey key)throws IOException{
		//服务器可读取消息:得到事件发生的socket通道
		SocketChannel channel = (SocketChannel)key.channel();
		//创建读取的缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(10);
		//BufferedReader br = new BufferedReader(buffer);
		/*channel.read(buffer);
		byte[] data = buffer.array();
		String msg = new String(data).trim();
		System.out.println("服务端收到信息1:"+msg);
		buffer.clear();
		channel.read(buffer);
		data = buffer.array();
		msg = new String(data).trim();
		System.out.println("服务端收到信息2:"+msg);
		buffer.clear();
		System.out.println("read size:"+channel.read(buffer));*/
		String msg = "";
		int len;
		while((len = channel.read(buffer)) >0){
			System.out.println("len:"+len);
			byte[] data = buffer.array();
			System.out.println("data="+charset.decode(buffer));
			msg += new String(data,0,len,"utf-8").trim();
			buffer.clear();
		}
		channel.write(ByteBuffer.wrap(msg.getBytes("utf-8")));
		System.out.println("服务端收到信息:"+msg);
		
		
	}
	
	public static void main(String[] args)throws IOException{
		NIOServer server = new NIOServer();
		server.initServer(8000);
		server.listen();
	}
}
