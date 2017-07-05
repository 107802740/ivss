package com.api.bean;

/**
 * 药品信息
 * @author Administrator
 *
 */
public class DrugInfo {

	private String cname;// 药品名称
	private String price;// 单价
	private String drug_bcode;// 产品条码
	private String drug_code;// 药品编码

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDrug_bcode() {
		return drug_bcode;
	}

	public void setDrug_bcode(String drug_bcode) {
		this.drug_bcode = drug_bcode;
	}

	public String getDrug_code() {
		return drug_code;
	}

	public void setDrug_code(String drug_code) {
		this.drug_code = drug_code;
	}

}
