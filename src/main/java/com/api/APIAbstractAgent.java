package com.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CancellationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.umeng.analytics.MobclickAgent;
import com.zmap.datagather.activity1.MyApplication;

public abstract class APIAbstractAgent extends ApacheHttpClientFramework {

	protected final static String OPERATE_POST = "POST";

	protected String mToken;

	private String[] dirs = null;

	protected APIAbstractAgent() {
	}

	protected void prepare() throws Exception {
		reset();
		if (MyApplication.sUser == null) {
			throw new Exception("请登录帐号后重试…");
		}
	}

	private Random rd = new Random();

	protected <T> T doPost(Map<String, String> map, String serverUrl,
			String uriAction, Class<T> objClass) throws CancellationException,
			Exception {
		String key = uriAction.replaceAll("/|\\.do", "");
		MobclickAgent.onEvent(MyApplication.instance, key);
		T resultData = null;
		RequestParams rp = new RequestParams(RequestParams.METHOD_POST,
				serverUrl + uriAction);
		setupRequiredHeader(rp, uriAction);
		if (map != null) {
			Set<Entry<String, String>> set = map.entrySet();
			Iterator<Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				String value = entry.getValue();
				if (!TextUtils.isEmpty(value)) {
					value = URLEncoder.encode(entry.getValue(), "utf-8");
				}
				rp.addFormParam(entry.getKey(), value);
			}
		}
		HttpResponse response = null;
		try {
			response = send(rp);
			int statusCode = getStatusCode(response);
			if (statusCode >= 200 && statusCode <= 206) {
				if (response.getEntity() != null) {
					try {
						// if (uriAction.contains("getDrugStoreInfos.do")) {
						// HttpEntity entity = response.getEntity();
						// InputStream dis = entity.getContent();
						// int contenLen = dis.available();
						// int len = -1;
						// byte[] buffer = new byte[8192];
						// StringBuffer sb = new StringBuffer();
						// while ((len = dis.read(buffer)) != -1) {
						// String str = new String(buffer, 0, len,"utf-8");
						// sb.append(str);
						// Log.d("Test",
						// "len =" + len + ":" + str.length()
						// + ":" + sb.length() + ":"
						// + contenLen);
						// }
						// Log.d("Test", "result =" + sb.toString());
						// }
						resultData = parseObjectFromInputStreamByGson(response,
								objClass);
					} catch (Exception e) {
						throw new Exception("ERRCODE_SERVER_RETURN_JSON_TRANS");
					}
					if (resultData == null) {
						throw new Exception("ERRCODE_SERVER_RETURN_EMPTY");
					} else {
						return resultData;
					}
				} else {
					// 抛j服务器内容返回异常
					throw new Exception("ERRCODE_SERVER_RETURN_EMPTY");
				}
			} else {
				// step5: parse wrong returnString
				parseReturnStringAndThrowErrorException(response);
			}
		} catch (ConnectTimeoutException e) {
			throw new Exception("服务连接出错，请稍后重试。");
		} catch (SocketTimeoutException e) {
			throw new Exception("服务连接超时，请稍后重试。");
		} catch (HttpHostConnectException e) {
			throw new Exception("服务器连接失败，请检查网络或稍后重试。");
		} finally {
			releaseRequest(response);
		}
		return null;
	}

	protected void setupRequiredHeader(RequestParams reqParams, String actionUrl) {
		if ("/uploadPhoto.do".equals(actionUrl)) {
			reqParams.addHeader("Content-Type", "application/octet-stream");
		}
	}

	@Override
	protected void setDefaultHttpParams() {
		super.setDefaultHttpParams();
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);// 从socket读数据时发生阻塞的超时时间
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);// 连接的超时时间
		mHttpClient.setParams(params);
	}

	/**
	 * 利用gson包方法将网络返回的inputstream流转成对象（objClass类型）
	 * 
	 * @param response
	 * @param objClass
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	protected <T> T parseObjectFromInputStreamByGson(HttpResponse response,
			Class<T> objClass) throws IllegalStateException, IOException {
		Gson gson = new Gson();
		HttpEntity entity = response.getEntity();
		Reader reader = new InputStreamReader(entity.getContent());
		JsonReader jReader = new JsonReader(reader);
		return (T) gson.fromJson(jReader, objClass);
	}

	/**
	 * 利用gson包方法将网络返回的inputstream流转成对象（typeOfT类型）
	 * 
	 * @param response
	 * @param typeOfT
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	protected <T> T parseObjectFromInputStreamByGson(HttpResponse response,
			Type typeOfT) throws IllegalStateException, IOException {
		Gson gson = new Gson();
		HttpEntity entity = response.getEntity();
		Reader reader = new InputStreamReader(entity.getContent());
		JsonReader jReader = new JsonReader(reader);
		return (T) gson.fromJson(jReader, typeOfT);
	}

	/**
	 * 根据返回错误码，抛出相应的自定义错误描述
	 * 
	 * @param response
	 * @throws IOException
	 * @throws ContactResponseException
	 */
	protected void parseReturnStringAndThrowErrorException(HttpResponse response)
			throws CancellationException, Exception {
		if (response != null) {
			String returnString = null;
			returnString = EntityUtils.toString(response.getEntity());
			if (returnString != null) {
				ErrorResponse err = null;
				try {
					err = JsonUtil.fromJsonString(returnString,
							ErrorResponse.class);
				} catch (Exception e) {
					throw new Exception("ERRCODE_SERVER_RETURN_JSON_TRANS");
				}

			} else {
				throw new Exception("ERRCODE_SERVER_RETURN_EMPTY");
			}
		} else {
			// 抛j服务器内容返回异常
			throw new Exception("ERRCODE_SERVER_RETURN_EMPTY");
		}
	}

	/**
	 * 获得请求头日期信息
	 * 
	 * @return
	 */
	protected static String getRequestHeaderDate() {
		Date nowDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String result = sdf.format(nowDate);
		if (result != null) {
			return result;
		} else {
			return "";
		}
	}

	protected static class ErrorResponse {
		public int errorCode;
		public String message;
	}
}
