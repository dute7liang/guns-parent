package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVo;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVo;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderVo;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.rest.common.persistence.dao.OrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.OrderT;
import com.stylefeng.guns.rest.common.util.FtpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: zl
 * @Date: 2018-11-19 17:34
 */
@Slf4j
@Component
@Service
public class DefaultOrderServiceImpl implements OrderServiceAPI {

	@Autowired
	private OrderTMapper orderTMapper;

	@Autowired
	private FtpUtil ftpUtil;

	@Autowired
	private CinemaServiceAPI cinemaServiceAPI;

	@Override
	public boolean isTrueSeats(String fieldId, String seats) {
		// 根据FieldId找到对应的座位位置图
		String seatPath = orderTMapper.getSeatsByFieldId(fieldId);

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

	@Override
	public boolean isNotSoldSeats(String fieldId, String seats) {
		EntityWrapper entityWrapper = new EntityWrapper();
		entityWrapper.eq("field_id",fieldId);
		List<OrderT> list = orderTMapper.selectList(entityWrapper);
		String[] seatArrs = seats.split(",");
		// 有任何一个编号匹配上，则直接返回失败
		for(OrderT orderT : list){
			String[] ids = orderT.getSeatsIds().split(",");
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

	@Override
	public OrderVo saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {
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

		OrderT orderT = new OrderT();
		orderT.setUuid(uuid);
		orderT.setSeatsName(seatsName);
		orderT.setSeatsIds(soldSeats);
		orderT.setOrderUser(userId);
		orderT.setOrderPrice(totalPrice);
		orderT.setFilmPrice(filmPrice);
		orderT.setFilmId(filmId);
		orderT.setFieldId(fieldId);
		orderT.setCinemaId(cinemaId);

		Integer insert = orderTMapper.insert(orderT);
		if(insert>0){
			// 返回查询结果
			OrderVo orderVO = orderTMapper.getOrderInfoById(uuid);
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
	public Page<OrderVo> getOrderByUserId(Integer userId, Page<OrderVo> page) {
		Page<OrderVo> result = new Page<>();
		if(userId == null){
			log.error("订单查询业务失败，用户编号未传入");
			return null;
		}else{
			List<OrderVo> ordersByUserId = orderTMapper.getOrdersByUserId(userId,page);
			if(ordersByUserId==null && ordersByUserId.size()==0){
				result.setTotal(0);
				result.setRecords(new ArrayList<>());
				return result;
			}else{
				// 获取订单总数
				EntityWrapper<OrderT> entityWrapper = new EntityWrapper<>();
				entityWrapper.eq("order_user",userId);
				Integer counts = orderTMapper.selectCount(entityWrapper);
				// 将结果放入Page
				result.setTotal(counts);
				result.setRecords(ordersByUserId);

				return result;
			}
		}
	}

	@Override
	public String getSoldSeatsByFieldId(Integer fieldId) {
		if(fieldId == null){
			log.error("查询已售座位错误，未传入任何场次编号");
			return "";
		}else{
			return orderTMapper.getSoldSeatsByFieldId(fieldId);
		}
	}
}
