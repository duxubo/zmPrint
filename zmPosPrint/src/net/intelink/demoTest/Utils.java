package net.intelink.demoTest;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSON;

public class Utils {

	private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };

	private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	private static Map<String, String> charMap = new HashMap<String, String>() {
		{
			this.put("a", "-");
			this.put("c", "#");
			this.put("x", "^");
			this.put("M", "$");
		}
	};

	/**
	 * 从map 返回int
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	public static Integer getInteger(Map map, Object key) {
		Number answer = getNumber(map, key);
		if (answer == null)
			return null;
		if (answer instanceof Integer) {
			return ((Integer) answer);
		}
		return new Integer(answer.intValue());
	}

	/**
	 * 从map 返回 number
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	private static Number getNumber(Map map, Object key) {
		if (map != null) {
			Object answer = map.get(key);
			if (answer != null) {
				if (answer instanceof Number) {
					return ((Number) answer);
				}
				if (answer instanceof String) {
					try {
						String text = (String) answer;
						return NumberFormat.getInstance().parse(text);
					} catch (ParseException e) {
						// logger.error(e.getMessage(),e);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 从map 返回字符串
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	public static String getString(Map map, Object key) {
		if (map != null) {
			Object answer = map.get(key);
			if (answer != null) {
				return answer.toString();
			}
		}
		return null;
	}

	/**
	 * 判断字符串非空
	 * 
	 * @param str
	 * @return
	 */
	public static Boolean isNotBlank(String str) {
		if (str == null) {
			return false;
		}
		if (str.trim().length() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 判断字符串 是空的
	 * 
	 * @param str
	 * @return
	 */
	public static Boolean isBlank(String str) {
		return !isNotBlank(str);
	}

	/**
	 * 对象转字符串
	 * 
	 * @param <T>
	 * @param t
	 * @return
	 */
	public static <T> String toJson(T t) {
		if (t == null) {
			return "";
		} else {
			return JSON.toJSON(t) != null ? JSON.toJSON(t).toString() : "";
		}
	}

	/**
	 * json 字符串转对象
	 * 
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T json2Object(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);
	}

	/**
	 * 计算签名
	 * 
	 * @param params
	 * @param secret
	 * @return
	 */
	public static String sign(Map<String, Object> params, String secret) {
		if (params != null && !params.isEmpty()) {
			Set<String> keysSet = params.keySet();
			Object[] keys = keysSet.toArray();
			Arrays.sort(keys);
			StringBuffer stringBuffer = new StringBuffer();
			Object[] var5 = keys;
			int var6 = keys.length;
			for (int var7 = 0; var7 < var6; ++var7) {
				Object key = var5[var7];
				System.out.println("参与计算的key:" + key);
				Object value = params.get(key);
				if (value != null && value.toString().length() > 0) {
					stringBuffer.append("&").append(key).append("=").append(value);
				}
			}

			if (stringBuffer.length() > 0) {
				return cal(secret, stringBuffer);
			} else {
				return "";
			}
		} else {
			throw new RuntimeException("参数错误.");
		}
	}

	public static void main(String[] args) {
		StringBuffer b=new StringBuffer();
		b.append("&body1={\"coid\":\"HYGJ\",\"clno\":\"KEN\",\"hubInCode\":\"FED-优先型\",\"jobno\":\"TEST202020\",\"sd_airport_code\":\"1\",\"sd_oper_center\":\"2\",\"sdCompany\":\"KAPEIXIfeimei\",\"sdName\":\"KAPEIXIfeimei\",\"sdaddr\":\"J6 park\",\"sdaddr2\":\"\",\"sdaddr3\":\"\",    \"reCompany\":\"Amy Howard\",    \"reCountry\":\"US\",    \"reCountryCode\":\"US\",    \"reState\":\"NY\",    \"reAddr\":\"12 Ridge Road\",    \"reAddr2\":\"12 Ridge Road\",    \"reAddr3\":\"12 Ridge Road\",    \"reZip\":\"10987\",    \"reCity\":\"Tuxedo Park\",    \"reConsinee\":\"Amy Howard\",    \"reTel\":\"2672428449\",    \"reCctaxNo\":\"\",\"re_airport_code\":\"1\",\"re_oper_center\":\"2\",    \"pcs\":\"1\",    \"shipWeig\":\"0.62\",    \"packing\":\"PAK\",    \"packagingType\":\"\",    \"descrType\":\"普货\",    \"contents\":\"100 cotton shirt sample6106100090 for bedding\",    \"contents2\":\"\",    \"contents3\":\"\",    \"contentsCn\":\"棉质衬衫样品\",    \"decValue\":\"15.00\",\"decValueCur\":\"\",    \"codCharge\":\"\",\"codChargeCur\":\"\",    \"ccPayMent\":\"PP\",    \"cctax\":\"CC\",    \"dgContentId\":\"\",    \"remark\":\"\",\"is_plt\":\"\",    \"invoice\":[{\"e_descr_namee\":\"100 cotton shirt sample6106100090 for bedding\",\"descr_name\":\"棉质衬衫样品\",\"qty\":\"1\",\"g_weig\":\"0.100\",\"n_weig\":\"0.000\",\"price\":\"15.00\",\"hs_code\":\"6106100090\",\"origin\":\"CN\",\"unit\":\"Pieces\"}],\"pay_account_no\":\"\",\"service_no\":\"\"}&nonce=slnkda&timestamp=1608837566638&token=95c7b302-c930-4389-bf48-a296bf0a6fff&version=1.0");
		
		String str=cal("2e63124d88fd460f82f18bbe063cdb75",b);
		System.out.println(str);
	}
	private static String cal(String secret, StringBuffer stringBuffer) {
		if (Utils.isNotBlank(secret)) {
			String ss = stringBuffer.toString().substring(1);
			byte[] bs = null;
			try {
				bs = Base64.encodeBase64(ss.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String text = new String(bs);
			return md5Hex(text + secret).toUpperCase();
		} else {
			return "";
		}
	}

//----------------以下是签名计算相关------------------------------------------------------------------  
	public static String encodeMixChar(String source) {
		String encodeStr = null;
		try {
			encodeStr = Base64.encodeBase64String(source.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Entry entry;
		for (Iterator var2 = charMap.entrySet().iterator(); var2.hasNext(); encodeStr = encodeStr
				.replace((CharSequence) entry.getKey(), (CharSequence) entry.getValue())) {
			entry = (Entry) var2.next();
		}
		return encodeStr;
	}

	public static String md5Hex(final String data) {
		byte[] hex = getBytes(data, Charset.forName("UTF-8"));
		return encodeHexString(getDigest("MD5").digest(hex));
	}

	public static MessageDigest getDigest(final String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (final NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static byte[] getBytes(final String string, final Charset charset) {
		if (string == null) {
			return null;
		}
		return string.getBytes(charset);
	}

	public static String encodeHexString(final byte[] data) {
		return new String(encodeHex(data));
	}

	public static char[] encodeHex(final byte[] data) {
		return encodeHex(data, true);
	}

	public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	public static char[] encodeHex(final byte[] data, final char[] toDigits) {
		final int l = data.length;
		final char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
		return out;
	}
//----------------------------------------------------------------------------------    
}
