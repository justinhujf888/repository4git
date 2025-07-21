package com.weavict.edu.rest

import cn.hutool.core.date.DateField
import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClient
import com.aliyun.oss.common.utils.BinaryUtil
import com.aliyun.oss.model.CannedAccessControlList
import com.aliyun.oss.model.PolicyConditions
import com.aliyuncs.CommonRequest
import com.aliyuncs.CommonResponse
import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.IAcsClient
import com.aliyuncs.http.MethodType
import com.aliyuncs.profile.DefaultProfile
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.MathUtil
import com.weavict.edu.entity.BuyerBook
import com.weavict.edu.entity.BuyerBookPK
import com.weavict.edu.module.RedisApi
import com.weavict.edu.module.SourcesService
import com.weavict.common.util.DateUtil
import com.weavict.website.common.OtherUtils
import com.weavict.weichat.notifies.WxNotifiesFun
import com.yicker.utility.DES
import groovy.json.JsonSlurper
import jodd.datetime.JDateTime
import jodd.datetime.Period
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody

import jakarta.inject.Inject
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

    @Inject
    SourcesService sourceService;

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
        File fd = new File("""${request.getSession().getServletContext().getRealPath("/")}uploads/images/${ym}""");
        if (!fd.exists())
        {
            fd.mkdir();
        }
        RequestStreamUtil.uploadFile(request,fd.getPath(),false,true,2000,2000,{
            fileName -> println "/uploads/images/${ym}/${fileName}";
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
            ObjectMapper objectMapper = new ObjectMapper();
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
            return """{"version":"0.9.26","apkurl":"http://m.daxiabang.club/xiashidai.apk","desc":"优化项目：……"}""";
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
    @Path("/systemInfo")
    String systemInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "serverDateTime":({
                         return ["currentTimeMillis":System.currentTimeMillis(),
                                "currentTimeSeconds":System.currentTimeSeconds(),
                                "dateTime":cn.hutool.core.date.DateUtil.now()];
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
    @Path("/delImgFormOss")
    String delImgFormOss(@RequestBody Map<String,Object> query)
    {
        try
        {
            //oss
            OSSClient ossClient = OtherUtils.genOSSClient();
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
    @Path("/delImgListFormOss")
    String delImgListFormOss(@RequestBody Map<String,Object> query)
    {
        try
        {
            this.objToBean(query.imgList,List.class,null).each {
                //oss
                OSSClient ossClient = OtherUtils.genOSSClient();
                ossClient.deleteObject(OtherUtils.givePropsValue("ali_oss_bucketName"), it);
                ossClient.shutdown();
                //oss end
            }
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
            ObjectMapper objectMapper = new ObjectMapper();
            DefaultProfile profile = DefaultProfile.getProfile("default", OtherUtils.givePropsValue("ali_sms_AccessKeyId"), OtherUtils.givePropsValue("ali_sms_AccessKeySecret"));
            IAcsClient client = new DefaultAcsClient(profile);
            CommonRequest request = new CommonRequest();
            request.setMethod(MethodType.POST);
            request.setDomain("dysmsapi.aliyuncs.com");
            request.setVersion("2017-05-25");
            request.setAction("SendSms");
            request.putQueryParameter("PhoneNumbers", query.phone);
            request.putQueryParameter("SignName", query.signName);
            request.putQueryParameter("TemplateCode", "${query.templateCode}");
            request.putQueryParameter("TemplateParam", "${query.templateParam}");
            request.putQueryParameter("SendDate", DateUtil.format(new Date(),"yyyy-MM-dd"));
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "smsInfo":["returnInfo":({
                         CommonResponse response = client.getCommonResponse(request);
                         println response.getData();
                         return response;
                     }).call(),
                                "templateParam":({
                                    DES crypt = new DES(OtherUtils.givePropsValue("publickey"));
                                    return crypt.encrypt(query.templateParam);
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
    @Path("/uploadObj2Oss")
    String uploadObj2Oss(@RequestBody Map<String, Object> query)
    {
        OSSClient ossClient = null;
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            // oss
            ossClient = OtherUtils.genOSSClient();
            ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), query.dataUrl, new ByteArrayInputStream(objectMapper.writeValueAsString(query.obj).getBytes("UTF-8")));
            ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), query.dataUrl, CannedAccessControlList.PublicRead);
            // oss end
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
        finally
        {
            // 关闭OSSClient。
            if (ossClient != null)
            {
                ossClient.shutdown();
            }
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/updateTheObject")
    String updateTheObject(@RequestBody Map<String, Object> query)
    {
        try
        {
            this.sourceService.updateTheObject(objToBean(query.obj, Class.forName(query.clazz), null));
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
    @Path("/updateTheObjectFilds")
    String updateTheObjectFilds(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            this.sourceService.updateTheObjectFilds(query.objName,objToBean(query.fields, Map.class, objectMapper),objToBean(query.keys, Map.class, objectMapper),query.isNative as boolean);
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
    @Path("/deleteTheObject8Fields")
    String deleteTheObject8Fields(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            this.sourceService.deleteTheObject8Fields(query.objName,objToBean(query.keys, Map.class, objectMapper),query.isNative as boolean);
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
    @Path("/queryObj8FieldsJoin")
    String queryObj8FieldsJoin(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "dataList":({
                         return this.sourceService.queryObj8FieldsJoin(
                                 objToBean(query.joinMap, Map.class, objectMapper),
                                 query.isNative as boolean);
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
    @Path("/genAliOssAccessInfo")
    String genAliOssAccessInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
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
        WxNotifiesFun.send_publicMsg(true,MathUtil.getPNewId());
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
