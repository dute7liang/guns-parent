package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zl
 * @Date: 2018/11/18 21:45
 */
@Component
@Service
public class DefaultFilmServiceImpl implements FilmServiceApi {

	@Autowired
	private BannerTMapper bannerTMapper;

	@Autowired
	private FilmTMapper filmTMapper;

	@Autowired
	private CatDictTMapper catDictTMapper;

	@Autowired
	private SourceDictTMapper sourceDictTMapper;

	@Autowired
	private YearDictTMapper yearDictTMapper;

	@Autowired
	private FilmInfoTMapper filmInfoTMapper;

	@Autowired
	private ActorTMapper actorTMapper;

	@Override
	public List<BannerVo> getBanners() {
		List<BannerVo> result = new ArrayList<>();
		List<BannerT> banners = bannerTMapper.selectList(null);

		for(BannerT bannerT : banners){
			BannerVo bannerVO = new BannerVo();
			bannerVO.setBannerId(bannerT.getUuid()+"");
			bannerVO.setBannerUrl(bannerT.getBannerUrl());
			bannerVO.setBannerAddress(bannerT.getBannerAddress());
			result.add(bannerVO);
		}

		return result;
	}


	private List<FilmInfo> getFilmInfos(List<FilmT> films){
		List<FilmInfo> filmInfos = new ArrayList<>();
		for(FilmT filmT : films){
			FilmInfo filmInfo = new FilmInfo();
			filmInfo.setScore(filmT.getFilmScore());
			filmInfo.setImgAddress(filmT.getImgAddress());
			filmInfo.setFilmType(filmT.getFilmType());
			filmInfo.setFilmScore(filmT.getFilmScore());
			filmInfo.setFilmName(filmT.getFilmName());
			filmInfo.setFilmId(filmT.getUuid()+"");
			filmInfo.setExpectNum(filmT.getFilmPresalenum());
			filmInfo.setBoxNum(filmT.getFilmBoxOffice());
			filmInfo.setShowTime(DateUtil.getDay(filmT.getFilmTime()));

			// 将转换的对象放入结果集
			filmInfos.add(filmInfo);
		}

		return filmInfos;
	}

	@Override
	public FilmVo getHotFilms(boolean isLimit, int nums, int nowPage, int sortId, int sourceId, int yearId, int catId) {
		FilmVo filmVo = new FilmVo();
		List<FilmInfo> filmInfos = new ArrayList<>();

		// 热映影片的限制条件
		EntityWrapper<FilmT> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("film_status","1");
		// 判断是否是首页需要的内容
		if(isLimit){
			// 如果是，则限制条数、限制内容为热映影片
			Page<FilmT> page = new Page<>(1,nums);
			List<FilmT> moocFilms = filmTMapper.selectPage(page, entityWrapper);
			// 组织filmInfos
			filmInfos = getFilmInfos(moocFilms);
			filmVo.setFilmNum(moocFilms.size());
			filmVo.setFilmInfo(filmInfos);
		}else{
			// 如果不是，则是列表页，同样需要限制内容为热映影片
			Page<FilmT> page = null;
			// 根据sortId的不同，来组织不同的Page对象
			// 1-按热门搜索，2-按时间搜索，3-按评价搜索
			switch (sortId){
				case 1 :
					page = new Page<>(nowPage,nums,"film_box_office");
					break;
				case 2 :
					page = new Page<>(nowPage,nums,"film_time");
					break;
				case 3 :
					page = new Page<>(nowPage,nums,"film_score");
					break;
				default:
					page = new Page<>(nowPage,nums,"film_box_office");
					break;
			}

			// 如果sourceId,yearId,catId 不为99 ,则表示要按照对应的编号进行查询
			if(sourceId != 99){
				entityWrapper.eq("film_source",sourceId);
			}
			if(yearId != 99){
				entityWrapper.eq("film_date",yearId);
			}
			if(catId != 99){
				// #2#4#22#
				String catStr = "%#"+catId+"#%";
				entityWrapper.like("film_cats",catStr);
			}

			List<FilmT> moocFilms = filmTMapper.selectPage(page, entityWrapper);
			// 组织filmInfos
			filmInfos = getFilmInfos(moocFilms);
			filmVo.setFilmNum(moocFilms.size());

			// 需要总页数 totalCounts/nums -> 0 + 1 = 1
			// 每页10条，我现在有6条 -> 1
			int totalCounts = filmTMapper.selectCount(entityWrapper);
			int totalPages = (totalCounts/nums)+1;

			filmVo.setFilmInfo(filmInfos);
			filmVo.setTotalPage(totalPages);
			filmVo.setNowPage(nowPage);
		}

		return filmVo;
	}

