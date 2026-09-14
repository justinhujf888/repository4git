using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.ChangeTracking;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq.Expressions;
using System.Reflection;
using System.Text.Json;

namespace tumaiWeb.Data.Entities;

public abstract class BaseEntity
{
    // public long Id { get; set; }

    /// <summary>软删除标记</summary>
    //public bool Deleted { get; set; }

    /// <summary>创建时间（UTC）</summary>
    [Column("createtime")]
    public DateTime CreateTime { get; set; } = DateTime.UtcNow;

    /// <summary>更新时间（UTC）</summary>
    [Column("updatetime")]
    public DateTime? UpdateTime { get; set; } = DateTime.UtcNow;

    [NotMapped]
    public Dictionary<string, object> TempMap { get; set; } = new();
}

public partial class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options)
        : base(options)
    {
    }

    public DbSet<StockBasic> StockBasics => Set<StockBasic>();
    public DbSet<StockQuoteSnapshot> StockQuoteSnapshots => Set<StockQuoteSnapshot>();
    public DbSet<StockQuoteLatest> StockQuoteLatests => Set<StockQuoteLatest>();
    public DbSet<StockFinancialReport> StockFinancialReports => Set<StockFinancialReport>();
    public DbSet<StockValuation> StockValuations => Set<StockValuation>();

    //    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    //#warning To protect potentially sensitive information in your connection string, you should move it out of source code. You can avoid scaffolding the connection string by using the Name= syntax to read it from configuration - see https://go.microsoft.com/fwlink/?linkid=2131148. For more guidance on storing connection strings, see https://go.microsoft.com/fwlink/?LinkId=723263.
    //        => optionsBuilder.UseNpgsql("Host=127.0.0.1;Port=5432;Database=cptdb;Username=juser;Password=weav2880com;Pooling=true;MaxPoolSize=100");

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);
        // 自动加载所有 IEntityTypeConfiguration<T>
        modelBuilder.ApplyConfigurationsFromAssembly(Assembly.GetExecutingAssembly());
        var dictConverter = new DictionaryJsonConverter();
        var dictComparer = new DictionaryValueComparer();
        // 全局软删除过滤器：只给直接子类注册，TPH继承避免重复注册过滤器
        var baseEntityType = typeof(BaseEntity);
        foreach (var entityType in modelBuilder.Model.GetEntityTypes())
        {
            var clrType = entityType.ClrType;

            // 表名
            entityType.SetTableName(clrType.Name.ToLower());
            // 字段名
            foreach (var prop in entityType.GetProperties())
            {
                prop.SetColumnName(prop.Name.ToLower());
                if (prop.ClrType == typeof(Dictionary<string, object>))
                {
                    prop.SetColumnType("jsonb");
                    prop.SetValueConverter(dictConverter);
                    prop.SetValueComparer(dictComparer);
                }
            }

            // 不是BaseEntity子类，跳过
            if (!clrType.IsSubclassOf(baseEntityType))
                continue;
            // 存在父实体，说明是继承派生类，跳过，仅顶层实体注册过滤器
            if (entityType.BaseType is not null)
                continue;

            //var param = Expression.Parameter(clrType);
            //var propExpr = Expression.Property(param, nameof(BaseEntity.Deleted));
            //var filterExpr = Expression.Lambda(Expression.Equal(propExpr, Expression.Constant(false)), param);
            //modelBuilder.Entity(clrType).HasQueryFilter(filterExpr);
        }

        

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}

public class DictionaryJsonConverter : ValueConverter<Dictionary<string, object>, string>
{
    public DictionaryJsonConverter() : base(
        dict => JsonSerializer.Serialize(dict),
        str => JsonSerializer.Deserialize<Dictionary<string, object>>(str) ?? new Dictionary<string, object>()
    )
    { }
}

public class DictionaryValueComparer : ValueComparer<Dictionary<string, object>>
{
    public DictionaryValueComparer() : base(
        (dictA, dictB) => DeepEquals(dictA, dictB),
        dict => GetDeepHashCode(dict),
        dict => dict == null ? null : new Dictionary<string, object>(dict)
    )
    { }

    /// <summary>深度比较两个字典</summary>
    private static bool DeepEquals(Dictionary<string, object>? dictA, Dictionary<string, object>? dictB)
    {
        if (ReferenceEquals(dictA, dictB)) return true;
        if (dictA == null || dictB == null) return false;
        if (dictA.Count != dictB.Count) return false;

        foreach (var kv in dictA)
        {
            if (!dictB.TryGetValue(kv.Key, out var bValue))
                return false;

            if (!DeepValueEquals(kv.Value, bValue))
                return false;
        }
        return true;
    }

    /// <summary>递归深度比较对象值（支持嵌套字典、数组、基础类型）</summary>
    private static bool DeepValueEquals(object? a, object? b)
    {
        if (ReferenceEquals(a, b)) return true;
        if (a == null || b == null) return false;

        if (a is Dictionary<string, object> dictA && b is Dictionary<string, object> dictB)
        {
            return DeepEquals(dictA, dictB);
        }
        if (a is List<object> listA && b is List<object> listB)
        {
            if (listA.Count != listB.Count) return false;
            for (int i = 0; i < listA.Count; i++)
            {
                if (!DeepValueEquals(listA[i], listB[i]))
                    return false;
            }
            return true;
        }
        return object.Equals(a, b);
    }

    /// <summary>生成深度HashCode</summary>
    private static int GetDeepHashCode(Dictionary<string, object> dict)
    {
        int hash = 17;
        foreach (var kv in dict.OrderBy(x => x.Key))
        {
            hash = hash * 31 + kv.Key.GetHashCode();
            hash = hash * 31 + GetValueHash(kv.Value);
        }
        return hash;
    }

    private static int GetValueHash(object? value)
    {
        if (value == null) return 0;
        if (value is Dictionary<string, object> dict)
            return GetDeepHashCode(dict);
        if (value is List<object> list)
        {
            int h = 17;
            foreach (var item in list)
                h = h * 31 + GetValueHash(item);
            return h;
        }
        return value.GetHashCode();
    }
}

/*
   public class DictionaryJsonConverter : ValueConverter<Dictionary<string, object>, string>
    {
        public static object? UnwrapJsonElement(object? value)
        {
            if (value is JsonElement jsonElement)
            {
                return jsonElement.ValueKind switch
                {
                    JsonValueKind.String => jsonElement.GetString(),
                    JsonValueKind.Number => jsonElement.TryGetDecimal(out var dec) ? dec : jsonElement.GetDouble(),
                    JsonValueKind.True => true,
                    JsonValueKind.False => false,
                    JsonValueKind.Null => null,
                    JsonValueKind.Object => JsonSerializer.Deserialize<Dictionary<string, object>>(jsonElement.GetRawText()),
                    JsonValueKind.Array => JsonSerializer.Deserialize<List<object>>(jsonElement.GetRawText()),
                    _ => jsonElement.GetRawText()
                };
            }
            return value;
        }

        public DictionaryJsonConverter() : base(
            dict => JsonSerializer.Serialize(dict),
            str =>
            {
                var rawDict = JsonSerializer.Deserialize<Dictionary<string, object>>(str);
                if (rawDict == null)
                    return new Dictionary<string, object>();

                var result = new Dictionary<string, object>();
                foreach (var kv in rawDict)
                {
                    result[kv.Key] = UnwrapJsonElement(kv.Value);
                }
                return result;
            })
        {
        }
    }

}
 */