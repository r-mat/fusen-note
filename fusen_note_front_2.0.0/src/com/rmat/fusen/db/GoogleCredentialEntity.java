package com.rmat.fusen.db;

import java.util.Date;

public class GoogleCredentialEntity {

	private String userid;
	private String access_token;
	private String refresh_token;
	private Date upd_date;
	
	
	public GoogleCredentialEntity(String userid, String access_token, String refresh_token,
			Date upd_date) {
		super();
		this.userid = userid;
		this.access_token = access_token;
		this.refresh_token = refresh_token;
		this.upd_date = upd_date;
	}
	
	public String getUserId() {
		return userid;
	}
	public void setUserId(String userid) {
		this.userid = userid;
	}

	public String getAccessToken(){
		return access_token;
	}
	public void setAccessToken(String access_token){
		this.access_token = access_token;
	}
	
	public String getRefreshToken(){
		return refresh_token;
	}
	public void setRefreshToken(String refresh_token){
		this.refresh_token = refresh_token;
	}
	
	public Date getUpd_date() {
		return upd_date;
	}
	public void setUpd_date(Date upd_date) {
		this.upd_date = upd_date;
	}
	
	
}
