package com.api.bean;

public class InstoreInfo {
	private String ds_code2;// 药店编号
	private String ds_name;// 药店名称
	private String floginId;// 入店人账号
	private String in_date;// 入店时间

	private String img_name;// 图片名称
	private String img_path;// 图片路径
	private String area_code;// 地区编码
	private String ds_upload;// 上传时间
	private String ds_upload_tag;// 上传标志 这个字段不需要上传

	public String getDs_code2() {
		return ds_code2;
	}

	public void setDs_code2(String ds_code2) {
		this.ds_code2 = ds_code2;
	}

	public String getDs_name() {
		return ds_name;
	}

	public void setDs_name(String ds_name) {
		this.ds_name = ds_name;
	}

	public String getFloginId() {
		return floginId;
	}

	public void setFloginId(String floginId) {
		this.floginId = floginId;
	}

	public String getIn_date() {
		return in_date;
	}

	public void setIn_date(String in_date) {
		this.in_date = in_date;
	}

	public String getImg_name() {
		return img_name;
	}

	public void setImg_name(String img_name) {
		this.img_name = img_name;
	}

	public String getImg_path() {
		return img_path;
	}

	public void setImg_path(String img_path) {
		this.img_path = img_path;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public String getDs_upload() {
		return ds_upload;
	}

	public void setDs_upload(String ds_upload) {
		this.ds_upload = ds_upload;
	}

	public String isDs_upload_tag() {
		return ds_upload_tag;
	}

	public void setDs_upload_tag(String ds_upload_tag) {
		this.ds_upload_tag = ds_upload_tag;
	}

}