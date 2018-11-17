package com.stylefeng.guns.rest.modular.vo;

import lombok.Data;

/**
 * @author: zl
 * @Date: 2018-11-17 16:48
 */
@Data
public class ResponseVo<T> {

	private int status;

	private String msg;

	private T data;

	private ResponseVo (){}

	public static <T> ResponseVo success(T data){
		ResponseVo responseVo = new ResponseVo();
		responseVo.setStatus(0);
		responseVo.setData(data);
		return responseVo;
	}


	public static <T> ResponseVo serviceFail(String msg){
		ResponseVo responseVo = new ResponseVo();
		responseVo.setStatus(1);
		responseVo.setMsg(msg);
		return responseVo;
	}

	public static <T> ResponseVo appFail(String msg){
		ResponseVo responseVo = new ResponseVo();
		responseVo.setStatus(999);
		responseVo.setMsg(msg);
		return responseVo;
	}

}
