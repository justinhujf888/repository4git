using System.Diagnostics;
using Microsoft.AspNetCore.Mvc;
using tumaiWeb.Data.Entities;
using tumaiWeb.Data.Repository;

namespace tumaiWeb.Controller;

[ApiController]
[Route("api/stockinfo")]
public class StockInfoController : ControllerBase
{
    // private readonly IRepository<StockBasic,long> _stockRepo;
    //
    // public StockInfoController(IRepository<StockBasic,long> stockRepo)
    // {
    //     _stockRepo = stockRepo;
    // }
    
    private readonly IBaseService baseService;

    // 构造函数注入
    public StockInfoController(IBaseService _baseService)
    {
        baseService = _baseService;
    }
    
    // 根据id查询（自动过滤软删除）
    [HttpGet("{id}")]
    public async Task<IActionResult> Get(long id)
    {
        var stockBasic = await baseService.QuerySingleObjectAsync<StockBasic>(ctx=>
                from stock in ctx.Set<StockBasic>()
                where stock.Id == id
                select stock
            );
        if (stockBasic == null) return NotFound();
        return Ok(stockBasic);
    }
    
    [HttpPost("TestQuery")]
    public async Task<string> TestQuery([FromBody] Dictionary<string, object> query)
    {
        foreach (var kvp in query)
        {
            Console.WriteLine($"{kvp.Key}: {kvp.Value}");
        }

        return "ok";
    }
    
}