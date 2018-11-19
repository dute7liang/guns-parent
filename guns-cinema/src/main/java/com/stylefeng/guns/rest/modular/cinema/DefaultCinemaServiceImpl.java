package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = CinemaServiceAPI.class,executes = 10)
public class DefaultCinemaServiceImpl implements CinemaServiceAPI {

    @Autowired
    private CinemaTMapper cinemaTMapper;
    @Autowired
    private AreaDictTMapper areaDictTMapper;
    @Autowired
    private BrandDictTMapper brandDictTMapper;
    @Autowired
    private HallDictTMapper hallDictTMapper;
    @Autowired
    private HallFilmInfoTMapper hallFilmInfoTMapper;
    @Autowired
    private FieldTMapper fieldTMapper;


    //1、根据CinemaQueryVO，查询影院列表
    @Override
    public Page<CinemaVo> getCinemas(CinemaQueryVo cinemaQueryVO){
        // 业务实体集合
        List<CinemaVo> cinemas = new ArrayList<>();

        Page<CinemaT> page = new Page<>(cinemaQueryVO.getNowPage(),cinemaQueryVO.getPageSize());
        // 判断是否传入查询条件 -> brandId,distId,hallType 是否==99
        EntityWrapper<CinemaT> entityWrapper = new EntityWrapper<>();
        if(cinemaQueryVO.getBrandId() != 99){
            entityWrapper.eq("brand_id",cinemaQueryVO.getBrandId());
        }
        if(cinemaQueryVO.getDistrictId() != 99){
            entityWrapper.eq("area_id",cinemaQueryVO.getDistrictId());
        }
        if(cinemaQueryVO.getHallType() != 99){  // %#3#%
            entityWrapper.like("hall_ids","%#+"+cinemaQueryVO.getHallType()+"+#%");
        }

        // 将数据实体转换为业务实体
        List<CinemaT> cinemaTList = cinemaTMapper.selectPage(page, entityWrapper);
        for(CinemaT cinemaT : cinemaTList){
            CinemaVo cinemaVO = new CinemaVo();

            cinemaVO.setUuid(cinemaT.getUuid()+"");
            cinemaVO.setMinimumPrice(cinemaT.getMinimumPrice()+"");
            cinemaVO.setCinemaName(cinemaT.getCinemaName());
            cinemaVO.setAddress(cinemaT.getCinemaAddress());

            cinemas.add(cinemaVO);
        }

        // 根据条件，判断影院列表总数
        long counts = cinemaTMapper.selectCount(entityWrapper);

        // 组织返回值对象
        Page<CinemaVo> result = new Page<>();
        result.setRecords(cinemas);
        result.setSize(cinemaQueryVO.getPageSize());
        result.setTotal(counts);

        return result;
    }
    //2、根据条件获取品牌列表[除了就99以外，其他的数字为isActive]
    @Override
    public List<BrandVo> getBrands(int brandId){
        boolean flag = false;
        List<BrandVo> brandVOS = new ArrayList<>();
        // 判断brandId是否存在
        BrandDictT crandDictT = brandDictTMapper.selectById(brandId);
        // 判断brandId 是否等于 99
        if(brandId == 99 || crandDictT==null || crandDictT.getUuid() == null){
            flag = true;
        }
        // 查询所有列表
        List<BrandDictT> brandDictTList = brandDictTMapper.selectList(null);
        // 判断flag如果为true，则将99置为isActive
        for(BrandDictT brand : brandDictTList){
            BrandVo brandVO = new BrandVo();
            brandVO.setBrandName(brand.getShowName());
            brandVO.setBrandId(brand.getUuid()+"");
            // 如果flag为true，则需要99，如为false，则匹配上的内容为active
            if(flag){
                if(brand.getUuid() == 99){
                    brandVO.setActive(true);
                }
            }else{
                if(brand.getUuid() == brandId){
                    brandVO.setActive(true);
                }
            }

            brandVOS.add(brandVO);
        }

        return brandVOS;
    }
    //3、获取行政区域列表
    @Override
    public List<AreaVo> getAreas(int areaId){
        boolean flag = false;
        List<AreaVo> areaVOS = new ArrayList<>();
        // 判断brandId是否存在
        AreaDictT areaDictT = areaDictTMapper.selectById(areaId);
        // 判断brandId 是否等于 99
        if(areaId == 99 || areaDictT==null || areaDictT.getUuid() == null){
            flag = true;
        }
        // 查询所有列表
        List<AreaDictT> areaDictTSList = areaDictTMapper.selectList(null);
        // 判断flag如果为true，则将99置为isActive
        for(AreaDictT area : areaDictTSList){
            AreaVo areaVO = new AreaVo();
            areaVO.setAreaName(area.getShowName());
            areaVO.setAreaId(area.getUuid()+"");
            // 如果flag为true，则需要99，如为false，则匹配上的内容为active
            if(flag){
                if(area.getUuid() == 99){
                    areaVO.setActive(true);
                }
            }else{
                if(area.getUuid() == areaId){
                    areaVO.setActive(true);
                }
            }

            areaVOS.add(areaVO);
        }

        return areaVOS;
    }
    //4、获取影厅类型列表
    @Override
    public List<HallTypeVo> getHallTypes(int hallType){
        boolean flag = false;
        List<HallTypeVo> hallTypeVOS = new ArrayList<>();
        // 判断brandId是否存在
        HallDictT hallDictT = hallDictTMapper.selectById(hallType);
        // 判断brandId 是否等于 99
        if(hallType == 99 || hallDictT==null || hallDictT.getUuid() == null){
            flag = true;
        }
        // 查询所有列表
        List<HallDictT> hallDictTList = hallDictTMapper.selectList(null);
        // 判断flag如果为true，则将99置为isActive
        for(HallDictT hall : hallDictTList){
            HallTypeVo hallTypeVO = new HallTypeVo();
            hallTypeVO.setHalltypeName(hall.getShowName());
            hallTypeVO.setHalltypeId(hall.getUuid()+"");
            // 如果flag为true，则需要99，如为false，则匹配上的内容为active
            if(flag){
                if(hall.getUuid() == 99){
                    hallTypeVO.setActive(true);
                }
            }else{
                if(hall.getUuid() == hallType){
                    hallTypeVO.setActive(true);
                }
            }

            hallTypeVOS.add(hallTypeVO);
        }

        return hallTypeVOS;
    }
    //5、根据影院编号，获取影院信息
    @Override
    public CinemaInfoVo getCinemaInfoById(int cinemaId){

        CinemaT cinemaT = cinemaTMapper.selectById(cinemaId);
        if(cinemaT == null){
            return new CinemaInfoVo();
        }
		CinemaInfoVo cinemaInfoVO = new CinemaInfoVo();
        cinemaInfoVO.setImgUrl(cinemaT.getImgAddress());
        cinemaInfoVO.setCinemaPhone(cinemaT.getCinemaPhone());
        cinemaInfoVO.setCinemaName(cinemaT.getCinemaName());
        cinemaInfoVO.setCinemaId(cinemaT.getUuid()+"");
        cinemaInfoVO.setCinemaId(cinemaT.getCinemaAddress());

        return cinemaInfoVO;
    }
    //6、获取所有电影的信息和对应的放映场次信息，根据影院编号
    @Override
    public List<FilmInfoVo> getFilmInfoByCinemaId(int cinemaId){

        List<FilmInfoVo> filmInfos = fieldTMapper.getFilmInfos(cinemaId);

        return filmInfos;
    }
    //7、根据放映场次ID获取放映信息
    @Override
    public HallInfoVo getFilmFieldInfo(int fieldId){

		HallInfoVo hallInfoVO = fieldTMapper.getHallInfo(fieldId);

        return hallInfoVO;
    }
    //8、根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
    @Override
    public FilmInfoVo getFilmInfoByFieldId(int fieldId){

        FilmInfoVo filmInfoVO = fieldTMapper.getFilmInfoById(fieldId);

        return filmInfoVO;
    }

    @Override
    public OrderQueryVo getOrderNeeds(int fieldId) {
        OrderQueryVo orderQueryVo = new OrderQueryVo();

        FieldT fieldT = fieldTMapper.selectById(fieldId);

        orderQueryVo.setCinemaId(fieldT.getCinemaId() + "");
        orderQueryVo.setFilmPrice(fieldT.getPrice() + "");

        return null;
    }

}
