package com.weavict.common

import cn.hutool.core.date.DateUtil
import cn.hutool.core.io.resource.ClassPathResource
import cn.hutool.setting.dialect.Props
import com.weavict.competition.module.RedisApi
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OtherUtils
{
	private static RedisApi redisApi;

	private static Props props;

	private static List mapDatas;

	static Props giveTheProps()
	{
		try
		{
			if (props == null)
			{
				props = new Props();
//				props.load(new File(ClassLoaderUtil.getResourceUrl(
//						"/config/global.props").getPath()));
				props.load(new ClassPathResource("/config/global.props").getStream());
			}
			return props;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	static String givePropsValue(String key)
	{
		try
		{
			return giveTheProps().getStr(key);
		}
		catch (Exception e)
		{
			return "";
		}
	}

	static String givePropsValue(String key, String key2)
	{
		try
		{
			return giveTheProps().getValue(key, key2);
		}
		catch (Exception e)
		{
			return "";
		}
	}
	
	static void clearTheProps()
	{
		props = null;
	}

	
	static String giveTheRoundNumber(int a, int b)
	{
		return String.valueOf((int) (Math.random() * a + b));
	}

	static List giveMapDatas()
	{
		return mapDatas;
	}

	static void setMapDatas(List m)
	{
		mapDatas = m;
	}

    static String makePId(String ps="")
    {
        return "$ps${DateUtil.format(new Date(),"yyyyMMddHHmmssSSS")}${(int)(Math.random() * (double)8999.0F + (double)1000.0F)}";
    }

    /**
     * 多次原地替换，同一个缓冲区，减少字符串拷贝
     * @param source 原始字符串
     * @param replaces 待替换map key:原文本 value:替换后文本
     * @return 处理完成字符串
     */
    static String multiReplace(String source, Map<String, String> replaces) {
        if (source == null || source.isEmpty() || replaces == null || replaces.isEmpty()) {
            return source;
        }
        StringBuilder sb = new StringBuilder(source);
        for (Map.Entry<String, String> entry : replaces.entrySet()) {
            String oldStr = entry.getKey();
            String newStr = entry.getValue();
            int idx;
            //循环把当前oldStr全部替换完成
            while ((idx = sb.indexOf(oldStr)) != -1) {
                sb.replace(idx, idx + oldStr.length(), newStr);
            }
        }
        return sb.toString();
    }
}
