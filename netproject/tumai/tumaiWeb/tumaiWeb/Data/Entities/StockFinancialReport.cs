using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace tumaiWeb.Data.Entities;

/// <summary>
/// 合并财务报表：利润表 + 资产负债表 + 现金流量表，金额单位：万元
/// </summary>
public class StockFinancialReport : BaseEntity
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
    /// 财报期截止日，如2025-12-31年报
    /// </summary>
    public DateOnly ReportDate { get; set; }

    /// <summary>
    /// 财报类型：Q1一季报/H1中报/Q3三季报/Annual年报
    /// </summary>
    public string? ReportType { get; set; }

    /// <summary>
    /// 营业收入(万元)
    /// </summary>
    public decimal? Income { get; set; }

    /// <summary>
    /// 营业成本(万元)
    /// </summary>
    public decimal? Cost { get; set; }

    /// <summary>
    /// 营业利润(万元)
    /// </summary>
    public decimal? Profit { get; set; }

    /// <summary>
    /// 利润总额(万元)
    /// </summary>
    public decimal? TotalProfit { get; set; }

    /// <summary>
    /// 净利润(万元)
    /// </summary>
    public decimal? NetProfit { get; set; }

    /// <summary>
    /// 扣非净利润(万元)
    /// </summary>
    public decimal? DeductedProfit { get; set; }

    /// <summary>
    /// 资产总计(万元)
    /// </summary>
    public decimal? TotalAssets { get; set; }

    /// <summary>
    /// 负债合计(万元)
    /// </summary>
    public decimal? TotalLiabilities { get; set; }

    /// <summary>
    /// 股东权益合计/净资产(万元)
    /// </summary>
    public decimal? ShareholdersEquity { get; set; }

    /// <summary>
    /// 每股净资产(元)
    /// </summary>
    public decimal? NetAssetPerShare { get; set; }

    /// <summary>
    /// 经营活动现金流净额(万元)
    /// </summary>
    public decimal? OperatingCashFlow { get; set; }

    /// <summary>
    /// 投资活动现金流净额(万元)
    /// </summary>
    public decimal? InvestingCashFlow { get; set; }

    /// <summary>
    /// 筹资活动现金流净额(万元)
    /// </summary>
    public decimal? FinancingCashFlow { get; set; }

    /// <summary>
    /// 毛利率(%)，业务代码计算后入库
    /// </summary>
    public decimal? GrossRate { get; set; }

    /// <summary>
    /// 净利率(%)，业务代码计算后入库
    /// </summary>
    public decimal? NetRate { get; set; }

    /// <summary>
    /// 资产负债率(%)，业务代码计算后入库
    /// </summary>
    public decimal? DebtRate { get; set; }

    /// <summary>
    /// ROE净资产收益率(%)，业务代码计算后入库
    /// </summary>
    public decimal? Roe { get; set; }

    /// <summary>
    /// 拉取入库时间
    /// </summary>
    public DateTime PullTime { get; set; } = DateTime.UtcNow;

    /// <summary>
    /// 原始接口返回jsonb片段，用于校验排错
    /// </summary>
    public Dictionary<string, object?>? RawJson { get; set; }

    /// <summary>
    /// 关联股票基础信息
    /// </summary>
    public StockBasic? StockBasic { get; set; }

    /// <summary>
    /// 披露日期
    /// </summary>
    public DateOnly? DiscloseDate { get; set; }

    /// <summary>
    /// 归母净利润(万元)
    /// </summary>
    public decimal? ParentCompanyNetProfit { get; set; }

    /// <summary>
    /// 所得税费用(万元)
    /// </summary>
    public decimal? IncomeTaxExpense { get; set; }

    /// <summary>
    /// 基本每股收益
    /// </summary>
    public decimal? BasicEps { get; set; }

    /// <summary>
    /// 稀释每股收益
    /// </summary>
    public decimal? DilutedEps { get; set; }
}

public class StockFinancialReportConfiguration : IEntityTypeConfiguration<StockFinancialReport>
{
    public void Configure(EntityTypeBuilder<StockFinancialReport> b)
    {
            b.Property(e => e.Id).HasColumnName("id");
            b.Property(e => e.StockCode).HasColumnType("varchar(20)").IsRequired();
        b.Property(e => e.Market).HasColumnType("varchar(4)").IsRequired();
        b.Property(e => e.ReportDate).HasColumnType("date").IsRequired();
            b.Property(e => e.ReportType).HasColumnType("varchar(20)");

            b.Property(e => e.Income).HasColumnType("numeric(24,2)");
            b.Property(e => e.Cost).HasColumnType("numeric(24,2)");
            b.Property(e => e.Profit).HasColumnType("numeric(24,2)");
            b.Property(e => e.TotalProfit).HasColumnType("numeric(24,2)");
            b.Property(e => e.NetProfit).HasColumnType("numeric(24,2)");
            b.Property(e => e.DeductedProfit).HasColumnType("numeric(24,2)");
            b.Property(e => e.TotalAssets).HasColumnType("numeric(24,2)");
            b.Property(e => e.TotalLiabilities).HasColumnType("numeric(24,2)");
            b.Property(e => e.ShareholdersEquity).HasColumnType("numeric(24,2)");
            b.Property(e => e.NetAssetPerShare).HasColumnType("numeric(14,4)");
            b.Property(e => e.OperatingCashFlow).HasColumnType("numeric(24,2)");
            b.Property(e => e.InvestingCashFlow).HasColumnType("numeric(24,2)");
            b.Property(e => e.FinancingCashFlow).HasColumnType("numeric(24,2)");

            b.Property(e => e.GrossRate).HasColumnType("numeric(10,4)");
            b.Property(e => e.NetRate).HasColumnType("numeric(10,4)");
            b.Property(e => e.DebtRate).HasColumnType("numeric(10,4)");
            b.Property(e => e.Roe).HasColumnType("numeric(10,4)");

            b.Property(e => e.PullTime);

            b.Property(e => e.RawJson)
                .HasColumnType("jsonb");

            b.HasKey(e => e.Id);
            b.HasAlternateKey(e => new { e.StockCode, e.ReportDate,e.Market }).HasName("uk_stock_report");
            b.HasIndex(e => new { e.StockCode, e.ReportDate, e.Market })
                .HasDatabaseName("idx_fin_stock_date")
                .IsDescending(false, true, true);
    }
}