using Microsoft.EntityFrameworkCore;
using System.Collections.Concurrent;
using System.ComponentModel.DataAnnotations.Schema;
using System.Data;
using System.Linq.Expressions;
using System.Reflection;
using System.Transactions;
using tumaiWeb.Data.Entities;

namespace tumaiWeb.Data.Repository;

public class BaseService : IBaseService
{
    private readonly AppDbContext _dbContext;

    public BaseService(AppDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    #region QueryObject

    public async Task<List<T>> QueryObjectAsync<T>(Func<DbContext, IQueryable<T>> queryBuilder) where T : class
    {
        IQueryable<T> query = queryBuilder(_dbContext);
        return await query.ToListAsync();
    }

    public async Task<List<T>> QueryObjectAsync<T>(Expression<Func<T, bool>> predicate) where T : class
    {
        // 1. 获取DbSet
        var dbSet = _dbContext.Set<T>();
        // 2. 拼接where条件（上层传进来的表达式树）
        var query = dbSet.Where(predicate);
        // ✅关键点：在这里内部执行查询，物化，返回List<T>，绝不返回IQueryable
        var result = await query.ToListAsync();
        return result;
    }

    public async Task<List<object>> QueryObjectAsync(string sql, Dictionary<string, object>? paramsMap = null)
    {
        if (string.IsNullOrWhiteSpace(sql))
            return new List<object>();

        var query = BuildRawSqlQuery<object>(sql, paramsMap);
        var list = await query.ToListAsync();
        return list.Count == 0 ? new List<object>() : list;
    }

    public async Task<List<object>> QueryObjectAsync(string sql, Dictionary<string, object>? paramsMap, int firstRecord, int pageSize)
    {
        if (string.IsNullOrWhiteSpace(sql))
            return new List<object>();

        var query = BuildRawSqlQuery<object>(sql, paramsMap)
            .Skip(firstRecord)
            .Take(pageSize);

        var list = await query.ToListAsync();
        Evit();
        return list.Count == 0 ? new List<object>() : list;
    }
    
    public async Task<T?> QuerySingleObjectAsync<T>(Func<DbContext, IQueryable<T>> queryBuilder) where T : class
    {
        IQueryable<T> query = queryBuilder(_dbContext);
        return await query.FirstOrDefaultAsync();
    }

    public async Task<object?> QuerySingleObjectAsync<T>(string sql, Dictionary<string, object>? paramsMap = null)
        where T : class
    {
        if (string.IsNullOrWhiteSpace(sql))
            return null;

        var q = BuildRawSqlQuery<T>(sql, paramsMap);
        return await q.FirstOrDefaultAsync();
    }

    #endregion

    #region Count
    
    public async Task<int> QueryTotalRecordsCountByAllAsync<TEntity>() where TEntity : class
    {
        return await _dbContext.Set<TEntity>().CountAsync();
    }

    public async Task<int> QueryTotalRecordsCountByConditionAsync(string countSql, Dictionary<string, object>? paramsMap = null)
    {
        if (string.IsNullOrWhiteSpace(countSql))
            return -1;

        var (realSql, pars) = RewriteNamedSql(countSql, paramsMap);
        long val = await _dbContext.Database
            .SqlQueryRaw<long>(realSql, pars)
            .FirstOrDefaultAsync();

        return (int)val;
    }

    #endregion

    #region Pager

    public async Task<Dictionary<string, object>> QueryRecordsInfoForPagerAsync(string dataSql, string countSql, Dictionary<string, object>? paramsMap, int currentPage, int pageSize)
    {
        int currentPageNumber = currentPage - 1;
        int firstRecord = currentPageNumber * pageSize;

        int totalRecords = paramsMap == null
            ? await QueryTotalRecordsCountByConditionAsync(countSql)
            : await QueryTotalRecordsCountByConditionAsync(countSql, paramsMap);

        int totalPageNumber = totalRecords / pageSize;
        if (totalPageNumber * pageSize < totalRecords)
            totalPageNumber++;

        var results = await QueryObjectAsync(dataSql, paramsMap, firstRecord, pageSize);

        var pagerInfo = new Dictionary<string, object>
        {
            ["CURRENT_PAGE_NUMBER"] = currentPage,
            ["PAGE_SIZE_NUMBER"] = pageSize,
            ["TOTAL_PAGE_NUMBER"] = totalPageNumber,
            ["TOTAL_RECORD_NUMBER"] = totalRecords,
            ["PAGE_RECORDS"] = results
        };
        return pagerInfo;
    }

    #endregion

    #region CRUD

    public async Task AddObjectAsync(object obj)
    {
        await _dbContext.AddAsync(obj);
        await _dbContext.SaveChangesAsync();
    }

    public void Evit()
    {
        _dbContext.ChangeTracker.Clear();
    }

    public async Task RemoveObjectAsync(object obj)
    {
        if (obj == null) return;
        _dbContext.Remove(obj);
        await _dbContext.SaveChangesAsync();
    }

    public async Task<object> UpdateObjectAsync(object obj)
    {
        var merged = _dbContext.Update(obj).Entity;
        await _dbContext.SaveChangesAsync();
        return merged;
    }

    public async Task<object?> FindObjectByIdAsync(Type clazz, object id)
    {
        return await _dbContext.FindAsync(clazz, id);
    }
    /// <summary>
    /// 泛型封装，业务优先调用，复用上面非泛型逻辑
    /// </summary>
    public async Task<T?> FindObjectByIdAsync<T>(object id) where T : class
    {
        var obj = await FindObjectByIdAsync(typeof(T), id);
        return obj as T;
    }
    

    public async Task<int> ExecuteEQLAsync(string sql, Dictionary<string, object>? paramsMap = null)
    {
        var (realSql, pars) = RewriteNamedSql(sql, paramsMap);
        return await _dbContext.Database.ExecuteSqlRawAsync(realSql, pars);
    }

    public async Task SaveOrUpdateObjectAsync(Type clazz, object instance, object id)
    {
        var exist = await FindObjectByIdAsync(clazz, id);
        if (exist != null)
        {
            await UpdateObjectAsync(instance);
        }
        else
        {
            await AddObjectAsync(instance);
        }
    }

    #endregion

    #region NativeSQL Delete / Insert / Query

    public async Task RemoveObjectByNativeSQLAsync(string tableName, Dictionary<string, object> paramsMap)
    {
        var keys = paramsMap.Keys.ToList();
        var whereSegments = keys.Select(k => $"{k}=@{k}");
        string sql = $"DELETE FROM {tableName} WHERE {string.Join(" AND ", whereSegments)}";
        await ExecuteEQLAsync(sql, paramsMap);
    }

    public async Task AddObjectByNativeSQLAsync(string tableName, Dictionary<string, object> paramsMap)
    {
        var cols = paramsMap.Keys.ToList();
        var colStr = string.Join(",", cols);
        var paramStr = string.Join(",", cols.Select(x => $"@{x}"));
        string sql = $"INSERT INTO {tableName} ({colStr}) VALUES ({paramStr})";
        await ExecuteEQLAsync(sql, paramsMap);
    }

    /// <summary>
    /// 原生SQL查询实体，多余查询字段自动放入实体Temps字典
    /// 实体必须包含 [NotMapped] public Dictionary<string,object> Temps {get;set;}
    /// </summary>
    /// <typeparam name="T">目标实体</typeparam>
    /// <param name="sql">支持命名参数 @xxx</param>
    /// <param name="paramsMap">参数字典</param>
    /// <returns></returns>
    public async Task<List<T>> QueryWithExtraColsAsync<T>(string sql, Dictionary<string, object>? paramsMap = null)
        where T : class, new()
    {
        // 1. 解析命名参数，得到最终sql和Npgsql参数数组（复用你原有方法）
        var (realSql, pars) = RewriteNamedSql(sql, paramsMap);

        // 2. 读取或缓存当前T类型的属性信息
        var typeInfo = _typePropertyCache.GetOrAdd(typeof(T), type =>
        {
            var props = type.GetProperties(BindingFlags.Instance | BindingFlags.Public);
            var normalProps = new Dictionary<string, PropertyInfo>(StringComparer.OrdinalIgnoreCase);
            PropertyInfo? tempsProp = null;

            foreach (var p in props)
            {
                // 跳过带NotMapped特性的属性，除了Temps
                var notMappedAttr = p.GetCustomAttribute<NotMappedAttribute>();
                if (notMappedAttr != null)
                {
                    if (p.Name == "TempMap" && p.PropertyType == typeof(Dictionary<string, object>))
                    {
                        tempsProp = p;
                    }
                    continue;
                }
                normalProps[p.Name] = p;
            }
            return (normalProps, tempsProp);
        });

        var (propertyDict, tempsProperty) = typeInfo;
        if (tempsProperty == null)
        {
            throw new InvalidOperationException($"类型 {typeof(T).Name} 没有定义 [NotMapped] 的 Temps 字典属性");
        }

        var resultList = new List<T>();
        await using var conn = _dbContext.Database.GetDbConnection();
        if (conn.State != System.Data.ConnectionState.Open)
        {
            await conn.OpenAsync();
        }

        await using var cmd = conn.CreateCommand();
        cmd.CommandText = realSql;
        cmd.Parameters.AddRange(pars);

        await using var reader = await cmd.ExecuteReaderAsync();

        while (await reader.ReadAsync())
        {
            T item = new T();
            var tempsDict = new Dictionary<string, object>();
            tempsProperty.SetValue(item, tempsDict);

            for (int i = 0; i < reader.FieldCount; i++)
            {
                string dbCol = reader.GetName(i);
                object rawVal = reader.IsDBNull(i) ? null! : reader.GetValue(i);

                // 【PG兼容】下划线列名转驼峰：stock_code → StockCode
                //string camelName = ToCamelCaseFromUnderscore(dbCol);

                if (propertyDict.TryGetValue(dbCol, out var propInfo))
                {
                    propInfo.SetValue(item, rawVal);
                }
                else
                {
                    // 多余字段，丢入Temps字典
                    tempsDict[dbCol] = rawVal;
                }
            }
            resultList.Add(item);
        }

        return resultList;
    }

    #endregion

    #region TransactionCall 事务回调，对标groovy closure

    /// <summary>
    /// 事务封装，对标Spring传播行为
    /// propagationBehavior: -1 = PROPAGATION_REQUIRES_NEW
    /// 注意：同一DbContext无法实现真正Spring REQUIRES_NEW；外层已有事务时会嵌套，适合外层无事务场景
    /// </summary>
    public async Task TransactionCallAsync(int propagationBehavior, Func<Task> closure)
    {
        if (propagationBehavior != -1)
        {
            throw new NotSupportedException($"暂不支持该传播行为值：{propagationBehavior}，仅支持 PROPAGATION_REQUIRES_NEW(-1)");
        }

        using var tx = await _dbContext.Database
            .BeginTransactionAsync(System.Data.IsolationLevel.ReadCommitted);
        try
        {
            await closure.Invoke();
            await tx.CommitAsync();
        }
        catch (Exception)
        {
            await tx.RollbackAsync();
            throw;
        }
    }

    /// <summary>
    /// 事务封装【带返回值重载】
    /// propagationBehavior: -1 = PROPAGATION_REQUIRES_NEW
    /// </summary>
    public async Task<T> TransactionCallAsync<T>(int propagationBehavior, Func<Task<T>> closure)
    {
        if (propagationBehavior != -1)
        {
            throw new NotSupportedException($"暂不支持该传播行为值：{propagationBehavior}，仅支持 PROPAGATION_REQUIRES_NEW(-1)");
        }

        using var tx = await _dbContext.Database
            .BeginTransactionAsync(System.Data.IsolationLevel.ReadCommitted);
        try
        {
            var result = await closure.Invoke();
            await tx.CommitAsync();
            return result;
        }
        catch (Exception)
        {
            await tx.RollbackAsync();
            throw;
        }
    }

    #endregion

    #region 内部工具
    
    /// <summary>
    /// 将SQL中的 @Key 命名占位符转换为 EF {0}序号占位，返回处理后的sql+参数值数组
    /// 数据库无关，不依赖Npgsql
    /// </summary>
    /// <param name="sql">原始sql，使用 :Key 占位</param>
    /// <param name="parameters">参数字典</param>
    /// <returns>(processedSql, values)</returns>
    private (string processedSql, object[] values) RewriteNamedSql(string sql, Dictionary<string, object>? parameters)
    {
        if (parameters == null || parameters.Count == 0)
        {
            return (sql, Array.Empty<object>());
        }

        var valueList = new List<object>();
        var idx = 0;
        var result = sql;

        foreach (var kv in parameters)
        {
            string name = $":{kv.Key}";
            // 把 @Key 替换成 {0},{1}
            result = result.Replace(name, $"{{{idx}}}");
            valueList.Add(kv.Value);
            idx++;
        }
        return (result, valueList.ToArray());
    }

    /// <summary>
    /// 构建FromSqlRaw查询
    /// </summary>
    private IQueryable<T> BuildRawSqlQuery<T>(string sql, Dictionary<string, object>? parameters) where T : class
    {
        var (realSql, pars) = RewriteNamedSql(sql, parameters);
        return _dbContext.Set<T>().FromSqlRaw(realSql, pars);
    }

    // 静态全局属性缓存，程序生命周期只反射一次
    private static readonly ConcurrentDictionary<Type, (
        Dictionary<string, PropertyInfo> NormalProps,
        PropertyInfo? TempsProperty
    )> _typePropertyCache = new();

    #endregion
}