using Microsoft.EntityFrameworkCore;
using System.Linq.Expressions;
using tumaiWeb.Data.Entities;

namespace tumaiWeb.Data.Repository
{
    public interface IRepository<T,TId> where T : BaseEntity
    {
        /// <summary>
        /// 查询链路（默认启用全局软删除过滤器）
        /// </summary>
        IQueryable<T> Query();

        /// <summary>
        /// 查询链路（忽略全局过滤器，可查询已删除数据）
        /// </summary>
        IQueryable<T> QueryAll();

        /// <summary>主键查询</summary>
        Task<T?> FindByIdAsync(TId id);

        /// <summary>条件查询第一条</summary>
        Task<T?> FirstAsync(Expression<Func<T, bool>> predicate);

        /// <summary>判断是否存在</summary>
        Task<bool> ExistsAsync(Expression<Func<T, bool>> predicate);

        /// <summary>新增单条</summary>
        Task AddAsync(T entity);

        /// <summary>批量新增</summary>
        Task AddRangeAsync(IEnumerable<T> entities);

        /// <summary>
        /// 标记实体：新增 / 修改
        /// ⚠️【重要】≠ JPA save()！
        /// 仅依靠主键是否有值做状态标记，**不会查询数据库校验记录是否真实存在**
        /// 外部传入游离实体慎用；不存在的主键执行SaveChanges会更新0行无异常
        /// </summary>
        void MarkAsCreateOrUpdate(T entity);

        /// <summary>批量标记新增/修改</summary>
        void MarkAsCreateOrUpdateRange(IEnumerable<T> entities);

        /// <summary>物理删除单条</summary>
        void Remove(T entity);

        /// <summary>批量物理删除</summary>
        void RemoveRange(IEnumerable<T> entities);

        /// <summary>
        /// 根据ID软删除（加载实体方式）
        /// 适合少量数据；大批量推荐使用 ExecuteUpdateAsync
        /// </summary>
        Task SoftDeleteAsync(TId id);

        /// <summary>提交上下文变更</summary>
        Task<int> SaveChangesAsync();
    }

    public static class QueryableExtensions
    {
        /// <summary>
        /// 分页查询
        /// 注意：执行两条SQL（COUNT + SELECT）；超大表禁止深度分页
        /// </summary>
        /// <param name="query">查询链路</param>
        /// <param name="pageNum">页码，从1开始</param>
        /// <param name="pageSize">页大小</param>
        public static async Task<PageResult<T>> ToPageAsync<T>(this IQueryable<T> query, int pageNum, int pageSize)
        {
            if (pageNum < 1) pageNum = 1;
            if (pageSize < 1) pageSize = 10;

            var total = await query.CountAsync();
            var list = await query
                .Skip((pageNum - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();

            return new PageResult<T>(total, list);
        }
    }

    /// <summary>分页返回对象，对标SpringData Page<T></summary>
    public class PageResult<T>
    {
        /// <summary>总条数</summary>
        public long Total { get; set; }

        /// <summary>当前页数据</summary>
        public List<T> Records { get; set; } = [];

        public PageResult(long total, List<T> records)
        {
            Total = total;
            Records = records;
        }
    }
}
