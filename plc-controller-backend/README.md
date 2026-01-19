# PLC控制器项目

## 项目概述

这是一个基于Spring Boot、Maven和静态前端页面的PLC控制器项目，使用H2数据库存储配置信息，通过Modbus TCP协议实现对PLC的控制和状态查询。

## 技术栈

- **Java**：JDK 8
- **Spring Boot**：2.7.18
- **Maven**：项目构建工具
- **H2 Database**：嵌入式数据库
- **iot-communication**：PLC通信库
- **Bootstrap**：前端UI框架
- **jQuery**：前端JavaScript库

## 功能特性

1. **地址管理**：维护需要展示和控制的PLC地址
2. **状态监控**：实时展示PLC地址状态
3. **PLC控制**：通过前端页面控制PLC输出
4. **定时采集**：定时采集PLC状态数据
5. **数据存储**：使用H2数据库存储地址配置

## 配置参数

在`src/main/resources/application.properties`文件中配置以下参数：

```properties
# PLC配置
plc.ip=192.168.1.100  # PLC IP地址
plc.port=502          # PLC端口号，默认502
plc.refresh-interval=5000  # 数据采集间隔（毫秒）

# 数据库配置
spring.datasource.url=jdbc:h2:mem:plc_db  # H2数据库URL
spring.datasource.username=sa  # 数据库用户名
spring.datasource.password=password  # 数据库密码
spring.h2.console.enabled=true  # 启用H2控制台
spring.h2.console.path=/h2-console  # H2控制台路径

# 服务器配置
server.port=8080  # 服务器端口
```

## Modbus地址格式

支持以下Modbus地址格式：

- **线圈(Coil)**：0x地址，例如：`0x0001`，可读写
- **离散输入(Discrete Input)**：1x地址，例如：`1x0001`，只读
- **输入寄存器(Input Register)**：3x地址，例如：`3x0001`，只读
- **保持寄存器(Hold Register)**：4x地址，例如：`4x0001`，可读写

## 部署步骤

1. **编译项目**：
   ```bash
   mvn clean package
   ```

2. **运行项目**：
   ```bash
   java -jar target/plc-controller-1.0.0.jar
   ```

3. **访问系统**：
   - 地址管理页面：`http://localhost:8080/index.html`
   - 监控控制页面：`http://localhost:8080/monitor.html`
   - H2数据库控制台：`http://localhost:8080/h2-console`

## 使用说明

### 1. 地址管理

1. 打开地址管理页面：`http://localhost:8080/index.html`
2. 点击"添加地址"按钮添加PLC地址
3. 填写地址信息：
   - 名称：地址的显示名称
   - 地址：Modbus地址，例如：`0x0001`
   - 类型：输入或输出
   - 数据类型：布尔或数值
   - 描述：地址的描述信息
4. 点击"保存"按钮保存地址
5. 可对已添加的地址进行编辑和删除操作

### 2. 监控控制

1. 打开监控控制页面：`http://localhost:8080/monitor.html`
2. 页面会自动定时刷新PLC状态
3. 对于输入类型的地址，只能查看状态
4. 对于输出类型的地址：
   - 布尔类型：使用开关控制
   - 数值类型：输入数值并点击"设置"按钮控制

## API接口

### 地址管理接口

- **GET /api/addresses**：获取所有地址
- **GET /api/addresses/{id}**：获取指定ID的地址
- **POST /api/addresses**：添加新地址
- **PUT /api/addresses/{id}**：更新指定ID的地址
- **DELETE /api/addresses/{id}**：删除指定ID的地址

### 控制接口

- **GET /api/control/{addressId}**：获取指定地址的当前值
- **POST /api/control/{addressId}**：控制指定地址
  - 请求体：`{"value": 值}`

## 项目结构

```
src/
├── main/
│   ├── java/com/example/plc/
│   │   ├── controller/    # 控制器层
│   │   ├── entity/        # 实体类
│   │   ├── repository/    # 数据访问层
│   │   ├── service/       # 业务逻辑层
│   │   └── PlcControllerApplication.java  # 应用主类
│   └── resources/
│       ├── static/        # 静态资源
│       └── application.properties  # 配置文件
└── test/                  # 测试代码
```

## 注意事项

1. 确保PLC已正确配置Modbus TCP协议
2. 确保PLC的IP地址和端口号与配置文件一致
3. 确保PLC的Modbus地址格式与系统要求一致
4. 首次运行时，H2数据库会自动创建表结构
5. 数据采集间隔不宜设置过短，避免对PLC造成过大负载

## 联系方式

如有问题，请联系项目维护人员。