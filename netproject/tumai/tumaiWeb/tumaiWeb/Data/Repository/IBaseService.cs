using System.Linq.Expressions;
using Microsoft.EntityFrameworkCore;

namespace tumaiWeb.Data.Repository;

/// <summary>
/// 对标Groovy BaseService JPA工具接口
/// </summary>
public interface IBaseService
{
    Task<List<T>> QueryObjectAsync<T>(Func<DbContext, IQueryable<T>> queryBuilder) where T : class;
    Task<List<T>> QueryObjectAsync<T>(Expression<Func<T, bool>> predicate) where T : class;
    /// <summary>
    /// 原生SQL查询，返回List[object]
    /// </summary>
    Task<List<object>> QueryObjectAsync(string sql, Dictionary<string, object>? paramsMap = null);

    /// <summary>
    /// 分页查询
    /// </summary>
    Task<List<object>> QueryObjectAsync(string sql, Dictionary<string, object>? paramsMap, int firstRecord, int pageSize);

    Task<T?> QuerySingleObjectAsync<T>(Func<DbContext, IQueryable<T>> queryBuilder) where T : class;
    /// <summary>
    /// 单条查询
    /// </summary>
    Task<object?> QuerySingleObjectAsync<T>(string sql, Dictionary<string, object>? paramsMap = null) where T : class;

    /// <summary>
    /// 统计全表数量
    /// </summary>
    Task<int> QueryTotalRecordsCountByAllAsync<TEntity>() where TEntity : class;

    /// <summary>
    /// 条件count，count sql
    /// </summary>
    Task<int> QueryTotalRecordsCountByConditionAsync(string countSql, Dictionary<string, object>? paramsMap = null);

    /// <summary>
    /// 分页封装返回：CURRENT_PAGE_NUMBER,PAGE_SIZE_NUMBER,TOTAL_PAGE_NUMBER,TOTAL_RECORD_NUMBER,PAGE_RECORDS
    /// </summary>
    Task<Dictionary<string, object>> QueryRecordsInfoForPagerAsync(string dataSql, string countSql, Dictionary<string, object>? paramsMap, int currentPage, int pageSize);

    /// <summary>
    /// 新增实体
    /// </summary>
    Task AddObjectAsync(object obj);

    /// <summary>
    /// 清空一级缓存，对标evit() → em.clear()
    /// </summary>
    void Evit();

    /// <summary>
    /// 删除实体（托管实体）
    /// </summary>
    Task RemoveObjectAsync(object obj);

    /// <summary>
    /// 更新实体，对标merge
    /// </summary>
    Task<object> UpdateObjectAsync(object obj);

    /// <summary>
    /// 根据Id查找实体，对标em.find(clazz,id)
    /// </summary>
    // 非泛型，动态/反射场景用
    Task<object?> FindObjectByIdAsync(Type clazz, object id);

    // 泛型，业务代码优先使用
    Task<T?> FindObjectByIdAsync<T>(object id) where T : class;

    /// <summary>
    /// 执行DML原生SQL(update/delete) 返回受影响行数
    /// </summary>
    Task<int> ExecuteEQLAsync(string sql, Dictionary<string, object>? paramsMap = null);

    /// <summary>
    /// saveOrUpdate：id存在则update，不存在insert
    /// </summary>
    Task SaveOrUpdateObjectAsync(Type clazz, object instance, object id);

    /// <summary>
    /// 原生SQL删除
    /// </summary>
    Task RemoveObjectByNativeSQLAsync(string tableName, Dictionary<string, object> paramsMap);

    /// <summary>
    /// 原生SQL插入
    /// </summary>
    Task AddObjectByNativeSQLAsync(string tableName, Dictionary<string, object> paramsMap);

    /// <summary>
    /// 事务回调，对标groovy closure
    /// </summary>
    Task TransactionCallAsync(int propagationBehavior, Func<Task> closure);
}