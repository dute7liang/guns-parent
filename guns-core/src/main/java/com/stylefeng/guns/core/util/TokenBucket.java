package com.stylefeng.guns.core.util;

/**
 * @author: zl 令牌桶限流
 * @Date: 2018-11-20 14:50
 */
public class TokenBucket {

	// 容量
	private int bucketNums = 100;
	// 流入速度
	private int rate = 1;
	// 当前令牌数量
	private int nowTokens;
	// 时间
	private long timestamp = getNowTime();;


	private long getNowTime(){
		return System.currentTimeMillis();
	}

	private int min(int nowTokens){
		if(nowTokens < bucketNums){
			return nowTokens;
		}
		return bucketNums;

	}

	public boolean getToken(){
		// 记录来拿令牌的时间
		long nowTime = getNowTime();
		// 添加令牌
		nowTokens = nowTokens + (int)((nowTime - timestamp)*rate);
		// 添加以后的令牌数量于桶的容量哪个小
		nowTokens = min(nowTokens);
		// 修改拿令牌的时间
		timestamp = nowTime;
		System.out.println("当前令牌数量:"+nowTokens);
		// 判断令牌时间
		if(nowTokens >= 1){
			nowTokens--;
			return true;
		}
		return false;
	}


	public static void main(String[] args) throws InterruptedException {

		TokenBucket tokenBucket = new TokenBucket();
		for (int i = 0; i < 500; i++) {
			if(i == 10){
				Thread.sleep(500);
			}
			System.out.println("第"+i+"次请求结果！"+tokenBucket.getToken());
		}
		
		
	}


}
