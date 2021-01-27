using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using System.IO;
using System.Net;
using System.Net.Security;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using Newtonsoft.Json;

namespace OopstonApi.Api
{
    class ZmRequest
    {

          private string appKey;
          private string appSecret;
          private string token;
          private string ts;
          private string nonce;
          private string version;

         private int timeOut = 120000;

        Dictionary<String, String> datas;

        /// <summary>
        ///    哲盟发放授权格式：
        ///    客户代号:YT8060
        ///    账号:YT8060_HTYGXJ
        ///    秘钥:816e7669d18542e57853a9fd32445e25
        ///    指定路线:义乌FEDEX-X-IP,义乌FEDEX-A-IP
        /// </summary>
        /// <param name="appKey">账号</param>
        /// <param name="appSecret">秘钥</param>
        /// <param name="nonce"> 如：slnkda   可以随机</param>
        /// <param name="version">默认1.0</param>
        public ZmRequest(string appKey, string appSecret,   string nonce, string version)
        {
            this.appKey = appKey;
            this.appSecret = appSecret;
            this.ts = this.GetTimeStamp();
            this.nonce = nonce;
            this.version = version;
        }

        public void setToken(string token) {
            this.token = token;
        }
        public void setTimeOut(int timeOut)
        {
            this.timeOut = timeOut;
        }
        public void setDatas(Dictionary<String, String> datas)
        {
            this.datas = datas;
        }
        // 获取token
        public  string getToken(string  url)
        {
            Dictionary<String, String> datas = new Dictionary<String, String>();
            datas.Add("appKey", this.appKey);
            datas.Add("appSecret", this.appSecret);
            //
            Dictionary<String, String> headers = new Dictionary<String, String>();
            headers.Add("contentType", "application/x-www-form-urlencoded");
            string str = this.http(url, datas, headers,"POST");

            dynamic retObj = JsonConvert.DeserializeObject<dynamic>(str);
            string token = retObj["body"];

            return token;
        }


        /// 伪装token 
        public string CamouflageToken()
        {
            string content1 = "{\"nonce\":\"" + this.nonce + "\",\"timestamp\":" + this.ts + ",\"token\":\"" + this.token + "\"}";
            string newToken = Convert.ToBase64String(Encoding.UTF8.GetBytes(content1));
            newToken = newToken.Replace("a", "-").Replace("c", "#").Replace("x", "^").Replace("M", "$");
            return newToken;
        }


        /// <summary>
        /// datas  具体接口的表单参数  如：打单参数  body1:{}
        /// </summary>
        /// <param name="datas"></param>
        /// <returns></returns>
        public Dictionary<string, string> getHeaders()
        {


            // 计算伪装token
            string tokenPlus = this.CamouflageToken();

            //计算签名
            Dictionary<String, String> map = new Dictionary<String, String>();
            foreach (KeyValuePair<string, string> kv in this.datas)
            {
                map.Add(kv.Key, kv.Value);
            }

            map.Add("nonce", this.nonce);
            map.Add("timestamp", this.ts);
            map.Add("token", this.token);
            map.Add("version", this.version);

            string sign = this.GenSign(map);


            // 7.1 封装头信息
            Dictionary<String, String> headers = new Dictionary<String, String>();
            headers.Add("token", tokenPlus);
            headers.Add("sign", sign);
            headers.Add("version", version);
            headers.Add("contentType", "application/x-www-form-urlencoded");
            Console.WriteLine("header 封装完成          ");
            return headers;
        }

        /// <summary>
        ///  获取当前时间戳
        /// </summary>
        /// <returns></returns>
        public  string GetTimeStamp()
        {
            long currentTicks = DateTime.Now.Ticks;
            DateTime dtFrom = new DateTime(1970, 1, 1, 0, 0, 0, 0);
            long currentMillis = (currentTicks - dtFrom.Ticks) / 10000;

            //TimeSpan ts = DateTime.UtcNow - new DateTime(1970, 1, 1, 0, 0, 0, 0);
            return Convert.ToInt64(currentMillis).ToString();
        }

        /// <summary>
        /// 生成md5
        /// </summary>
        /// <param name="str"></param>
        /// <returns></returns>
        public  string GetMd5(string str)
        {
            System.Security.Cryptography.MD5CryptoServiceProvider md5 = new MD5CryptoServiceProvider();
            string a = BitConverter.ToString(md5.ComputeHash(System.Text.Encoding.UTF8.GetBytes(str)));
            a = a.Replace("-", "");
            return a.ToUpper();
        }




