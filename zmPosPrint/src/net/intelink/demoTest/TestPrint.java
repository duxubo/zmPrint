package net.intelink.demoTest;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 *  对外打单接口测试
 * @author adu
 *
 */ 
public class TestPrint    {


	public static void main(String[] args) {
    	Integer timeout=50000*5;
    	String  appKey="YF756_MGE";
    	String  appSecret="9742394b92184451beb158e7c141ff6b";
    	
    	appKey="1293921_01372";
    	appSecret="d4b12b46e0ea41d29c5a40c375d1255a";
    	
    	appKey="XCLC_SHT";
    	appSecret="81498a37809e4211aad7cae3cae68799";
      	
    	System.out.println("appKey:"+appKey); 
    	System.out.println("appSecret:"+appSecret); 
    	
    	
    	//请求参数
    	String body ="{\"coid\":\"HXUCN\",\"clno\":\"YAF\",\"hubInCode\":\"4031\",\"jobno\":\"test2020122101\",\"reCompany\":\"WEST\",\"reCountryCode\":\"US\",\"reCountry\":\"US\",\"reState\":\"NY\",\"reAddr\":\"5623  LGH  DKHGKG FHHHG\\nNEW YORK\\nNY\",\"reAddr2\":\"\",\"reZip\":\"10001\",\"reCity\":\"NEW YORK\",\"reConsinee\":\"WEST\",\"reTel\":\"5642256521\",\"reCctaxNo\":\"\",\"sdCompany\":\"SZ GM CO LTD\",\"sdCountryCode\":\"CN\",\"sdCountry\":\"CN\",\"sdState\":\"BAOAN\",\"sdAddr\":\"11 Blocks 202Huangtian Jinbi Industrial Zone, Baoan District, Shenzhen City\",\"sdAddr2\":\"\",\"sdAddr3\":\"\",\"sdCity\":\"SZ\",\"sdZip\":\"518102\",\"sdName\":\"SZ GM CO LTD\",\"sdTel\":\"13510522499\",\"pcs\":1,\"shipWeig\":0.5,\"packing\":\"WPX\",\"packagingType\":\"\",\"descrType\":\"\",\"contents\":\"hello\",\"contents2\":\"\",\"contents3\":\"\",\"contentsCn\":\"\",\"decValue\":10,\"codCharge\":222,\"remark\":\"\",\"randAppendJobno\":1,\"invoice\":[{\"eDescrNamee\":\"SHOES\",\"descrName\":\"鞋子\",\"hsCode\":\"000000\",\"qty\":1,\"price\":10,\"totalPrice\":10,\"unit\":\"\",\"gWeig\":0.02,\"nWeig\":0.02}]}";
    	body="{\"descrType\":\"普货\",\"reCity\":\"Malta\",\"telCode\":\"356\",\"reZip\":\"NXR1017\",\"randAppendJobno\":0,\"reTel\":\"99063758\",\"hubInCode\":\"SHT-FEDEXlm\",\"decValueCur\":\"USD\",\"reCctaxNo\":\"\",\"coid\":\"SHT\",\"decValue\":40.0,\"contents\":\"backdrop\",\"jobno\":\"HYF-201222-02\",\"reConsinee\":\"Francesca Manicolo\",\"reState\":\"Malta\",\"reCountryCode\":\"MT\",\"reCountry\":\"MT\",\"clno\":\"XCLC\",\"packing\":\"WPX\",\"reAddr2\":\"\",\"reAddr3\":\"\",\"reArea\":\"Furry Tails, 74 Triq l Eghnieq, Naxxar\",\"contentsCn\":\"背景墙\",\"pcs\":1,\"reCompany\":\"Francesca Manicolo\",\"shipWeig\":1.0,\"reAddr\":\"Furry Tails, 74 Triq l Eghnieq, Naxxar\",\"ccPayMent\":\"PP\",\"invoice\":[{\"unit\":\"个\",\"eDescrNamee\":\"backdrop\",\"hsCode\":\"5603939000\",\"descrName\":\"背景墙\",\"totalPrice\":40.0,\"price\":10.0,\"origin\":\"CN\",\"qty\":4}]}";
    	System.out.println("body:"+body);
    	String url="http://121.37.214.18:8001/pos-web/";//
    	//url="http://192.168.8.110:8001/web/";//
    	//url="http://117.28.246.19:20001/pos-web/";
    	ZMrequest request=new ZMrequest(url,appKey,appSecret,timeout);
    	//1 获取原始token
    	String sourceToken =request.getPosToken();
    	System.out.println("原始token:"+sourceToken);
    	 
       // String result = print( request, sourceToken,body);
        String result = eiPrintv2(url,request, sourceToken,body);
        System.out.println("打单返回:"+result);
	}

