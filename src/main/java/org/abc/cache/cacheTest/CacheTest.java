package org.abc.cache.cacheTest;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CacheTest {
	private int maxSize = 10;
	public MemoryCache<String, String> memoryCache = new MemoryCache<>(maxSize);
	private static int MAXSIZE = 10;
	private static String DIRECTORY_PATH = "E:/github/abc";
	public static DiskCache diskCache;
	static{
		File directory = new File(DIRECTORY_PATH);
		try {
			diskCache = DiskCache.open(directory, 1, 1, MAXSIZE);
		} catch (IOException e) {
			
		}
	}
	
	/**
	 * 下载数据并缓存到内存和硬盘中
	 * @param url
	 * @return
	 */
	public String downLoadFromUrl(String url){
		System.out.println("模拟读取网络数据");
		String value = SimulateURLValue.getString(url);
		if(value == null){
			System.out.println("未查询到URL对应数据");
			throw new RuntimeException("未找到资源");
		}
		String key = MD5(url);
		//存储到一级缓存中
		memoryCache.put(key, value);
		//存储到二级缓存中
		
		
		
		return value;
	}
	
	/**
	 * 查询url对应资源
	 */
	public String getValue(String url){
		String key = MD5(url);
		//查询一级缓存
		String value;
		System.out.println("-----查询一级缓存------");
		value = memoryCache.get(key);
		if(value != null){
			System.out.println("-----一级缓存查询成功------");
			return value;
		}
		//查询二级缓存
		System.out.println("-----查询二级缓存------");
		value = getFromDisk(key);
		if(value != null){
			System.out.println("-----二级缓存查询成功------");
			return value;
		}
		
		
		//未在缓存中查询到参数，调用下载接口
		value = downLoadFromUrl(url);
		
		return value;
		
		
	}
	
	
	
	/**
	 * 根据URL获取唯一Key
	 * @param url
	 * @return
	 */
	private String MD5(String url){
		 String cacheKey;
		    try {
		        final MessageDigest mDigest = MessageDigest.getInstance("MD5");
		        mDigest.update(url.getBytes());
		        cacheKey = bytesToHexString(mDigest.digest());
		    } catch (NoSuchAlgorithmException e) {
		        cacheKey = String.valueOf(url.hashCode());
		    }
		    // 返回将作为缓存的key值
		    return cacheKey;
	}
	
	private String bytesToHexString(byte[] bytes) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < bytes.length; i++) {
	        String hex = Integer.toHexString(0xFF & bytes[i]);
	        if (hex.length() == 1) {
	            sb.append('0');
	        }
	        sb.append(hex);
	    }
	    return sb.toString();
	}
	
	/**
	 * 从二级缓存获取信息
	 * @param key
	 * @return
	 */
	private String getFromDisk(String key){
		try{
			DiskCache.Snapshot snapShot = diskCache.get(key);
			if(snapShot != null){
				FileInputStream fileInputStream = (FileInputStream)snapShot.getInputStream(0);
		        FileDescriptor fileDescriptor = fileInputStream.getFD();
				return getStringFromStream(fileInputStream);
			}
		}catch(Exception e){
		}
		return null;
	}
	
	private String getStringFromStream(FileInputStream in){
		try{
			if(in == null){
				return null;
			}
			StringBuilder stringBuilder = new StringBuilder();
			int len = 0;  
	        byte[] buf = new byte[1024];  
	        while((len=in.read(buf))!=-1){  
	        	stringBuilder.append(new String(buf,0,len));  
	        }
	        return stringBuilder.toString();
		}catch(Exception e){
			throw new RuntimeException("读取文件失败");
		}
	}
}
