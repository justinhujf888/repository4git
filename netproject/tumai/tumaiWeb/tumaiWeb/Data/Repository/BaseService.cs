using Microsoft.EntityFrameworkCore;
using System.Collections.Concurrent;
using System.ComponentModel.DataAnnotations.Schema;
using System.Data;
using System.Linq.Expressions;
using System.Reflection;
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
    /// LINQ投影查询，自动映射到实体T，不在实体的字段自动放入 Temps。
    /// 匹配规则：投影属性名（小写） == 实体属性名（ToLower()）。
    /// 已处理：空行跳过、DBNull转null、赋值异常包装。
    /// </summary>
    /// <typeparam name="T">目标实体</typeparam>
    /// <typeparam name="TProjection">投影类型（匿名类/DTO）</typeparam>
    public async Task<List<T>> QueryLinqWithTempsAsync<T, TProjection>(IQueryable<TProjection> query)
        where T : class, new()
    {
        var projectionList = await query.ToListAsync();
        if (projectionList.Count == 0)
        {
            return new List<T>();
        }

        var (entityPropDict, tempsProperty) = GetEntityTypeInfo(typeof(T));
        if (tempsProperty == null)
        {
            throw new InvalidOperationException(
                $"实体 {typeof(T).Name} 缺少 [NotMapped] public Dictionary<string,object> Temps {{ get; set; }}");
        }

        var result = new List<T>();
        foreach (var projItem in projectionList)
        {
            if (projItem == null)   // 左连接 DefaultIfEmpty 场景，跳过空行
                continue;

            T entity = new T();
            var tempsDict = new Dictionary<string, object>();
            tempsProperty.SetValue(entity, tempsDict);

            var projProps = projItem.GetType().GetProperties(BindingFlags.Instance | BindingFlags.Public);
            foreach (var projProp in projProps)
            {
                object? rawVal = projProp.GetValue(projItem);
                object? val = rawVal is DBNull ? null : rawVal;   // DBNull 转 null

                string projNameLower = projProp.Name.ToLower();

                if (entityPropDict.TryGetValue(projNameLower, out var entityProp))
                {
                    try
                    {
                        entityProp.SetValue(entity, val);
                    }
                    catch (Exception ex)
                    {
                        throw new InvalidOperationException(
                            $"实体【{typeof(T).Name}】属性 {entityProp.Name} 赋值失败，" +
                            $"投影属性:{projProp.Name}，值类型:{val?.GetType().Name ?? "null"}",
                            ex);
                    }
                }
                else
                {
                    tempsDict[projNameLower] = val;
                }
            }
            result.Add(entity);
        }
        return result;
    }

    /// <summary>重载：省略第二个泛型参数，直接传 IQueryable&lt;object&gt;</summary>
    public async Task<List<T>> QueryLinqWithTempsAsync<T>(IQueryable<object> query)
        where T : class, new()
    {
        return await QueryLinqWithTempsAsync<T, object>(query);
    }

    /// <summary>
    /// 手写原生SQL查询，读取全部返回列。
    /// 自动映射到实体T的属性，多余列自动放入实体 Temps 字典。
    /// 匹配规则：数据库列名（小写） == 实体属性名（ToLower()）。
    /// 已处理：DBNull转null、参数通用化、赋值异常包装。
    /// </summary>
    /// <typeparam name="T">目标实体，必须带无参构造，包含 [NotMapped] Temps 字典</typeparam>
    /// <param name="sql">原生SQL（支持 @xxx 命名参数，写法与驱动一致）</param>
    /// <param name="paramsMap">命名参数字典</param>
    public async Task<List<T>> QueryWithExtraColsAsync<T>(string sql, Dictionary<string, object>? paramsMap = null)
        where T : class, new()
    {
        var (entityPropDict, tempsProperty) = GetEntityTypeInfo(typeof(T));
        if (tempsProperty == null)
        {
            throw new InvalidOperationException(
                $"实体 {typeof(T).Name} 缺少 [NotMapped] public Dictionary<string,object> Temps {{ get; set; }}");
        }

        var resultList = new List<T>();

        await using var conn = _dbContext.Database.GetDbConnection();
        await conn.OpenAsync();

        await using var cmd = conn.CreateCommand();
        cmd.CommandText = sql;

        // ========== 通用参数组装（不依赖 Npgsql，适配 PG/MySQL/SqlServer）==========
        if (paramsMap != null && paramsMap.Count > 0)
        {
            foreach (var kv in paramsMap)
            {
                var dbParam = cmd.CreateParameter();          // 由当前驱动生成对应的 DbParameter
                dbParam.ParameterName = kv.Key;
                dbParam.Value = kv.Value ?? DBNull.Value;     // null 转 DBNull，避免数据库收到 CLR null 报错
                cmd.Parameters.Add(dbParam);
            }
        }

        await using var reader = await cmd.ExecuteReaderAsync();
        while (await reader.ReadAsync())
        {
            T item = new T();
            var tempsDict = new Dictionary<string, object>();
            tempsProperty.SetValue(item, tempsDict);

            // 遍历这一行的所有数据库列
            for (int i = 0; i < reader.FieldCount; i++)
            {
                string colName = reader.GetName(i).ToLower();
                object rawVal = reader.GetValue(i);

                // DBNull 统一转 C# null，避免反射赋值报错
                object? val = rawVal is DBNull ? null : rawVal;

                if (entityPropDict.TryGetValue(colName, out var propInfo))
                {
                    try
                    {
                        propInfo.SetValue(item, val);
                    }
                    catch (Exception ex)
                    {
                        throw new InvalidOperationException(
                            $"实体【{typeof(T).Name}】属性 {propInfo.Name} 赋值失败，" +
                            $"原始列名:{reader.GetName(i)}，值类型:{val?.GetType().Name ?? "null"}",
                            ex);
                    }
                }
                else
                {
                    // 不在实体属性中的额外列，放入 Temps（key 为小写列名）
                    tempsDict[colName] = val;
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

    /// <summary>
    /// 实体属性缓存，QueryLinqWithTempsAsync 和 QueryWithExtraColsAsync 共用
    /// key: 实体类型 => (小写属性名->PropertyInfo, Temps属性元数据)
    /// </summary>
    private static readonly ConcurrentDictionary<Type, (
        Dictionary<string, PropertyInfo> LowerNameToProperty,
        PropertyInfo? TempsProperty
    )> _entityPropCache = new();

    /// <summary>
    /// 构建/读取实体T的元数据缓存
    /// </summary>
    private static (
        Dictionary<string, PropertyInfo> LowerNameToProperty,
        PropertyInfo? TempsProperty
    ) GetEntityTypeInfo(Type type)
    {
        return _entityPropCache.GetOrAdd(type, t =>
        {
            var props = t.GetProperties(BindingFlags.Instance | BindingFlags.Public);
            var lowerDict = new Dictionary<string, PropertyInfo>();
            PropertyInfo? tempsProp = null;

            foreach (var p in props)
            {
                var notMappedAttr = p.GetCustomAttribute<NotMappedAttribute>();
                if (notMappedAttr != null)
                {
                    // 修复：用字符串字面量 "Temps"，不能 nameof(Temps)（当前作用域无此符号，会编译报错）
                    if (p.Name == "Temps" && p.PropertyType == typeof(Dictionary<string, object>))
                    {
                        tempsProp = p;
                    }
                    continue; // NotMapped 属性不加入数据库字段映射字典
                }

                string lowerPropName = p.Name.ToLower();
                lowerDict[lowerPropName] = p;
            }
            return (lowerDict, tempsProp);
        });
    }

    #endregion
}