package com.api.bean;

import java.io.Serializable;

public class User extends BaseResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9079095218816286542L;
	private String loginid;
	private String name;
	private String pwd;
	private String areaCode;

	public String getLoginid() {
		return loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@Override
	public String toString() {
		return "User [loginid=" + loginid + ", name=" + name + ", pwd=" + pwd
				+ ", areaCode=" + areaCode + "]";
	}
	
	

}
