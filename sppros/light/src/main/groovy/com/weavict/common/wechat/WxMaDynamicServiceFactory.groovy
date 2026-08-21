package com.weavict.common.wechat

import cn.binarywang.wx.miniapp.api.WxMaService
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl
import org.springframework.stereotype.Service

import java.util.concurrent.ConcurrentHashMap

@Service
class WxMaDynamicServiceFactory<T> {
    private final Map<String, WxMaService> appIdToServiceMap = new ConcurrentHashMap<>();
    private final Map<String, T> stsAppMap = new ConcurrentHashMap<>();

    WxMaDynamicServiceFactory() {
        // 应用启动时加载所有配置
//        loadAllConfigs();
    }

    Map getStsByAppId(String appId)
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

    // 获取或创建某个 appId 的服务实例
    WxMaService getServiceByAppId(String appId) {
        // 从缓存获取
        WxMaService service = appIdToServiceMap.get(appId);
//        if (service != null) {
//            return service;
//        }
        return service;

        // 缓存未命中，尝试从数据库加载该 appId 的配置
//        WechatMpConfig config = configRepository.findById(appId).orElse(null);
//        if (config == null) {
//            throw new RuntimeException("AppId 未配置: " + appId);
//        }
//        return createAndCacheService(config);
    }

    // 创建并缓存服务实例
    WxMaService createAndCacheService(T pw,Map config) {
        stsAppMap[config.appId as String] = pw;
        WxMaDefaultConfigImpl wxConfig = new WxMaDefaultConfigImpl();
        wxConfig.setAppid(config.appId as String);
        wxConfig.setSecret(config.secret as String);
        wxConfig.setToken(config.token as String);
        wxConfig.setAesKey(config.aesKey as String);
        wxConfig.setMsgDataFormat(config.msgDataFormat as String);

        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(wxConfig);

        appIdToServiceMap.put(config.appId as String, service);
        return service;
    }

    // 加载所有配置
//    private void loadAllConfigs() {
//        List<WechatMpConfig> allConfigs = configRepository.findAll();
//        appIdToServiceMap.clear();
//        for (WechatMpConfig config : allConfigs) {
//            createAndCacheService(config);
//        }
//        println "Loaded ${allConfigs.size()} wechat mp configs.";
//    }
}

//class WechatMpConfig {
//    String appId;
//    String secret;
//    String token;
//    String aesKey;
//    String msgDataFormat;
//}
