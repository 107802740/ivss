package com.api.bean;

import java.util.List;

/**
 * 药品信息列表
 * @author Administrator
 *
 */
public class DrugInfoList  extends BaseResponse {

	private List<DrugInfo> root;

	public List<DrugInfo> getRoot() {
		return root;
	}

	public void setRoot(List<DrugInfo> root) {
		this.root = root;
	}
	
}
