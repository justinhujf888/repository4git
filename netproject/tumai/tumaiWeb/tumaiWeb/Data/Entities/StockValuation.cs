using AngleSharp.Dom;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using System.ComponentModel.DataAnnotations.Schema;
using tumaiWeb.Data.Entities;

namespace tumaiWeb.Data.Entities
{
    public class StockValuation : BaseEntity
    {
        /// <summary>
        /// 主键自增ID
        /// </summary>
        public long Id { get; set; }

        /// <summary>
        /// 股票代码
        /// </summary>
        public required string StockCode { get; set; }

        /// <summary>
        /// 市场标识 SH/SZ/BJ，和StockBasic联合外键
        /// </summary>
        public required string Market { get; set; }

        /// <summary>
        /// 估值计算对应的交易日
        /// </summary>
        public DateOnly TradeDate { get; set; }

        /// <summary>
        /// 总市值（万元）
        /// </summary>
        public decimal? TotalMarketCap { get; set; }

        /// <summary>
        /// PE-TTM 滚动市盈率，净利润为负时存null
        /// </summary>
        public decimal? PeTtm { get; set; }

        /// <summary>
        /// PB 市净率，净资产为负时存null
        /// </summary>
        public decimal? Pb { get; set; }

        /// <summary>
        /// PS-TTM 滚动市销率
        /// </summary>
        public decimal? PsTtm { get; set; }

        /// <summary>
        /// 记录本次指标计算时间
        /// </summary>
        public DateTime CalcTime { get; set; }

        /// <summary>创建时间（UTC）</summary>
        [Column("createtime")]
        public DateTime CreateTime { get; set; } = DateTime.UtcNow;

        /// <summary>更新时间（UTC）</summary>
        [Column("updatetime")]
        public DateTime? UpdateTime { get; set; } = DateTime.UtcNow;
    }
}

public class StockValuationConfiguration : IEntityTypeConfiguration<StockValuation>
{
    public void Configure(EntityTypeBuilder<StockValuation> entity)
    {
        entity.HasKey(x => x.Id);
        // 联合唯一索引：同一只股票同一个交易日只能一条估值记录，用于Upsert
        entity.HasIndex(x => new { x.StockCode,x.Market,x.TradeDate }).IsUnique();

        entity.Property(x => x.StockCode)
            .IsRequired()
            .HasMaxLength(20);

        entity.Property(x => x.Market)
            .IsRequired()
            .HasMaxLength(4);

        entity.Property(x => x.TradeDate).IsRequired();
        entity.Property(x => x.TotalMarketCap);
        entity.Property(x => x.PeTtm);
        entity.Property(x => x.Pb);
        entity.Property(x => x.PsTtm);
        entity.Property(x => x.CalcTime);

        // 关联StockBasic
        entity.HasOne<StockBasic>()
            .WithMany()
            .HasForeignKey(x => new { x.StockCode, x.Market })
            .HasPrincipalKey(x => new { x.StockCode, x.Market });
    }
}
