package com.fuyao.example.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TestBuffer {
	static private final byte message[] = { 83, 111, 109, 101, 32,  
		98, 121, 116, 101, 115, 46 };  

	public static void main(String[] args) throws IOException {
		// 获取通道，该通道允许写操作
		//FileOutputStream fout = new FileOutputStream( "D:\\data.txt",true);  
		MappedByteBuffer buffer = new RandomAccessFile("D:\\data.txt","rw").getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 128);
		//ByteBuffer buffer = ByteBuffer.allocate( 1024 );  
		for (int i=0; i<message.length; ++i) {  
				   buffer.put( message[i] );  
		}  
	    //buffer.flip(); 
	    //buffer.limit(2);
	    //fc.position(20);
		//fc.write( buffer );  
		//fout.close();

	}

}
