package com.weavict.competition.rest

import com.aliyun.dysmsapi20170525.Client
import com.aliyun.dysmsapi20170525.models.SendSmsRequest
import com.aliyun.dysmsapi20170525.models.SendSmsResponse
import com.aliyun.oss.OSS
import com.aliyun.oss.common.utils.BinaryUtil
import com.aliyun.oss.internal.OSSHeaders
import com.aliyun.oss.model.CannedAccessControlList
import com.aliyun.oss.model.ObjectMetadata
import com.aliyun.oss.model.PolicyConditions
import com.aliyun.oss.model.PutObjectRequest
import com.aliyun.oss.model.StorageClass
import com.aliyun.teaopenapi.models.Config

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.DateUtil
import com.weavict.common.util.MathUtil
import com.weavict.competition.entity.Buyer
import com.weavict.competition.entity.Work
import com.weavict.competition.module.RedisApi

//import com.weavict.website.common.ImgCompress
import com.weavict.website.common.OtherUtils
import com.yicker.utility.DES
import groovy.json.JsonSlurper
import jakarta.ws.rs.GET
import jodd.datetime.JDateTime
import jodd.datetime.Period
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType

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
                         OSS client = OtherUtils.genOSSClient();
                         long expireTime = 30;
                         long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
                         PolicyConditions policyConds = new PolicyConditions();
                         policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
                         String postPolicy = client.generatePostPolicy(new Date(expireEndTime), policyConds);
                         String encodedPolicy = BinaryUtil.toBase64String(postPolicy.getBytes("utf-8"));
                         String postSignature = client.calculatePostSignature(postPolicy);
                         client.shutdown();
                         return ["accessId":redisApi.ganAliYunStsValue("accessId"),"accessKey":redisApi.ganAliYunStsValue("accessKey"),"policy":encodedPolicy,"signature":postSignature,"securityToken":redisApi.ganAliYunStsValue("securityToken"),"bucketUrl":redisApi.ganAliYunStsValue("bucketUrl"),"expire":String.valueOf(expireEndTime / 1000),"region":OtherUtils.givePropsValue("ali_oss_region"),"bucketName":OtherUtils.givePropsValue("ali_oss_bucketName")];
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
            OSS ossClient = OtherUtils.genOSSClient();
            ossClient.deleteObject(OtherUtils.givePropsValue("ali_oss_bucketName"), query.imgPath);
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
            OSS ossClient = OtherUtils.genOSSClient();
            PutObjectRequest putObjectRequest = new PutObjectRequest(OtherUtils.givePropsValue("ali_oss_bucketName"), query.filePathName as String, new ByteArrayInputStream(objectMapper.writeValueAsString(
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
            ObjectMapper objectMapper = buildObjectMapper();
            Config config = new Config().setAccessKeyId(OtherUtils.givePropsValue("ali_sms_AccessKeyId")).setAccessKeySecret(OtherUtils.givePropsValue("ali_sms_AccessKeySecret"));
            config.endpoint = "dysmsapi.aliyuncs.com";
            Client client = new Client(config);

            String templateCode = "";
            String templateParam = "";
            if (query.accessCode.equals("regist"))
            {
                templateCode = "SMS_169175064";
                templateParam = """{"code":"${redisApi.userBean.phoneCode()}"}""".toString();
            }
            else if (query.accessCode.equals("editPassword"))
            {
                templateCode = "SMS_169175063";
                templateParam = """{"code":"${redisApi.userBean.phoneCode()}"}""".toString();
            }

            SendSmsRequest sendSmsRequest = new SendSmsRequest().setPhoneNumbers(query.phone).setSignName(OtherUtils.givePropsValue("ali_sms_SignName")).setTemplateCode(templateCode).setTemplateParam(templateParam);
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);

            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "smsInfo":["returnInfo":({
                         return "";
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
                         return ["expiration":redisApi.ganAliYunStsValue("expiration"),"accessId":redisApi.ganAliYunStsValue("accessId"),"accessKey":redisApi.ganAliYunStsValue("accessKey"),"securityToken":redisApi.ganAliYunStsValue("securityToken"),"requestId":redisApi.ganAliYunStsValue("requestId"),"endPoint":OtherUtils.givePropsValue("ali_oss_endPoint"),"region":OtherUtils.givePropsValue("ali_oss_region"),"bucketName":OtherUtils.givePropsValue("ali_oss_bucketName"),"bucketUrl":redisApi.ganAliYunStsValue("bucketUrl")];
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

        return objectMapper.writeValueAsString(
                ["status":"OK",
                 "datas":({
                     List<Work> workList = redisApi.userBean.newQueryUtils(true,true).masterTable("work","work",[
                             ["sf":"id","bf":"id"],
                             ["sf":"otherfields","bf":"otherFields","convertType":"json"]
                     ])
                             .beanSetup(Work.class,null,null)
                             .where("otherfields->>'name' = 'iuy'",null,null,null)
                             .buildSql().run().content;
                     for(Work work in workList)
                     {
                         println work?.id;
                         println work?.otherFields;
                     }
                     return workList;
                 }).call()
                ]);


        return """{"status":"OK"}""";
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
    }

}