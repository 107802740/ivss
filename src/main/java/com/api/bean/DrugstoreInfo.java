package com.api.bean;

import java.io.Serializable;

public class DrugstoreInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5214102131243363703L;
	private String cid;// 主键
	private String ds_code2;// 药店编号
	private String cname;// 药店名称
	private String pointx;// 经度坐标 新增时自动带
	private String pointy;// 纬度坐标 新增时自动带
	private String address;// 地址 新增时自动带或手输
	private String levl;// 等级 选择
	private String month_amt;// 月营业额 手输
	private String sales;// 销售代表 自动带
	private String sale_dbzh;// 销售代表帐号 自动带
	private String dest_flag;// 是/否 目标药店 选择
	private String fcreatetime;// 创建时间 自动
	private String area_code;// 地区编码 带业务员的所属地区编码
	private String use_flag;// 是否禁用
	private String city_name;// 城市名称 根据gsp自动带
	private String province_name;// 省名称 根据gsp自动带

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getDs_code2() {
		return ds_code2;
	}

	public void setDs_code2(String ds_code2) {
		this.ds_code2 = ds_code2;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getPointx() {
		return pointx;
	}

	public void setPointx(String pointx) {
		this.pointx = pointx;
	}

	public String getPointy() {
		return pointy;
	}

	public void setPointy(String pointy) {
		this.pointy = pointy;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLevl() {
		return levl;
	}

	public void setLevl(String levl) {
		this.levl = levl;
	}

	public String getMonth_amt() {
		return month_amt;
	}

	public void setMonth_amt(String month_amt) {
		this.month_amt = month_amt;
	}

	public String getSales() {
		return sales;
	}

	public void setSales(String sales) {
		this.sales = sales;
	}

	public String getSale_dbzh() {
		return sale_dbzh;
	}

	public void setSale_dbzh(String sale_dbzh) {
		this.sale_dbzh = sale_dbzh;
	}

	public String getDest_flag() {
		return dest_flag;
	}

	public void setDest_flag(String dest_flag) {
		this.dest_flag = dest_flag;
	}

	public String getFcreatetime() {
		return fcreatetime;
	}

	public void setFcreatetime(String fcreatetime) {
		this.fcreatetime = fcreatetime;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public String getUse_flag() {
		return use_flag;
	}

	public void setUse_flag(String use_flag) {
		this.use_flag = use_flag;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getProvince_name() {
		return province_name;
	}

	public void setProvince_name(String province_name) {
		this.province_name = province_name;
	}

}
