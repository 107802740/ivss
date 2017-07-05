package com.api.bean;

import java.util.List;

/**
 * 药品信息列表
 * @author Administrator
 *
 */
public class DrugNumbInfoList extends BaseResponse {

	private List<DrugNumbInfo> root;

	public List<DrugNumbInfo> getRoot() {
		return root;
	}

	public void setRoot(List<DrugNumbInfo> root) {
		this.root = root;
	}
	
}
