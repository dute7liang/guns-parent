package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.api.user.UserApi;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import org.springframework.stereotype.Component;

/**
 * @author: zl
 * @Date: 2018-11-17 16:24
 */
@Component
@Service
public class UserImpl implements UserApi {

	@Override
	public int login(String userName, String password) {
		System.out.println("userName:"+userName+":password:"+password);
		return 0;
	}

	@Override
	public boolean register(UserModel userModel) {
		return false;
	}

	@Override
	public boolean checkUserName(String userName) {
		return false;
	}

	@Override
	public UserInfoModel getUserInfo(int id) {
		return null;
	}

	@Override
	public UserInfoModel updateUserInfo(UserInfoModel userInfoModel) {
		return null;
	}
}
