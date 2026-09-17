using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;

namespace tumaiWeb.Data.Entities;

public class DesignTimeDbContextFactory : IDesignTimeDbContextFactory<AppDbContext>
{
    public AppDbContext CreateDbContext(string[] args)
    {
        var config = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("appsettings.json", optional: false)
            .AddJsonFile("appsettings.Development.json", optional: true)
            .Build();

        var connStr = config.GetConnectionString("tumaiDb")!;
        var optionsBuilder = new DbContextOptionsBuilder<AppDbContext>();

        optionsBuilder.UseNpgsql(connStr, npgsqlOpt =>
        {
            npgsqlOpt.ConfigureDataSource(dsBuilder =>
            {
                dsBuilder.EnableDynamicJson();
            });
        });
        optionsBuilder.EnableSensitiveDataLogging();

        return new AppDbContext(optionsBuilder.Options);
    }
}