using AngleSharp.Dom;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using tumaiWeb.Data.Entities;

namespace tumaiWeb.Data.Entities
{
    public class BalanceSheetItemDto : BaseEntity
    {
        // 主键ID
        public long Id { get; set; }
        /// <summary>
        /// 股票代码，外键关联stock_basic
        /// </summary>
        public required string StockCode { get; set; }

        /// <summary>
        /// 市场标识 SH/SZ/BJ，和StockBasic联合外键
        /// </summary>
        public required string Market { get; set; }
        // 报告期 yyyy-MM-dd，对应接口jzrq
        public string ReportDate { get; set; } = string.Empty;
        // 披露日期，对应接口plrq
        public string? PublishDate { get; set; }
        // 报告类型：年报/一季报/中报/三季报
        public string ReportType { get; set; } = string.Empty;

        // 流动资产合计
        public decimal? TotalCurrentAsset { get; set; }
        // 货币资金
        public decimal? MonetaryFund { get; set; }
        // 交易性金融资产
        public decimal? TradingFinancialAsset { get; set; }
        // 应收票据
        public decimal? BillReceivable { get; set; }
        // 应收账款
        public decimal? AccountReceivable { get; set; }
        // 预付款项
        public decimal? Prepayment { get; set; }
        // 存货
        public decimal? Inventory { get; set; }
        // 其他流动资产
        public decimal? OtherCurrentAsset { get; set; }

        // 非流动资产合计
        public decimal? TotalNonCurrentAsset { get; set; }
        // 固定资产原值
        public decimal? FixedAssetOriginal { get; set; }
        // 固定资产净值
        public decimal? FixedAssetNet { get; set; }
        // 无形资产
        public decimal? IntangibleAsset { get; set; }
        // 商誉
        public decimal? Goodwill { get; set; }
        // 长期股权投资
        public decimal? LongTermEquityInvest { get; set; }

        // 资产总计
        public decimal? TotalAsset { get; set; }

        // 流动负债合计
        public decimal? TotalCurrentLiability { get; set; }
        // 短期借款
        public decimal? ShortTermLoan { get; set; }
        // 应付票据
        public decimal? BillPayable { get; set; }
        // 应付账款
        public decimal? AccountPayable { get; set; }
        // 预收款项
        public decimal? AdvanceReceived { get; set; }
        // 应付职工薪酬
        public decimal? SalaryPayable { get; set; }
        // 应交税费
        public decimal? TaxPayable { get; set; }

        // 非流动负债合计
        public decimal? TotalNonCurrentLiability { get; set; }
        // 长期借款
        public decimal? LongTermLoan { get; set; }
        // 应付债券
        public decimal? BondPayable { get; set; }

        // 负债合计
        public decimal? TotalLiability { get; set; }

        // 所有者权益合计
        public decimal? TotalEquity { get; set; }
        // 实收资本（股本）
        public decimal? PaidInCapital { get; set; }
        // 资本公积
        public decimal? CapitalReserve { get; set; }
        // 盈余公积
        public decimal? SurplusReserve { get; set; }
        // 未分配利润
        public decimal? UndistributedProfit { get; set; }
        // 归属于母公司股东权益
        public decimal? ParentEquity { get; set; }
        // 少数股东权益
        public decimal? MinorityEquity { get; set; }

        // 原始单条行json，存入PostgreSQL jsonb
        public Dictionary<string, object> RawJson { get; set; } = new();
    }
}

public class BalanceSheetItemDtoConfiguration : IEntityTypeConfiguration<BalanceSheetItemDto>
{
    public void Configure(EntityTypeBuilder<BalanceSheetItemDto> entity)
    {
        entity.HasKey(x => x.Id);
        entity.Property(x => x.StockCode).HasColumnType("varchar(32)").IsRequired();
        entity.Property(x => x.Market).HasColumnType("varchar(4)").IsRequired();
        entity.Property(x => x.ReportDate).HasColumnType("varchar(32)").IsRequired();
        entity.Property(x => x.PublishDate).HasColumnType("varchar(32)");
        entity.Property(x => x.ReportType).HasColumnType("varchar(32)").IsRequired();

        entity.Property(x => x.TotalCurrentAsset).HasColumnType("numeric(20,4)");
        entity.Property(x => x.MonetaryFund).HasColumnType("numeric(20,4)");
        entity.Property(x => x.TradingFinancialAsset).HasColumnType("numeric(20,4)");
        entity.Property(x => x.BillReceivable).HasColumnType("numeric(20,4)");
        entity.Property(x => x.AccountReceivable).HasColumnType("numeric(20,4)");
        entity.Property(x => x.Prepayment).HasColumnType("numeric(20,4)");
        entity.Property(x => x.Inventory).HasColumnType("numeric(20,4)");
        entity.Property(x => x.OtherCurrentAsset).HasColumnType("numeric(20,4)");
        entity.Property(x => x.TotalNonCurrentAsset).HasColumnType("numeric(20,4)");
        entity.Property(x => x.FixedAssetOriginal).HasColumnType("numeric(20,4)");
        entity.Property(x => x.FixedAssetNet).HasColumnType("numeric(20,4)");
        entity.Property(x => x.IntangibleAsset).HasColumnType("numeric(20,4)");
        entity.Property(x => x.Goodwill).HasColumnType("numeric(20,4)");
        entity.Property(x => x.LongTermEquityInvest).HasColumnType("numeric(20,4)");
        entity.Property(x => x.TotalAsset).HasColumnType("numeric(20,4)");
        entity.Property(x => x.TotalCurrentLiability).HasColumnType("numeric(20,4)");
        entity.Property(x => x.ShortTermLoan).HasColumnType("numeric(20,4)");
        entity.Property(x => x.BillPayable).HasColumnType("numeric(20,4)");
        entity.Property(x => x.AccountPayable).HasColumnType("numeric(20,4)");
        entity.Property(x => x.AdvanceReceived).HasColumnType("numeric(20,4)");
        entity.Property(x => x.SalaryPayable).HasColumnType("numeric(20,4)");
        entity.Property(x => x.TaxPayable).HasColumnType("numeric(20,4)");
        entity.Property(x => x.TotalNonCurrentLiability).HasColumnType("numeric(20,4)");
        entity.Property(x => x.LongTermLoan).HasColumnType("numeric(20,4)");
        entity.Property(x => x.BondPayable).HasColumnType("numeric(20,4)");
        entity.Property(x => x.TotalLiability).HasColumnType("numeric(20,4)");
        entity.Property(x => x.TotalEquity).HasColumnType("numeric(20,4)");
        entity.Property(x => x.PaidInCapital).HasColumnType("numeric(20,4)");
        entity.Property(x => x.CapitalReserve).HasColumnType("numeric(20,4)");
        entity.Property(x => x.SurplusReserve).HasColumnType("numeric(20,4)");
        entity.Property(x => x.UndistributedProfit).HasColumnType("numeric(20,4)");
        entity.Property(x => x.ParentEquity).HasColumnType("numeric(20,4)");
        entity.Property(x => x.MinorityEquity).HasColumnType("numeric(20,4)");

        entity.Property(x => x.RawJson).HasColumnType("jsonb");
        entity.HasIndex(x => new { x.StockCode, x.Market, x.ReportDate }).IsUnique();
    }
}
