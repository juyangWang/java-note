package com.fuyao.example.lock;

public class TestRead implements Runnable {

	public ReadWriteLock lock;
	
	public TestRead(ReadWriteLock lock) {
		this.lock = lock;
	}

	@Override
	public void run() {
	}

}
