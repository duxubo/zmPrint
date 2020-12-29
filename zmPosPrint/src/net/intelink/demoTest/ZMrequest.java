package net.intelink.demoTest;

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
import java.util.Map.Entry;
import java.util.Set;

 

public class ZMrequest{

	Integer timeout=50000*5;
	String appKey;
	String appSecret;
	
    String baseUrl="";
    public ZMrequest(String baseUrl) {
    	this.baseUrl=baseUrl;
    }
    public ZMrequest(String baseUrl,String appKey, String appSecret,Integer timeout) {
    	this(baseUrl);
    	if(null!=timeout) {
    		this.timeout=timeout;
    	}
    	this.appKey=appKey;
    	this.appSecret=appSecret;
    }

	/**
     * @Description  token组装编码
     * @Author liuqi
     * @Date 2020/4/7 14:41
     * @Version 1.0.0
     */
    public   String buildToken(Long time, String nonce ,String sourceToken){
        //从POS获取原始token
       // String posOriToken = getPosPrintSourceToken(appKey, appSecret);
        System.out.print("从pos获取的原始token：{}"+sourceToken);
        if (Utils.isBlank(sourceToken)) {
        	throw new RuntimeException("获取token异常");
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("timestamp",time);
        map.put("nonce",nonce);
        map.put("token",sourceToken);
        System.out.print("待生成新token的参数 ：{}"+ map.toString());
        String newTokenJson = Utils.toJson(map);
        String newToken = Utils.encodeMixChar(newTokenJson);
        System.out.print("最终生成的新token：{}"+ newToken);
        return newToken;
    }

    /**
     * @Description  从pos获取原始token
     * @Author liuqi
     * @Date 2020/4/7 14:44
     * @Version 1.0.0
     */
   @SuppressWarnings("unchecked")
   public  String getPosToken(){
	       String url="auth/access/token";
           Map<String,Object> params=new HashMap<String,Object>();
           params.put("appKey",appKey);
           params.put("appSecret",appSecret);
           String result = http(url,params,null,"POST");
           if (Utils.isNotBlank(result)){
        	   Map<String,Object> resultMap=Utils.json2Object(result, Map.class);
        	   Integer code=Utils.getInteger(resultMap, "result_code");
        	   if(null!=code && code==0) { 
        		   String token=Utils.getString(resultMap, "body");
        		   return token;
        	   }else {
        		   System.out.println(result);
        		   throw new RuntimeException(Utils.getString(resultMap, "message"));
        	   }
           }else {
        	   throw new RuntimeException("返回空数据");
           }
   }
   /**
   * POST请求
   *
   * @param requestUrl 请求地址
   * @param param 请求数据
   * @return
   */
   public  String http(String path, Map<String, Object> params,Map<String,String> headers,String method) {
	   String param="";
	   String requestUrl=this.baseUrl+path;
	   if(null!=params) {
		   param=convertParams(params); 
		   System.out.println("path:"+requestUrl+"?"+param);
	   } 
	    HttpURLConnection connection = null;
	    InputStream is = null;
	    OutputStream os = null;
	    BufferedReader br = null;
	    String result = null;
		try {
			/** 创建远程url连接对象 */
			URL url = new URL(requestUrl);
			/** 通过远程url对象打开一个连接，强制转换为HttpUrlConnection类型 */
			connection = (HttpURLConnection) url.openConnection();
			/** 设置连接方式：POST */
			connection.setRequestMethod(method.toUpperCase());
			connection.setConnectTimeout(timeout);
			/** 设置读取远程返回的数据时间：60000毫秒 */
			connection.setReadTimeout(timeout);
			/** 设置是否向httpUrlConnection输出，设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个 */
			// 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
			connection.setDoOutput(true);
			// 默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
			connection.setDoInput(true);
			/** 设置通用的请求属性 */
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 设置传入参数的格式:请求参数应该是 name1=value1&name2=value2 的形式
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			if(null!=headers) {
				Set<String> set = headers.keySet();
				if(null!=set &&  set.size()>0) {
					for (String key : set) {
						String value = headers.get(key);
						connection.setRequestProperty(key,value);
					}
				}
			}
			/** 通过连接对象获取一个输出流 */
			os = connection.getOutputStream();
			/** 通过输出流对象将参数写出去/传输出去，它是通过字节数组写出的 */
			// 若使用os.print(param);则需要释放缓存：os.flush();即使用字符流输出需要释放缓存，字节流则不需要
			if (param != null && param.length() > 0) {
				os.write(param.getBytes());
			}
			/** 请求成功：返回码为200 */
			if (connection.getResponseCode() == 200) {
				/** 通过连接对象获取一个输入流，向远程读取 */
				is = connection.getInputStream();
				/** 封装输入流is，并指定字符集 */
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				/** 存放数据 */
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
			/** 关闭资源 */
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
			/** 关闭远程连接 */
			// 断开连接，最好写上，disconnect是在底层tcp socket链接空闲时才切断。如果正在被其他线程使用就不切断。
			// 固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息。写上disconnect后正常一些
			connection.disconnect();
		}
		return result;
	}
   
	/**
	 * map参数转 url 编码数据
	 *
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public  String convertParams(Map<String, Object> params) {
		try {
			// 建立输入流，向指向的URL传入参数
			String queryString = "";
			if (params != null) {
				Set<Entry<String, Object>> entrySet = params.entrySet();
				for (Entry<String, Object> entry : entrySet) {
					if(queryString.length()>0) queryString=queryString +"&";
					queryString += entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), "utf-8") ;
				}
			}
			return queryString;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 通用调用哲盟接口请求方法
	 * 所有接口均使用表单方式提交请求数据
	 * @param params 	接口请求参数
	 * @param path	 	具体接口的地址     如：   print 是接口名
	 * @param method	GET/POST
	 * @param token		getPosToken 返回的原始token
	 * @return			接口返回的字符串数据
	 * 返回参考：
	 * 	result_code 为0 表示接口返回成功    非0 表示失败
	 * {"message":"AppKey或AppSecret错误,请检查！","result_code":1001}
     * {"body":"6e2d0d18-4739-4fb4-bd7c-0677b12c80dd","message":"请求成功","result_code":0}
     * 
     * 当result_code=0 ,在业务上也有可能失败所以具体的接口会有针对说明
	 */
	public  String callPosPrint( Map<String,Object> params, String path,String method,String token ) {
    	Map<String, String> headers = getHeader( params,token);
        String result="";
        result = http(path,params,headers,method);
		return result;
	}
	
	// 计算获取完整的头信息
	private Map<String, String> getHeader( Map<String,Object> param,String sourceToken) {
		String nonce="slnkda";
    	String version="1.0";
    	Long time = System.currentTimeMillis();
    	 
    	System.out.println("时间戳:"+time);
    	System.out.println("原始token:"+sourceToken);
    	//2 获取伪装token
    	String newToken=buildToken(time,nonce, sourceToken);
    	System.out.println("伪装后的token:"+newToken);
    	//3 计算签名
        Map<String,Object> signMap = new HashMap<String, Object>();
        if(null!=param && param.size()>0) {
        	for (Map.Entry<String, Object> entry : param.entrySet()) {
        	    signMap.put(entry.getKey(),entry.getValue());
        	}
        }
        signMap.put("version",version);
        signMap.put("nonce",nonce);
        signMap.put("timestamp",time);
        signMap.put("token",sourceToken);
        
        // 调用签名方法签名
        String sign = Utils.sign(signMap, appSecret);
        System.out.println("计算后的签名:"+sign);

        Map<String,String> headers =new HashMap<String,String>();
        headers.put("token", newToken);
        headers.put("version", version);
        headers.put("sign", sign);
		return headers;
	}
}
