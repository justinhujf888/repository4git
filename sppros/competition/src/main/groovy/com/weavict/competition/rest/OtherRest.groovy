package com.weavict.competition.rest

import com.alibaba.fastjson2.JSON
import com.aliyun.credentials.Client
import com.aliyun.credentials.models.Config
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse

import com.aliyun.oss.OSS
import com.aliyun.oss.common.utils.BinaryUtil
import com.aliyun.oss.internal.OSSHeaders
import com.aliyun.oss.model.CannedAccessControlList
import com.aliyun.oss.model.ObjectMetadata
import com.aliyun.oss.model.PolicyConditions
import com.aliyun.oss.model.PutObjectRequest
import com.aliyun.oss.model.StorageClass

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.DateUtil
import com.weavict.common.util.MathUtil
import com.weavict.competition.entity.Buyer
import com.weavict.competition.entity.Work
import com.weavict.competition.module.RedisApi

//import com.weavict.website.common.ImgCompress
import com.weavict.website.common.OtherUtils
import com.yicker.utility.DES
import darabonba.core.client.ClientOverrideConfiguration
import groovy.json.JsonSlurper
import jakarta.websocket.RemoteEndpoint
import jakarta.ws.rs.GET
import jodd.datetime.JDateTime
import jodd.datetime.Period
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties
import org.springframework.cache.annotation.Cacheable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType

