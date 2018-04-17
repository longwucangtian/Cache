package org.abc.cache.cacheTest;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟访问网络下载数据
 * @author 
 *
 */
public class SimulateURLValue {

	static Map<String, String> map = new HashMap<>();
	public static void init(){
		map.put("URL1","1111");
		map.put("URL2","2222");
		map.put("URL3","3333");
		map.put("URL4","4444");
		map.put("URL5","5555");
		map.put("URL6","6666");
		map.put("URL7","7777");
		map.put("URL8","8888");
		map.put("URL9","9999");
		map.put("URL10","aaaa");
		map.put("URL11","bbbb");
	}
	public static String getString(String url){
		if(map.containsKey(url)){
			return map.get(url);
		}
		return null;
	}
	
	
	
}
