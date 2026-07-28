package com.weavict.common


import cn.hutool.core.io.resource.ClassPathResource
import cn.hutool.setting.dialect.Props
import com.weavict.competition.module.RedisApi
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OtherUtils
{
	@Autowired
	private RedisApi sourceRedisApi;

	private static RedisApi redisApi;

	private static Props props;

	private static List mapDatas;

	@PostConstruct
	void init()
	{
		redisApi = sourceRedisApi;
	}

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
}
