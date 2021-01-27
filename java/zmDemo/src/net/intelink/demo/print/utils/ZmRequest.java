package net.intelink.demo.print.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * ����ӿ������װ �������Է�װҲҪ���ֲ�������һ��
 * 
 * @author adu
 *
 */
public class ZmRequest {

	private String appKey;
	private String appSecret;
	private String nonce;
	private String version;
	
	// token ��Ҫ��ʵ�������ʧЧ�� ��getToken�������ֶ�����
	private String token;
	private String ts;
	
	private Integer timeOut = 18000;// ����
	
	// ���ýӿ���Ҫ�Ĳ��������ýӿ�ǰһ��Ҫ���øò���������ӿڲ���Ҫ���� Ҫ���ÿ�
	Map<String, Object> datas;

	/**
	 * 
     *���˷�����Ȩ��ʽ��
     *	�ͻ�����:YT8060
     *	�˺�:YT8060_HTYGXJ
     *	��Կ:816e7669d18542e57853a9fd32445e25
     *	ָ��·��:����FEDEX-X-IP,����FEDEX-A-IP
	 * @param appKey     �˺�
	 * @param appSecret  ��Կ
	 * @param nonce   �磺slnkda   �������
	 * @param version Ĭ��1.0
	 */
	public ZmRequest(String appKey, String appSecret, String nonce, String version) {
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.ts = this.getTimeStamp();
		this.nonce = nonce;
		this.version = version;
	}

	// һ��Ҫ����
	public void setToken(String token) {
		this.token = token;
	}

	// һ��Ҫ����
	public void setDatas(Map<String, Object> datas) {
		this.datas = datas;
	}

