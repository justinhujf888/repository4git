package com.weavict.competition.module

import cn.hutool.core.util.StrUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.ejb.BaseBean
import com.weavict.common.ejb.StaticBean
import com.weavict.competition.rest.BaseRest
import jakarta.persistence.Query
import org.springframework.transaction.annotation.Transactional

import javax.sql.DataSource

class ModuleBean extends BaseBean
{
//	@Resource(mappedName="java:jboss/datasources/mysql")
//    protected DataSource dataSource;

    @Transactional
    Object updateTheObject(Object o)
    {
        return this.updateObject(o);
    }

    @Transactional
    void updateTheObjectFilds(String objName,String con,Map fields,Map keys,boolean isNative)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "update ${objName} set";
        fields?.each {k,v ->
            sbf << " ${k} = :${k},";
            paramsMap[k] = v;
        }
        sbf.delete(sbf.length()-1,sbf.length());
        sbf << " where ${con}";
        keys?.each {k,v ->
            paramsMap[k] = v;
        }
        if (isNative)
        {
            this.createNativeQuery4Params(sbf.toString(),paramsMap).executeUpdate();
        }
        else
        {
            this.executeEQL(sbf.toString(),paramsMap);
        }
    }

    @Transactional
    void deleteTheObject8Fields(String objName,String con,Map keys,boolean isNative)
    {
        StringBuffer sbf = new StringBuffer();
        if (isNative)
        {
            sbf << "delete from ${objName} where ${con}";
            this.createNativeQuery4Params(sbf.toString(),keys).executeUpdate();
        }
        else
        {
            sbf << "delete ${objName} where ${con}";
            this.executeEQL(sbf.toString(),keys);
        }
    }

    QueryUtils newQueryUtils(boolean isNative)
    {
        return new QueryUtils(isNative,this);
    }

    QueryUtils newQueryUtils(boolean isNative,boolean isFieldObj)
    {
        return new QueryUtils(isNative,isFieldObj,this);
    }

    PageUtil queryRecordsForPager(String sql, String sqlcount, Map<String, Object> paramsMap, int currentPage, int pageSize) throws Exception
    {
        int firstRecord = currentPage * pageSize;
        int totalRecords = paramsMap == null?this.queryTotalRecordsCountByCondition(sqlcount):this.queryTotalRecordsCountByCondition(sqlcount, paramsMap);
        int totalPageNumber = totalRecords / pageSize;
        if(totalPageNumber * pageSize < totalRecords) {
            ++totalPageNumber;
        }
        List results = this.queryObject(sql, paramsMap, firstRecord, pageSize);
        PageUtil pageUtil = new PageUtil(pageSize,currentPage,totalRecords);
        pageUtil.content = results;
        return pageUtil;
    }

    PageUtil createNativeQueryLimit(String sql, String sqlcount, int currentPageNu, int pageSize)
    {
        int firstRecord = currentPageNu * pageSize;
        int totalRecords = this.em.createNativeQuery(sqlcount).getResultList()[0] as int;
        int totalPageNumber = totalRecords / pageSize;
        if (totalPageNumber * pageSize < totalRecords)
        {
            ++totalPageNumber;
        }
        PageUtil pageUtil = new PageUtil(pageSize,currentPageNu,totalRecords);
        pageUtil.content = createNativeQuery("$sql limit ${pageSize} offset ${firstRecord}").getResultList();
        return pageUtil;
    }

    PageUtil createNativeQueryLimit(String sql, String sqlcount, Map<String, Object> paramsMap, int currentPageNu, int pageSize)
    {
        int firstRecord = currentPageNu * pageSize;
        int totalRecords = createNativeQuery4Params(sqlcount,paramsMap).getSingleResult() as int;

        int totalPageNumber = totalRecords / pageSize;
        if (totalPageNumber * pageSize < totalRecords)
        {
            ++totalPageNumber;
        }
        PageUtil pageUtil = new PageUtil(pageSize,currentPageNu,totalRecords);
        pageUtil.content = createNativeQuery4Params("$sql limit ${pageSize} offset ${firstRecord}",paramsMap).getResultList();
        return pageUtil;
    }

    Query createNativeQuery(String sql)
    {
        this.em.createNativeQuery(sql.replaceAll("<script", ""));
    }

    Query createNativeQuery4Params(String sql,Map<String, Object> paramsMap)
    {
        Query query = createNativeQuery(sql);
        if (paramsMap!=null)
        {
            Iterator iter = paramsMap.keySet().iterator();
            while(iter.hasNext()) {
                String key = (String)iter.next();
                query.setParameter(key, paramsMap.get(key));
            }
        }
        return query;
    }
}


