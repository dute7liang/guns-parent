package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVo;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVo;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderVo;
import com.stylefeng.guns.core.util.FtpUtil;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.rest.common.persistence.dao.Order2018TMapper;
import com.stylefeng.guns.rest.common.persistence.model.Order2018T;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@Service(interfaceClass = OrderServiceAPI.class,group = "order2018")
public class OrderServiceImpl2018 implements OrderServiceAPI {

    @Autowired
    private Order2018TMapper order2018TMapper;

    @Reference(interfaceClass = CinemaServiceAPI.class,check = false)
    private CinemaServiceAPI cinemaServiceAPI;

    @Autowired
    private FtpUtil ftpUtil;

    // 验证是否为真实的座位编号
    @Override
    public boolean isTrueSeats(String fieldId, String seats) {
        // 根据FieldId找到对应的座位位置图
        String seatPath = order2018TMapper.getSeatsByFieldId(fieldId);

        // 读取位置图，判断seats是否为真
        String fileStrByAddress = ftpUtil.getFileStrByAddress(seatPath);

        // 将fileStrByAddress转换为JSON对象
        JSONObject jsonObject = JSONObject.parseObject(fileStrByAddress);
        // seats=1,2,3   ids="1,3,4,5,6,7,88"
        String ids = jsonObject.get("ids").toString();

        // 每一次匹配上的，都给isTrue+1
        String[] seatArrs = seats.split(",");
        String[] idArrs = ids.split(",");
        int isTrue = 0;
        for(String id : idArrs){
            for(String seat : seatArrs){
                if(seat.equalsIgnoreCase(id)){
                    isTrue++;
                }
            }
        }

        // 如果匹配上的数量与已售座位数一致，则表示全都匹配上了
        if(seatArrs.length == isTrue){
            return true;
        }else{
            return false;
        }
    }

    // 判断是否为已售座位
    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {

        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("field_id",fieldId);

        List<Order2018T> list = order2018TMapper.selectList(entityWrapper);
        String[] seatArrs = seats.split(",");
        // 有任何一个编号匹配上，则直接返回失败
        for(Order2018T moocOrderT : list){
            String[] ids = moocOrderT.getSeatsIds().split(",");
            for(String id : ids){
                for(String seat : seatArrs){
                    if(id.equalsIgnoreCase(seat)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // 创建新的订单
    @Override
    public OrderVo saveOrderInfo(
            Integer fieldId, String soldSeats, String seatsName, Integer userId) {

        // 编号
        String uuid = UUIDUtil.genUuid();

        // 影片信息
        FilmInfoVo filmInfoVO = cinemaServiceAPI.getFilmInfoByFieldId(fieldId);
        Integer filmId = Integer.parseInt(filmInfoVO.getFilmId());

        // 获取影院信息
        OrderQueryVo orderQueryVO = cinemaServiceAPI.getOrderNeeds(fieldId);
        Integer cinemaId = Integer.parseInt(orderQueryVO.getCinemaId());
        double filmPrice = Double.parseDouble(orderQueryVO.getFilmPrice());

        // 求订单总金额  // 1,2,3,4,5
        int solds = soldSeats.split(",").length;
        double totalPrice = getTotalPrice(solds,filmPrice);

        Order2018T order2018T = new Order2018T();
        order2018T.setUuid(uuid);
        order2018T.setSeatsName(seatsName);
        order2018T.setSeatsIds(soldSeats);
        order2018T.setOrderUser(userId);
        order2018T.setOrderPrice(totalPrice);
        order2018T.setFilmPrice(filmPrice);
        order2018T.setFilmId(filmId);
        order2018T.setFieldId(fieldId);
        order2018T.setCinemaId(cinemaId);

        Integer insert = order2018TMapper.insert(order2018T);
        if(insert>0){
            // 返回查询结果
            OrderVo orderVO = order2018TMapper.getOrderInfoById(uuid);
            if(orderVO == null || orderVO.getOrderId() == null){
                log.error("订单信息查询失败,订单编号为{}",uuid);
                return null;
            }else {
                return orderVO;
            }
        }else{
            // 插入出错
            log.error("订单插入失败");
            return null;
        }
    }

    private static double getTotalPrice(int solds,double filmPrice){
        BigDecimal soldsDeci = new BigDecimal(solds);
        BigDecimal filmPriceDeci = new BigDecimal(filmPrice);

        BigDecimal result = soldsDeci.multiply(filmPriceDeci);

        // 四舍五入，取小数点后两位
        BigDecimal bigDecimal = result.setScale(2, RoundingMode.HALF_UP);

        return bigDecimal.doubleValue();
    }


    @Override
    public Page<OrderVo> getOrderByUserId(Integer userId,Page<OrderVo> page) {
        Page<OrderVo> result = new Page<>();
        if(userId == null){
            log.error("订单查询业务失败，用户编号未传入");
            return null;
        }else{
            List<OrderVo> ordersByUserId = order2018TMapper.getOrdersByUserId(userId,page);
            if(ordersByUserId==null && ordersByUserId.size()==0){
                result.setTotal(0);
                result.setRecords(new ArrayList<>());
                return result;
            }else{
                // 获取订单总数
                EntityWrapper<Order2018T> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("order_user",userId);
                Integer counts = order2018TMapper.selectCount(entityWrapper);
                // 将结果放入Page
                result.setTotal(counts);
                result.setRecords(ordersByUserId);

                return result;
            }
        }
    }

    // 根据放映查询，获取所有的已售座位
    /*

        1  1,2,3,4
        1  5,6,7

     */
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        if(fieldId == null){
            log.error("查询已售座位错误，未传入任何场次编号");
            return "";
        }else{
            String soldSeatsByFieldId = order2018TMapper.getSoldSeatsByFieldId(fieldId);
            return soldSeatsByFieldId;
        }
    }

    @Override
    public OrderVo getOrderInfoById(String orderId) {

        OrderVo orderInfoById = order2018TMapper.getOrderInfoById(orderId);

        return orderInfoById;
    }


    @Override
    public boolean paySuccess(String orderId) {

//        String userId = RpcContext.getContext().getAttachment("userId");
//        System.out.println("userId="+userId);

        Order2018T orderT = new Order2018T();
        orderT.setUuid(orderId);
        orderT.setOrderStatus(1);
        Integer integer = order2018TMapper.updateById(orderT);
        if(integer>=1){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean payFail(String orderId) {
        Order2018T orderT = new Order2018T();
        orderT.setUuid(orderId);
        orderT.setOrderStatus(2);

        Integer integer = order2018TMapper.updateById(orderT);
        if(integer>=1){
            return true;
        }else{
            return false;
        }
    }
}
