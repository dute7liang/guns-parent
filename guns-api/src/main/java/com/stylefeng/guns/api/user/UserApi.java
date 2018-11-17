package com.stylefeng.guns.api.user;

import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;

/**
 * @author: zl
 * @Date: 2018-11-17 16:03
 */
public interface UserApi {


	int login(String userName,String password);

	boolean register(UserModel userModel);

	boolean checkUserName(String userName);

	UserInfoModel getUserInfo(int id);

	UserInfoModel updateUserInfo(UserInfoModel userInfoModel);

}