class QueryUtils
{
    ModuleBean moduleBean;
    StringBuffer sbf = new StringBuffer();

    boolean isNative = true;
    boolean isPageSplit = false;
    boolean isFieldObj = false;

    Map map = [:];

    List joinOnList;

    Map<String, Object> paramsMap;

    int pageSize = 1;
    int currentPageNu = 0;
    int firstRecord = currentPageNu * pageSize;
    int totalRecords = 0;

    PageUtil pageUtil;

    Object convertObj(int tableFieldIndex,Object distObj)
    {
        if (!(map["tableFields"][tableFieldIndex]["convertType"] in [null,""]))
        {
            if (map["tableFields"][tableFieldIndex]["convertType"]=="json")
            {
                ObjectMapper objectMapper = StaticBean.buildObjectMapper();
                return objectMapper.readValue(distObj as String,Map.class);
            }
        }
        else
        {
            return distObj;
        }
    }

    void beanSet(Object it,Object obj,List fields,int startIndex)
    {
//        println it;
//        println obj;
//        println fields;
//        println map["tableFields"];
        fields?.eachWithIndex{f,findex->
            def sp = f.split("\\.");
            if (sp.length>0)
            {
                switch (sp.length)
                {
                    case 0:
                        obj[f] = convertObj(findex,it[findex+startIndex]);
                        break;
                    case 1:
                        obj[f] = convertObj(findex,it[findex+startIndex]);
                        break;
                    case 2:
                        if (obj[sp[0]]==null)
                        {
                            obj[sp[0]] = [:];
                        }
                        obj[sp[0]][sp[1]] = convertObj(findex,it[findex+startIndex]);
                        break;
                    case 3:
                        if (obj[sp[0]]==null)
                        {
                            obj[sp[0]] = [:];
                        }
                        if (obj[sp[0]][sp[1]]==null)
                        {
                            obj[sp[0]][sp[1]] = [:];
                        }
                        obj[sp[0]][sp[1]][sp[2]] = convertObj(findex,it[findex+startIndex]);
                        break;
                    case 4:
                        if (obj[sp[0]]==null)
                        {
                            obj[sp[0]] = [:];
                        }
                        if (obj[sp[0]][sp[1]]==null)
                        {
                            obj[sp[0]][sp[1]] = [:];
                        }
                        if (obj[sp[0]][sp[1]][sp[2]]==null)
                        {
                            obj[sp[0]][sp[1]][sp[2]] = [:];
                        }
                        obj[sp[0]][sp[1]][sp[2]][sp[3]] = convertObj(findex,it[findex+startIndex]);
                        break;
                    case 5:
                        if (obj[sp[0]]==null)
                        {
                            obj[sp[0]] = [:];
                        }
                        if (obj[sp[0]][sp[1]]==null)
                        {
                            obj[sp[0]][sp[1]] = [:];
                        }
                        if (obj[sp[0]][sp[1]][sp[2]]==null)
                        {
                            obj[sp[0]][sp[1]][sp[2]] = [:];
                        }
                        if (obj[sp[0]][sp[1]][sp[2]][sp[3]]==null)
                        {
                            obj[sp[0]][sp[1]][sp[2]][sp[3]] = [:];
                        }
                        obj[sp[0]][sp[1]][sp[2]][sp[3]][sp[4]] = convertObj(findex,it[findex+startIndex]);
                        break;
                }
            }
        }
    }


    QueryUtils()
    {
        map["insStr"] = "";
    }

