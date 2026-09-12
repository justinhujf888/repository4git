using AngleSharp.Dom;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace tumaiWeb.Data.Entities;

/// <summary>
/// 股票基础字典表：维护股票代码、名称、市场等元信息
/// </summary>
public class StockBasic : BaseEntity
{
    /// <summary>
    /// 主键自增ID
    /// </summary>
    public long Id { get; set; }

    /// <summary>
    /// 股票代码，外键关联stock_basic
    /// </summary>
    public required string StockCode { get; set; }

    /// <summary>
    /// 市场标识 SH/SZ/BJ，和StockBasic联合外键
    /// </summary>
    public required string Market { get; set; }

    /// <summary>
    /// 股票中文名称
    /// </summary>
    public string StockName { get; set; } = string.Empty;


    /// <summary>
    /// 兼容tushare的ts_code（备用）
    /// </summary>
    //public string? TsCode { get; set; }

    /// <summary>
    /// 是否上市有效
    /// </summary>
    public bool IsActive { get; set; } = true;

    /// <summary>
    /// 行业名称
    /// </summary>
    public string? Industry { get; set; }

    /// <summary>
    /// 上市日期
    /// </summary>
    public DateTime? ListDate { get; set; }

    /// <summary>
    /// 是否退市 0正常 1退市
    /// </summary>
    public int IsDelist { get; set; }

    /// <summary>
    /// 行情快照集合
    /// </summary>
    public ICollection<StockQuoteSnapshot> QuoteSnapshots { get; set; } = new List<StockQuoteSnapshot>();

    /// <summary>
    /// 最新行情集合
    /// </summary>
    public ICollection<StockQuoteLatest> QuoteLatests { get; set; } = new List<StockQuoteLatest>();

    /// <summary>
    /// 财务报表集合
    /// </summary>
    public ICollection<StockFinancialReport> FinancialReports { get; set; } = new List<StockFinancialReport>();
}

public class StockBasicConfiguration : IEntityTypeConfiguration<StockBasic>
{
    public void Configure(EntityTypeBuilder<StockBasic> b)
    {
        b.Property(e => e.Id);

        b.Property(e => e.StockCode)
            .HasColumnType("varchar(20)")
            .IsRequired();

        b.Property(e => e.StockName)
            .HasColumnType("varchar(100)")
            .IsRequired();

        b.Property(e => e.Market)
            .HasColumnType("varchar(4)")
            .IsRequired();

        b.Property(e => e.IsActive);

        b.Property(e => e.Industry)
        .HasMaxLength(100);

        b.Property(e => e.ListDate);

        b.Property(e => e.IsDelist);

        b.HasKey(e => e.Id);
        // AlternateKey 就是备用主键，HasPrincipalKey 必须对应这里
        b.HasAlternateKey(e => new { e.StockCode, e.Market }).HasName("uk_stock_code_market");
        b.HasIndex(e => e.Market).HasDatabaseName("idx_stock_basic_market");

        // ========= 全部导航加上 HasPrincipalKey =========
        b.HasMany(e => e.QuoteSnapshots)
            .WithOne(s => s.StockBasic)
            .HasForeignKey(s => new { s.StockCode, s.Market })
            .HasPrincipalKey(sb => new { sb.StockCode, sb.Market })
            .HasConstraintName("fk_quote_stock");

        b.HasMany(e => e.QuoteLatests)
            .WithOne(l => l.StockBasic)
            .HasForeignKey(l => new { l.StockCode, l.Market })
            .HasPrincipalKey(sb => new { sb.StockCode, sb.Market })
            .HasConstraintName("fk_latest_stock");

        b.HasMany(e => e.FinancialReports)
            .WithOne(f => f.StockBasic)
            .HasForeignKey(f => new { f.StockCode, f.Market }  )
            .HasPrincipalKey(sb => new { sb.StockCode, sb.Market })
            .HasConstraintName("fk_fin_stock");
    }
}