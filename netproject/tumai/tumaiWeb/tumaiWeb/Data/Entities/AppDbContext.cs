using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq.Expressions;
using System.Reflection;

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
    public Dictionary<string,object> TempMap { get; set; } = new();
}

public partial class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options)
        : base(options)
    {
    }

    //public DbSet<StockBasic> StockBasics => Set<StockBasic>();
    //public DbSet<StockQuoteSnapshot> StockQuoteSnapshots => Set<StockQuoteSnapshot>();
    //public DbSet<StockQuoteLatest> StockQuoteLatests => Set<StockQuoteLatest>();
    //public DbSet<StockFinancialReport> StockFinancialReports => Set<StockFinancialReport>();

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