    QueryUtils(boolean isNative,ModuleBean moduleBean)
    {
        this.isNative = isNative;
        this.moduleBean = moduleBean;
        map["insStr"] = "";
        map["beanTempDir"] = "tempMap";
    }

    QueryUtils(boolean isNative,boolean isFieldObj,ModuleBean moduleBean)
    {
        this.isNative = isNative;
        this.moduleBean = moduleBean;
        this.isFieldObj = isFieldObj;
        map["insStr"] = "";
        map["beanTempDir"] = "tempMap";
    }

    QueryUtils setupArr(Map map)
    {
        if (map["isFieldObj"]!=null)
        {
            this.isFieldObj = map["isFieldObj"];
        }
        if (map["beanTempDir"]!=null)
        {
            this.map["beanTempDir"] = map["beanTempDir"];
        }
        return this;
    }

    QueryUtils insertInsString(String insStr,String position)
    {
        map["insStr"] = insStr;
        map["position"] = position;
        return this;
    }

    QueryUtils masterTable(String tableName,String aliasName,List fields)
    {
        map["masterTableName"] = tableName;
        if (aliasName in [null,""])
        {
            map["masterTableAliasName"] = tableName;
        }
        else
        {
            map["masterTableAliasName"] = aliasName;
        }
        sbf << "select ${map["position"]=="L" ? map["insStr"] : ""}";
        if (fields!=null)
        {
            fields?.eachWithIndex {field,index->
                if (this.isFieldObj)
                {
                    if (field.isCop)
                    {
                        sbf << " ${field.cop} as ${map["masterTableAliasName"]}_${field.sf}";
                    }
                    else
                    {
                        sbf << " ${map["masterTableAliasName"]}.${field.sf}";
                    }
                }
                else
                {
                    sbf << " ${map["masterTableAliasName"]}.${field}";
                }

                if (index < fields.size()-1)
                {
                    sbf << ",";
                }
            }
            if (this.isFieldObj)
            {
                map["tableFields"] = fields;
            }
        }
        else if (!isNative)
        {
            sbf << " ${map["masterTableAliasName"]}";
        }
        sbf << " ${map["position"]=="R" ? map["insStr"] : ""}"
        return this;
    }

    QueryUtils joinTable(String tableName,String aliasName,String joinType,String on,List fields)
    {
        if (aliasName in [null,""])
        {
            aliasName = tableName;
        }
        if (joinOnList==null)
        {
            joinOnList = new ArrayList();
        }
        sbf << ",";
        if (fields != null)
        {
            fields?.eachWithIndex {field,index->
                if (this.isFieldObj)
                {
                    if (field.isCop)
                    {
                        sbf << " ${field.cop} as ${aliasName}_${field.sf}";
                    }
                    else
                    {
                        sbf << " ${aliasName}.${field.sf} as ${aliasName}_${field.sf}";
                    }
                }
                else
                {
                    sbf << " ${aliasName}.${field} as ${aliasName}_${field}";
                }

                if (index < fields.size()-1)
                {
                    sbf << ",";
                }
            }
            if (this.isFieldObj)
            {
                map["tableFields"] = map["tableFields"] + fields;
            }
        }

        joinOnList << " ${joinType} ${tableName} as ${aliasName} on ${on}";
        return this;
    }

    QueryUtils where(String con,Map paramsMap,String joinType,Closure fun)
    {
        if (fun!=null && !fun())
        {
            if (this.paramsMap==null)
            {
                this.paramsMap = [:];
            }
            return this;
        }
        else
        {
            if (map["where"]==null)
            {
                map["where"] = new StringBuffer();
                map["where"] << " where";
            }
            map["where"] << " ${joinType==null ? "" : joinType} ${con}";
            if (this.paramsMap==null)
            {
                this.paramsMap = [:];
            }
            paramsMap.each {
                this.paramsMap << it;
            }
            return this;
        }
    }

    QueryUtils orderBy(String orderBy)
    {
        map["orderBy"] = " order by ${orderBy}";
        return this;
    }

    QueryUtils groupBy(String groupBy)
    {
        map["groupBy"] = " group by ${groupBy}";
        return this;
    }

