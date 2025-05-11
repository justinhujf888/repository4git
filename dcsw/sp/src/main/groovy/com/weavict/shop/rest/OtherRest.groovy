package com.weavict.shop.rest

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
import com.weavict.common.util.DateUtil
//import com.weavict.website.common.ImgCompress
import com.weavict.website.common.OtherUtils
import com.yicker.utility.DES
import groovy.json.JsonSlurper
import jodd.datetime.JDateTime
import jodd.datetime.Period
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
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "signatureInfo":({
                         def ossKey = OtherUtils.genOssAccessKey(query.ramUser);
                         OSSClient client = new OSSClient(OtherUtils.genOosHostUrl(), ossKey.accessId, ossKey.accessKey);
                         long expireTime = 30;
                         long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
                         PolicyConditions policyConds = new PolicyConditions();
                         policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
                         String postPolicy = client.generatePostPolicy(new Date(expireEndTime), policyConds);
                         String encodedPolicy = BinaryUtil.toBase64String(postPolicy.getBytes("utf-8"));
                         String postSignature = client.calculatePostSignature(postPolicy);
                         return ["accessId":ossKey.accessId,"policy":encodedPolicy,"signature":postSignature,"expire":String.valueOf(expireEndTime / 1000)];
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
    @Path("/uploadFile2Oss")
    String uploadFile2Oss(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            // oss
            OSSClient ossClient = OtherUtils.genOSSClient();
            ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), query.filePathName as String, new ByteArrayInputStream(objectMapper.writeValueAsString(
                    ({return query.fileObj}).call()
            ).getBytes("UTF-8")));
            ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), query.filePathName as String, CannedAccessControlList.PublicRead);
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
}
