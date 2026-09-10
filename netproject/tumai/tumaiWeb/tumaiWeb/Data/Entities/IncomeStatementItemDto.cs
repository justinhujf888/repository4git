using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using tumaiWeb.Data.Entities;

namespace tumaiWeb.Data.Entities
{
    public class IncomeStatementItemDto
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
        // 报告期/截止日期 yyyy-MM-dd，对应接口jzrq
        public string ReportDate { get; set; } = string.Empty;
        // 披露日期，对应接口plrq，财报对外发布时间
        public string? PublishDate { get; set; }
        // 报告类型：年报/一季报/中报/三季报
        public string ReportType { get; set; } = string.Empty;

        // 营业总收入
        public decimal? TotalRevenue { get; set; }
        // 营业收入
        public decimal? OperatingRevenue { get; set; }
        // 营业成本
        public decimal? OperatingCost { get; set; }
        // 税金及附加
        public decimal? TaxAndSurcharges { get; set; }
        // 销售费用
        public decimal? SellingExpense { get; set; }
        // 管理费用
        public decimal? AdminExpense { get; set; }
        // 研发费用
        public decimal? RndExpense { get; set; }
        // 财务费用
        public decimal? FinanceExpense { get; set; }
        // 利息费用
        public decimal? InterestExpense { get; set; }
        // 利息收入
        public decimal? InterestIncome { get; set; }

        // 营业利润
        public decimal? OperatingProfit { get; set; }
        // 营业外收入
        public decimal? NonOperatingIncome { get; set; }
        // 营业外支出
        public decimal? NonOperatingExpense { get; set; }
        // 利润总额
        public decimal? TotalProfit { get; set; }
        // 所得税费用
        public decimal? IncomeTaxExpense { get; set; }
        // 净利润
        public decimal? NetProfit { get; set; }
        // 归属于母公司股东净利润
        public decimal? NetProfitParent { get; set; }
        // 少数股东损益
        public decimal? MinorityProfit { get; set; }
        // 扣除非经常性损益净利润
        public decimal? DeductNonProfit { get; set; }

        // 基本每股收益
        public decimal? BasicEps { get; set; }
        // 稀释每股收益
        public decimal? DilutedEps { get; set; }

        // 原始单条行json，存入PostgreSQL jsonb
        public Dictionary<string, object> RawJson { get; set; } = new();
        // 入库时间
        public DateTime CreateTime { get; set; }
    }
}

public class IncomeStatementItemDtoConfiguration : IEntityTypeConfiguration<IncomeStatementItemDto>
{
    public void Configure(EntityTypeBuilder<IncomeStatementItemDto> entity)
    {
        entity.HasKey(x => x.Id);
        entity.Property(x => x.StockCode).HasColumnType("varchar(32)").IsRequired();
        entity.Property(x => x.Market).HasColumnType("varchar(4)").IsRequired();
        entity.Property(x => x.ReportDate).HasColumnType("varchar(32)").IsRequired();
        entity.Property(x => x.PublishDate).HasColumnType("varchar(32)");
        entity.Property(x => x.ReportType).HasColumnType("varchar(32)").IsRequired();

        entity.Property(x => x.TotalRevenue).HasColumnType("numeric(20,4)");
        entity.Property(x => x.OperatingRevenue).HasColumnType("numeric(20,4)");
        entity.Property(x => x.OperatingCost).HasColumnType("numeric(20,4)");
        entity.Property(x => x.TaxAndSurcharges).HasColumnType("numeric(20,4)");
        entity.Property(x => x.SellingExpense).HasColumnType("numeric(20,4)");
        entity.Property(x => x.AdminExpense).HasColumnType("numeric(20,4)");
        entity.Property(x => x.RndExpense).HasColumnType("numeric(20,4)");
        entity.Property(x => x.FinanceExpense).HasColumnType("numeric(20,4)");
        entity.Property(x => x.InterestExpense).HasColumnType("numeric(20,4)");
        entity.Property(x => x.InterestIncome).HasColumnType("numeric(20,4)");
        entity.Property(x => x.OperatingProfit).HasColumnType("numeric(20,4)");
        entity.Property(x => x.NonOperatingIncome).HasColumnType("numeric(20,4)");
        entity.Property(x => x.NonOperatingExpense).HasColumnType("numeric(20,4)");
        entity.Property(x => x.TotalProfit).HasColumnType("numeric(20,4)");
        entity.Property(x => x.IncomeTaxExpense).HasColumnType("numeric(20,4)");
        entity.Property(x => x.NetProfit).HasColumnType("numeric(20,4)");
        entity.Property(x => x.NetProfitParent).HasColumnType("numeric(20,4)");
        entity.Property(x => x.MinorityProfit).HasColumnType("numeric(20,4)");
        entity.Property(x => x.DeductNonProfit).HasColumnType("numeric(20,4)");
        entity.Property(x => x.BasicEps).HasColumnType("numeric(12,6)");
        entity.Property(x => x.DilutedEps).HasColumnType("numeric(12,6)");

        entity.Property(x => x.RawJson).HasColumnType("jsonb");
        entity.Property(x => x.CreateTime).HasColumnType("timestamp without time zone");

        // 唯一索引：股票代码 + 报告截止日期（Upsert用）
        entity.HasIndex(x => new { x.StockCode, x.ReportDate, x.Market  }).IsUnique();
    }
}
