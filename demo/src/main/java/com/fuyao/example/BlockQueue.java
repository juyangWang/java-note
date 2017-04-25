package com.fuyao.example;

import java.util.LinkedList;
import java.util.List;

public class BlockQueue<E> {
	
	private List<E>  queue = new LinkedList<>();
	
	private static final int max_count = 10;
	
	public synchronized void produce(E msg){
		System.out.println("add begin---------------"+msg);
		while(queue.size() == max_count){
			try {
				this.wait();
			} catch (InterruptedException e) {
					e.printStackTrace();
			}
		}
		System.out.println("add end---------------"+msg);
		queue.add(msg);
	}

	public synchronized E consume(){
		while(queue.size() == 0){
			try {
				this.wait();
			} catch (InterruptedException e) {
					e.printStackTrace();
			}
		}
		E e = queue.remove(0);
		this.notifyAll();
		return e;
	}
	public static void main(String[] args) {
		final BlockQueue<String> bq = new BlockQueue<>();
		
		Runnable t1 = new Runnable(){

			@Override
			public void run() {
				for(int i = 0;i<10;i++){
					bq.produce("aa"+i);
				}
				bq.produce("123");
			}
			
		};
		new Thread(t1).start();
		Runnable t2 = new Runnable(){

			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				bq.consume();
			}
			
		};
		new Thread(t2).start();
	}
}
