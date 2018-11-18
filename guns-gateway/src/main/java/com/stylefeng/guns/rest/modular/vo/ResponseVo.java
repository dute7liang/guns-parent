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

	// 图片前缀
	private String imgPre;

	// 分页使用
	// 当前页
	private int nowPage;
	// 总页数
	private int totalPage;

	private ResponseVo (){}

	public static <T> ResponseVo success(T data){
		ResponseVo responseVo = new ResponseVo();
		responseVo.setStatus(0);
		responseVo.setData(data);
		return responseVo;
	}

	public static ResponseVo success(String msg){
		ResponseVo responseVo = new ResponseVo();
		responseVo.setStatus(0);
		responseVo.setMsg(msg);
		return responseVo;
	}

	public static<M> ResponseVo success(String imgPre,M m){
		ResponseVo responseVO = new ResponseVo();
		responseVO.setStatus(0);
		responseVO.setData(m);
		responseVO.setImgPre(imgPre);

		return responseVO;
	}

	public static<M> ResponseVo success(int nowPage,int totalPage,String imgPre,M m){
		ResponseVo responseVO = new ResponseVo();
		responseVO.setStatus(0);
		responseVO.setData(m);
		responseVO.setImgPre(imgPre);
		responseVO.setTotalPage(totalPage);
		responseVO.setNowPage(nowPage);

		return responseVO;
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
