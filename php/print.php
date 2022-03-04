<?php

class ZmRequest{
    var $timeOut = 15 * 60 ;// 超时时间（单位:s）
    var $appKey;
    var $appSecret;
    var $nonce;
    var $version;
    var $ts;
    var $token;
    var array $datas;


    /**

     *      哲盟发放授权格式：
     *      客户代号:YT8060
     *      账号:YT8060_HTYGXJ
     *      秘钥:816e7669d18542e57853a9fd32445e25
     *      指定路线:义乌FEDEX-X-IP,义乌FEDEX-A-IP
     * @param $appKey    账号
     * @param $appSecret 秘钥
     * @param $nonce     如：slnkda   可以随机
     * @param $version   默认1.0
     */
    function __construct( $appKey, $appSecret,$nonce,$version ) {
        $this->appKey = $appKey;
        $this->appSecret = $appSecret;
        $this->nonce=$nonce;
        $this->version=$version;
        $this->ts=$this->getTimeStamp();

    }

    function setToken($token) {
            $this->token = $token;
    }
    function setTimeOut($timeOut){
        $this->timeOut = timeOut;
    }
    function setDatas($datas){
        $this->datas = $datas;
    }

    //获取token  $url.'/auth/access/token/'
    function getToken($url){
        $post_data = array(
            'appKey' => $this->appKey,
            'appSecret' =>  $this->appSecret
        );
        $header = array('Content-type:application/x-www-form-urlencoded' );
        $obj= $this->sendPost($url, $post_data,$header,"POST");
        return $obj['body'];
    }

// 伪装token
    function camouflageToken()
    {

        $tokenArr = array(
            'nonce' => $this->nonce,
            'timestamp' => $this->ts,
            'token' => $this->token,
        );
        $jsonStr=json_encode($tokenArr);
        var_dump("伪装token json字符串：",$jsonStr);
        $tokenStr = base64_encode($jsonStr);
        var_dump('伪装token base64',$tokenStr);
        $tokenStr = strtr($tokenStr, array('a' => '-', 'c' => '#', 'x' => '^', 'M' => '$'));
        var_dump('伪装token 替换后',$tokenStr);
        return $tokenStr;
    }
    function getTimeStamp() {
        list($microsecond , $time) = explode(' ', microtime()); //' '中间是一个空格
        return (float)sprintf('%.0f',(floatval($microsecond)+floatval($time))*1000);
    }

    /**
     * 计算签名
     *  //2.2.1. 拼接所有参数（表单参数和http header原始值） ，将所有参数自然排序，然后做拼接（参数无值的无需拼接）。如： a=1&b=2&c=3&d=4
    //2.2.2. 将已经拼接好的参数字符串Base64. 如：Base64(str)
    //2.2.3. 将Base64后的字符串+appSecret做MD5。 如：MD5（Base64(str)+appSecret）
    //2.2.4. 将MD5后的字符串转换成大写
     * @param array $printData
     * @param $appSecret
     * @return string
     */
    function genSign(){
        $printData = array( );
        foreach ($this->datas as $key=>$value) {
            $printData[$key]=$value;
        }
        // 计算签名
        $printData['version']=$this->version;
        $printData['nonce']=$this->nonce;
        $printData['timestamp']=$this->ts;
        $printData['token']=$this->token;
        ksort($printData);

        $str="";
        foreach ($printData as $key=>$value) {
            if (strlen ($str)>0){
                $str=$str."&".$key."=".$value;
            }else{
                $str=$str."".$key."=".$value;
            }
        }
        $paramStr=$str;

        $base64Str = base64_encode($paramStr);
        var_dump("签名计算字符串",$paramStr);
        var_dump("签名计算字base64符串",$base64Str);
        $md5=md5($base64Str.$this->appSecret);
        var_dump("签名计算字md5符串",$md5);
        $sign = strtoupper($md5);
        return $sign;
    }


