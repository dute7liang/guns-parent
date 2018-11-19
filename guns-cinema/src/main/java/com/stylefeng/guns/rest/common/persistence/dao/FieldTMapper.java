package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.cinema.vo.FilmInfoVo;
import com.stylefeng.guns.api.cinema.vo.HallInfoVo;
import com.stylefeng.guns.rest.common.persistence.model.FieldT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 放映场次表 Mapper 接口
 * </p>
 *
 * @author zl
 * @since 2018-11-19
 */
public interface FieldTMapper extends BaseMapper<FieldT> {

	List<FilmInfoVo> getFilmInfos(@Param("cinemaId") int cinemaId);

	HallInfoVo getHallInfo(@Param("fieldId") int fieldId);

	FilmInfoVo getFilmInfoById(@Param("fieldId") int fieldId);
}
