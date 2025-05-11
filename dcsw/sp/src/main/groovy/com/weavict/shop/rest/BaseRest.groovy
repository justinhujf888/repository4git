package com.weavict.shop.rest

import com.beust.jcommander.internal.Lists
import com.beust.jcommander.internal.Maps
import com.fasterxml.jackson.databind.ObjectMapper
import net.sf.cglib.beans.BeanMap

/**
 * Created by Justin on 2018/6/10.
 */

class BaseRest
{
    void processExcetion(Exception e)
    {
        e.printStackTrace();
    }

    Map<String, Object> beanToMap(bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null)
        {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key, beanMap.get(key));
            }
        }
        return map;
    }

    Object mapToBean(Map<String, Object> map,bean)
    {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    List<Map<String, Object>> objectsToMaps(List objList)
    {
        List<Map<String, Object>> list = Lists.newArrayList();
        if (objList != null && objList.size() > 0) {
            Map<String, Object> map = null;
            for (Object bean in objList)
            {
                map = beanToMap(bean);
                list.add(map);
            }
        }
        return list;
    }

    List mapsToObjects(List<Map<String, Object>> maps,Class clazz) throws InstantiationException, IllegalAccessException
    {
        List list = Lists.newArrayList();
        if (maps != null && maps.size() > 0) {
            def bean = null;
            for (Map<String, Object> map in maps) {
                bean = clazz.newInstance();
                mapToBean(map, bean);
                list.add(bean);
            }
        }
        return list;
    }

    Object objToBean(Object o, Class clazz, ObjectMapper objectMapper)
    {
        if (objectMapper==null)
        {
            objectMapper = new ObjectMapper();
        }
        return objectMapper.convertValue(o,clazz);
    }
}
