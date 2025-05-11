package com.weavict.common.ejb

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.Query
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource
import java.sql.Connection
import java.sql.PreparedStatement

class BaseBean implements BaseService
{

    @PersistenceContext
    protected EntityManager em;

    protected DataSource dataSource;

    @Autowired
    PlatformTransactionManager transactionManager;

    BaseBean()
    {
    }

    List queryObject(String EQL, Map<String, Object> paramsMap) throws Exception
    {
        List list = null;

        try
        {
            if (!EQL.equals("") && EQL != null)
            {
                Query query = this.em.createQuery(EQL);
                if (paramsMap!=null)
                {
                    Iterator<String> iter = paramsMap.keySet().iterator();

                    while (iter.hasNext())
                    {
                        String key = (String) iter.next();
                        query.setParameter(key, paramsMap.get(key));
                    }
                }
                list = query.getResultList();
                if (list != null && list.size() == 0)
                {
                    list = null;
                }
            }

            return list;
        } catch (Exception var7)
        {
            throw new Exception(var7);
        }
    }

    void evit()
    {
        this.em.clear();
    }

    List queryObject(String EQL) throws Exception
    {
        List list = null;

        try
        {
            if (!EQL.equals("") && EQL != null)
            {
                list = this.em.createQuery(EQL).getResultList();
                if (list != null && list.size() == 0)
                {
                    list = null;
                }
            }

            return list;
        } catch (Exception var4)
        {
            throw new Exception(var4);
        }
    }

    List queryObject(String EQL, Map<String, Object> paramsMap, int firstRecord, int pageSize) throws Exception
    {
        List list = null;

        try
        {
            if (!EQL.equals("") && EQL != null)
            {
                if (paramsMap == null)
                {
                    list = this.em.createQuery(EQL).setMaxResults(pageSize).setFirstResult(firstRecord).getResultList();
                } else
                {
                    Query query = this.em.createQuery(EQL);
                    Iterator<String> iter = paramsMap.keySet().iterator();

                    while (iter.hasNext())
                    {
                        String key = (String) iter.next();
                        query.setParameter(key, paramsMap.get(key));
                    }

                    list = query.setMaxResults(pageSize).setFirstResult(firstRecord).getResultList();
                }

                if (list != null && list.size() == 0)
                {
                    list = null;
                }
            }
        } catch (Exception var9)
        {
            throw new Exception(var9);
        }

        this.em.clear();
        return list;
    }

    Object querySingleObject(String EQL, Map<String, Object> paramsMap) throws Exception
    {
        try
        {
            if (!EQL.equals("") && EQL != null)
            {
                Query query = this.em.createQuery(EQL);
                Iterator<String> iter = paramsMap.keySet().iterator();

                while (iter.hasNext())
                {
                    String key = (String) iter.next();
                    query.setParameter(key, paramsMap.get(key));
                }

                return query.getSingleResult();
            } else
            {
                return null;
            }
        } catch (Exception var6)
        {
            throw var6;
        }
    }

    Object querySingleObject(String EQL) throws Exception
    {
        try
        {
            return !EQL.equals("") && EQL != null ? this.em.createQuery(EQL).getSingleResult() : null;
        } catch (Exception var3)
        {
            throw var3;
        }
    }

    int queryTotalRecordsCountByAll(Class clazz) throws Exception
    {
        String et = clazz.getName();
        String EQL = "select count(*) from " + et;
        return ((Long) this.em.createQuery(EQL).getSingleResult()).intValue();
    }

    int queryTotalRecordsCountByCondition(String EQL) throws Exception
    {
        return !EQL.equals("") && EQL != null ? ((Long) this.em.createQuery(EQL).getSingleResult()).intValue() : -1;
    }

    int queryTotalRecordsCountByCondition(String EQL, Map<String, Object> paramsMap) throws Exception
    {
        if (!EQL.equals("") && EQL != null)
        {
            Query query = this.em.createQuery(EQL);
            Iterator<String> iter = paramsMap.keySet().iterator();

            while (iter.hasNext())
            {
                String key = (String) iter.next();
                query.setParameter(key, paramsMap.get(key));
            }

            return ((Long) query.getSingleResult()).intValue();
        } else
        {
            return -1;
        }
    }

    Map<String, Object> queryRecordsInfoForPager(String EQL, String EQL2, Map<String, Object> paramsMap, int currentPage, int pageSize) throws Exception
    {
        int currentPageNumber = currentPage - 1;
        int firstRecord = currentPageNumber * pageSize;
        int totalRecords = paramsMap == null ? this.queryTotalRecordsCountByCondition(EQL2) : this.queryTotalRecordsCountByCondition(EQL2, paramsMap);
        int totalPageNumber = totalRecords / pageSize;
        if (totalPageNumber * pageSize < totalRecords)
        {
            ++totalPageNumber;
        }

        List results = this.queryObject(EQL, paramsMap, firstRecord, pageSize);
        Map<String, Object> pagerInfo = new HashMap();
        pagerInfo.put("CURRENT_PAGE_NUMBER", currentPage);
        pagerInfo.put("PAGE_SIZE_NUMBER", pageSize);
        pagerInfo.put("TOTAL_PAGE_NUMBER", totalPageNumber);
        pagerInfo.put("TOTAL_RECORD_NUMBER", totalRecords);
        pagerInfo.put("PAGE_RECORDS", results);
        return pagerInfo;
    }

