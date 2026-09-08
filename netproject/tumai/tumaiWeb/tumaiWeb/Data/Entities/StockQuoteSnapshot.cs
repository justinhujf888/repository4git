using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

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
    public string StockCode { get; set; } = string.Empty;

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
        b.ToTable("stock_quote_snapshot");

        b.Property(e => e.Id).HasColumnName("id");
        b.Property(e => e.StockCode).HasColumnName("stock_code").HasColumnType("varchar(20)").IsRequired();

        b.Property(e => e.Price).HasColumnName("p").HasColumnType("numeric(16,4)");
        b.Property(e => e.YesterdayClose).HasColumnName("yc").HasColumnType("numeric(16,4)");
        b.Property(e => e.Open).HasColumnName("o").HasColumnType("numeric(16,4)");
        b.Property(e => e.High).HasColumnName("h").HasColumnType("numeric(16,4)");
        b.Property(e => e.Low).HasColumnName("l").HasColumnType("numeric(16,4)");
        b.Property(e => e.ChangePercent).HasColumnName("pc").HasColumnType("numeric(10,4)");
        b.Property(e => e.ChangeAmount).HasColumnName("ud").HasColumnType("numeric(16,4)");
        b.Property(e => e.Volume).HasColumnName("v");
        b.Property(e => e.Turnover).HasColumnName("cje").HasColumnType("numeric(24,2)");
        b.Property(e => e.TotalMarketValue).HasColumnName("sz").HasColumnType("numeric(24,2)");
        b.Property(e => e.Pe).HasColumnName("pe").HasColumnType("numeric(12,4)");
        b.Property(e => e.PbRatio).HasColumnName("pb_ratio").HasColumnType("numeric(12,4)");
        b.Property(e => e.TurnoverRate).HasColumnName("hs").HasColumnType("numeric(10,4)");
        b.Property(e => e.Amplitude).HasColumnName("zf").HasColumnType("numeric(10,4)");
        b.Property(e => e.FiveMinChange).HasColumnName("fm").HasColumnType("numeric(10,4)");

        b.Property(e => e.SnapshotTime).HasColumnName("snapshot_time").IsRequired();
        b.Property(e => e.PullTime).HasColumnName("pull_time");

        b.Property(e => e.RawJson)
            .HasColumnName("raw_json")
            .HasColumnType("jsonb");

        b.HasKey(e => e.Id);
        b.HasIndex(e => new { e.StockCode, e.SnapshotTime })
            .HasDatabaseName("idx_quote_stock_time")
            .IsDescending(false, true);
    }
}