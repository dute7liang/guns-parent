package com.stylefeng.guns.api.order;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderVo;

public interface OrderServiceAPI {

    /**
     * 验证售出的票是否为真
     * @param fieldId
     * @param seats
     * @return
     */
    boolean isTrueSeats(String fieldId, String seats);

    /**
     * 已经销售的座位里，有没有这些座位
     * @param fieldId
     * @param seats
     * @return
     */
    boolean isNotSoldSeats(String fieldId, String seats);

    /**
     * 创建订单信息
     * @param fieldId
     * @param soldSeats
     * @param seatsName
     * @param userId
     * @return
     */
    OrderVo saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId);

    /**
     * 使用当前登陆人获取已经购买的订单
     * @param userId
     * @param page
     * @return
     */
    Page<OrderVo> getOrderByUserId(Integer userId, Page<OrderVo> page);

    /**
     * 根据FieldId 获取所有已经销售的座位编号
     * @param fieldId
     * @return
     */
    String getSoldSeatsByFieldId(Integer fieldId);


    OrderVo getOrderInfoById(String orderId);

    boolean paySuccess(String orderId);

    boolean payFail(String orderId);

}