	private static String eiPrintv2(String url,ZMrequest request,String sourceToken, String body) {
		String token=sourceToken.toUpperCase();
		 for (int i = 0; i < 3; i++) {
			 token=stringToMD5(token);
		}
		 Map<String,Object> params=new HashMap<String,Object>();
		 params.put("body1", body);
		 Map<String,String> headers=new HashMap<String,String>();
		 headers.put("token1", sourceToken);
		 headers.put("sign", token+"3");
		 
		 String res=request.http("eiPrintv2", params, headers, "POST");
		 
		return res;//{token1=29498053-df2d-4622-b76c-ef0f000b71d2, sign=fe2c83baf0386048d708a02942b11c3b3}
	}
	
	 public static String stringToMD5(String plainText) {
	        byte[] secretBytes = null;
	        try {
	            secretBytes = MessageDigest.getInstance("md5").digest(
	                    plainText.getBytes());
	        } catch (NoSuchAlgorithmException e) {
	            throw new RuntimeException("没有这个md5算法！");
	        }
	        String md5code = new BigInteger(1, secretBytes).toString(16);
	        for (int i = 0; i < 32 - md5code.length(); i++) {
	            md5code = "0" + md5code;
	        }
	        return md5code;
	    }

	/**
	 * 调用 打单接口
	 * @param request
	 * @param sourceToken
	 * @param body
	 *  成功响应
	 *  {body":{
	 *			"airwayBillNumber":"3678381980",
	 *			"airwayBillNumbers":["3678381980"],
	 *			"conditionCode":"success",
	 *			"conditionData":"",
	 *			"downLoadUrls":["http://139.159.209.189:8090/group1/M00/B4/C9/wKgAFF_KBtKARcDEAAAVU18tw04142.pdf"],
	 *			"exceptionSource":"pos",
	 *			"flag":0,// 成功0       非0 异常
	 *			"mergeLabelUrl":"http://139.159.209.189:8090/group1/M00/B4/C9/wKgAFF_KBtKARcDEAAAVU18tw04142.pdf",
	 *			"messageReference":"d43f71f493af46b89ba3d4630a6842a7",
	 *			"orderId":"",
	 *			"pieceMarks":[],
	 *			"proxyOrderId":"",
	 *			"resultReturnMode":1
	 *		},
	 *		"message":"请求成功",
	 *		"result_code":0
	 *	}
	 *  失败响应
	 *{"body":{
	 *			"conditionCode":"1",
	 *			"conditionData":"没有获取到客户打单规则",
	 *			"exceptionSource":"pos",
	 *			"flag":1,
	 *			"pieceMarks":[],
	 *			"proxyOrderId":"",
	 *			"resultReturnMode":1
	 *		},
	 *		"message":"请求成功",
	 *		"result_code":0
	 *	}
	 */
	private static String print( ZMrequest request, String sourceToken,String body) {
		//4 调用打单接口
        Map<String,Object> params =new HashMap<String,Object>();
        params.put("body1", body);
        String result = request.callPosPrint(params, "eiPrintv2", "post", sourceToken);
		return result;
	}
	
}
