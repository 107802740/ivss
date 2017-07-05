package com.api.bean;

/**
 * 药店采集信息
 * @author Administrator
 *
 */
public class DisplayInfo extends BaseResponse {
	private String ds_code;// 药店编码
	private String ds_name;// 药店名称
	private String drug_code;// 药品编码
	private String drug_numb;// 药品批号
	private String drug_bcode;// 药品条码
	private String drug_name;// 药品名称
	private String disp_surf;// 陈列面编码
	private String drug_price;// 陈列价格
	private String disp_posi;// 陈列位置编码
	private String store_num;// 库存
	private String monthly_sales;//月销量
	private String seq_numb;// 监管码
	private String loginId;// 销售代表编码
	private String createTime;// 创建时间
	private String img_name;// 图片名称
	private String img_path;// 图片名称
	private String Uploadtime;// 上传时间
	private String uploadTag;// 上传标志 不需要上传

	public String getDs_code() {
		return ds_code;
	}

	public void setDs_code(String ds_code) {
		this.ds_code = ds_code;
	}

	public String getDs_name() {
		return ds_name;
	}

	public void setDs_name(String ds_name) {
		this.ds_name = ds_name;
	}

	public String getDrug_code() {
		return drug_code;
	}

	public void setDrug_code(String drug_code) {
		this.drug_code = drug_code;
	}

	public String getDrug_numb() {
		return drug_numb;
	}

	public void setDrug_numb(String drug_numb) {
		this.drug_numb = drug_numb;
	}

	public String getDrug_bcode() {
		return drug_bcode;
	}

	public void setDrug_bcode(String drug_bcode) {
		this.drug_bcode = drug_bcode;
	}

	public String getDrug_name() {
		return drug_name;
	}

	public void setDrug_name(String drug_name) {
		this.drug_name = drug_name;
	}

	public String getDisp_surf() {
		return disp_surf;
	}

	public void setDisp_surf(String disp_surf) {
		this.disp_surf = disp_surf;
	}

	public String getDrug_price() {
		return drug_price;
	}

	public void setDrug_price(String drug_price) {
		this.drug_price = drug_price;
	}

	public String getDisp_posi() {
		return disp_posi;
	}

	public void setDisp_posi(String disp_posi) {
		this.disp_posi = disp_posi;
	}

	public String getStore_num() {
		return store_num;
	}

	public void setStore_num(String store_num) {
		this.store_num = store_num;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
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

	public String getUploadtime() {
		return Uploadtime;
	}

	public void setUploadtime(String uploadtime) {
		Uploadtime = uploadtime;
	}

	public String getUploadTag() {
		return uploadTag;
	}

	public void setUploadTag(String uploadTag) {
		this.uploadTag = uploadTag;
	}

	public String getSeq_numb() {
		return seq_numb;
	}

	public void setSeq_numb(String seq_numb) {
		this.seq_numb = seq_numb;
	}

	public String getMonthly_sales() {
		return monthly_sales;
	}

	public void setMonthly_sales(String monthly_sales) {
		this.monthly_sales = monthly_sales;
	}
}
