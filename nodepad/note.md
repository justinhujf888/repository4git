## windows下，如果docker pgsql发生5432端口占用

windows下，如果docker pgsql发生5432端口占用，可以在windows shell中  
net stop winnat  
net start winnat  
然后启动pgsql容器
