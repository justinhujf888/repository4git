package com.weavict.light.rest

import cn.hutool.core.date.DateUtil
import cn.hutool.crypto.SecureUtil
import com.alibaba.fastjson2.JSON
import com.aliyun.credentials.Client
import com.aliyun.credentials.models.Config
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse
import com.aliyun.oss.OSS
import com.aliyun.oss.common.utils.BinaryUtil
import com.aliyun.oss.internal.OSSHeaders
import com.aliyun.oss.model.*
import com.aliyuncs.CommonRequest
import com.aliyuncs.CommonResponse
import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.IAcsClient
import com.aliyuncs.http.MethodType
import com.aliyuncs.profile.DefaultProfile
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.aliyun.AliyunStsFactory
import com.weavict.light.module.RedisApi
import com.weavict.common.OtherUtils

//import com.weavict.website.common.ImgCompress

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import org.dromara.mica.mqtt.codec.MqttQoS
import org.dromara.mica.mqtt.spring.client.MqttClientTemplate
import org.springframework.beans.factory.annotation.Autowired

//import org.dromara.mica.mqtt.spring.server.MqttServerTemplate

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.RequestBody

import java.security.KeyPair
/**
 * Created by Justin on 2018/6/10.
 */
@Path("/other")
class OtherRest extends BaseRest
{
    @Context
    HttpServletRequest request;

    @Autowired
    RedisApi redisApi;

    @Autowired
    AliyunStsFactory aliyunStsFactory;

//    @Autowired
//    MqttServerTemplate mqttServerTemplate;

    @Autowired
    @Qualifier(MqttClientTemplate.DEFAULT_CLIENT_TEMPLATE_BEAN)
    MqttClientTemplate mqttClientTemplate;

