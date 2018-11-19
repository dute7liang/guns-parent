package com.stylefeng.guns.rest.common.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author: zl
 * @Date: 2018-11-19 17:42
 */
@Slf4j
@Data
@Configurable
@ConfigurationProperties(prefix = FtpUtil.FTP_PREFIX)
public class FtpUtil {

	public static final String FTP_PREFIX = "ftp";

	private String hostName = "192.168.74.43";
	private Integer port = 2100;
	private String userName = "ftp";
	private String password = "ftp";

	private FTPClient ftpClient;

	private void initFtpClient(){
		try {
			ftpClient = new FTPClient();
			ftpClient.setControlEncoding("UTF-8");
			ftpClient.connect(hostName, port);
			ftpClient.login(userName,password);
		} catch (Exception e){
			log.error("ftp初始化失败！", e);
		}
	}

	// 输入一个路径，	然后路径里面的文件转换成字符串给我
	public String getFileStrByAddress(String fileAddress){
		initFtpClient();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(
					new InputStreamReader(ftpClient.retrieveFileStream(fileAddress)));
			StringBuilder sb = new StringBuilder();
			while (true){
				String s = bufferedReader.readLine();
				if(s == null){
					break;
				}
				sb.append(s);
			}
			return sb.toString();
		} catch (Exception e){
			log.error("获取文件信息失败", e);
		} finally {
			try {
				bufferedReader.close();
			}catch (IOException e){

			}
		}
		return null;
	}

	public static void main(String[] args) {
		FtpUtil ftpUtil = new FtpUtil();
		ftpUtil.initFtpClient();
		String fileStrByAddress = ftpUtil.getFileStrByAddress("22222.txt");
		System.out.println(fileStrByAddress);
	}

}
