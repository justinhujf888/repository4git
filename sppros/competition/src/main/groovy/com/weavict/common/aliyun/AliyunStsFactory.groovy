package com.weavict.common.aliyun

import com.aliyun.oss.ClientBuilderConfiguration
import com.aliyun.oss.ClientException
import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.common.auth.CredentialsProvider
import com.aliyun.oss.common.auth.DefaultCredentialProvider
import com.aliyun.oss.common.comm.SignVersion
import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.auth.sts.AssumeRoleRequest
import com.aliyuncs.auth.sts.AssumeRoleResponse
import com.aliyuncs.http.MethodType
import com.aliyuncs.profile.DefaultProfile
import com.aliyuncs.profile.IClientProfile
import com.weavict.competition.redis.RedisUtil
import org.springframework.stereotype.Service

import java.util.concurrent.ConcurrentHashMap

@Service
class AliyunStsFactory<T>
{
    private final Map<String, T> stsAppMap = new ConcurrentHashMap<>();

    private final RedisUtil redisUtil;

    AliyunStsFactory(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    Map getAliyunStsByAppId(String appId)
    {
        return stsAppMap.get(appId);
    }

    int getStsAppCount()
    {
        return stsAppMap.entrySet().size();
    }

    Map getStsApps()
    {
        return stsAppMap;
    }

    void createAndCacheService(T pw,String pwId) {
        stsAppMap[pwId] = pw;
    }

    Map genOssAccessKey(Map map)
    {
        try
        {
            // 发起STS请求所在的地域。建议保留默认值，默认值为空字符串（""）。
            String regionId = map["regionId"] as String;
            // 添加endpoint。适用于Java SDK 3.12.0及以上版本。
            DefaultProfile.addEndpoint(regionId, "Sts", map["ali_ram_endPoint"] as String);
            // 添加endpoint。适用于Java SDK 3.12.0以下版本。
            // DefaultProfile.addEndpoint("",regionId, "Sts", endpoint);
            // 构造default profile。
            IClientProfile profile = DefaultProfile.getProfile(regionId, map["ali_ram_id"] as String, map["ali_ram_key"] as String);
            // 构造client。
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            // 适用于Java SDK 3.12.0及以上版本。
            request.setSysMethod(MethodType.POST);
            // 适用于Java SDK 3.12.0以下版本。
            // request.setMethod(MethodType.POST);
            request.setRoleArn(map["ali_ram_arn"] as String);
            request.setRoleSessionName("ossSts");
            request.setPolicy(null);
            request.setDurationSeconds(900L);
            final AssumeRoleResponse response = client.getAcsResponse(request);
            return ["expiration":response.getCredentials().getExpiration(),"accessId":response.getCredentials().getAccessKeyId(),"accessKey":response.getCredentials().getAccessKeySecret(),"securityToken":response.getCredentials().getSecurityToken(),"requestId":response.getRequestId()];
        }
        catch (ClientException e)
        {
            println("Failed：");
            println("Error code: " + e.getErrCode());
            println("Error message: " + e.getErrMsg());
            println("RequestId: " + e.getRequestId());
        }
    }

    String ganAliYunStsValue(String appId,String field)
    {
        return redisUtil.hGet("${appId}_aliyun_sts",field) as String;
    }

    OSS genOSSClient(String appId)
    {
        // 使用DefaultCredentialProvider方法直接设置AK和SK
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(ganAliYunStsValue(appId,"accessId"), ganAliYunStsValue(appId,"accessKey"), ganAliYunStsValue(appId,"securityToken"));
        // 使用credentialsProvider初始化客户端
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        // 显式声明使用 V4 签名算法
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        // 创建OSSClient实例。
        // 当OSSClient实例不再使用时，调用shutdown方法以释放资源。
        return OSSClientBuilder.create()
        // 请设置目的OSS访问域名  例如杭州地域：https://oss-cn-hangzhou.aliyuncs.com
                .endpoint(ganAliYunStsValue(appId,"ali_oss_endPoint"))
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
        // 请设置为目标Bucket所处region  例如杭州地域：cn-hangzhou
                .region(ganAliYunStsValue(appId,"regionId"))
                .build();
    }
}
