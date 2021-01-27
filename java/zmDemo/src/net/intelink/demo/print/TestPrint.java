package net.intelink.demo.print;

import java.util.HashMap;
import java.util.Map;

import net.intelink.demo.print.utils.ZmRequest;

/**
 *  ����򵥽ӿڲ���
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
    	
    	String body = "{\"coid\":\"HXUCN\",\"clno\":\"YAF\",\"hubInCode\":\"����FEDEX-X-IP\",\"jobno\":\"test2020122101\",\"reCompany\":\"WEST\",\"reCountryCode\":\"US\",\"reCountry\":\"US\",\"reState\":\"NY\",\"reAddr\":\"5623  LGH  DKHGKG FHHHG \",\"reAddr2\":\"\",\"reZip\":\"10001\",\"reCity\":\"NEW YORK\",\"reConsinee\":\"WEST\",\"reTel\":\"5642256521\",\"reCctaxNo\":\"\",\"sdCompany\":\"SZ GM CO LTD\",\"sdCountryCode\":\"CN\",\"sdCountry\":\"CN\",\"sdState\":\"BAOAN\",\"sdAddr\":\"11 Blocks 202Huangtian Jinbi Industrial Zone, Baoan District, Shenzhen City\",\"sdAddr2\":\"\",\"sdAddr3\":\"\",\"sdCity\":\"SZ\",\"sdZip\":\"518102\",\"sdName\":\"SZ GM CO LTD\",\"sdTel\":\"13510522499\",\"pcs\":1,\"shipWeig\":0.5,\"packing\":\"WPX\",\"packagingType\":\"YOUR_PACKAGING\",\"descrType\":\"\",\"contents\":\"hello\",\"contents2\":\"\",\"contents3\":\"\",\"contentsCn\":\"\",\"decValue\":10,\"codCharge\":222,\"remark\":\"\",\"randAppendJobno\":1,\"invoice\":[{\"eDescrNamee\":\"SHOES\",\"descrName\":\"Ь��\",\"hsCode\":\"123123\",\"qty\":1,\"price\":10,\"totalPrice\":10,\"unit\":\"PCS\",\"gWeig\":0.02,\"nWeig\":0.02}]}";
    	ZmRequest client=new ZmRequest(appKey,appSecret,nonce,version);
    	String token=client.getToken(url + "/auth/access/token");
    	client.setToken(token);
    	
    	//4 ���ô򵥽ӿ�
        Map<String,Object> params =new HashMap<String,Object>();
        params.put("body1", body);
        client.setDatas(params);
        
        String result = client.request(url+"/print", "post");
        System.out.println("�򵥷���:"+result);
	}

	 

	/**
	 * ���� �򵥽ӿ�
	 * @param request
	 * @param sourceToken
	 * @param body
	 *  �ɹ���Ӧ
	 *  {body":{
	 *			"airwayBillNumber":"3678381980",
	 *			"airwayBillNumbers":["3678381980"],
	 *			"conditionCode":"success",
	 *			"conditionData":"",
	 *			"downLoadUrls":["http://139.159.209.189:8090/group1/M00/B4/C9/wKgAFF_KBtKARcDEAAAVU18tw04142.pdf"],
	 *			"exceptionSource":"pos",
	 *			"flag":0,// �ɹ�0       ��0 �쳣
	 *			"mergeLabelUrl":"http://139.159.209.189:8090/group1/M00/B4/C9/wKgAFF_KBtKARcDEAAAVU18tw04142.pdf",
	 *			"messageReference":"d43f71f493af46b89ba3d4630a6842a7",
	 *			"orderId":"",
	 *			"pieceMarks":[],
	 *			"proxyOrderId":"",
	 *			"resultReturnMode":1
	 *		},
	 *		"message":"����ɹ�",
	 *		"result_code":0
	 *	}
	 *  ʧ����Ӧ
	 *{"body":{
	 *			"conditionCode":"1",
	 *			"conditionData":"û�л�ȡ���ͻ��򵥹���",
	 *			"exceptionSource":"pos",
	 *			"flag":1,
	 *			"pieceMarks":[],
	 *			"proxyOrderId":"",
	 *			"resultReturnMode":1
	 *		},
	 *		"message":"����ɹ�",
	 *		"result_code":0
	 *	}
	 */
 
	
}
