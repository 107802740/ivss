package com.api.bean;

public class AddStoreInfo extends BaseResponse {
	private String ds_code2;
	private String cid;

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

}
