package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVo;
import com.stylefeng.guns.rest.modular.vo.ResponseVo;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

/**
 * @author: zl
 * @Date: 2018/11/18 21:09
 */
@RestController
@RequestMapping("film")
public class FilmController {


	@Reference
	private FilmServiceApi filmServiceApi;

	// 获取首页信息接口
    /*
        API网关：
            1、功能聚合【API聚合】
            好处：
                1、六个接口，一次请求，同一时刻节省了五次HTTP请求
                2、同一个接口对外暴漏，降低了前后端分离开发的难度和复杂度
            坏处：
                1、一次获取数据过多，容易出现问题
     */
	@RequestMapping(value = "getIndex",method = RequestMethod.GET)
	public ResponseVo getIndex(){
		FilmIndexVo filmIndexVO = new FilmIndexVo();
		// 获取banner信息
		filmIndexVO.setBanners(filmServiceApi.getBanners());
		// 获取正在热映的电影
		filmIndexVO.setHotFilms(filmServiceApi.getHotFilms(true,8,1,1,99,99,99));
		// 即将上映的电影
		filmIndexVO.setSoonFilms(filmServiceApi.getSoonFilms(true,8,1,1,99,99,99));
		// 票房排行榜
		filmIndexVO.setBoxRanking(filmServiceApi.getBoxRanking());
		// 获取受欢迎的榜单
		filmIndexVO.setExpectRanking(filmServiceApi.getExpectRanking());
		// 获取前一百
		filmIndexVO.setTop100(filmServiceApi.getTop());

		return ResponseVo.success(filmIndexVO);
	}


	@RequestMapping(value = "getConditionList",method = RequestMethod.GET)
	public ResponseVo getConditionList(@RequestParam(name = "catId",required = false,defaultValue = "99")String catId,
									   @RequestParam(name = "sourceId",required = false,defaultValue = "99")String sourceId,
									   @RequestParam(name = "yearId",required = false,defaultValue = "99")String yearId){
		return null;
	}


	@RequestMapping(value = "getFilms",method = RequestMethod.GET)
	public ResponseVo getFilms(){

		return null;
	}


	@RequestMapping(value = "films/{searchParam}",method = RequestMethod.GET)
	public ResponseVo films(@PathVariable("searchParam")String searchParam,
							int searchType) throws ExecutionException, InterruptedException {

		return null;
	}
}
