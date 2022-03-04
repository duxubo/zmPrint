package net.intelink.demo.print;

import java.util.HashMap;
import java.util.Map;

import net.intelink.demo.print.utils.ZmRequest;

/**
 *  对外打单接口测试
 * @author adu
 *
 */ 
public class TestPrint    {


	public static void main(String[] args) {
    	String url="http://121.37.214.18:8001/pos-web";//
    	String appKey = "YW80606_HYGJ";
    	String appSecret = "816e76869da0542e1a853a9fda8445e25";
    	String nonce = "slnkda";
    	String version= "1.0";
    	
    	String body = "{\"coid\":\"HXUCN\",\"clno\":\"YAF\",\"hubInCode\":\"义乌FEDEX-X-IP\",\"jobno\":\"test2020122101\",\"reCompany\":\"WEST\",\"reCountryCode\":\"US\",\"reCountry\":\"US\",\"reState\":\"NY\",\"reAddr\":\"5623  LGH  DKHGKG FHHHG \",\"reAddr2\":\"\",\"reZip\":\"10001\",\"reCity\":\"NEW YORK\",\"reConsinee\":\"WEST\",\"reTel\":\"5642256521\",\"reCctaxNo\":\"\",\"sdCompany\":\"SZ GM CO LTD\",\"sdCountryCode\":\"CN\",\"sdCountry\":\"CN\",\"sdState\":\"BAOAN\",\"sdAddr\":\"11 Blocks 202Huangtian Jinbi Industrial Zone, Baoan District, Shenzhen City\",\"sdAddr2\":\"\",\"sdAddr3\":\"\",\"sdCity\":\"SZ\",\"sdZip\":\"518102\",\"sdName\":\"SZ GM CO LTD\",\"sdTel\":\"13510522499\",\"pcs\":1,\"shipWeig\":0.5,\"packing\":\"WPX\",\"packagingType\":\"YOUR_PACKAGING\",\"descrType\":\"\",\"contents\":\"hello\",\"contents2\":\"\",\"contents3\":\"\",\"contentsCn\":\"\",\"decValue\":10,\"codCharge\":222,\"remark\":\"\",\"randAppendJobno\":1,\"invoice\":[{\"eDescrNamee\":\"SHOES\",\"descrName\":\"鞋子\",\"hsCode\":\"123123\",\"qty\":1,\"price\":10,\"totalPrice\":10,\"unit\":\"PCS\",\"gWeig\":0.02,\"nWeig\":0.02}]}";
    	ZmRequest client=new ZmRequest(appKey,appSecret,nonce,version);
    	// 这是打单的token获取接口，会自动解析返回值的token,订单接口这里要手动解析 切记
	String token=client.getToken(url + "/auth/access/token");
    	client.setToken(token);
    	
    	//4 调用打单接口
        Map<String,Object> params =new HashMap<String,Object>();
        params.put("body1", body);
        client.setDatas(params);
        
        String result = client.request(url+"/print", "post");
        System.out.println("打单返回:"+result);
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
 
	
}
