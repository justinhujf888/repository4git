#!/bin/zsh
echo "===== 1. 清理全部NuGet本地缓存 ====="
dotnet nuget locals all --clear

echo ""
echo "===== 2. 查看可更新包（预览，不会修改） ====="
dotnet outdated

echo ""
read "confirm?确认执行批量升级所有包？(y/n) "
if [[ $confirm == "y" || $confirm == "Y" ]]; then
    echo "===== 3. 开始批量升级 ====="
    dotnet outdated --upgrade
    echo ""
    echo "===== 4. 还原包 ====="
    dotnet restore
    echo "✅ 更新完成！回到Rider等待项目重新加载"
else
    echo "❌ 已取消升级"
fi

