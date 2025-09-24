package com.weavict.common.ejb

interface BaseService
{
    List queryObject(String var1, Map<String, Object> var2) throws Exception;

    List queryObject(String var1, Map<String, Object> var2, int var3, int var4) throws Exception;

    List queryObject(String var1) throws Exception;

    Object querySingleObject(String var1) throws Exception;

    Object querySingleObject(String var1, Map<String, Object> var2) throws Exception;

    int queryTotalRecordsCountByAll(Class var1) throws Exception;

    int queryTotalRecordsCountByCondition(String var1) throws Exception;

    int queryTotalRecordsCountByCondition(String var1, Map<String, Object> var2) throws Exception;

    Map<String, Object> queryRecordsInfoForPager(String var1, String var2, Map<String, Object> var3, int var4, int var5) throws Exception;

    void addObject(Object var1) throws Exception;

    void evit();

    void removeObject(Object var1) throws Exception;

    Object updateObject(Object var1) throws Exception;

    Object findObjectById(Class var1, Object var2) throws Exception;

    Integer executeEQL(String var1) throws Exception;

    void saveOrUpdateObject(Class var1, Object var2, Object var3) throws Exception;

    void removeObjectByNativeSQL(Object var1, Map<String, Object> var2) throws Exception;

    Integer executeEQL(String var1, Map<String, Object> var2) throws Exception;

    void addObjectByNativeSQL(Object var1, Map<String, Object> var2) throws Exception;

    void transactionCall(int propagationBehavior,Closure closure) throws Exception;
}