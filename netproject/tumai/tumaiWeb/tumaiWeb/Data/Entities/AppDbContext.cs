using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.ChangeTracking;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq.Expressions;
using System.Reflection;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace tumaiWeb.Data.Entities;

public abstract class BaseEntity
{
    // public long Id { get; set; }

    /// <summary>软删除标记</summary>
    //public bool Deleted { get; set; }

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
                    //prop.SetColumnType("jsonb");
                    //prop.SetValueConverter(dictConverter);
                    //prop.SetValueComparer(dictComparer);
                    //prop.SetValueComparer(
                    //    new ValueComparer<Dictionary<string, object>>(
                    //        (d1, d2) =>
                    //            System.Text.Json.JsonSerializer.Serialize(d1, (System.Text.Json.JsonSerializerOptions?)null)
                    //            == System.Text.Json.JsonSerializer.Serialize(d2, (System.Text.Json.JsonSerializerOptions?)null),
                    //        d => d == null ? 0 : d.GetHashCode()
                    //    )
                    //);
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

public static class JsonbPropertyExtensions
{
    public static PropertyBuilder<Dictionary<string, object>> ConfigureAsJsonb(
        this PropertyBuilder<Dictionary<string, object>> builder)
    {
        var converter = new Microsoft.EntityFrameworkCore.Storage.ValueConversion.ValueConverter<Dictionary<string, object>, string>(
            v => System.Text.Json.JsonSerializer.Serialize(v, (System.Text.Json.JsonSerializerOptions?)null),
            v => System.Text.Json.JsonSerializer.Deserialize<Dictionary<string, object>>(
                v, (System.Text.Json.JsonSerializerOptions?)null)
        );

        var propertyBuilder = builder
            .HasConversion(converter)
            .HasColumnType("jsonb");

        propertyBuilder.Metadata.SetValueComparer(
            new Microsoft.EntityFrameworkCore.ChangeTracking.ValueComparer<Dictionary<string, object>>(
                (d1, d2) =>
                    System.Text.Json.JsonSerializer.Serialize(d1, (System.Text.Json.JsonSerializerOptions?)null)
                    == System.Text.Json.JsonSerializer.Serialize(d2, (System.Text.Json.JsonSerializerOptions?)null),
                d => d == null ? 0 : d.GetHashCode()
            )  
        );

        return propertyBuilder;
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