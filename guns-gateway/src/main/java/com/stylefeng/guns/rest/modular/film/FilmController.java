package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.stylefeng.guns.api.film.FilmAsyncServiceApi;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVo;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVo;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVo;
import com.stylefeng.guns.rest.modular.vo.ResponseVo;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author: zl
 * @Date: 2018/11/18 21:09
 */
@RestController
@RequestMapping("film")
public class FilmController {


	@Reference(check = false)
	private FilmServiceApi filmServiceApi;

	@Reference(check = false)
	private FilmAsyncServiceApi filmAsyncServiceApi;

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
		FilmConditionVo filmConditionVO = new FilmConditionVo();

		// 标识位
		boolean flag = false;
		// 类型集合
		List<CatVo> cats = filmServiceApi.getCats();
		List<CatVo> catResult = new ArrayList<>();
		CatVo cat = null;
		for(CatVo catVO : cats){
			// 判断集合是否存在catId，如果存在，则将对应的实体变成active状态
			// 6
			// 1,2,3,99,4,5 ->
            /*
                优化：【理论上】
                    1、数据层查询按Id进行排序【有序集合 -> 有序数组】
                    2、通过二分法查找
             */
			if(catVO.getCatId().equals("99")){
				cat = catVO;
				continue;
			}
			if(catVO.getCatId().equals(catId)){
				flag = true;
				catVO.setActive(true);
			}else{
				catVO.setActive(false);
			}
			catResult.add(catVO);
		}
		// 如果不存在，则默认将全部变为Active状态
		if(!flag){
			cat.setActive(true);
			catResult.add(cat);
		}else{
			cat.setActive(false);
			catResult.add(cat);
		}


		// 片源集合
		flag=false;
		List<SourceVo> sources = filmServiceApi.getSources();
		List<SourceVo> sourceResult = new ArrayList<>();
		SourceVo sourceVO = null;
		for(SourceVo source : sources){
			if(source.getSourceId().equals("99")){
				sourceVO = source;
				continue;
			}
			if(source.getSourceId().equals(catId)){
				flag = true;
				source.setActive(true);
			}else{
				source.setActive(false);
			}
			sourceResult.add(source);
		}
		// 如果不存在，则默认将全部变为Active状态
		if(!flag){
			sourceVO.setActive(true);
			sourceResult.add(sourceVO);
		}else{
			sourceVO.setActive(false);
			sourceResult.add(sourceVO);
		}

		// 年代集合
		flag=false;
		List<YearVo> years = filmServiceApi.getYears();
		List<YearVo> yearResult = new ArrayList<>();
		YearVo yearVO = null;
		for(YearVo year : years){
			if(year.getYearId().equals("99")){
				yearVO = year;
				continue;
			}
			if(year.getYearId().equals(catId)){
				flag = true;
				year.setActive(true);
			}else{
				year.setActive(false);
			}
			yearResult.add(year);
		}
		// 如果不存在，则默认将全部变为Active状态
		if(!flag){
			yearVO.setActive(true);
			yearResult.add(yearVO);
		}else{
			yearVO.setActive(false);
			yearResult.add(yearVO);
		}

		filmConditionVO.setCatInfo(catResult);
		filmConditionVO.setSourceInfo(sourceResult);
		filmConditionVO.setYearInfo(yearResult);

		return ResponseVo.success(filmConditionVO);
	}


	@RequestMapping(value = "getFilms",method = RequestMethod.GET)
	public ResponseVo getFilms(FilmRequestVo filmRequestVO){
		String img_pre = "http://img.meetingshop.cn/";

		FilmVo filmVO = null;
		// 根据showType判断影片查询类型
		switch (filmRequestVO.getShowType()){
			case 1 :
				filmVO = filmServiceApi.getHotFilms(
						false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
						filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),
						filmRequestVO.getCatId());
				break;
			case 2 :
				filmVO = filmServiceApi.getSoonFilms(
						false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
						filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),
						filmRequestVO.getCatId());
				break;
			case 3 :
				filmVO = filmServiceApi.getClassicFilms(
						filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
						filmRequestVO.getSortId(),filmRequestVO.getSourceId(),
						filmRequestVO.getYearId(), filmRequestVO.getCatId());
				break;
			default:
				filmVO = filmServiceApi.getHotFilms(
						false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
						filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),
						filmRequestVO.getCatId());
				break;
		}
		// 根据sortId排序
		// 添加各种条件查询
		// 判断当前是第几页

		return ResponseVo.success(filmVO.getNowPage(),filmVO.getTotalPage(),
				img_pre,filmVO.getFilmInfo());
	}


	@RequestMapping(value = "films/{searchParam}",method = RequestMethod.GET)
	public ResponseVo films(@PathVariable("searchParam")String searchParam,
							int searchType) throws ExecutionException, InterruptedException {
		// 根据searchType，判断查询类型
		FilmDetailVo filmDetail = filmServiceApi.getFilmDetail(searchType, searchParam);

		if(filmDetail==null){
			return ResponseVo.serviceFail("没有可查询的影片");
		}else if(filmDetail.getFilmId()==null || filmDetail.getFilmId().trim().length()==0){
			return ResponseVo.serviceFail("没有可查询的影片");
		}

		String filmId = filmDetail.getFilmId();
		// 查询影片的详细信息 -> Dubbo的异步调用
		// 获取影片描述信息
		filmAsyncServiceApi.getFilmDesc(filmId);
		Future<FilmDescVo> filmDescVOFuture = RpcContext.getContext().getFuture();
		// 获取图片信息
		filmAsyncServiceApi.getImgs(filmId);
		Future<ImgVo> imgVOFuture = RpcContext.getContext().getFuture();
		// 获取导演信息
		filmAsyncServiceApi.getDectInfo(filmId);
		Future<ActorVo> actorVOFuture = RpcContext.getContext().getFuture();
		// 获取演员信息
		filmAsyncServiceApi.getActors(filmId);
		Future<List<ActorVo>> actorsVOFutrue = RpcContext.getContext().getFuture();

		// 组织info对象
		InfoRequstVo infoRequstVO = new InfoRequstVo();

		// 组织Actor属性
		ActorRequestVo actorRequestVO = new ActorRequestVo();
		actorRequestVO.setActors(actorsVOFutrue.get());
		actorRequestVO.setDirector(actorVOFuture.get());

		// 组织info对象
		infoRequstVO.setActors(actorRequestVO);
		infoRequstVO.setBiography(filmDescVOFuture.get().getBiography());
		infoRequstVO.setFilmId(filmId);
		infoRequstVO.setImgVO(imgVOFuture.get());

		// 组织成返回值
		filmDetail.setInfo04(infoRequstVO);

		return ResponseVo.success("http://img.meetingshop.cn/",filmDetail);
	}
}
