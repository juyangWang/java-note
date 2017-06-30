package com.fuyao.example.lock;

public class ReadWriteLock{

	private int readers = 0;
	private int writers = 0;
	private int writeRequests = 0;

	public synchronized void lockRead() throws InterruptedException {
		while (writers > 0 || writeRequests > 0) {
			wait();
		}
		readers++;
	}

	public synchronized void unlockRead() {
		readers--;
		notifyAll();
	}

	public synchronized void lockWrite() throws InterruptedException {
		writeRequests++;

		while (readers > 0 || writers > 0) {
			wait();
		}
		writeRequests--;
		writers++;
	}

	public synchronized void unlockWrite() throws InterruptedException {
		writers--;
		notifyAll();
	}

	public static void main(String[] args) {
		ReadWriteLock lock = new ReadWriteLock();
		Thread t1 = new Thread(new TestRead(lock));
		Thread t2 = new Thread(new TestWrite(lock));
		t1.start();t2.start();
	}
}
