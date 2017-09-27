package com.fuyao.example;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 实现搜索服务
 * 输入查找关键字可以显示相关数据
 * 可以设置显示条数 尽量实现可以对数据CRUD
 * 线程安全下尽量保证数据的并行能力
 * 不使用elasticsearch的情况下
 */
public class SearchServer<T> {
	private final CharCloumn<T> [] columns = new CharCloumn[Character.MAX_VALUE];
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();
	
	public void put(T t,String value){
		w.lock();
		try{
			char[] chars = value.toCharArray();
			for(int i = 0;i<chars.length;i++){
				char c = chars[i];
				CharCloumn<T> cloumn = columns[c];
				if(cloumn == null){
					cloumn = new CharCloumn<T>();
					columns[c] = cloumn;
				}
				cloumn.add(t,(byte)i);
			}
		}finally{
			w.unlock();
		}
	}
	
	public void update(T t,String newValue){
		w.lock();
		try{
			remove(t);
			put(t,newValue);
		}finally{
			w.unlock();
		}
	}
	
	public boolean remove(T t){
		w.lock();
		try{
			for(CharCloumn<T> column : columns){
				if(column != null){
					if(column.remove(t)){
						return true;
					}
				}
			}
			return false;
		}finally{
			w.unlock();
		}
	}
	
	public Collection search(String word,int limit){
		r.lock();
		try{
			int n = word.length();
			char chars[] = word.toCharArray();
			Context context = new Context();
			for(int i =0;i<chars.length;i++){
				CharCloumn<T> column = columns[chars[i]];
				System.out.println("column:"+column+",index:"+(int)chars[i]);
				for(T s : column.poxIndex.keySet()){
					System.out.println("key:"+s);
					for(byte b : column.poxIndex.get(s)){
						System.out.println("value:"+b);
					}
				}
				
				if(column == null){
					break;
				}
				if(!context.filter(column)){
					break;
				}
				n--;
			}
			if(n == 0){
				return context.limit(limit);
			}
			
			return Collections.emptySet();
		}finally{
			r.unlock();
		}
			
	}
	
	private class Context<T>{
		Map<T,byte[]> result;
		boolean used = false;
		private boolean filter(CharCloumn<T> columns){
			if(this.used == false){
				this.result = new TreeMap<T,byte[]>(columns.poxIndex);
				this.used = true;
				return true;
			}
			Map<T,byte[]> newResult = new TreeMap<T,byte[]>();
			Set<Map.Entry<T,byte[]>> entrySet = columns.poxIndex.entrySet();
			for(Map.Entry<T,byte[]> entry : entrySet){
				T id = entry.getKey();
				byte[] charPox = entry.getValue();
				if(!result.containsKey(id)){
					continue;
				}
				byte[] before = result.get(id);
				boolean in = false;
				for(byte pox : before){
					if(contain(charPox,(byte)(pox+1))){
						in = true;
						break;
					}
				}
				if(in){
					newResult.put(id, charPox);
				}
			}
			result = newResult;
			return true;
		}
		private Collection limit(int limit){
			if(result.size() <= limit){
				return result.keySet();
			}
			Collection<T> ids = new TreeSet<T>();
			for(T id : result.keySet()){
				ids.add(id);
				if(ids.size() >= limit){
					break;
				}
			}
			return ids;
		}
	}
	
	private class CharCloumn<T>{
		ConcurrentHashMap<T, byte[]> poxIndex = new ConcurrentHashMap<T, byte[]>();
		void add(T t,byte pox){
			byte[] arr = poxIndex.get(t);
			if(arr == null){
				arr = new byte[]{pox};
			}else{
				arr = copy(arr,pox);
			}
			poxIndex.put(t, arr);
		}
		boolean remove(T id){
			if(poxIndex.remove(id) != null){
				return true;
			}
			return false;
		}
		@Override
		public String toString() {
			String ss = "";
			for(T s : poxIndex.keySet()){
				ss +="key:"+s+",value:"+Arrays.asList(poxIndex.get(s));
			}
			return "CharCloumn [poxIndex=" + ss + "]";
		}
		
	}
	
	private static byte[] copy(byte[] arr,byte value){
		Arrays.sort(arr);
		if(contain(arr,value)){
			return arr;
		}
		byte[] newArr = new byte[arr.length+1];
		newArr[newArr.length-1] = value;
		System.arraycopy(arr, 0, newArr, 0, arr.length);
		Arrays.sort(newArr);
		return newArr;
	}
	private static boolean contain(byte[] arr,byte value){
		int pox = Arrays.binarySearch(arr, value);
		return (pox>=0)?true:false;
	}
	public static void main(String[] args) {
		SearchServer searchServer = new SearchServer();
		searchServer.put("hn","湖南");
		searchServer.put("hb","湖北");
		searchServer.put("bj","北京");
		searchServer.put("hb","河北");
		searchServer.put("hn","河南");
		Set<String> set = (Set) searchServer.search("北京", 10);
		for(Iterator it = set.iterator(); it.hasNext();){
			System.out.println(it.next());
		}
	}
}
