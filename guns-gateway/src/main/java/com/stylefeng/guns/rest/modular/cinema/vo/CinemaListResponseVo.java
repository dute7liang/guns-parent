package com.stylefeng.guns.rest.modular.cinema.vo;

import com.stylefeng.guns.api.cinema.vo.CinemaVo;
import lombok.Data;

import java.util.List;

@Data
public class CinemaListResponseVo {

    private List<CinemaVo> cinemas;

}
