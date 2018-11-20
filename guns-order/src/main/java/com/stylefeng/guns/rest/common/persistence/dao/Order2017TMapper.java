package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderVo;
import com.stylefeng.guns.rest.common.persistence.model.Order2017T;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author zl
 * @since 2018-11-19
 */
public interface Order2017TMapper extends BaseMapper<Order2017T> {

	String getSeatsByFieldId(@Param("fieldId") String fieldId);

	OrderVo getOrderInfoById(@Param("orderId") String orderId);

	List<OrderVo> getOrdersByUserId(@Param("userId")Integer userId, Page<OrderVo> page);

	String getSoldSeatsByFieldId(@Param("fieldId")Integer fieldId);
}