        /// <summary>
        /// 计算签名
        /// "3e251e6eb0774d358e99a4d6e1ff9cb0"
        /// </summary>
        /// <param name="map"></param>
        /// <param name="appSecret"></param>
        /// <returns></returns>
        public  string GenSign(Dictionary<String, String> map)
        {

            var dicSort = from objDic in map orderby objDic.Key ascending select objDic;

            StringBuilder buffer = new StringBuilder("");
            foreach (KeyValuePair<string, string> kv in dicSort)
            {
                string key = kv.Key;
                string value = kv.Value;
                if (buffer.Length > 1)
                {
                    buffer.Append("&");
                }
                buffer.Append(key).Append("=").Append(value);
            }
            string str = buffer.ToString();
            string base64 = Convert.ToBase64String(Encoding.UTF8.GetBytes(str));
            string sign = GetMd5(base64 + appSecret);
            Console.WriteLine(">> md5 计算签名结果：" + sign);
            return sign;
        }



        public String request(string url,  string method)
        {
            String result = "";
            result = this.request(url, this.datas,  method);
            return result;
        }

        //接口请求
        public String request(string url,Dictionary<string, string> datas, string method)
        {
            // 计算头信息
            Dictionary<string, string> headers = this.getHeaders();
            String result = "";
            result = this.http(url,datas, headers, method);
            return result;
        }


        /// <summary>
        ///  哲盟公共调用接口
        /// </summary>
        /// <param name="url"> 接口地址</param>
        /// <param name="datas">请求KV表单参数  </param>
        /// <param name="headers">http 头信息</param>
        /// <returns></returns>
        private   string http(string url, Dictionary<String, String> datas, Dictionary<String, String> headers,string method)
        {

            ServicePointManager.Expect100Continue = true;
            ServicePointManager.SecurityProtocol = (SecurityProtocolType)192 | (SecurityProtocolType)768 | (SecurityProtocolType)3072; 
            ServicePointManager.ServerCertificateValidationCallback = new RemoteCertificateValidationCallback(CheckValidationResult);


            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.Method = method.ToUpper();
            request.ContinueTimeout = this.timeOut;
            request.ReadWriteTimeout = this.timeOut;

            // request.Accept = "application/json";

            if (null != headers)
            {
                // 设置头信息
                foreach (KeyValuePair<string, string> kv in headers)
                {
                    string key = kv.Key;
                    string value = kv.Value;
                    if ("contentType".Equals(key))
                    {
                        request.ContentType = value;// "application/x-www-form-urlencoded";
                    }
                    request.Headers.Add(key, value);
                }
            }


            // 拼接数据
            StringBuilder buffer = new StringBuilder("");
            if (null != datas)
            {
                foreach (KeyValuePair<string, string> kv in datas)
                {
                    string key = kv.Key;
                    string value = kv.Value;
                    if (buffer.Length > 1)
                    {
                        buffer.Append("&");
                    }
                    buffer.Append(key).Append("=").Append(value);
                }

            }
            string data = buffer.ToString();
            byte[] postBytes = Encoding.GetEncoding("utf-8").GetBytes(data);
            // String ss = System.Web.HttpUtility.UrlEncode(data, System.Text.Encoding.UTF8);
            // postBytes = System.Text.Encoding.UTF8.GetBytes(data);
            request.ContentLength = postBytes.Length;

            request.GetRequestStream().Write(postBytes, 0, postBytes.Length);

            HttpWebResponse response;
            try
            {
                response = (HttpWebResponse)request.GetResponse();
            }
            catch (WebException ex)
            {
                response = (System.Net.HttpWebResponse)ex.Response;

            }


            string encoding = response.ContentEncoding;
            if (encoding == null || encoding.Length < 1)
            {
                encoding = "UTF-8"; //默认编码  
            }
            StreamReader reader = new StreamReader(response.GetResponseStream(), Encoding.UTF8);
            string retString = reader.ReadToEnd();
            Console.WriteLine(retString);
            return retString;
        }
        public static bool CheckValidationResult(object sender, X509Certificate certificate, X509Chain chain, SslPolicyErrors errors)
        {
            return true; //总是接受  
        }
    }
}
