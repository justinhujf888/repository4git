using AngleSharp.Dom;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using SharpCompress.Common;

namespace tumaiWeb.Data.Entities;

/// <summary>
/// A股实时行情快照表：每次批量拉取，每只股票生成一条历史快照
/// </summary>
public class StockQuoteSnapshot : BaseEntity
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
    /// 当前现价(元)
    /// </summary>
    public decimal? Price { get; set; }

    /// <summary>
    /// 昨收价(元)
    /// </summary>
    public decimal? YesterdayClose { get; set; }

    /// <summary>
    /// 开盘价(元)
    /// </summary>
    public decimal? Open { get; set; }

    /// <summary>
    /// 最高价(元)
    /// </summary>
    public decimal? High { get; set; }

    /// <summary>
    /// 最低价(元)
    /// </summary>
    public decimal? Low { get; set; }

    /// <summary>
    /// 涨跌幅(%)
    /// </summary>
    public decimal? ChangePercent { get; set; }

    /// <summary>
    /// 涨跌额(元)
    /// </summary>
    public decimal? ChangeAmount { get; set; }

    /// <summary>
    /// 成交量，单位：手，1手=100股
    /// </summary>
    public long? Volume { get; set; }

    /// <summary>
    /// 成交额，单位：元
    /// </summary>
    public decimal? Turnover { get; set; }

    /// <summary>
    /// 总市值，单位：元
    /// </summary>
    public decimal? TotalMarketValue { get; set; }

    /// <summary>
    /// 动态市盈率
    /// </summary>
    public decimal? Pe { get; set; }

    /// <summary>
    /// 市净率
    /// </summary>
    public decimal? PbRatio { get; set; }

    /// <summary>
    /// 换手率(%)
    /// </summary>
    public decimal? TurnoverRate { get; set; }

    /// <summary>
    /// 振幅(%)
    /// </summary>
    public decimal? Amplitude { get; set; }

    /// <summary>
    /// 5分钟涨跌幅(%)
    /// </summary>
    public decimal? FiveMinChange { get; set; }

    /// <summary>
    /// 接口返回快照时间
    /// </summary>
    public DateTime SnapshotTime { get; set; }

    /// <summary>
    /// 程序本地拉取入库时间
    /// </summary>
    public DateTime PullTime { get; set; } = DateTime.UtcNow;

    /// <summary>
    /// 原始接口返回jsonb，排错使用
    /// </summary>
    public Dictionary<string, object?>? RawJson { get; set; }

    /// <summary>
    /// 关联股票基础信息
    /// </summary>
    public StockBasic? StockBasic { get; set; }
}

public class StockQuoteSnapshotConfiguration : IEntityTypeConfiguration<StockQuoteSnapshot>
{
    public void Configure(EntityTypeBuilder<StockQuoteSnapshot> b)
    {
        b.Property(e => e.Id);
        b.Property(e => e.StockCode).HasColumnType("varchar(20)").IsRequired();
        b.Property(x => x.Market).HasColumnType("varchar(4)")
        .IsRequired();
        b.Property(e => e.Price).HasColumnType("numeric(16,4)");
        b.Property(e => e.YesterdayClose).HasColumnType("numeric(16,4)");
        b.Property(e => e.Open).HasColumnType("numeric(16,4)");
        b.Property(e => e.High).HasColumnType("numeric(16,4)");
        b.Property(e => e.Low).HasColumnType("numeric(16,4)");
        b.Property(e => e.ChangePercent).HasColumnType("numeric(10,4)");
        b.Property(e => e.ChangeAmount).HasColumnType("numeric(16,4)");
        b.Property(e => e.Volume);
        b.Property(e => e.Turnover).HasColumnType("numeric(24,2)");
        b.Property(e => e.TotalMarketValue).HasColumnType("numeric(24,2)");
        b.Property(e => e.Pe).HasColumnType("numeric(12,4)");
        b.Property(e => e.PbRatio).HasColumnType("numeric(12,4)");
        b.Property(e => e.TurnoverRate).HasColumnType("numeric(10,4)");
        b.Property(e => e.Amplitude).HasColumnType("numeric(10,4)");
        b.Property(e => e.FiveMinChange).HasColumnType("numeric(10,4)");

        b.Property(e => e.SnapshotTime).IsRequired();
        b.Property(e => e.PullTime);

        b.Property(e => e.RawJson)
            .HasColumnType("jsonb");

        b.HasKey(e => e.Id);
        // 联合外键，引用StockBasic备用键(StockCode,Market)
        b.HasOne(x => x.StockBasic)
            .WithMany(x => x.QuoteSnapshots)
            .HasForeignKey(x => new { x.StockCode, x.Market })
            .HasPrincipalKey(x => new { x.StockCode, x.Market });
        // 唯一索引：同一只股票 + 快照时间，防止同一时刻重复入库
        b.HasIndex(x => new { x.StockCode, x.Market, x.SnapshotTime })
            .IsUnique();
    }
}