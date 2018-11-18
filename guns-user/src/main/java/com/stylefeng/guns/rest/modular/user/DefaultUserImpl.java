package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.user.UserApi;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.persistence.dao.UserTMapper;
import com.stylefeng.guns.rest.persistence.model.UserT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author: zl
 * @Date: 2018-11-17 16:24
 */
@Component
@Service(interfaceClass = UserApi.class,loadbalance = "roundrobin")
public class DefaultUserImpl implements UserApi {

	@Autowired
	private UserTMapper userTMapper;

	@Override
	public int login(String userName, String password) {
		UserT userT = new UserT();
		userT.setUserName(userName);
		UserT result = userTMapper.selectOne(userT);
		if(result != null && result.getUuid() > 0){
			String encrypt = MD5Util.encrypt(password);
			if(encrypt.equals(result.getUserPwd())){
				return result.getUuid();
			}
		}
		return 0;
	}

	@Override
	public boolean register(UserModel userModel) {
		UserT userT = new UserT();
		userT.setUserName(userModel.getUsername());
		userT.setUserPwd(MD5Util.encrypt(userModel.getPassword()));
		userT.setEmail(userModel.getEmail());
		userT.setAddress(userModel.getAddress());
		userT.setUserPhone(userModel.getPhone());
		Integer insert = userTMapper.insert(userT);
		if(insert > 0){
			return true;
		}
		return false;
	}

	@Override
	public boolean checkUserName(String userName) {
		EntityWrapper<UserT> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("user_name", userName);
		Integer integer = userTMapper.selectCount(entityWrapper);
		if(integer != null && integer == 0){
			return true;
		}
		return false;
	}

	private UserInfoModel do2UserInfo(UserT userT){
		if(userT == null ) return null;
		UserInfoModel userInfoModel = new UserInfoModel();

		userInfoModel.setUuid(userT.getUuid());
		userInfoModel.setHeadAddress(userT.getHeadUrl());
		userInfoModel.setPhone(userT.getUserPhone());
		userInfoModel.setUpdateTime(userT.getUpdateTime().getTime());
		userInfoModel.setEmail(userT.getEmail());
		userInfoModel.setUsername(userT.getUserName());
		userInfoModel.setNickname(userT.getNickName());
		userInfoModel.setLifeState(""+userT.getLifeState());
		userInfoModel.setBirthday(userT.getBirthday());
		userInfoModel.setAddress(userT.getAddress());
		userInfoModel.setSex(userT.getUserSex());
		userInfoModel.setBeginTime(userT.getBeginTime().getTime());
		userInfoModel.setBiography(userT.getBiography());

		return userInfoModel;
	}

	@Override
	public UserInfoModel getUserInfo(int id) {
		UserT userT = userTMapper.selectById(id);
		return do2UserInfo(userT);
	}

	@Override
	public UserInfoModel updateUserInfo(UserInfoModel userInfoModel) {
		// 将传入的参数转换为DO
		UserT userT = new UserT();
		userT.setUuid(userInfoModel.getUuid());
		userT.setNickName(userInfoModel.getNickname());
		userT.setLifeState(Integer.parseInt(userInfoModel.getLifeState()));
		userT.setBirthday(userInfoModel.getBirthday());
		userT.setBiography(userInfoModel.getBiography());
		if(userInfoModel.getBeginTime() > 0){
			userT.setBeginTime(new Date(userInfoModel.getBeginTime()));
		}
		userT.setHeadUrl(userInfoModel.getHeadAddress());
		userT.setEmail(userInfoModel.getEmail());
		userT.setAddress(userInfoModel.getAddress());
		userT.setUserPhone(userInfoModel.getPhone());
		userT.setUserSex(userInfoModel.getSex());
		userT.setUpdateTime(new Date());
		Integer integer = userTMapper.updateById(userT);
		if(integer != null && integer > 0){
			return getUserInfo(userInfoModel.getUuid());
		}
		return null;
	}
}
