package com.mio.security.dto;

public class JJWTBean {
	private String laguage;
	private String uuid;
	
	private String salt;
	public String getLaguage() {
		return laguage;
	}
	public void setLaguage(String laguage) {
		this.laguage = laguage;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}




}
