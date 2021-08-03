# MySQLMonitor
MySQL实时监控工具（代码审计、黑盒测试辅助工具）

## 使用
1.自行打包使用

```
git clone https://github.com/fupinglee/MySQLMonitor
cd MySQLMonitor
mvn clean package -DskipTests=true
```
打开target下的jar文件即可执行

2.直接下载使用

https://github.com/fupinglee/MySQLMonitor/releases
## 使用说明

### 数据库连接
数据库连接失败，下断、更新、清空等按钮不可用
![数据库连接失败](images/数据库连接失败.png)

数据库连接成功，下断按钮可以使用，更新、清空等按钮不可用
![数据库连接成功](images/数据库连接成功.png)


### 下断
下断点后可以更新和清空
![下断点](images/下断点.png)

### 更新
点击更新查看执行的SQL语句
![查看执行的sql语句](images/更新.png)

### 搜索
在搜索框里输入内容可以对所需要的sql语句进行过滤
![搜索](images/搜索.png)

### 清空
清空按钮清空表格里面的内容
![清空](images/清空.png)

### 其他
单击选中一行后，鼠标移动可以悬浮显示该行的sql语句
![其他](images/悬浮提示.png)
>双击可以复制sql语句到剪贴板上
