package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.api.film.FilmAsyncServiceApi;
import com.stylefeng.guns.api.film.vo.ActorVo;
import com.stylefeng.guns.api.film.vo.FilmDescVo;
import com.stylefeng.guns.api.film.vo.ImgVo;
import com.stylefeng.guns.rest.common.persistence.dao.ActorTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.FilmInfoTMapper;
import com.stylefeng.guns.rest.common.persistence.model.ActorT;
import com.stylefeng.guns.rest.common.persistence.model.FilmInfoT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Service
public class DefaultFilmAsyncServiceImpl implements FilmAsyncServiceApi {

    @Autowired
    private FilmInfoTMapper filmInfoTMapper;

    @Autowired
    private ActorTMapper actorTMapper;

    private FilmInfoT getFilmInfo(String filmId){

        FilmInfoT filmInfoT = new FilmInfoT();
        filmInfoT.setFilmId(filmId);

        filmInfoT = filmInfoTMapper.selectOne(filmInfoT);

        return filmInfoT;
    }

    @Override
    public FilmDescVo getFilmDesc(String filmId) {

        FilmInfoT filmInfoT = getFilmInfo(filmId);

        FilmDescVo filmDescVO = new FilmDescVo();
        filmDescVO.setBiography(filmInfoT.getBiography());
        filmDescVO.setFilmId(filmId);

        return filmDescVO;
    }

    @Override
    public ImgVo getImgs(String filmId) {
        FilmInfoT moocFilmInfoT = getFilmInfo(filmId);
        // 图片地址是五个以逗号为分隔的链接URL
        String filmImgStr = moocFilmInfoT.getFilmImgs();
        String[] filmImgs = filmImgStr.split(",");

        ImgVo imgVO = new ImgVo();
        imgVO.setMainImg(filmImgs[0]);
        imgVO.setImg01(filmImgs[1]);
        imgVO.setImg02(filmImgs[2]);
        imgVO.setImg03(filmImgs[3]);
        imgVO.setImg04(filmImgs[4]);

        return imgVO;
    }

    @Override
    public ActorVo getDectInfo(String filmId) {
        FilmInfoT filmInfoT = getFilmInfo(filmId);

        // 获取导演编号
        Integer directId = filmInfoT.getDirectorId();

        ActorT actorT = actorTMapper.selectById(directId);

        ActorVo actorVO = new ActorVo();
        actorVO.setImgAddress(actorT.getActorImg());
        actorVO.setDirectorName(actorT.getActorName());

        return actorVO;
    }

    @Override
    public List<ActorVo> getActors(String filmId) {
//		List<ActorVo> actors = actorTMapper.getActors(filmId);
//		return actors;
        return null;
    }


}
