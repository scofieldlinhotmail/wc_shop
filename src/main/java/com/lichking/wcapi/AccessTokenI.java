package com.lichking.wcapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import net.sf.json.JSONObject;

import com.lichking.pojo.wechat.AccessToken;

/**
 * 从微信服务器获取接口调用凭据ACESSTOKEN
 * 目前只使用了doget方法  dopost保留
 * @author LichKing
 *
 */
public class AccessTokenI {

	protected static final String APPID= "wxc80287bf18597edf";
	protected static final String APPSECRET= "25237dcb22448672ca668ae0a7a3c061";
	
	protected static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	
	
	
	/**
	 * 获取access token
	 * @return
	 * @throws IOException 
	 */
	public static AccessToken getAccessToken() throws IOException{
		AccessToken token = new AccessToken();
		
		File file = new File("/WeChat_token");
		//System.out.println(file.getAbsolutePath());
		if(!file.exists()){
			file.createNewFile();
			
			String url = ACCESS_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
			JSONObject jsonObject = HttpRequestI.doGetStr(url);
			if(jsonObject!=null){
				token.setToken(jsonObject.getString("access_token"));
				token.setExpiresIn(jsonObject.getInt("expires_in"));
			}
			
			FileOutputStream out = new FileOutputStream(file,true);
			StringBuffer sb = new StringBuffer();
			sb.append(token.getToken()+"\n");
			sb.append(new Date().getTime()+"\n");
			out.write(sb.toString().getBytes("UTF-8"));
			out.close();
		}else{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String temp1 = null,temp2 = null;
			temp1 = br.readLine();
			temp2 = br.readLine();
			long before = Long.valueOf(temp2);
			Date date = new Date();
			long now = date.getTime();
			if((now-before)/1000 > 7200){
				String url = ACCESS_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
				JSONObject jsonObject = HttpRequestI.doGetStr(url);
				if(jsonObject!=null){
					token.setToken(jsonObject.getString("access_token"));
					token.setExpiresIn(jsonObject.getInt("expires_in"));
				}
				
				FileOutputStream out = new FileOutputStream(file);
				StringBuffer sb = new StringBuffer();
				sb.append(token.getToken()+"\n");
				sb.append(new Date().getTime()+"\n");
				out.write(sb.toString().getBytes("UTF-8"));
				out.close();
				
			}else{
				int t = (int) (7200-(now-before)/1000);
				token.setToken(temp1);
				token.setExpiresIn(t);
			}
			br.close();
		}
		
		
		return token;
	}
	
	
}
