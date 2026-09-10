using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using tumaiWeb.Data.Entities;

namespace tumaiWeb.Data.Entities
{
    public class CashFlowItemDto
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
        // 报告期，yyyy-MM-dd，对应接口jzrq
        public string ReportDate { get; set; } = string.Empty;
        // 披露日期，对应接口plrq
        public string? PublishDate { get; set; }
        // 报告类型：年报/一季报/中报/三季报
        public string ReportType { get; set; } = string.Empty;

        // 经营活动现金流入小计
        public decimal? OperateCashIn { get; set; }
        // 经营活动现金流出小计
        public decimal? OperateCashOut { get; set; }
        // 经营活动产生的现金流量净额
        public decimal? NetOperateCashFlow { get; set; }

        // 销售商品、提供劳务收到的现金
        public decimal? CashFromSales { get; set; }
        // 收到的税费返还
        public decimal? TaxRefundReceived { get; set; }
        // 收到其他与经营活动有关的现金
        public decimal? OtherOperateCashIn { get; set; }
        // 购买商品、接受劳务支付的现金
        public decimal? CashPayForGoods { get; set; }
        // 支付给职工以及为职工支付的现金
        public decimal? CashPayToStaff { get; set; }
        // 支付的各项税费
        public decimal? TaxPaid { get; set; }
        // 支付其他与经营活动有关的现金
        public decimal? OtherOperateCashOut { get; set; }

        // 投资活动现金流入小计
        public decimal? InvestCashIn { get; set; }
        // 投资活动现金流出小计
        public decimal? InvestCashOut { get; set; }
        // 投资活动产生的现金流量净额
        public decimal? NetInvestCashFlow { get; set; }
        // 收回投资收到的现金
        public decimal? CashFromInvestRecall { get; set; }
        // 取得投资收益收到的现金
        public decimal? CashFromInvestIncome { get; set; }
        // 处置固定资产、无形资产和其他长期资产收回的现金净额
        public decimal? CashFromDisposeLongAsset { get; set; }
        // 购建固定资产、无形资产和其他长期资产支付的现金（CAPEX）
        public decimal? Capex { get; set; }
        // 投资支付的现金
        public decimal? CashPayForInvest { get; set; }

        // 筹资活动现金流入小计
        public decimal? FinanceCashIn { get; set; }
        // 筹资活动现金流出小计
        public decimal? FinanceCashOut { get; set; }
        // 筹资活动产生的现金流量净额
        public decimal? NetFinanceCashFlow { get; set; }
        // 吸收投资收到的现金
        public decimal? CashFromEquity { get; set; }
        // 取得借款收到的现金
        public decimal? CashFromBorrow { get; set; }
        // 偿还债务支付的现金
        public decimal? CashRepayDebt { get; set; }
        // 分配股利、利润或偿付利息支付的现金
        public decimal? CashPayDividendInterest { get; set; }

        // 汇率变动对现金及现金等价物的影响
        public decimal? ForexEffectOnCash { get; set; }
        // 现金及现金等价物净增加额
        public decimal? NetIncreaseCash { get; set; }
        // 期初现金及现金等价物余额
        public decimal? BeginCashBalance { get; set; }
        // 期末现金及现金等价物余额
        public decimal? EndCashBalance { get; set; }

        // 原始单条行json，存入PostgreSQL jsonb
        public Dictionary<string, object> RawJson { get; set; } = new();
        // 入库时间
        public DateTime CreateTime { get; set; }
    }
}

public class CashFlowItemDtoConfiguration : IEntityTypeConfiguration<CashFlowItemDto>
{
    public void Configure(EntityTypeBuilder<CashFlowItemDto> entity)
    {
        entity.HasKey(x => x.Id);
        entity.Property(x => x.StockCode).HasColumnType("varchar(32)").IsRequired();
        entity.Property(x => x.Market).HasColumnType("varchar(4)").IsRequired();
        entity.Property(x => x.ReportDate).HasColumnType("varchar(32)").IsRequired();
        entity.Property(x => x.PublishDate).HasColumnType("varchar(32)");
        entity.Property(x => x.ReportType).HasColumnType("varchar(32)").IsRequired();

        entity.Property(x => x.OperateCashIn).HasColumnType("numeric(20,4)");
        entity.Property(x => x.OperateCashOut).HasColumnType("numeric(20,4)");
        entity.Property(x => x.NetOperateCashFlow).HasColumnType("numeric(20,4)");
        entity.Property(x => x.CashFromSales).HasColumnType("numeric(20,4)");
        entity.Property(x => x.TaxRefundReceived).HasColumnType("numeric(20,4)");
        entity.Property(x => x.OtherOperateCashIn).HasColumnType("numeric(20,4)");
        entity.Property(x => x.CashPayForGoods).HasColumnType("numeric(20,4)");
        entity.Property(x => x.CashPayToStaff).HasColumnType("numeric(20,4)");
        entity.Property(x => x.TaxPaid).HasColumnType("numeric(20,4)");
        entity.Property(x => x.OtherOperateCashOut).HasColumnType("numeric(20,4)");
        entity.Property(x => x.InvestCashIn).HasColumnType("numeric(20,4)");
        entity.Property(x => x.InvestCashOut).HasColumnType("numeric(20,4)");
        entity.Property(x => x.NetInvestCashFlow).HasColumnType("numeric(20,4)");
        entity.Property(x => x.CashFromInvestRecall).HasColumnType("numeric(20,4)");
        entity.Property(x => x.CashFromInvestIncome).HasColumnType("numeric(20,4)");
        entity.Property(x => x.CashFromDisposeLongAsset).HasColumnType("numeric(20,4)");
        entity.Property(x => x.Capex).HasColumnType("numeric(20,4)");
        entity.Property(x => x.CashPayForInvest).HasColumnType("numeric(20,4)");
        entity.Property(x => x.FinanceCashIn).HasColumnType("numeric(20,4)");
        entity.Property(x => x.FinanceCashOut).HasColumnType("numeric(20,4)");
        entity.Property(x => x.NetFinanceCashFlow).HasColumnType("numeric(20,4)");
        entity.Property(x => x.CashFromEquity).HasColumnType("numeric(20,4)");
        entity.Property(x => x.CashFromBorrow).HasColumnType("numeric(20,4)");
        entity.Property(x => x.CashRepayDebt).HasColumnType("numeric(20,4)");
        entity.Property(x => x.CashPayDividendInterest).HasColumnType("numeric(20,4)");
        entity.Property(x => x.ForexEffectOnCash).HasColumnType("numeric(20,4)");
        entity.Property(x => x.NetIncreaseCash).HasColumnType("numeric(20,4)");
        entity.Property(x => x.BeginCashBalance).HasColumnType("numeric(20,4)");
        entity.Property(x => x.EndCashBalance).HasColumnType("numeric(20,4)");

        entity.Property(x => x.RawJson).HasColumnType("jsonb");
        entity.Property(x => x.CreateTime).HasColumnType("timestamp without time zone");
        entity.HasIndex(x => new { x.StockCode, x.ReportDate, x.Market }).IsUnique();
    }
}