    function request($url,$method) {
        return $this->sendPost($url,$this->datas,$this->getheaders(),$method);
    }
    function sendPost($url, $params, $headers,$method) {
        $data = http_build_query($params);

        print_r($headers);
        $ch = curl_init ();
        //curl_setopt($ch, CURLOPT_PROXY, "127.0.0.1");
        //curl_setopt($ch, CURLOPT_PROXYPORT, "8888");

        curl_setopt ( $ch, CURLOPT_URL, $url );
        curl_setopt ( $ch, CURLOPT_RETURNTRANSFER, 1 );
        curl_setopt ( $ch, CURLOPT_CUSTOMREQUEST, $method );
        curl_setopt ( $ch, CURLOPT_POSTFIELDS, $data );
        curl_setopt ( $ch, CURLOPT_HTTPHEADER, $headers );
        curl_setopt ( $ch, CURLOPT_TIMEOUT, 60 );
        $result = curl_exec ( $ch );
        curl_close ( $ch );
        $json=json_decode($result,true);
        return $json;
    }

    function getheaders(){
        // 计算伪装token
        $tokenPlus = $this->camouflageToken();
        var_dump( '伪装token ', $tokenPlus);

        $sign = $this->genSign();
        $printData['sign']=$sign;

        var_dump('签名：',$sign);
        $header=array(
            'Content-type:application/x-www-form-urlencoded',
            'token:'.$tokenPlus,
            'sign:'.$sign,
            'version:'.$this->version
        );
        return $header;
    }

}

function callPrint(){

    $url="http://121.37.214.18:8001/pos-web";
    $appKey='YW80606_HYGJ';
    $appSecret='816e76869da0542e1a853a9fda8445e25';
    $nonce='slnkda';
    $version='1.0';

    //打单参数
    $body1= "{\"coid\":\"HXUCN\",\"clno\":\"YAF\",\"hubInCode\":\"义乌FEDEX-X-IP\",\"jobno\":\"test2020122101\",\"reCompany\":\"WEST\",\"reCountryCode\":\"US\",\"reCountry\":\"US\",\"reState\":\"NY\",\"reAddr\":\"5623  LGH  DKHGKG FHHHG \",\"reAddr2\":\"\",\"reZip\":\"10001\",\"reCity\":\"NEW YORK\",\"reConsinee\":\"WEST\",\"reTel\":\"5642256521\",\"reCctaxNo\":\"\",\"sdCompany\":\"SZ GM CO LTD\",\"sdCountryCode\":\"CN\",\"sdCountry\":\"CN\",\"sdState\":\"BAOAN\",\"sdAddr\":\"11 Blocks 202Huangtian Jinbi Industrial Zone, Baoan District, Shenzhen City\",\"sdAddr2\":\"\",\"sdAddr3\":\"\",\"sdCity\":\"SZ\",\"sdZip\":\"518102\",\"sdName\":\"SZ GM CO LTD\",\"sdTel\":\"13510522499\",\"pcs\":1,\"shipWeig\":0.5,\"packing\":\"WPX\",\"packagingType\":\"YOUR_PACKAGING\",\"descrType\":\"\",\"contents\":\"hello\",\"contents2\":\"\",\"contents3\":\"\",\"contentsCn\":\"\",\"decValue\":10,\"codCharge\":222,\"remark\":\"\",\"randAppendJobno\":1,\"invoice\":[{\"eDescrNamee\":\"SHOES\",\"descrName\":\"鞋子\",\"hsCode\":\"123123\",\"qty\":1,\"price\":10,\"totalPrice\":10,\"unit\":\"PCS\",\"gWeig\":0.02,\"nWeig\":0.02}]}";
    $datas = array(
        'body1' => $body1
    );
    $request = new ZmRequest($appKey,$appSecret,$nonce,$version);
    //1 获取原始token // 这是打单的token获取接口，会自动解析返回值的token,订单接口这里要手动解析 切记
    $sourceToken=$request->getToken($url.'/auth/access/token/');
    var_dump(  '原始token:', $sourceToken);
    $request->setToken($sourceToken);
    $request->setDatas($datas);
    var_dump('调用打单接口');
    $res = $request->request($url . '/print/',"POST");
    var_dump($res);
}

callPrint();

