using Newtonsoft.Json;
using OopstonApi.Api;
using System;
using System.Collections.Generic;


namespace OopstonApi
{
    class Program
    {


        static void Main(string[] args)
        {
            requestPrint( );
        }
         

        // 调用哲盟打单接口
        private static void requestPrint()
        {
            string url = "http://121.37.214.18:8001/pos-web";
            string appKey = "YW80606_HYGJ";
            string appSecret = "816e76869da0542e1a853a9fda8445e25";
            string nonce = "slnkda";
            string  version= "1.0";
            ZmRequest client = new ZmRequest(appKey, appSecret, nonce, version);
            // 1 获取token   这是打单的token获取接口，会自动解析返回值的token,订单接口这里要手动解析 切记
            string token = client.getToken(url + "/auth/access/token");
            client.setToken(token);


            String jsonStr = "{\"coid\":\"HXUCN\",\"clno\":\"YAF\",\"hubInCode\":\"义乌FEDEX-X-IP\",\"jobno\":\"test2020122101\",\"reCompany\":\"WEST\",\"reCountryCode\":\"US\",\"reCountry\":\"US\",\"reState\":\"NY\",\"reAddr\":\"5623  LGH  DKHGKG FHHHG \",\"reAddr2\":\"\",\"reZip\":\"10001\",\"reCity\":\"NEW YORK\",\"reConsinee\":\"WEST\",\"reTel\":\"5642256521\",\"reCctaxNo\":\"\",\"sdCompany\":\"SZ GM CO LTD\",\"sdCountryCode\":\"CN\",\"sdCountry\":\"CN\",\"sdState\":\"BAOAN\",\"sdAddr\":\"11 Blocks 202Huangtian Jinbi Industrial Zone, Baoan District, Shenzhen City\",\"sdAddr2\":\"\",\"sdAddr3\":\"\",\"sdCity\":\"SZ\",\"sdZip\":\"518102\",\"sdName\":\"SZ GM CO LTD\",\"sdTel\":\"13510522499\",\"pcs\":1,\"shipWeig\":0.5,\"packing\":\"WPX\",\"packagingType\":\"YOUR_PACKAGING\",\"descrType\":\"\",\"contents\":\"hello\",\"contents2\":\"\",\"contents3\":\"\",\"contentsCn\":\"\",\"decValue\":10,\"codCharge\":222,\"remark\":\"\",\"randAppendJobno\":1,\"invoice\":[{\"eDescrNamee\":\"SHOES\",\"descrName\":\"鞋子\",\"hsCode\":\"123123\",\"qty\":1,\"price\":10,\"totalPrice\":10,\"unit\":\"PCS\",\"gWeig\":0.02,\"nWeig\":0.02}]}";
            // 封装参数
            Dictionary<String, String> datas = new Dictionary<String, String>();
            datas.Add("body1", jsonStr);
            client.setDatas(datas);
            
            // 订单接口调用
            string result = client.request(url + "/print", "POST");
            Console.WriteLine(result);
        }
    } 
    
}