import java.util.concurrent.CompletableFuture

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
        JDateTime jdt = new JDateTime(new Date());
        String ym = "${jdt.getYear() as String}/${jdt.getMonth() as String}";
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
                         OSS client = OtherUtils.genOSSClient(query.appId as String);
                         long expireTime = 30;
                         long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
                         PolicyConditions policyConds = new PolicyConditions();
                         policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
                         String postPolicy = client.generatePostPolicy(new Date(expireEndTime), policyConds);
                         String encodedPolicy = BinaryUtil.toBase64String(postPolicy.getBytes("utf-8"));
                         String postSignature = client.calculatePostSignature(postPolicy);
                         client.shutdown();
                         return ["accessId":redisApi.ganAliYunStsValue(query.appId as String,"accessId"),"accessKey":redisApi.ganAliYunStsValue(query.appId as String,"accessKey"),"policy":encodedPolicy,"signature":postSignature,"securityToken":redisApi.ganAliYunStsValue(query.appId as String,"securityToken"),"bucketUrl":redisApi.ganAliYunStsValue(query.appId as String,"bucketUrl"),"expire":String.valueOf(expireEndTime / 1000),"region":OtherUtils.givePropsValue("ali_oss_region"),"bucketName":OtherUtils.givePropsValue("ali_oss_bucketName")];
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
    @Path("/checkScanCode")
    String checkScanCode(@RequestBody Map<String,Object> query)
    {
        try
        {
            DES crypt = new DES(OtherUtils.givePropsValue("publickey"));
            def jsonSlpuer = new JsonSlurper();
            def obj = jsonSlpuer.parseText(crypt.decrypt(query.datas));
            JDateTime jt = new JDateTime(DateUtil.parse(obj.datas.date as String,"yyyy-MM-dd HH:mm:ss"));
            JDateTime jd = new JDateTime(new Date());
            Period period = new Period(jd,jt);
            if (period.getMinutes() > 5 && obj.datas.overTime as boolean)
            {
                return """{"status":"FA_OVERTIME"}""";
            }
            else
            {
                return """{"status":"OK","qType":"${obj.datas.qType}","url":"${obj.datas.url}","param":"${obj.datas.param}"}""";
            }
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
            OSS ossClient = OtherUtils.genOSSClient(query.appId as String);
            ossClient.deleteObject(redisApi.ganAliYunStsValue(query.appId as String,"ali_oss_bucketName"), query.imgPath);
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
            OSS ossClient = OtherUtils.genOSSClient(query.appId);
            PutObjectRequest putObjectRequest = new PutObjectRequest(redisApi.ganAliYunStsValue(query.appId as String,"ali_oss_bucketName"), query.filePathName as String, new ByteArrayInputStream(objectMapper.writeValueAsString(
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
            Config credentialConfig = new Config().setType("sts").setAccessKeyId(redisApi.ganAliYunStsValue(query.appId as String,"accessId")).setAccessKeySecret(redisApi.ganAliYunStsValue(query.appId as String,"accessKey")).setSecurityToken(redisApi.ganAliYunStsValue(query.appId as String,"securityToken"));
            Client credentialClient = new Client(credentialConfig);
            com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
            config.setCredential(credentialClient);
            config.endpoint = redisApi.ganAliYunStsValue(query.appId as String,"ali_sms_endPoint");
            com.aliyun.dypnsapi20170525.Client client = new com.aliyun.dypnsapi20170525.Client(config);

            String templateCode = "";
            String templateParam = "";
            if (query.accessCode.equals("regist"))
            {
                templateCode = "100001";
                templateParam = """{"code":"${redisApi.userBean.phoneCode()}","min":"5"}""".toString();
            }
            else if (query.accessCode.equals("editPassword"))
            {
                templateCode = "100003";
                templateParam = """{"code":"${redisApi.userBean.phoneCode()}","min":"5"}""".toString();
            }
            println templateParam;
            SendSmsVerifyCodeRequest sendSmsVerifyCodeRequest = new SendSmsVerifyCodeRequest()
                    .setPhoneNumber(query.phone as String)
                    .setTemplateCode(templateCode)
//                    .setTemplateParam("{\"code\":\"##code##\",\"min\":\"5\"}")
                    .setTemplateParam(templateParam)
                    .setSignName(redisApi.ganAliYunStsValue(query.appId as String,"ali_sms_SignName"));
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
//                templateParam = """{"code":"${redisApi.userBean.phoneCode()}"}""".toString();
//            }
//            else if (query.accessCode.equals("editPassword"))
//            {
//                templateCode = "SMS_169175063";
//                templateParam = """{"code":"${redisApi.userBean.phoneCode()}"}""".toString();
//            }
//
//            SendSmsRequest sendSmsRequest = new SendSmsRequest().setPhoneNumbers(query.phone).setSignName(redisApi.ganAliYunStsValue(query.appId as String,"ali_sms_SignName")).setTemplateCode(templateCode).setTemplateParam(templateParam);
//            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);

            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "smsInfo":["returnInfo":({
                         return resp;
                     }).call(),
                        "templateParam":({
                            DES crypt = new DES(OtherUtils.givePropsValue("publickey"));
                            return crypt.encrypt(templateParam);
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
                         return ["expiration":redisApi.ganAliYunStsValue(query.appId as String,"expiration"),"accessId":redisApi.ganAliYunStsValue(query.appId as String,"accessId"),"accessKey":redisApi.ganAliYunStsValue(query.appId as String,"accessKey"),"securityToken":redisApi.ganAliYunStsValue(query.appId as String,"securityToken"),"requestId":redisApi.ganAliYunStsValue(query.appId as String,"requestId"),"endPoint":redisApi.ganAliYunStsValue(query.appId as String,"ali_oss_endPoint"),"region":redisApi.ganAliYunStsValue(query.appId as String,"ali_oss_region"),"bucketName":redisApi.ganAliYunStsValue(query.appId as String,"ali_oss_bucketName"),"bucketUrl":redisApi.ganAliYunStsValue(query.appId as String,"bucketUrl")];
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
    @Cacheable("test")
    String test(@RequestBody Map<String,Object> query)
    {
        ObjectMapper objectMapper = buildObjectMapper();
//        NotificationResource.broadcast("""{"id":"111","type":"publcMsg","mode":true,"data":"${MathUtil.getPNewId()}"}""".toString());

//        Work work = new Work();
//        work.id = MathUtil.getPNewId();
//        work.name = "Math Product";
//        work.appId = "001";
//        work.buyer = null;
//        Map map = [:];
//        map["name"] = "iuy";
//        map["org"] = "提供香港CN2 GIA、香港CMI、日本软银、美国CN2 GIA、美国CMIN2、荷兰CUII、加拿大CN2 GIA、迪拜国际BGP系列的VPS。最低1Gbps，最高10Gbps带宽，根据VPS配置从低到高给与1Gbps~10Gbps带宽，支持一键切换IP（收费）、免费快照/备份、可在多个数据中心自由切换。2004年成立运作至今，隶属于加拿大IT7公司";
//        map["tempMap"] = ["workName":"火山枫林","remark":"三上悠亚","ling":[1,2,3,4,5,6,7,8]]
//        work.otherFields = map;
//        redisApi.userBean.updateTheObject(work);
//        return """{"status":"OK"}""";

        return objectMapper.writeValueAsString(
                ["status":"OK",
                 "datas":({
                     return new Buyer();
                 }).call()
                ]);

//        try
//        {
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
//            return """{"status":"OK"}""";
//        }
//        catch (Exception e)
//        {
//            processExcetion(e);
//            return """{"status":"FA_ER"}""";
//        }

//package demo;
//
//import com.aliyun.auth.credentials.Credential;
//import com.aliyun.auth.credentials.provider.DefaultCredentialProvider;
//import com.aliyun.core.http.HttpClient;
//import com.aliyun.core.http.HttpMethod;
//import com.aliyun.core.http.ProxyOptions;
//import com.aliyun.httpcomponent.httpclient.ApacheAsyncHttpClientBuilder;
//import com.aliyun.sdk.service.dypnsapi20170525.models.*;
//import com.aliyun.sdk.service.dypnsapi20170525.*;
//import com.google.gson.Gson;
//import darabonba.core.RequestConfiguration;
//import darabonba.core.client.ClientOverrideConfiguration;
//import darabonba.core.utils.CommonUtil;
//import darabonba.core.TeaPair;
//
////import javax.net.ssl.KeyManager;
////import javax.net.ssl.X509TrustManager;
//import java.net.InetSocketAddress;
//import java.time.Duration;
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.io.*;
//
//public class SendSmsVerifyCode {
//    public static void main(String[] args) throws Exception {
//
//        // HttpClient Configuration
//        /*HttpClient httpClient = new ApacheAsyncHttpClientBuilder()
//                .connectionTimeout(Duration.ofSeconds(10)) // Set the connection timeout time, the default is 10 seconds
//                .responseTimeout(Duration.ofSeconds(10)) // Set the response timeout time, the default is 20 seconds
//                .maxConnections(128) // Set the connection pool size
//                .maxIdleTimeOut(Duration.ofSeconds(50)) // Set the connection pool timeout, the default is 30 seconds
//                // Configure the proxy
//                .proxy(new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress("<your-proxy-hostname>", 9001))
//                        .setCredentials("<your-proxy-username>", "<your-proxy-password>"))
//                // If it is an https connection, you need to configure the certificate, or ignore the certificate(.ignoreSSL(true))
//                .x509TrustManagers(new X509TrustManager[]{})
//                .keyManagers(new KeyManager[]{})
//                .ignoreSSL(false)
//                .build();*/
//
//        // Configure Credentials authentication information
//        DefaultCredentialProvider provider = DefaultCredentialProvider.builder().build();
//
//        // Configure the Client
//        try (AsyncClient client = AsyncClient.builder()
//                .region("ap-southeast-1") // Region ID
//        //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
//                .credentialsProvider(provider)
//        //.serviceConfiguration(Configuration.create()) // Service-level configuration
//        // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
//                .overrideConfiguration(
//                        ClientOverrideConfiguration.create()
//                        // Endpoint 请参考 https://api.aliyun.com/product/Dypnsapi
//                                .setEndpointOverride("dypnsapi.aliyuncs.com")
//                        //.setConnectTimeout(Duration.ofSeconds(30))
//                )
//                .build()) {
//
//            // Parameter settings for API request
//            SendSmsVerifyCodeRequest sendSmsVerifyCodeRequest = SendSmsVerifyCodeRequest.builder()
//                    .phoneNumber("13268990066")
//                    .templateCode("100001")
//                    .templateParam("{\"code\":\"##code##\",\"min\":\"5\"}")
//                    .signName("速通互联验证码")
//            // Request-level configuration rewrite, can set Http request parameters, etc.
//            // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
//                    .build();
//
//            // Asynchronously get the return value of the API request
//            CompletableFuture<SendSmsVerifyCodeResponse> response = client.sendSmsVerifyCode(sendSmsVerifyCodeRequest);
//            // Synchronously get the return value of the API request
//            SendSmsVerifyCodeResponse resp = response.get();
//            System.out.println(new Gson().toJson(resp));
//            // Asynchronous processing of return values
//            /*response.thenAccept(resp -> {
//                System.out.println(new Gson().toJson(resp));
//            }).exceptionally(throwable -> { // Handling exceptions
//                System.out.println(throwable.getMessage());
//                return null;
//            });*/
//
//        }
//    }
//
//}




//        package com.aliyun.sample;
//
//        import com.aliyun.tea.*;
//
//        public class Sample {
//
//            /**
//             * <b>description</b> :
//             * <p>使用凭据初始化账号Client</p>
//             * @return Client
//             *
//             * @throws Exception
//             */
//            public static com.aliyun.dypnsapi20170525.Client createClient() throws Exception {
//                // 工程代码建议使用更安全的无AK方式，凭据配置方式请参见：https://help.aliyun.com/document_detail/378657.html。
//                com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();
//                com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
//                        .setCredential(credential);
//                // Endpoint 请参考 https://api.aliyun.com/product/Dypnsapi
//                config.endpoint = "dypnsapi.aliyuncs.com";
//                return new com.aliyun.dypnsapi20170525.Client(config);
//            }
//
//            public static void main(String[] args_) throws Exception {
//
//                com.aliyun.dypnsapi20170525.Client client = Sample.createClient();
//                com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest sendSmsVerifyCodeRequest = new com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest()
//                        .setPhoneNumber("13268990066")
//                        .setTemplateCode("100001")
//                        .setTemplateParam("{\"code\":\"##code##\",\"min\":\"5\"}")
//                        .setSignName("速通互联验证码");
//                com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
//                try {
//                    com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse resp = client.sendSmsVerifyCodeWithOptions(sendSmsVerifyCodeRequest, runtime);
//                    System.out.println(new com.google.gson.Gson().toJson(resp));
//                } catch (TeaException error) {
//                    // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
//                    // 错误 message
//                    System.out.println(error.getMessage());
//                    // 诊断地址
//                    System.out.println(error.getData().get("Recommend"));
//                } catch (Exception _error) {
//                    TeaException error = new TeaException(_error.getMessage(), _error);
//                    // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
//                    // 错误 message
//                    System.out.println(error.getMessage());
//                    // 诊断地址
//                    System.out.println(error.getData().get("Recommend"));
//                }
//            }
//        }


    }

}