    /**
     * 图片上传
     *
     * @param fileInputStream
     * @param disposition
     * @return
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/uploadimage")
    void uploadimage()
    {
//        InputStream inn = request.getInputStream();
//        int len = 0;
//        byte[] buffer = new byte[1024];
//        while ((len = inn.read(buffer)) > 0)
//        {
//            println(new String(buffer, 0, len));
//        }
//        JDateTime jdt = new JDateTime(new Date());
//        String ym = "${jdt.getYear() as String}/${jdt.getMonth() as String}";
        File fd = new File("""${request.getSession().getServletContext().getRealPath("/")}uploads/products/images/${ym}""");
        if (!fd.exists())
        {
            fd.mkdir();
        }
//        boolean userResize,int maxImgPoint,int updateImgPoint,
        RequestStreamUtil.uploadFile(request,fd.getPath(),false,{
            fileName,filePathName,jm ->
                println "${filePathName}";
                println jm.dump();

//                ImgCompress imgCom = null;//new ImgCompress(filePathName);
//                if (imgCom.getWidth() > 2000)
//                {
//                    imgCom.resizeByWidth(2000,filePathName);
//                }

//                ProductImages img = new ProductImages();
//                img.id = MathUtil.getPNewId();
//                img.isBannerImg = false;
//                img.isMasterImg = false;
//                img.isProductImg = false;
//                img.isVideo = false;
//                img.orderListNum = 0;
//                img.path = "${filePathName}/${fileName}";
//                Product product = new Product();
//                product.id = "";
//                img.product = product;
//                productsBean.uploadProductImages(img);
        });

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/genSignature")
    String genSignature(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "signatureInfo":({
                         OSS client = aliyunStsFactory.genOSSClient(query.appId as String);
                         long expireTime = 30;
                         long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
                         PolicyConditions policyConds = new PolicyConditions();
                         policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
                         String postPolicy = client.generatePostPolicy(new Date(expireEndTime), policyConds);
                         String encodedPolicy = BinaryUtil.toBase64String(postPolicy.getBytes("utf-8"));
                         String postSignature = client.calculatePostSignature(postPolicy);
                         client.shutdown();
                         return ["accessId":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"accessId"),"accessKey":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"accessKey"),"policy":encodedPolicy,"signature":postSignature,"securityToken":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"securityToken"),"bucketUrl":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"bucketUrl"),"expire":String.valueOf(expireEndTime / 1000),"region":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"ali_oss_region"),"bucketName":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"ali_oss_bucketName")];
                     }).call()
                    ]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Path("/checkScanCode")
//    String checkScanCode(@RequestBody Map<String,Object> query)
//    {
//        try
//        {
//            DES crypt = new DES(OtherUtils.givePropsValue("publickey"));
//            def jsonSlpuer = new JsonSlurper();
//            def obj = jsonSlpuer.parseText(crypt.decrypt(query.datas));
//            JDateTime jt = new JDateTime(DateUtil.parse(obj.datas.date as String,"yyyy-MM-dd HH:mm:ss"));
//            JDateTime jd = new JDateTime(new Date());
//            Period period = new Period(jd,jt);
//            if (period.getMinutes() > 5 && obj.datas.overTime as boolean)
//            {
//                return """{"status":"FA_OVERTIME"}""";
//            }
//            else
//            {
//                return """{"status":"OK","qType":"${obj.datas.qType}","url":"${obj.datas.url}","param":"${obj.datas.param}"}""";
//            }
//        }
//        catch (Exception e)
//        {
//            processExcetion(e);
//            return """{"status":"FA_ER"}""";
//        }
//    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/updateVersion")
    String updateVersion(@RequestBody Map<String,Object> query)
    {
        try
        {
            return """{"version":"0.9.27","apkurl":"http://m.daxiabang.club/xiashidai.apk","desc":"优化项目：……"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/delImgFormOss")
    String delImgFormOss(@RequestBody Map<String,Object> query)
    {
        try
        {
            //oss
            OSS ossClient = aliyunStsFactory.genOSSClient(query.appId as String);
            ossClient.deleteObject(aliyunStsFactory.ganAliYunStsValue(query.appId as String,"ali_oss_bucketName"), query.imgPath);
            ossClient.shutdown();
            //oss end
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/uploadFile2Oss")
    String uploadFile2Oss(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            // oss
            OSS ossClient = aliyunStsFactory.genOSSClient(query.appId);
            PutObjectRequest putObjectRequest = new PutObjectRequest(aliyunStsFactory.ganAliYunStsValue(query.appId as String,"ali_oss_bucketName"), query.filePathName as String, new ByteArrayInputStream(objectMapper.writeValueAsString(
                    ({return query.fileObj}).call()
            ).getBytes("UTF-8")));
            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
            if (query.fileAcl!=null)
            {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
                if (query.fileAcl.toUpperCase()=="PUBLIC")
                {
                    metadata.setObjectAcl(CannedAccessControlList.PublicRead);
                }
                putObjectRequest.setMetadata(metadata);
            }
            ossClient.putObject(putObjectRequest);
//            ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), query.filePathName as String, CannedAccessControlList.PublicRead);
            ossClient.shutdown();
            //oss end
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/sendSmsPublic")
    String sendSmsPublic(@RequestBody Map<String,Object> query)
    {
        try
        {
//            String vcode = "" + ((Math.random()) * 899999.0D + 100000.0D).toInteger();
//            println OtherUtils.givePropsValue("ali_sms_SignName");
//            query.appId = "temparky";
            ObjectMapper objectMapper = buildObjectMapper();
            Config credentialConfig = new Config().setType("sts").setAccessKeyId(aliyunStsFactory.ganAliYunStsValue(query.appId as String,"accessId")).setAccessKeySecret(aliyunStsFactory.ganAliYunStsValue(query.appId as String,"accessKey")).setSecurityToken(aliyunStsFactory.ganAliYunStsValue(query.appId as String,"securityToken"));
            Client credentialClient = new Client(credentialConfig);
            com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
            config.setCredential(credentialClient);
            config.endpoint = aliyunStsFactory.ganAliYunStsValue(query.appId as String,"ali_sms_endPoint");
            com.aliyun.dypnsapi20170525.Client client = new com.aliyun.dypnsapi20170525.Client(config);

            String templateCode = "";
            String templateParam = "";
            if (query.accessCode.equals("regist"))
            {
                templateCode = "100001";
                templateParam = """{"code":"${userBean.phoneCode()}","min":"5"}""".toString();
            }
            else if (query.accessCode.equals("editPassword"))
            {
                templateCode = "100003";
                templateParam = """{"code":"${userBean.phoneCode()}","min":"5"}""".toString();
            }
            println templateParam;
            SendSmsVerifyCodeRequest sendSmsVerifyCodeRequest = new SendSmsVerifyCodeRequest()
                    .setPhoneNumber(query.phone as String)
                    .setTemplateCode(templateCode)
//                    .setTemplateParam("{\"code\":\"##code##\",\"min\":\"5\"}")
                    .setTemplateParam(templateParam)
                    .setSignName(aliyunStsFactory.ganAliYunStsValue(query.appId as String,"ali_sms_SignName"));
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            SendSmsVerifyCodeResponse resp = client.sendSmsVerifyCodeWithOptions(sendSmsVerifyCodeRequest,runtime);
            // Synchronously get the return value of the API request
            println JSON.toJSONString(resp);


//            config.endpoint = "dysmsapi.aliyuncs.com";
//            Client client = new Client(config);
//
//            String templateCode = "";
//            String templateParam = "";
//            if (query.accessCode.equals("regist"))
//            {
//                templateCode = "SMS_169175064";
//                templateParam = """{"code":"${userBean.phoneCode()}"}""".toString();
//            }
//            else if (query.accessCode.equals("editPassword"))
//            {
//                templateCode = "SMS_169175063";
//                templateParam = """{"code":"${userBean.phoneCode()}"}""".toString();
//            }
//
//            SendSmsRequest sendSmsRequest = new SendSmsRequest().setPhoneNumbers(query.phone).setSignName(aliyunStsFactory.ganAliYunStsValue(query.appId as String,"ali_sms_SignName")).setTemplateCode(templateCode).setTemplateParam(templateParam);
//            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);

            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "smsInfo":["returnInfo":({
                         return resp;
                     }).call(),
                                "templateParam":({
//                            DES crypt = new DES(OtherUtils.givePropsValue("publickey"));
//                            return crypt.encrypt(templateParam);
                                    return SecureUtil.des(OtherUtils.givePropsValue("publickey").bytes).encryptHex(templateParam);
                                }).call()]
                    ]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/genAliOssAccessInfo")
    String genAliOssAccessInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "signatureInfo":({
                         return ["expiration":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"expiration"),"accessId":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"accessId"),"accessKey":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"accessKey"),"securityToken":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"securityToken"),"requestId":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"requestId"),"endPoint":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"ali_oss_endPoint"),"region":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"ali_oss_region"),"bucketName":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"ali_oss_bucketName"),"bucketUrl":aliyunStsFactory.ganAliYunStsValue(query.appId as String,"bucketUrl")];
                     }).call()
                    ]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/test")
    String test(@RequestBody Map<String,Object> query)
    {
        try
        {
            if (query.userName in [null,""])
            {
                KeyPair pair = SecureUtil.generateKeyPair("RSA");
                println pair.getPrivate();
                println pair.getPublic();
                mqttClientTemplate.publish("device/${query.deviceId}/cmd", JSON.toJSON([a:"abc"]), MqttQoS.QOS0);
            }
            else
            {
                println query.deviceId;
                println query.userName;
                println query.password;
            }

//            ObjectMapper objectMapper = new ObjectMapper();
//            // oss
//            OSS ossClient = OtherUtils.genOSSClient();
//
//
////            ossClient.deleteObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "a.txt");
//
//            PutObjectRequest putObjectRequest = new PutObjectRequest(OtherUtils.givePropsValue("ali_oss_bucketName"), "a.txt", new ByteArrayInputStream(objectMapper.writeValueAsString(
//                    """abcde adas"""
//            ).getBytes("UTF-8")));
//
//            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
//            metadata.setObjectAcl(CannedAccessControlList.PublicRead);
//            putObjectRequest.setMetadata(metadata);
//
//            ossClient.putObject(putObjectRequest);
////            ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), query.filePathName as String, CannedAccessControlList.PublicRead);
//            ossClient.shutdown();
//            //oss end
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

}
