package org.abc.cache.cacheTest;

public class Test {
	public static void main(String [] args){
		CacheTest cacheTest = new CacheTest();
		
		String url = "URL1";
		
		System.out.println(cacheTest.getValue(url));
		
		
		System.out.println(cacheTest.getValue(url));
		
		
		System.out.println(cacheTest.getValue(url));
	}
}
