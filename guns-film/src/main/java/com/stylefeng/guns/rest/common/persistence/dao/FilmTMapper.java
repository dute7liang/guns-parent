package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.film.vo.FilmDetailVo;
import com.stylefeng.guns.rest.common.persistence.model.FilmT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 影片主表 Mapper 接口
 * </p>
 *
 * @author zl
 * @since 2018-11-18
 */
public interface FilmTMapper extends BaseMapper<FilmT> {


	FilmDetailVo getFilmDetailByName(@Param("filmName") String filmName);



	FilmDetailVo getFilmDetailById(@Param("id") String id);


}
