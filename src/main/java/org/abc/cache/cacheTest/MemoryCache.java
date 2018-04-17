package org.abc.cache.cacheTest;

import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryCache<K,V> {
	
	private final LinkedHashMap<K, V> map;
	
	private int size;
	
	private int maxSize;
	
	/**
	 * 构造函数
	 * @param maxSize
	 */
	public MemoryCache (int maxSize){
		SimulateURLValue.init();
		if(maxSize <= 0){
			throw new IllegalArgumentException("maxSize <= 0");
		}
		this.maxSize = maxSize;
		this.map = new LinkedHashMap<>(0, 0.75f, true); //第三个参数表示是否采用访问排序
	}
	
	/**
	 * 设定新的最大存储值
	 * @param maxSize
	 */
	public void resetMaxSize(int maxSize){
		if(maxSize <= 0){
			throw new IllegalArgumentException("maxSize <= 0");
		}
		synchronized (this) {
			this.maxSize = maxSize;
		}
	}
	
	/**
	 * 从内存中读取对应的值
	 * @param key
	 * @return
	 */
	public final V get(K key){
		if(key == null){
			throw new NullPointerException("key == null");
		}
		V value;
		synchronized (this) {
			value = map.get(key);
			if(value != null){
				return value;
			}
		}
		return null;
	}
	
	/**
	 * 将缓存放置到内存中
	 * @param key
	 * @param value
	 * @return
	 */
	public final V put(K key,V value){
		if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }
		
		V pervious;
		synchronized (this) {
			size += safeSizeOf(key, value);
			pervious = map.put(key, value);
			if(pervious != null){
				size -= safeSizeOf(key, pervious);
			}
		}
		
		trimToSize(maxSize);
		return pervious;
	}
	
	/**
	 * 将内存存储缓存控制在最大存储范围以内
	 * @param maxSize
	 */
	public void trimToSize(int maxSize){
		while(true){
			K key;
			V value;
			synchronized (this) {
				if(size < 0 || (map.isEmpty() && map.size() != 0)){
					throw new IllegalStateException(getClass().getName());
				}
				
				if(size <= maxSize || map.isEmpty()){
					break;
				}
				Map.Entry<K, V> toDelete = map.entrySet().iterator().next();
				key = toDelete.getKey();
				value = toDelete.getValue();
				map.remove(key);
				size -= safeSizeOf(key, value);
			}
		}
	}
	
	/**
	 * 删除对应的缓存
	 * @param key
	 * @return
	 */
	public final V remove(K key){
		if(key == null){
			throw new NullPointerException("key  == null");
		}
		V pervious;
		synchronized (this) {
			pervious = map.remove(key);
			if(pervious != null){
				size -= safeSizeOf(key, pervious);
			}
		}
		return pervious;
	}
	
	
	/**
	 * 获取正确的内存大小--防止重写的ofSize方法出错
	 * @param key
	 * @param value
	 * @return
	 */
	private int safeSizeOf(K key,V value){
		int result = sizeof(key, value);
		if(result < 0){
			throw new IllegalStateException("Negative size: "+ key + ":" + value);
		}
		return result;
	}
	
	/**
	 * 获取单个文件大小--实际应用中需要重写该方法
	 * @param key
	 * @param value
	 * @return
	 */
	protected int sizeof(K key,V value){
		return 1;
	}
	
	/**
	 * 清除所有缓存
	 */
	public final void clearAll(){
		trimToSize(-1);
	}
	
	/**
	 * 获取当前缓存大小
	 * @return
	 */
	public synchronized final int size(){
		return size;
	}
	
}
