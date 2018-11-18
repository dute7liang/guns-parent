package com.stylefeng.guns.api.film.vo;

import lombok.Data;

/**
 * @author: zl
 * @Date: 2018/11/18 21:20
 */
@Data
public class FilmInfoVo {

	private String filmId;
	private int filmType;
	private String imgAddress;
	private String fileName;
	private String fileScore;
	private int expectNum;
	private String showTime;

	private int boxNum;
	private String score;


}
