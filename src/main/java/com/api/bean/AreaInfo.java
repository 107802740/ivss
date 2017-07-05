package com.api.bean;

/**
 * 地区信息
 * @author Administrator
 *
 */
public class AreaInfo  extends BaseResponse {
	private String area_code;// 地区编码
	private String area_name;// 地区名称
	private String dq_name;// 大区名称
	private String sq_name;// 省区名称
	private String quy_name;// 区域名称
	private String city;// 城市名称
	private String city_flag;// 城市标志
	private String sq_flag;// 省区标志
	public String getArea_code() {
		return area_code;
	}
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}
	public String getArea_name() {
		return area_name;
	}
	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}
	public String getDq_name() {
		return dq_name;
	}
	public void setDq_name(String dq_name) {
		this.dq_name = dq_name;
	}
	public String getSq_name() {
		return sq_name;
	}
	public void setSq_name(String sq_name) {
		this.sq_name = sq_name;
	}
	public String getQuy_name() {
		return quy_name;
	}
	public void setQuy_name(String quy_name) {
		this.quy_name = quy_name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCity_flag() {
		return city_flag;
	}
	public void setCity_flag(String city_flag) {
		this.city_flag = city_flag;
	}
	public String getSq_flag() {
		return sq_flag;
	}
	public void setSq_flag(String sq_flag) {
		this.sq_flag = sq_flag;
	}
	
	
}
