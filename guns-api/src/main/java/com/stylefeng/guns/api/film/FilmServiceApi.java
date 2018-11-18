package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

public interface FilmServiceApi {

    /**
     * 获取banners
     * @return
     */
    List<BannerVo> getBanners();

    /**
     * 获取热映影片
     * @return
     */
    FilmVo getHotFilms(boolean isLimit, int nums, int nowPage, int sortId, int sourceId, int yearId, int catId);

    /**
     * 获取即将上映影片[受欢迎程度做排序]
     * @return
     */
    FilmVo getSoonFilms(boolean isLimit, int nums, int nowPage, int sortId, int sourceId, int yearId, int catId);

    /**
     * 获取经典影片
     * @return
     */
    FilmVo getClassicFilms(int nums, int nowPage, int sortId, int sourceId, int yearId, int catId);
    /*
        在正式项目中，推荐大家使用的做法
     */
//    // 获取热映影片
//    FilmVO getHotFilms(int nowPage,int nums ...);

    /**
     * 获取票房排行榜
     * @return
     */
    List<FilmInfo> getBoxRanking();

    /**
     * 获取人气排行榜
     * @return
     */
    List<FilmInfo> getExpectRanking();

    /**
     * 获取Top100
     * @return
     */
    List<FilmInfo> getTop();

    // ==== 获取影片条件接口

    /**
     * 分类条件
     * @return
     */
    List<CatVo> getCats();

    /**
     * 片源条件
     * @return
     */
    List<SourceVo> getSources();

    /**
     *  获取年代条件
     * @return
     */
    List<YearVo> getYears();

    /**
     * 根据影片ID或者名称获取影片信息
     * @param searchType
     * @param searchParam
     * @return
     */
    FilmDetailVo getFilmDetail(int searchType, String searchParam);

    /**
     * 获取影片描述信息
     * @param filmId
     * @return
     */
    FilmDescVo getFilmDesc(String filmId);

    /**
     * 获取图片信息
     * @param filmId
     * @return
     */
    ImgVo getImgs(String filmId);

    /**
     * 获取导演信息
     * @param filmId
     * @return
     */
    ActorVo getDectInfo(String filmId);

    /**
     * 获取演员信息
     * @param filmId
     * @return
     */
    List<ActorVo> getActors(String filmId);

}
