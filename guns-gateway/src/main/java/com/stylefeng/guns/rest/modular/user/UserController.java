package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserApi;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zl
 * @Date: 2018/11/18 19:39
 */
@RequestMapping(value = "user")
@RestController
public class UserController {

	@Reference(check = false)
	private UserApi userApi;

	@RequestMapping(value="register",method = RequestMethod.POST)
	public ResponseVo register(UserModel userModel){
		if(userModel.getUsername() == null || userModel.getUsername().trim().length()==0){
			return ResponseVo.serviceFail("用户名不能为空");
		}
		if(userModel.getPassword() == null || userModel.getPassword().trim().length()==0){
			return ResponseVo.serviceFail("密码不能为空");
		}

		boolean isSuccess = userApi.register(userModel);
		if(isSuccess){
			return ResponseVo.success("注册成功");
		}else{
			return ResponseVo.serviceFail("注册失败");
		}
	}


	@RequestMapping(value="check",method = RequestMethod.POST)
	public ResponseVo check(String username) {
		if (username != null && username.trim().length() > 0) {
			// 当返回true的时候，表示用户名可用
			boolean exists = userApi.checkUserName(username);
			if (!exists) {
				return ResponseVo.success("用户名不存在");
			} else {
				return ResponseVo.serviceFail("用户名已存在");
			}

		} else {
			return ResponseVo.serviceFail("用户名不能为空");
		}
	}

	@RequestMapping(value="logout",method = RequestMethod.GET)
	public ResponseVo logout(){
        /*
            应用：
                1、前端存储JWT 【七天】 ： JWT的刷新
                2、服务器端会存储活动用户信息【30分钟】
                3、JWT里的userId为key，查找活跃用户
            退出：
                1、前端删除掉JWT
                2、后端服务器删除活跃用户缓存
            现状：
                1、前端删除掉JWT
         */
		return ResponseVo.success("用户退出成功");
	}

	@RequestMapping(value="getUserInfo",method = RequestMethod.GET)
	public ResponseVo getUserInfo(){
		// 获取当前登陆用户
		String userId = CurrentUser.getCurrentUser();
		if(userId != null && userId.trim().length()>0){
			// 将用户ID传入后端进行查询
			int uuid = Integer.parseInt(userId);
			UserInfoModel userInfo = userApi.getUserInfo(uuid);
			if(userInfo!=null){
				return ResponseVo.success(userInfo);
			}else{
				return ResponseVo.appFail("用户信息查询失败");
			}
		}else{
			return ResponseVo.serviceFail("用户未登陆");
		}
	}


	/**
	 * 修改用户信息，只能修改自己的用户信息
	 * @param userInfoModel
	 * @return
	 */
	@RequestMapping(value="updateUserInfo",method = RequestMethod.POST)
	public ResponseVo updateUserInfo(UserInfoModel userInfoModel){
		// 获取当前登陆用户
		String userId = CurrentUser.getCurrentUser();
		if(userId != null && userId.trim().length()>0){
			// 将用户ID传入后端进行查询
			int uuid = Integer.parseInt(userId);
			// 判断当前登陆人员的ID与修改的结果ID是否一致
			if(uuid != userInfoModel.getUuid()){
				return ResponseVo.serviceFail("请修改您个人的信息");
			}

			UserInfoModel userInfo = userApi.updateUserInfo(userInfoModel);
			if(userInfo!=null){
				return ResponseVo.success(userInfo);
			}else{
				return ResponseVo.appFail("用户信息修改失败");
			}
		}else{
			return ResponseVo.serviceFail("用户未登陆");
		}
	}

}