    QueryUtils union(String union)
    {
        map["union"] = " union ${union}";
        return this;
    }

    QueryUtils buildSql()
    {
        sbf << " from ${map["masterTableName"]} as ${map["masterTableAliasName"]}";
        if (joinOnList!=null && joinOnList.size()>0)
        {
            joinOnList.each {
                sbf << it;
            }
        }
        if (map["where"]!=null)
        {
            sbf << map["where"].toString();
        }
        if (map["groupBy"]!=null)
        {
            sbf << map["groupBy"];
        }
        if (map["orderBy"]!=null)
        {
            sbf << map["orderBy"];
        }
        if (map["union"]!=null)
        {
            sbf << map["union"];
        }
        return this;
    }

    QueryUtils pageLimit(int pageSize,int currentPageNu,String countKey)
    {
        this.isPageSplit = true;
        this.pageSize = pageSize;
        this.currentPageNu = currentPageNu;
        this.firstRecord = currentPageNu * pageSize;
        map["countKey"] = countKey;
        return this;
    }

    QueryUtils beanSetup(Class clazz,List fields,List beanTempMap)
    {
        map["beanClass"] = clazz.name;
        if (this.isFieldObj)
        {
            for(o in map["tableFields"])
            {
                if (o.isTemp)
                {
                    if (map["beanTempMap"]==null) map["beanTempMap"] = [];
                    map["beanTempMap"] << o.bf;
                }
                else
                {
                    if (map["beanFields"]==null) map["beanFields"] = [];
                    map["beanFields"] << o.bf;
                }
            }
        }
        else
        {
            map["beanFields"] = fields;
            map["beanTempMap"] = beanTempMap;
        }
        return this;
    }

    void clear()
    {
        paramsMap = null;
        joinOnList = null;
        if (map["where"]!=null)
        {
            map["where"].delete(0,map["where"].length());
            map["where"] = null;
        }
        map = null;
        sbf.delete(0,sbf.length());
        sbf = null;
    }

