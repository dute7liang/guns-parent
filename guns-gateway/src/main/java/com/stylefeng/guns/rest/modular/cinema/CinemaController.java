package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaConditionResponseVo;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldResponseVo;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldsResponseVo;
import com.stylefeng.guns.rest.modular.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/cinema/")
public class CinemaController {

    @Reference(interfaceClass = CinemaServiceAPI.class,
                    connections = 10,cache = "lru",check = false)
    private CinemaServiceAPI cinemaServiceAPI;

    @Reference(interfaceClass = OrderServiceAPI.class,check = false)
    private OrderServiceAPI orderServiceAPI;

    private static final String IMG_PRE = "http://img.meetingshop.cn/";


    @RequestMapping(value = "getCinemas")
    public ResponseVo getCinemas(CinemaQueryVo cinemaQueryVO){
        try{
            // 按照五个条件进行筛选
            Page<CinemaVo> cinemas = cinemaServiceAPI.getCinemas(cinemaQueryVO);
            // 判断是否有满足条件的影院
            if(cinemas.getRecords() == null || cinemas.getRecords().size()==0){
                return ResponseVo.success("没有影院可查");
            }else{
                return ResponseVo.success(cinemas.getCurrent(),(int)cinemas.getPages(),"",cinemas.getRecords());
            }

        }catch (Exception e){
            // 如果出现异常，应该如何处理
            log.error("获取影院列表异常",e);
            return ResponseVo.serviceFail("查询影院列表失败");
        }
    }

    // 获取影院的查询条件
    /*
        1、热点数据 -> 放缓存
        2、banner
     */
    @RequestMapping(value = "getCondition")
    public ResponseVo getCondition(CinemaQueryVo cinemaQueryVO){
        try{
            // 获取三个集合，然后封装成一个对象返回即可
            List<BrandVo> brands = cinemaServiceAPI.getBrands(cinemaQueryVO.getBrandId());
            List<AreaVo> areas = cinemaServiceAPI.getAreas(cinemaQueryVO.getDistrictId());
            List<HallTypeVo> hallTypes = cinemaServiceAPI.getHallTypes(cinemaQueryVO.getHallType());

            CinemaConditionResponseVo cinemaConditionResponseVO = new CinemaConditionResponseVo();
            cinemaConditionResponseVO.setAreaList(areas);
            cinemaConditionResponseVO.setBrandList(brands);
            cinemaConditionResponseVO.setHalltypeList(hallTypes);

            return ResponseVo.success(cinemaConditionResponseVO);
        }catch (Exception e) {
            log.error("获取条件列表失败", e);
            return ResponseVo.serviceFail("获取影院查询条件失败");
        }
    }


    @RequestMapping(value = "getFields")
    public ResponseVo getFields(Integer cinemaId){
        try{

            CinemaInfoVo cinemaInfoById = cinemaServiceAPI.getCinemaInfoById(cinemaId);

            List<FilmInfoVo> filmInfoByCinemaId = cinemaServiceAPI.getFilmInfoByCinemaId(cinemaId);

            CinemaFieldsResponseVo cinemaFieldResponseVO = new CinemaFieldsResponseVo();
            cinemaFieldResponseVO.setCinemaInfo(cinemaInfoById);
            cinemaFieldResponseVO.setFilmList(filmInfoByCinemaId);

            return ResponseVo.success(IMG_PRE,cinemaFieldResponseVO);
        }catch (Exception e){
            log.error("获取播放场次失败",e);
            return ResponseVo.serviceFail("获取播放场次失败");
        }
    }

    @RequestMapping(value = "getFieldInfo",method = RequestMethod.POST)
    public ResponseVo getFieldInfo(Integer cinemaId,Integer fieldId){
        try{

            CinemaInfoVo cinemaInfoById = cinemaServiceAPI.getCinemaInfoById(cinemaId);
            FilmInfoVo filmInfoByFieldId = cinemaServiceAPI.getFilmInfoByFieldId(fieldId);
            HallInfoVo filmFieldInfo = cinemaServiceAPI.getFilmFieldInfo(fieldId);

            // 造几个销售的假数据，后续会对接订单接口
//            filmFieldInfo.setSoldSeats("1,2,3");
            filmFieldInfo.setSoldSeats(orderServiceAPI.getSoldSeatsByFieldId(fieldId));

            CinemaFieldResponseVo cinemaFieldResponseVO = new CinemaFieldResponseVo();
            cinemaFieldResponseVO.setCinemaInfo(cinemaInfoById);
            cinemaFieldResponseVO.setFilmInfo(filmInfoByFieldId);
            cinemaFieldResponseVO.setHallInfo(filmFieldInfo);

            return ResponseVo.success(IMG_PRE,cinemaFieldResponseVO);
        }catch (Exception e){
            log.error("获取选座信息失败",e);
            return ResponseVo.serviceFail("获取选座信息失败");
        }
    }

}
