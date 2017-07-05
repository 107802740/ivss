package com.api;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.params.CoreConnectionPNames;

import android.net.Uri;
import android.text.TextUtils;

import com.api.bean.PicPathInfo;
import com.base.Constant;
import com.umeng.analytics.MobclickAgent;
import com.zmap.datagather.activity1.MyApplication;

public class UploadDataAgent {
	private String server_action_url = "";
	private static final String ACTION_UPLOAD_INSTOREINFO = "/upLoadInstoreInfo.do";
	private static final String ACTION_UPLOAD_DISPLAYINFO = "/upLoadDisplayInfo.do";
	// v2.0

	private PostMethod postMethod = null;

	public UploadDataAgent() {
		server_action_url = Constant.SERVICE_RUL;
	}

	/**
	 * 上传入店信息
	 * 
	 * @param accessToken
	 * @param filename
	 * @throws FPAPIException
	 */
	public PicPathInfo uploadInstoreInfo(String ds_code2, String ds_name,
			String floginId, String in_date, String area_code, String img_name,
			String filename) throws Exception {
		String key = ACTION_UPLOAD_INSTOREINFO.replaceAll("/|\\.do", "");
		MobclickAgent.onEvent(MyApplication.instance, key);
		String urlPath = server_action_url + ACTION_UPLOAD_INSTOREINFO;
		Map<String, String> params = new HashMap<String, String>();
		params.put("ds_code2", Uri.encode(ds_code2));
		params.put("ds_name", Uri.encode(ds_name));
		params.put("floginId", Uri.encode(floginId));
		params.put("in_date", Uri.encode(in_date));
		params.put("img_name", Uri.encode(img_name));
		params.put("area_code", Uri.encode(area_code));
		PicPathInfo response = getParams(urlPath, params, filename,
				PicPathInfo.class);
		if (response.result < 0) {
			throw new Exception(response.msg);

		}
		return response;
	}

	/**
	 * 上传巡店信息
	 * 
	 * @param accessToken
	 * @param filename
	 * @throws FPAPIException
	 */
	public PicPathInfo uploadDisplayInfo(String ds_code, String ds_name,
			String drug_code, String drug_numb, String drug_bcode,
			String drug_name, String disp_surf, String drug_price,
			String disp_posi, String store_num, String seq_numb,
			String loginId, String createTime, String img_name, String filename,String monthly_sales)
			throws Exception {
		String key = ACTION_UPLOAD_DISPLAYINFO.replaceAll("/|\\.do", "");
		MobclickAgent.onEvent(MyApplication.instance, key);
		String urlPath = server_action_url + ACTION_UPLOAD_DISPLAYINFO;
		Map<String, String> params = new HashMap<String, String>();
		params.put("ds_code", Uri.encode(ds_code));
		params.put("ds_name", Uri.encode(ds_name));
		params.put("drug_code", Uri.encode(drug_code));
		params.put("drug_numb", Uri.encode(drug_numb));
		params.put("drug_bcode", Uri.encode(drug_bcode));
		params.put("drug_name", Uri.encode(drug_name));
		params.put("disp_surf", Uri.encode(disp_surf));
		params.put("drug_price", Uri.encode(drug_price));
		params.put("disp_posi", Uri.encode(disp_posi));
		params.put("store_num", Uri.encode(store_num));
		params.put("seq_numb", Uri.encode(seq_numb));
		params.put("loginId", Uri.encode(loginId));
		params.put("createTime", Uri.encode(createTime));
		params.put("img_name", Uri.encode(img_name));
		params.put("monthly_sales", Uri.encode(monthly_sales));

		PicPathInfo response = getParams(urlPath, params, filename,
				PicPathInfo.class);
		if (response.result < 0) {
			throw new Exception(response.msg);

		}
		return response;
	}

	private <T> T getParams(String urlpath, Map<String, String> params,
			String filename, Class<T> objClass) throws Exception {
		T responseContent = uploadImg(urlpath, params, filename, objClass);
		return responseContent;
	}

	private <T> T uploadImg(String actionUrl, Map<String, String> params,
			String imagepath, Class<T> objClass) throws Exception {

		FilePart picFilePart = null;
		Map<String, String> requestData = params;
		int size = requestData.size();
		try {
			if (imagepath != null) {
				File imgFile = new File(imagepath);
				if (imgFile.exists()) {
					picFilePart = new FilePart("picContent", imgFile);
					size += 1;
				}
			}
			Part[] paramPart = new Part[size];
			int i = 0;
			for (Map.Entry<String, String> item : requestData.entrySet()) {
				String value = item.getValue();
				if (value == null) {
					value = "";
				}
				paramPart[i] = new StringPart(item.getKey(), value);
				++i;
			}
			if (picFilePart != null)
				paramPart[i] = picFilePart;
			postMethod = new PostMethod(actionUrl);
			postMethod.setRequestEntity(new MultipartRequestEntity(paramPart,
					postMethod.getParams()));
			org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 30000);
			httpClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
			httpClient.getParams().setContentCharset("utf-8");
			int resultStatus = httpClient.executeMethod(postMethod);
			if (resultStatus == 200) {
				String results = postMethod.getResponseBodyAsString();
				if (!TextUtils.isEmpty(results)) {
					T resultData = null;
					try {
						resultData = JsonUtil
								.fromJsonString2(results, objClass);
					} catch (Exception e) {
						throw new Exception("ERRCODE_SERVER_RETURN_JSON_TRANS");
					}
					if (resultData == null) {
						throw new Exception("ERRCODE_SERVER_RETURN_EMPTY");
					} else {
						return resultData;
					}
				} else {
					throw new Exception("ERRCODE_SERVER_RETURN_EMPTY");
				}
			} else {
				throw new Exception("上传失败。");
			}
		} catch (Exception e) {
			if (e instanceof HttpHostConnectException) {
				throw new Exception("服务器连接失败，请检查网络或稍后重试。");
			} else {
				throw new Exception(e.getMessage());
			}
		} finally {
			if (postMethod != null) {
				postMethod.abort();
			}
		}
	}

	public static String changeEmptyStrToQuotForPost(String str) {
		if (str == null || "".equals(str.trim())) {
			str = "\"\"";
		}
		return str;
	}

	/** 对传入的字符串如果是null或"null"或"  "则返回""，否则原样返回 */
	public static String getStringTrimAndRemoveNULL(String str) {
		String _str = null;
		if (str == null || "\"\"".equals(str) || "null".equals(str)
				|| "".equals(str.trim())) {
			_str = "";
		} else {
			_str = str;
		}
		return _str;
	}

	public void cencel() {
		if (postMethod != null) {
			postMethod.abort();
			postMethod = null;
		}
	}

}
