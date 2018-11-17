package com.stylefeng.guns.rest.common;

/**
 * @author: zl
 * @Date: 2018-11-17 16:54
 */
public class CurrentUser {

	private final static ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();


	public static void saveUserInfo(String userId){
		THREAD_LOCAL.set(userId);
	}


	public static String getCurrentUser(){
		return THREAD_LOCAL.get();
	}

}
