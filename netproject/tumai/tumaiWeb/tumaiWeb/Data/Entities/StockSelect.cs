using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace tumaiWeb.Data.Entities
{
    public class StockSelect : BaseEntity
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
        /// 股票中文名称
        /// </summary>
        public string StockName { get; set; } = string.Empty;
    }

    public class StockSelectConfiguration : IEntityTypeConfiguration<StockSelect>
    {
        public void Configure(EntityTypeBuilder<StockSelect> b)
        {
            b.Property(e => e.StockCode)
            .HasColumnType("varchar(20)")
            .IsRequired();

            b.Property(e => e.StockName)
                .HasColumnType("varchar(100)")
                .IsRequired();
            b.HasKey(e => new { e.StockCode, e.Market });
        }
    }
}
