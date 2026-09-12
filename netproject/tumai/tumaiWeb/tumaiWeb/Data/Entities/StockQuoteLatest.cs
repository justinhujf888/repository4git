using AngleSharp.Dom;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace tumaiWeb.Data.Entities;

/// <summary>
/// 每只股票仅保存最新一条行情，业务查询当前价格走这张表
/// </summary>
public class StockQuoteLatest : BaseEntity
{
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
    /// 成交量，单位：手
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
    /// 快照时间
    /// </summary>
    public DateTime SnapshotTime { get; set; }

    /// <summary>
    /// 关联股票基础信息
    /// </summary>
    public StockBasic? StockBasic { get; set; }
}

public class StockQuoteLatestConfiguration : IEntityTypeConfiguration<StockQuoteLatest>
{
    public void Configure(EntityTypeBuilder<StockQuoteLatest> b)
    {
        b.HasKey(e => e.StockCode);
        b.Property(e => e.StockCode)
            .HasColumnType("varchar(20)")
            .IsRequired();
        b.Property(x => x.Market).HasColumnType("varchar(4)").IsRequired();
        // 注意优先用 .HasPrecision(16, 4) 替代 .HasColumnType("numeric(16,4)")；下面示例不规范，迁移不是PGSQL的数据库会有问题
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
    }
}