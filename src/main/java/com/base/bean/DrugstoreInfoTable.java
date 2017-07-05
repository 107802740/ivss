package com.base.bean;

public class DrugstoreInfoTable{
	public static final String TABLE_NAME="b_drugstore_info";
	public static final String TEMP_TABLE_NAME="b_drugstore_info_temp";
	public static final String DS_CODE2="ds_code2";// 药店编号
	public static final String CNAME="cname";// 药店名称
	public static final String POINTX="pointx";// 经度坐标 新增时自动带
	public static final String POINTY="pointy";// 纬度坐标 新增时自动带
	public static final String ADDRESS="address";// 地址 新增时自动带或手输
	public static final String LEVL="levl";// 等级 选择
	public static final String MONTH_AMT="month_amt";// 月营业额 手输
	public static final String SALES="sales";// 销售代表 自动带
	public static final String SALE_DBZH="sale_dbzh";// 销售代表帐号 自动带
	public static final String DEST_FLAG="dest_flag";// 是/否 目标药店 选择
	public static final String FCREATETIME="fcreatetime";// 创建时间 自动
	public static final String AREA_CODE="area_code";// 地区编码 带业务员的所属地区编码
	public static final String USE_FLAG="use_flag";// 是否禁用
	public static final String CITY_NAME="city_name";// 城市名称 根据gsp自动带
	public static final String PROVINCE_NAME="province_name";// 省名称 根据gsp自动带


}
