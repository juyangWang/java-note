package com.fuyao.example.lock;

public class TestWrite implements Runnable {

	public ReadWriteLock lock;
	
	public TestWrite(ReadWriteLock lock) {
		this.lock = lock;
	}
	
	@Override
	public void run() {
	}
}