    PageUtil run()
    {
        List list = new ArrayList();
        if (this.isNative)
        {
            if (this.isPageSplit)
            {
                sbf << " limit ${pageSize} offset ${firstRecord}";
                this.totalRecords = this.moduleBean.createNativeQuery4Params("select count(${map["countKey"]}) ${StrUtil.sub(sbf.toString(),sbf.toString().indexOf("from"),map["orderBy"]!=null ? sbf.toString().indexOf("order by") : sbf.toString().length())}",paramsMap).getSingleResult() as int;
            }
            this.moduleBean.createNativeQuery4Params(sbf.toString(),paramsMap).getResultList().each {
                def obj = Class.forName(map["beanClass"]).newInstance();
                obj[map["beanTempDir"]] = [:];
                if (this.isFieldObj)
                {
                    if (map["beanFields"]!=null && map["beanFields"]?.size()>0)
                    {
                        this.beanSet(it,obj,map["beanFields"],0);
                    }
                    if (map["beanTempMap"]!=null && map["beanTempMap"]?.size()>0)
                    {
                        this.beanSet(it,obj.tempMap,map["beanTempMap"],map["beanFields"].size());
                    }
                }
                else
                {
                    if (map["beanFields"]!=null && map["beanFields"]?.size()>0)
                    {
                        this.beanSet(it,obj,map["beanFields"],0);
                    }
                    if (map["beanTempMap"]!=null && map["beanTempMap"]?.size()>0)
                    {
                        this.beanSet(it,obj.tempMap,map["beanTempMap"],map["beanFields"].size());
                    }
                }


//				map["beanTempMap"]?.eachWithIndex{f,findex->
//					def sp = f.split("\\.");
//					if (sp.length>0)
//					{
//						switch (sp.length)
//						{
//							case 0:
//								obj.tempMap[f] = it[findex+i+1];
//								break;
//							case 1:
//								obj.tempMap[f] = it[findex+i+1];
//								break;
//							case 2:
//								if (obj.tempMap[sp[0]]==null)
//								{
//									obj.tempMap[sp[0]] = [:];
//								}
//								obj.tempMap[sp[0]][sp[1]] = it[findex+i+1];
//								break;
//							case 3:
//								if (obj.tempMap[sp[0]]==null)
//								{
//									obj.tempMap[sp[0]] = [:];
//								}
//								if (obj.tempMap[sp[0]][sp[1]]==null)
//								{
//									obj.tempMap[sp[0]][sp[1]] = [:];
//								}
//								obj.tempMap[sp[0]][sp[1]][sp[2]] = it[findex+i+1];
//								break;
//							case 4:
//								if (obj.tempMap[sp[0]]==null)
//								{
//									obj.tempMap[sp[0]] = [:];
//								}
//								if (obj.tempMap[sp[0]][sp[1]]==null)
//								{
//									obj.tempMap[sp[0]][sp[1]] = [:];
//								}
//								if (obj.tempMap[sp[0]][sp[1]][sp[2]]==null)
//								{
//									obj.tempMap[sp[0]][sp[1]][sp[2]] = [:];
//								}
//								obj.tempMap[sp[0]][sp[1]][sp[2]][sp[3]] = it[findex+i+1];
//								break;
//							case 5:
//								if (obj.tempMap[sp[0]]==null)
//								{
//									obj.tempMap[sp[0]] = [:];
//								}
//								if (obj.tempMap[sp[0]][sp[1]]==null)
//								{
//									obj.tempMap[sp[0]][sp[1]] = [:];
//								}
//								if (obj.tempMap[sp[0]][sp[1]][sp[2]]==null)
//								{
//									obj.tempMap[sp[0]][sp[1]][sp[2]] = [:];
//								}
//								if (obj.tempMap[sp[0]][sp[1]][sp[2]][sp[3]]==null)
//								{
//									obj.tempMap[sp[0]][sp[1]][sp[2]][sp[3]] = [:];
//								}
//								obj.tempMap[sp[0]][sp[1]][sp[2]][sp[3]][sp[4]] = it[findex+i+1];
//								break;
//						}
//					}
////					obj.tempMap[f] = it[findex+i+1];
//				}
                list << obj;
            }
            this.pageUtil = new PageUtil(pageSize,currentPageNu,totalRecords);
            this.pageUtil.content = list;
        }
        else
        {
            if (this.isPageSplit)
            {
                totalRecords = this.moduleBean.querySingleObject("select count(${map["countKey"]}) ${StrUtil.sub(sbf.toString(),sbf.toString().indexOf("from"),map["orderBy"]!=null ? sbf.toString().indexOf("order by") : sbf.toString().length())}",paramsMap);
                pageUtil = new PageUtil(pageSize,currentPageNu,totalRecords);
                pageUtil.content = this.moduleBean.queryObject(sbf.toString(),paramsMap,firstRecord,pageSize);
            }
            else
            {
                pageUtil = new PageUtil(pageSize,currentPageNu,totalRecords);
                pageUtil.content = this.moduleBean.queryObject(sbf.toString(),paramsMap);
                pageUtil.content.each {
                    it.cancelLazyEr();
                }
            }
        }
        this.clear();
        return pageUtil;
    }
}

class PageUtil
{

    /**
     * 每页大小
     */
    int size;

    /**
     * 当前页为第几页
     */
    int number;

    /**
     * 是否为第一页
     */
    boolean first = false;

    /**
     * 是否为最后一页
     */
    boolean last = false;

    /**
     * 总共有多少页
     */
    int totalPages;

    /**
     * 总共有多少条数据
     */
    int totalElements;

    /**
     * 当前页一共有多少条数据
     */
    int numberOfElements;

    /**
     * 数据
     */
    List content = new ArrayList();

    /**
     * 根据传入的当前多少页
     * @param size
     * @param number
     * @param totalElements
     */
    PageUtil(int size, int number, int totalElements)
    {

        this.size = size;

        this.totalElements = totalElements;

        this.number = number < 0 ? 0 : number;

        this.totalPages = totalElements % size == 0 ? totalElements / size : (totalElements / size) + 1;

        this.first = number == 0 ? true : false;

        this.last = number == this.totalPages-1 ? true : false;
    }
}
