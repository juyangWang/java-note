package com.fuyao.example.lock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

/**
 * zookeeper分布式锁的流程
 * 1.在zookeeper指定节点(locks)下创建临时顺序节点node_n
 * 2.获取locks下所有子节点children
 * 3.对子节点按节点自增序号从小到大排序
 * 4.判断本节点是不是第一个节点，若是,则获取锁;若不是则监听比该节点小的那个节点的删除事件
 * 5.若监听事件生效,则回到第二步重新进行判断,直到获取到锁
 * @author admin
 *
 */
public class DistributedLock implements Lock,Watcher{

	private ZooKeeper zk = null;
	
	//根节点
	private String ROOT_LOCK="/locks";
	//节点名
	private String lockName;
	//等待锁
	private String WAIT_LOCK;
	//当前锁
	private String CURRENT_LOCK;
	//计数器
	private CountDownLatch countDownLatch;
	private int sessioTimeout = 30000;
	private List<Exception> exceptionList = new ArrayList<Exception>();
	
	/**
	 * 配置分布式锁
	 * @param config 连接的URL
	 * @param lockName 锁路径
	 */
	public DistributedLock(String config,String lockName){
		this.lockName = lockName;
		try {
			zk = new ZooKeeper(config,sessioTimeout,this);
			Stat stat = zk.exists(ROOT_LOCK, false);
			if(stat == null){
				//根节点不存在,则创建
				zk.create(ROOT_LOCK, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	//节点监视器
	public void process(WatchedEvent arg0) {
		//EventType.NodeDeleted
		if(this.countDownLatch != null){
			this.countDownLatch.countDown();
		}
	}

	@Override
	public void lock() {
		
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		
	}

	@Override
	public Condition newCondition() {
		return null;
	}

	@Override
	public boolean tryLock() {
		try{
			String splitStr = "_lock_";
			if(lockName.contains(splitStr)){
				throw new LockException("锁名有误");
			}
			//创建临时有序节点
			CURRENT_LOCK = zk.create(ROOT_LOCK + "/" + lockName + splitStr, new byte[0],
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			//取出所有子节点
			List<String> subNodes = zk.getChildren(ROOT_LOCK, false);
			 // 取出所有lockName的锁
            List<String> lockObjects = new ArrayList<String>();
            for (String node : subNodes) {
                String _node = node.split(splitStr)[0];
                if (_node.equals(lockName)) {
                    lockObjects.add(node);
                }
            }
            Collections.sort(lockObjects);
            if (CURRENT_LOCK.equals(ROOT_LOCK + "/" + lockObjects.get(0))) {
                return true;
            }
            // 若不是最小节点，则找到自己的前一个节点
            String prevNode = CURRENT_LOCK.substring(CURRENT_LOCK.lastIndexOf("/") + 1);
            WAIT_LOCK = lockObjects.get(Collections.binarySearch(lockObjects, prevNode) - 1);
		} catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
		return false;
	}

	@Override
	public boolean tryLock(long arg0, TimeUnit arg1)
			throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	private  boolean waitForLock(String prev,long waitTime)throws KeeperException, InterruptedException{
		Stat stat = zk.exists(ROOT_LOCK+"/"+prev,true);
		if(stat != null){
			this.countDownLatch = new CountDownLatch(1);
			// 计数等待，若等到前一个节点消失，则precess中进行countDown，停止等待，获取锁 
			this.countDownLatch.await(waitTime, TimeUnit.MILLISECONDS); 
			this.countDownLatch = null; 
		}
		return true;
	}
	@Override
	public void unlock() {
		 try {
	            System.out.println("释放锁 " + CURRENT_LOCK);
	            zk.delete(CURRENT_LOCK, -1);
	            CURRENT_LOCK = null;
	            zk.close();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        } catch (KeeperException e) {
	            e.printStackTrace();
	        }
		
	}

	public class LockException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public LockException(String e){
            super(e);
        }
        public LockException(Exception e){
            super(e);
        }
    }
}