    String buildSearchQLCondition(Map<String, Object> params)
    {
        StringBuffer sbu = new StringBuffer();
        sbu.append(" WHERE 1=1");
        Iterator<String> iter = params.keySet().iterator();

        while (iter.hasNext())
        {
            String key = (String) iter.next();
            sbu.append(" AND ").append(key).append(" LIKE :").append(key);
        }

        return sbu.toString();
    }

    HashMap<String, Object> buildSearchParamsMapCondition(Map<String, Object> params)
    {
        if (params == null)
        {
            return null;
        } else
        {
            HashMap<String, Object> ps = new HashMap();
            Iterator<String> iter = params.keySet().iterator();

            while (iter.hasNext())
            {
                String key = (String) iter.next();
                ps.put(key, "%" + params.get(key) + "%");
            }

            return ps;
        }
    }

    void addObject(Object obj) throws Exception
    {
        try
        {
            this.em.persist(obj);
            this.em.flush();
        } catch (Exception var3)
        {
            throw new Exception(var3);
        }
    }

    void removeObject(Object obj) throws Exception
    {
        if (obj != null)
        {
            try
            {
                this.em.remove(obj);
                this.em.flush();
            } catch (Exception var3)
            {
                throw new Exception(var3);
            }
        }

    }

    Object updateObject(Object obj) throws Exception
    {
        try
        {
            Object object = this.em.merge(obj);
            this.em.flush();
            return object;
        } catch (Exception var3)
        {
            throw new Exception(var3);
        }
    }

    Object findObjectById(Class clazz, Object id) throws Exception
    {
        Object newObj = null;

        try
        {
            newObj = this.em.find(clazz, id);
            return newObj;
        } catch (Exception var5)
        {
            throw new Exception(var5);
        }
    }

    Integer executeEQL(String EQL) throws Exception
    {
        try
        {
            return this.em.createQuery(EQL).executeUpdate();
        } catch (Exception var3)
        {
            throw new Exception(var3);
        }
    }

    Integer executeEQL(String EQL, Map<String, Object> paramsMap) throws Exception
    {
        Integer count = null;
        if (!EQL.equals("") && EQL != null)
        {
            Query query = this.em.createQuery(EQL);
            Iterator<String> iter = paramsMap.keySet().iterator();

            while (iter.hasNext())
            {
                String key = (String) iter.next();
                query.setParameter(key, paramsMap.get(key));
            }

            count = query.executeUpdate();
        }

        return count;
    }

    void saveOrUpdateObject(Class clazz, Object instance, Object id) throws Exception
    {
        if (this.findObjectById(clazz, id) != null)
        {
            this.updateObject(instance);
        } else
        {
            this.addObject(instance);
        }

    }

    void removeObjectByNativeSQL(Object object, Map<String, Object> paramsMap) throws Exception
    {
        try
        {
            StringBuffer SQL = new StringBuffer("DELETE FROM " + object + " WHERE ");
            Iterator<String> iter = paramsMap.keySet().iterator();

            String executeSQL;
            while (iter.hasNext())
            {
                executeSQL = (String) iter.next();
                SQL.append(executeSQL + "=? and ");
            }

            executeSQL = SQL.substring(0, SQL.length() - 4);
            System.out.println("hibernate SQL:" + executeSQL);
            Connection conn = this.dataSource.getConnection();
            PreparedStatement ptmt = conn.prepareStatement(executeSQL);
            iter = paramsMap.keySet().iterator();

            for (int t = 1; iter.hasNext(); ++t)
            {
                String key = (String) iter.next();
                Object value = paramsMap.get(key);
                ptmt.setObject(t, value);
            }

            ptmt.executeUpdate();
            ptmt.close();
            conn.close();
        } catch (Exception var11)
        {
            throw new Exception(var11);
        }
    }

    void addObjectByNativeSQL(Object obj, Map<String, Object> paramsMap) throws Exception
    {
        try
        {
            String SQL = "INSERT INTO " + obj + " (";
            String SQLValue = " values (";

            Iterator iter;
            String key;
            for (iter = paramsMap.keySet().iterator(); iter.hasNext(); SQLValue = SQLValue + "?,")
            {
                key = (String) iter.next();
                SQL = SQL + key + ",";
            }

            SQL = SQL.substring(0, SQL.length() - 1) + ") ";
            SQLValue = SQLValue.substring(0, SQLValue.length() - 1) + ")";
            SQL = SQL + SQLValue;
            Connection conn = this.dataSource.getConnection();
            PreparedStatement ptmt = conn.prepareStatement(SQL);
            System.out.println("hibernate SQL:" + SQL);
            int t = 1;

            for (iter = paramsMap.keySet().iterator(); iter.hasNext(); ++t)
            {
                key = (String) iter.next();
                Object value = paramsMap.get(key);
                ptmt.setObject(t, value);
            }

            ptmt.executeUpdate();
            ptmt.close();
            conn.close();
            ptmt = null;
            key = null;
        } catch (Exception var11)
        {
            throw new Exception(var11);
        }
    }

    @Override
    void transactionCall(int propagationBehavior, Closure closure) throws Exception
    {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(propagationBehavior);
        TransactionStatus transactionStatus = transactionManager.getTransaction(definition);
        try
        {
            closure.call();
            transactionManager.commit(transactionStatus);
        }
        catch(Exception e)
        {
            transactionManager.rollback(transactionStatus);
            throw e;
        }
    }

    EntityManager getEm()
    {
        return this.em;
    }

    void setEm(EntityManager em)
    {
        this.em = em;
    }
}