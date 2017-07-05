package com.base.bean;

import com.api.bean.BaseResponse;

public class ApkVersionInfo extends BaseResponse {
	private String apk_version;
	private String apk_url;

	public String getApkVersion() {
		return apk_version;
	}

	public void setApkVersion(String apk_version) {
		this.apk_version = apk_version;
	}

	public String getApkUrl() {
		return apk_url;
	}

	public void setApkUrl(String apk_url) {
		this.apk_url = apk_url;
	}

}
