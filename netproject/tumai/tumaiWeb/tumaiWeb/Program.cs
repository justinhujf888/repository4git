using Microsoft.EntityFrameworkCore;
using tumaiWeb.Controller;
using tumaiWeb.Data.Entities;
using tumaiWeb.Data.Repository;
using tumaiWeb.StockService.Mairui;
using tumaiWeb.StockService.Tushare;

// var builder = WebApplication.CreateBuilder(args);
var builder = WebApplication.CreateBuilder(new WebApplicationOptions
{
    // 强制内容根 = exe所在目录
    ContentRootPath = AppContext.BaseDirectory,
    Args = args
});
// ✅关键：注册控制器
builder.Services.AddControllers(); 

// 如果需要Swagger
builder.Services.AddEndpointsApiExplorer();
// builder.Services.AddSwaggerGen();

builder.Services.AddScoped<IBaseService, BaseService>();
builder.Services.AddScoped(typeof(IRepository<,>), typeof(Repository<,>));
builder.Services.AddScoped<MairuiFinancialService>();
// 注册Postgres EF上下文
builder.Services.AddDbContext<AppDbContext>(options =>
{
    var connStr = builder.Configuration.GetConnectionString("tumaiDb");
    // 打印基础信息
    Console.WriteLine($"ContentRootPath:{builder.Environment.ContentRootPath}");
    Console.WriteLine($"GetConnectionString(Default)=[{connStr}]");
    options.UseNpgsql(connStr,b =>
    {
        
    });
    options.EnableSensitiveDataLogging();
});

// Add services to the container.
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
// builder.Services.AddOpenApi();

builder.Services.AddHttpClient<MairuiDataService>();
builder.Services.AddHttpClient<TushareService>();

var app = builder.Build();

// using (var scope = app.Services.CreateScope())
// {
//     var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
//     // dotnet ef migrations add InitDb
//     // 自动执行所有未应用的迁移，更新数据库表结构 or dotnet ef database update
//     await db.Database.MigrateAsync();
// }

/*
 dotnet ef migrations add
 dotnet ef database update
 # 生成迁移，把你刚刚改的 HasAlternateKey / HasPrincipalKey / 外键变化输出到迁移文件
   dotnet ef migrations add FixStockCodeForeignKey
   #生成迁移，记录字段变更
   dotnet ef migrations add AddSoftDeleteFields
   # 此时Migrations目录会多出一个时间戳+FixStockCodeForeignKey的cs文件
   # 再执行应用到数据库
   dotnet ef database update
   
   
   dotnet publish -c Release -r linux-x64 --self-contained true
 */

// Configure the HTTP request pipeline.
// if (app.Environment.IsDevelopment())
// {
//     app.MapOpenApi();
// }
//
// app.UseHttpsRedirection();
//
// var summaries = new[]
// {
//     "Freezing", "Bracing", "Chilly", "Cool", "Mild", "Warm", "Balmy", "Hot", "Sweltering", "Scorching"
// };
//
// app.MapGet("/weatherforecast", () =>
//     {
//         var forecast = Enumerable.Range(1, 5).Select(index =>
//                 new WeatherForecast
//                 (
//                     DateOnly.FromDateTime(DateTime.Now.AddDays(index)),
//                     Random.Shared.Next(-20, 55),
//                     summaries[Random.Shared.Next(summaries.Length)]
//                 ))
//             .ToArray();
//         return forecast;
//     })
//     .WithName("GetWeatherForecast");

// 开发环境开启swagger
if (app.Environment.IsDevelopment())
{
    // app.UseSwagger();
    // app.UseSwaggerUI();
} 

// 放在路由、MapControllers前面！
//app.UseMiddleware<RequestDecryptMiddleware>();
// ✅关键：启用控制器路由中间件
app.MapControllers();

app.Run();

record WeatherForecast(DateOnly Date, int TemperatureC, string? Summary)
{
    public int TemperatureF => 32 + (int)(TemperatureC / 0.5556);
}