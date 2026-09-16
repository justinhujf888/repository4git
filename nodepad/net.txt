## .net Runtime linux 通用一键脚本（所有 Linux 发行版通用，推荐！）

不管 Ubuntu/CentOS/Alpine 都可以用这个脚本，**最省心**：

curl -L https://dot.net/v1/dotnet-install.sh -o dotnet-install.sh
chmod +x dotnet-install.sh
# 安装ASP.NET Core Runtime 10.0
./dotnet-install.sh --runtime aspnetcore --channel 10.0
# 把dotnet加入全局环境变量（永久生效写入 ~/.bashrc）
echo 'export PATH="$PATH:$HOME/.dotnet"' >> ~/.bashrc
echo 'export DOTNET_ROOT="$HOME/.dotnet"' >> ~/.bashrc
source ~/.bashrc
```