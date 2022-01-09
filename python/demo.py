# -*- coding:utf-8 -*-
import requests
import json
import time
import base64
import hashlib


# 参考开发文档中的地址
projectUrl='http://121.37.214.18:8001/pos-web'
url=projectUrl+"/ois/order/getAuth"
appKey='DGLUHT6874_YHECN'
appSecret='816e76869da054xe1af53a9fda8885e25'
token=''
newToken=''
sign=''
ts=int(round(time.time() * 1000)) 
nonce='slnkda'
version='1.0'

# 获取token 参数
postdatas = {'appKey': appKey,'appSecret': appSecret}
r = requests.post(url, data=postdatas)
authJsonStr=r.content  # 返回字节形式

authJson = json.loads(authJsonStr)
if 'false'==authJson['body']['ack']:
    print '获取token异常'
    exit

token=authJson['body']['token']
print 'token'
print token
# 字符串拼接
calToken='{"nonce":"'+nonce+'","timestamp":'+str(ts)+',"token":"'+token+'"}'
print '计算伪装token json字符串:'
print calToken

# 转base64
tokenStr= base64.b64encode(calToken).decode('utf8')
print "待替换的base64"
print tokenStr

# 3. 将 "a"->"-","c"->"#","x"->"^","M"->"$"字符替换
tokenStr=tokenStr.replace('a','-')
tokenStr=tokenStr.replace('c','#')
tokenStr=tokenStr.replace('x','^')
tokenStr=tokenStr.replace('M','$')
print "伪装后的token为："
print tokenStr
newToken=tokenStr

bodyJson='{"platformType":"ZYXT","referenceno":"testxD21121381362","refno":"","hubInCode":"D-USEXRP-PQ","pcs":"1","weig":0.209,"goodsType":"","codCharge":null,"codChargeCur":null,"remark":null,"decValueCur":"USD","buyerId":null,"sellerNick":"","isReturnsign":"N","labelUrl":"","insurance":null,"insuranceCur":"USD","ccPayment":"","ccCharge":null,"ccChargeCur":null,"extra1":"","extra2":"","extra3":"","extra4":"","extra5":"","oisInvoices":[{"descrName":"\u5fae\u7f29\u5c0f\u8d2d\u7269\u8f66","eDescrNamee":"Kids Role Play","qty":"1","price":18.65,"unit":"PCE","hsCode":"95030060","origin":"","uses":"","material":"","brand":"","sku":"57016229","transactionUrl":"","remark":"","nWeig":0.186,"unitWeig":0.186,"include":"","pcs":"1"}],"pieces":[{"actual":"0.209","length":"38.0","width":"25.0","height":"7.0"}],"reName":"Lori Michaelson","reCompany":"","reTel":"6038015138","retel2":"6038015138","reCountryCode":"US","reCountry":"US","reAddr":"14 St Mary Dr","reAddr2":"","reAddr3":"","reHouseNo":"","reZip":"03051","reState":"NH","reCity":"Hudson","reEmail":"","reCode":"","sdName":"DIDADI","sdCompany":"DIDADI LOGISTICS TECH LIMITED","sdTel":"00000000000","sdAddr":"Nanwan street19A, block B, wanguocheng, No.9 Pinglang RoadPingji Avenue, shanglilang community","sdCountry":"CN","sdCountryCode":"CN","sdState":"GUANGDONG","sdCity":"SHENZHEN","sdZip":"518000","sdEmail":""}'

paramsMap = {'body1': bodyJson}
paramsMap["nonce"]=nonce
paramsMap["timestamp"]= str(ts)
paramsMap["token"]=token
paramsMap["version"]=version

pinStr=""
for key in sorted(paramsMap):
   if(len(pinStr) > 0):
        pinStr=pinStr+"&" 
   pinStr=pinStr+(key+"="+paramsMap[key])

print '待计算签名的字符串'
# print pinStr
base64Str= base64.b64encode(pinStr).decode('utf8')
print '计算签名的字符串base64'
# print base64Str
sign= hashlib.md5(base64Str+appSecret).hexdigest().upper()

printUrl=projectUrl+'/ois/order/createOrder'

bd = {'body1': bodyJson}
headers = {'contentType': "application/x-www-form-urlencode","token":newToken,"sign":sign,"version":version}
req = requests.post(printUrl, data=bd,headers=headers)

print req.content  

