using Microsoft.EntityFrameworkCore;
using System.Linq.Expressions;
using tumaiWeb.Data.Entities;

namespace tumaiWeb.Data.Repository
{
    public class Repository<T,TId> : IRepository<T,TId> where T : BaseEntity
    {
        protected readonly AppDbContext _dbContext;
        protected DbSet<T> Set => _dbContext.Set<T>();

        public Repository(AppDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public IQueryable<T> Query()
        {
            return Set.AsQueryable();
        }

        public IQueryable<T> QueryAll()
        {
            return Set.IgnoreQueryFilters();
        }

        public async Task<T?> FindByIdAsync(TId id)
        {
            return await Set.FindAsync(id);
        }

        public async Task<T?> FirstAsync(Expression<Func<T, bool>> predicate)
        {
            return await Query().FirstOrDefaultAsync(predicate);
        }

        public async Task<bool> ExistsAsync(Expression<Func<T, bool>> predicate)
        {
            return await Query().AnyAsync(predicate);
        }

        public async Task AddAsync(T entity)
        {
            FillCreateAudit(entity);
            await Set.AddAsync(entity);
        }

        public async Task AddRangeAsync(IEnumerable<T> entities)
        {
            foreach (var item in entities)
            {
                FillCreateAudit(item);
            }
            await Set.AddRangeAsync(entities);
        }

        public void MarkAsCreateOrUpdate(T entity)
        {
            var entry = _dbContext.Entry(entity);
            if (entry.IsKeySet)
            {
                // 主键有值 → 标记修改
                entry.State = EntityState.Modified;
                entity.UpdateTime = DateTime.UtcNow;
            }
            else
            {
                FillCreateAudit(entity);
                entry.State = EntityState.Added;
            }
        }

        public void MarkAsCreateOrUpdateRange(IEnumerable<T> entities)
        {
            foreach (var item in entities)
            {
                MarkAsCreateOrUpdate(item);
            }
        }

        public void Remove(T entity)
        {
            Set.Remove(entity);
        }

        public void RemoveRange(IEnumerable<T> entities)
        {
            Set.RemoveRange(entities);
        }

        public async Task SoftDeleteAsync(TId id)
        {
            var entity = await FindByIdAsync(id);
            if (entity != null)
            {
                entity.Deleted = true;
                entity.UpdateTime = DateTime.UtcNow;
                MarkAsCreateOrUpdate(entity);
            }
        }

        public async Task<int> SaveChangesAsync()
        {
            return await _dbContext.SaveChangesAsync();
        }

        /// <summary>填充创建审计字段</summary>
        private void FillCreateAudit(T entity)
        {
            entity.CreateTime = DateTime.UtcNow;
            entity.Deleted = false;
        }
    }
}