	@Override
	public FilmVo getSoonFilms(boolean isLimit, int nums, int nowPage, int sortId, int sourceId, int yearId, int catId) {
		FilmVo filmVo = new FilmVo();
		List<FilmInfo> filmInfos = new ArrayList<>();

		// 即将上映影片的限制条件
		EntityWrapper<FilmT> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("film_status","2");
		// 判断是否是首页需要的内容
		if(isLimit){
			// 如果是，则限制条数、限制内容为热映影片
			Page<FilmT> page = new Page<>(1,nums);
			List<FilmT> moocFilms = filmTMapper.selectPage(page, entityWrapper);
			// 组织filmInfos
			filmInfos = getFilmInfos(moocFilms);
			filmVo.setFilmNum(moocFilms.size());
			filmVo.setFilmInfo(filmInfos);
		}else{
			// 如果不是，则是列表页，同样需要限制内容为即将上映影片
			Page<FilmT> page = null;
			// 根据sortId的不同，来组织不同的Page对象
			// 1-按热门搜索，2-按时间搜索，3-按评价搜索
			switch (sortId){
				case 1 :
					page = new Page<>(nowPage,nums,"film_preSaleNum");
					break;
				case 2 :
					page = new Page<>(nowPage,nums,"film_time");
					break;
				case 3 :
					page = new Page<>(nowPage,nums,"film_preSaleNum");
					break;
				default:
					page = new Page<>(nowPage,nums,"film_preSaleNum");
					break;
			}

			// 如果sourceId,yearId,catId 不为99 ,则表示要按照对应的编号进行查询
			if(sourceId != 99){
				entityWrapper.eq("film_source",sourceId);
			}
			if(yearId != 99){
				entityWrapper.eq("film_date",yearId);
			}
			if(catId != 99){
				// #2#4#22#
				String catStr = "%#"+catId+"#%";
				entityWrapper.like("film_cats",catStr);
			}

			List<FilmT> moocFilms = filmTMapper.selectPage(page, entityWrapper);
			// 组织filmInfos
			filmInfos = getFilmInfos(moocFilms);
			filmVo.setFilmNum(moocFilms.size());

			// 需要总页数 totalCounts/nums -> 0 + 1 = 1
			// 每页10条，我现在有6条 -> 1
			int totalCounts = filmTMapper.selectCount(entityWrapper);
			int totalPages = (totalCounts/nums)+1;

			filmVo.setFilmInfo(filmInfos);
			filmVo.setTotalPage(totalPages);
			filmVo.setNowPage(nowPage);
		}

		return filmVo;
	}

	@Override
	public FilmVo getClassicFilms(int nums, int nowPage, int sortId, int sourceId, int yearId, int catId) {
		FilmVo filmVO = new FilmVo();
		List<FilmInfo> filmInfos = new ArrayList<>();

		// 即将上映影片的限制条件
		EntityWrapper<FilmT> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("film_status","3");

		// 如果不是，则是列表页，同样需要限制内容为即将上映影片
		Page<FilmT> page = null;
		// 根据sortId的不同，来组织不同的Page对象
		// 1-按热门搜索，2-按时间搜索，3-按评价搜索
		switch (sortId){
			case 1 :
				page = new Page<>(nowPage,nums,"film_box_office");
				break;
			case 2 :
				page = new Page<>(nowPage,nums,"film_time");
				break;
			case 3 :
				page = new Page<>(nowPage,nums,"film_score");
				break;
			default:
				page = new Page<>(nowPage,nums,"film_box_office");
				break;
		}

		// 如果sourceId,yearId,catId 不为99 ,则表示要按照对应的编号进行查询
		if(sourceId != 99){
			entityWrapper.eq("film_source",sourceId);
		}
		if(yearId != 99){
			entityWrapper.eq("film_date",yearId);
		}
		if(catId != 99){
			// #2#4#22#
			String catStr = "%#"+catId+"#%";
			entityWrapper.like("film_cats",catStr);
		}

		List<FilmT> moocFilms = filmTMapper.selectPage(page, entityWrapper);
		// 组织filmInfos
		filmInfos = getFilmInfos(moocFilms);
		filmVO.setFilmNum(moocFilms.size());

		// 需要总页数 totalCounts/nums -> 0 + 1 = 1
		// 每页10条，我现在有6条 -> 1
		int totalCounts = filmTMapper.selectCount(entityWrapper);
		int totalPages = (totalCounts/nums)+1;

		filmVO.setFilmInfo(filmInfos);
		filmVO.setTotalPage(totalPages);
		filmVO.setNowPage(nowPage);

		return filmVO;
	}

	@Override
	public List<FilmInfo> getBoxRanking() {
		// 条件 -> 正在上映的，票房前10名
		EntityWrapper<FilmT> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("film_status","1");

		Page<FilmT> page = new Page<>(1,10,"film_box_office");

		List<FilmT> moocFilms = filmTMapper.selectPage(page,entityWrapper);

		List<FilmInfo> filmInfos = getFilmInfos(moocFilms);

		return filmInfos;
	}

