package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.film.vo.ActorVo;
import com.stylefeng.guns.rest.common.persistence.model.ActorT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 演员表 Mapper 接口
 * </p>
 *
 * @author zl
 * @since 2018-11-18
 */
public interface ActorTMapper extends BaseMapper<ActorT> {

	List<ActorVo> getActors(@Param("filmId") String filmId);
}