	// ��ȡ ��׼token
	public String getToken(String url) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appKey", appKey);
		params.put("appSecret", appSecret);
		String result = http(url, params, null, "POST");
		if (Utils.isNotBlank(result)) {
			Map<String, Object> resultMap = Utils.json2Object(result, Map.class);
			Integer code = Utils.getInteger(resultMap, "result_code");
			if (null != code && code == 0) {
				String token = Utils.getString(resultMap, "body");
				return token;
			} else {
				System.out.println(result);
				throw new RuntimeException(Utils.getString(resultMap, "message"));
			}
		} else {
			throw new RuntimeException("���ؿ�����");
		}
	}

	/**
	 * ��ȡͷ��Ϣ
	 * 
	 * @return
	 */
	public Map<String, String> getHeaders() {
		System.out.println("ʱ���:" + this.ts);
		System.out.println("ԭʼtoken:" + this.token);
		// 2 ��ȡαװtoken
		String newToken = this.camouflageToken();
		System.out.println("αװ���token:" + newToken);
		// 3 ����ǩ��
		String sign = this.genSign(this.datas);
		System.out.println("������ǩ��:" + sign);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("token", newToken);
		headers.put("version", version);
		headers.put("sign", sign);
		return headers;
	}

	/**
	 * ��ȡʱ��� ��Ҫ�Ǻ�.net ����һ��
	 * 
	 * @return
	 */
	public String getTimeStamp() {
		return String.valueOf(System.currentTimeMillis());
	}

	/**
	 * ��Ҫ�Ǻ�.net ����һ��,ʵ��java�汾����Ҫ
	 * 
	 * @param str
	 * @return
	 */
	public String getMd5(String str) {
		return null;
	}

	/**
	 * αװtoken
	 * 
	 * @return
	 */
	public String camouflageToken() {
		// ��POS��ȡԭʼtoken
		// String posOriToken = getPosPrintSourceToken(appKey, appSecret);
		System.out.println("��pos��ȡ��ԭʼtoken��{}" + this.token);
		if (Utils.isBlank(this.token)) {
			throw new RuntimeException("��ȡtoken�쳣");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("timestamp", this.ts);
		map.put("nonce", nonce);
		map.put("token", this.token);
		System.out.println("��������token�Ĳ��� ��{}" + map.toString());
		String newTokenJson = Utils.toJson(map);
		String newToken = Utils.encodeMixChar(newTokenJson);
		System.out.println("�������ɵ���token��{}" + newToken);
		return newToken;
	}

	/**
	 * ����ǩ��
	 * 
	 * @param pds
	 * @return
	 */
	public String genSign(Map<String, Object> pds) {
		Map<String, Object> signMap = new HashMap<String, Object>();
		if (null != pds && pds.size() > 0) {
			for (Map.Entry<String, Object> entry : pds.entrySet()) {
				signMap.put(entry.getKey(), entry.getValue());
			}
		}
		signMap.put("version", version);
		signMap.put("nonce", nonce);
		signMap.put("timestamp", this.ts);
		signMap.put("token", this.token);
		String sign = Utils.sign(signMap, appSecret);
		return sign;
	}

	/**
	 * ��¶���˽ӿڵ��÷���
	 * 
	 * @param url
	 * @param method
	 * @return
	 */
	public String request(String url, String method) {
		Map<String, String> headers = this.getHeaders();
		String result = "";
		result = this.http(url, this.datas, headers, method);
		return result;
	}

	/**
	 * POST����
	 *
	 * @param requestUrl �����ַ
	 * @param param      ��������
	 * @return
	 */
	private String http(String requestUrl, Map<String, Object> params, Map<String, String> headers, String method) {
		String param = "";

		if (null != params) {
			param = convertParams(params);
			System.out.println("path:" + requestUrl + "?" + param);
		}
		HttpURLConnection connection = null;
		InputStream is = null;
		OutputStream os = null;
		BufferedReader br = null;
		String result = null;
		try {
			/** ����Զ��url���Ӷ��� */
			URL url = new URL(requestUrl);
			/** ͨ��Զ��url�����һ�����ӣ�ǿ��ת��ΪHttpUrlConnection���� */
			connection = (HttpURLConnection) url.openConnection();
			/** �������ӷ�ʽ��POST */
			connection.setRequestMethod(method.toUpperCase());
			connection.setConnectTimeout(timeOut);

			connection.setReadTimeout(timeOut);
			/** �����Ƿ���httpUrlConnection����������Ƿ��httpUrlConnection���룬���ⷢ��post����������������� */
			// Ĭ��ֵΪ��false������Զ�̷�������������/д����ʱ����Ҫ����Ϊtrue
			connection.setDoOutput(true);
			// Ĭ��ֵΪ��true����ǰ��Զ�̷����ȡ����ʱ������Ϊtrue���ò������п���
			connection.setDoInput(true);
			/** ����ͨ�õ��������� */
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// ���ô�������ĸ�ʽ:�������Ӧ���� name1=value1&name2=value2 ����ʽ
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			if (null != headers) {
				Set<String> set = headers.keySet();
				if (null != set && set.size() > 0) {
					for (String key : set) {
						String value = headers.get(key);
						connection.setRequestProperty(key, value);
					}
				}
			}
			/** ͨ�����Ӷ����ȡһ������� */
			os = connection.getOutputStream();
			/** ͨ����������󽫲���д��ȥ/�����ȥ������ͨ���ֽ�����д���� */
			// ��ʹ��os.print(param);����Ҫ�ͷŻ��棺os.flush();��ʹ���ַ��������Ҫ�ͷŻ��棬�ֽ�������Ҫ
			if (param != null && param.length() > 0) {
				os.write(param.getBytes());
			}
			/** ����ɹ���������Ϊ200 */
			if (connection.getResponseCode() == 200) {
				/** ͨ�����Ӷ����ȡһ������������Զ�̶�ȡ */
				is = connection.getInputStream();
				/** ��װ������is����ָ���ַ��� */
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				/** ������� */
				StringBuffer sbf = new StringBuffer();
				String line = null;
				while ((line = br.readLine()) != null) {
					sbf.append(line);
					sbf.append("\r\n");
				}
				result = sbf.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/** �ر���Դ */
			try {
				if (null != br) {
					br.close();
				}
				if (null != is) {
					is.close();
				}
				if (null != os) {
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			/** �ر�Զ������ */
			// �Ͽ����ӣ����д�ϣ�disconnect���ڵײ�tcp socket���ӿ���ʱ���жϡ�������ڱ������߳�ʹ�þͲ��жϡ�
			// �̶����̵߳Ļ��������disconnect�����ӻ����ֱ࣬���շ�������Ϣ��д��disconnect������һЩ
			connection.disconnect();
		}
		return result;
	}

	/**
	 * map����ת url ��������
	 *
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String convertParams(Map<String, Object> params) {
		try {
			// ��������������ָ���URL�������
			String queryString = "";
			if (params != null) {
				Set<Entry<String, Object>> entrySet = params.entrySet();
				for (Entry<String, Object> entry : entrySet) {
					if (queryString.length() > 0)
						queryString = queryString + "&";
					queryString += entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), "utf-8");
				}
			}
			return queryString;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