	@Override
	public List<FilmInfo> getExpectRanking() {
		// 条件 -> 即将上映的，预售前10名
		EntityWrapper<FilmT> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("film_status","2");

		Page<FilmT> page = new Page<>(1,10,"film_preSaleNum");

		List<FilmT> films = filmTMapper.selectPage(page,entityWrapper);

		List<FilmInfo> filmInfos = getFilmInfos(films);

		return filmInfos;

	}

	@Override
	public List<FilmInfo> getTop() {
		// 条件 -> 正在上映的，评分前10名
		EntityWrapper<FilmT> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("film_status","1");

		Page<FilmT> page = new Page<>(1,10,"film_score");

		List<FilmT> films = filmTMapper.selectPage(page,entityWrapper);

		List<FilmInfo> filmInfos = getFilmInfos(films);

		return filmInfos;
	}

	@Override
	public List<CatVo> getCats() {
		List<CatVo> catVo = new ArrayList<>();
		// 查询实体对象 - MoocCatDictT
		List<CatDictT> cats = catDictTMapper.selectList(null);
		// 将实体对象转换为业务对象 - CatVO
		for(CatDictT catDictT : cats){
			CatVo catVO = new CatVo();
			catVO.setCatId(catDictT.getUuid()+"");
			catVO.setCatName(catDictT.getShowName());

			catVo.add(catVO);
		}

		return catVo;
	}

	@Override
	public List<SourceVo> getSources() {
		List<SourceVo> sources = new ArrayList<>();
		List<SourceDictT> sourceDicts = sourceDictTMapper.selectList(null);
		for (SourceDictT sourceDictT : sourceDicts) {
			SourceVo sourceVO = new SourceVo();

			sourceVO.setSourceId(sourceDictT.getUuid() + "");
			sourceVO.setSourceName(sourceDictT.getShowName());

			sources.add(sourceVO);
		}
		return sources;
	}

	@Override
	public List<YearVo> getYears() {
		List<YearVo> years = new ArrayList<>();
		// 查询实体对象 - MoocCatDictT
		List<YearDictT> yearDictTList = yearDictTMapper.selectList(null);
		// 将实体对象转换为业务对象 - CatVO
		for(YearDictT yearDictT : yearDictTList){
			YearVo yearVO = new YearVo();
			yearVO.setYearId(yearDictT.getUuid()+"");
			yearVO.setYearName(yearDictT.getShowName());

			years.add(yearVO);
		}
		return years;
	}

	@Override
	public FilmDetailVo getFilmDetail(int searchType, String searchParam) {
		FilmDetailVo filmDetailVO = null;
		// searchType 1-按名称  2-按ID的查找
		if(searchType == 1){
//			filmDetailVO = moocFilmTMapper.getFilmDetailByName("%"+searchParam+"%");
		}else{
//			filmDetailVO = moocFilmTMapper.getFilmDetailById(searchParam);
		}

		return filmDetailVO;
	}

	private FilmInfoT getFilmInfo(String filmId){

		FilmInfoT filmInfoT = new FilmInfoT();
		filmInfoT.setFilmId(filmId);
		filmInfoT = filmInfoTMapper.selectOne(filmInfoT);
		return filmInfoT;
	}

	@Override
	public FilmDescVo getFilmDesc(String filmId) {
		FilmInfoT filmInfoT = getFilmInfo(filmId);

		FilmDescVo filmDescVO = new FilmDescVo();
		filmDescVO.setBiography(filmInfoT.getBiography());
		filmDescVO.setFilmId(filmId);

		return filmDescVO;
	}

	@Override
	public ImgVo getImgs(String filmId) {
		FilmInfoT moocFilmInfoT = getFilmInfo(filmId);
		// 图片地址是五个以逗号为分隔的链接URL
		String filmImgStr = moocFilmInfoT.getFilmImgs();
		String[] filmImgs = filmImgStr.split(",");

		ImgVo imgVO = new ImgVo();
		imgVO.setMainImg(filmImgs[0]);
		imgVO.setImg01(filmImgs[1]);
		imgVO.setImg02(filmImgs[2]);
		imgVO.setImg03(filmImgs[3]);
		imgVO.setImg04(filmImgs[4]);

		return imgVO;
	}

	@Override
	public ActorVo getDectInfo(String filmId) {
		FilmInfoT filmInfoT = getFilmInfo(filmId);

		// 获取导演编号
		Integer directId = filmInfoT.getDirectorId();

		ActorT actorT = actorTMapper.selectById(directId);

		ActorVo actorVO = new ActorVo();
		actorVO.setImgAddress(actorT.getActorImg());
		actorVO.setDirectorName(actorT.getActorName());

		return actorVO;
	}

	@Override
	public List<ActorVo> getActors(String filmId) {
//		List<ActorVo> actors = actorTMapper.getActors(filmId);
//		return actors;
		return null;
	}
}
