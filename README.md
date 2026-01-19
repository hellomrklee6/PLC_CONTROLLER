# PLC控制器系统

## 项目介绍

PLC控制器系统是一个基于Web的工业自动化控制系统，用于实现对PLC（可编程逻辑控制器）设备的远程监控和管理。该系统采用前后端分离架构，后端基于Spring Boot框架开发，前端基于Vue.js框架构建，通过Modbus TCP和OPC UA协议与PLC设备进行通信，实现对PLC数据的实时采集、状态监控和远程控制。

本系统的主要设计目标是提供一个直观、易用的Web界面，使操作人员能够通过浏览器远程访问PLC设备，查看实时数据，执行控制操作，并管理多个PLC设备的配置信息。系统支持多设备并行管理，可以同时连接多个不同协议的PLC设备，并根据设备类型自动选择相应的通信协议。此外，系统还提供了按钮方案功能，允许用户自定义控制按钮，将复杂的控制操作简化为一键触发，极大地提高了操作效率。

系统的实时数据更新采用WebSocket技术实现，后端服务会定期采集PLC数据，并通过WebSocket通道向前端推送最新的状态信息，确保监控界面的数据始终保持实时更新。这种设计避免了传统轮询方式带来的延迟和网络开销，提供了更加流畅的用户体验。同时，系统的所有配置信息都存储在H2嵌入式数据库中，支持数据持久化和快速查询，无需额外的数据库服务器部署。

## 技术栈

本项目采用现代化的技术栈构建，确保系统的可维护性、可扩展性和性能表现。后端技术栈以Spring Boot 2.7.18为核心框架，配合Spring Data JPA实现数据持久化，使用H2作为嵌入式数据库存储配置信息。PLC通信方面使用了iot-communication库（版本1.5.4），该库提供了对Modbus TCP协议的完整支持，能够方便地实现与各类PLC设备的数据交互。WebSocket通信采用Spring Boot内置的WebSocket支持，配合STOMP协议实现可靠的消息传递。

前端技术栈以Vue.js 3为核心框架，配合Vue Router 4实现页面路由管理，使用Axios处理HTTP请求。界面设计采用Bootstrap 5框架，提供了丰富的UI组件和响应式布局支持。WebSocket客户端使用STOMP.js和SockJS-client实现，确保在各种浏览器环境下的兼容性。开发构建工具选用Vite 6，提供了快速的开发服务器和高效的打包优化功能。

整个项目的构建管理采用Maven（后端）和npm（前端）相结合的方式，后端通过pom.xml统一管理依赖和构建配置，前端通过package.json管理依赖包和脚本命令。这种技术选型既保证了开发效率，又确保了生产环境的稳定性和性能。

## 功能特性

本系统提供了完整的PLC设备管理解决方案，主要功能特性包括以下几个方面：

### 多设备管理

系统支持同时管理多个PLC设备，每个设备可以独立配置IP地址、端口号和通信协议。目前支持的通信协议包括Modbus TCP和OPC UA(未实现)两种标准工业协议。用户可以通过设备管理界面添加新设备、编辑设备信息、启用或禁用设备，以及查看设备的连接状态。系统会自动记录每个设备的最后连接时间和连接状态，帮助用户及时发现和处理设备故障。

### 地址配置与管理

每个PLC设备下可以配置多个地址点位，用于存储和访问设备中的数据。地址配置支持多种Modbus地址类型，包括线圈（0x地址，可读写）、离散输入（1x地址，只读）、输入寄存器（3x地址，只读）和保持寄存器（4x地址，可读写）。每个地址可以指定数据类型为布尔型或数值型，并可以选择作为输入或输出类型。输入类型的地址主要用于数据监控，输出类型的地址则可以进行控制操作。

### 实时监控

系统提供了实时监控功能界面，以卡片形式展示所有已配置地址的当前状态。对于布尔型地址，显示为开关或指示灯样式；对于数值型地址，显示为数值并支持自定义显示格式。监控数据通过WebSocket实时推送更新，确保界面显示的状态与设备实际状态保持同步。用户可以快速浏览所有地址的状态，也可以在监控界面直接对输出类型的地址进行控制操作。

### 按钮方案

按钮方案是本系统的一个重要特色功能，它允许用户将多个地址的控制操作组合成一个按钮，实现一键批量控制。用户可以创建不同的按钮方案，每个方案包含多个按钮，每个按钮可以配置目标地址和期望值。当点击按钮时，系统会按照预设的值依次对目标地址进行控制。这种设计特别适合需要频繁执行相同控制序列的场景，如设备启动、停止、复位等操作。

### 连接日志

系统内置了连接日志功能，自动记录所有与PLC设备的连接事件和通信日志。日志内容包括连接时间、断开时间、通信错误、异常信息等详细记录。用户可以通过日志查看界面浏览历史日志，了解设备的连接状况和通信质量。日志数据同样存储在数据库中，支持按时间范围、设备等条件进行筛选查询。

## 项目结构

### 后端目录结构

```
plc-controller-backend/
├── src/main/java/com/example/plc/
│   ├── PlcControllerApplication.java    # Spring Boot应用启动类
│   ├── client/                           # PLC通信客户端模块
│   │   ├── AbstractPLCClient.java        # 抽象PLC客户端基类
│   │   ├── ModbusTCPClient.java          # Modbus TCP协议实现
│   │   ├── ModbusTcpTestMain.java        # Modbus TCP测试程序
│   │   └── OpcUaClient.java              # OPC UA协议实现
│   ├── config/                           # 配置类模块
│   │   ├── ThreadPoolConfig.java         # 线程池配置
│   │   └── WebSocketConfig.java          # WebSocket配置
│   ├── controller/                       # REST API控制器
│   │   ├── ButtonController.java         # 按钮控制接口
│   │   ├── ButtonSchemeController.java   # 按钮方案管理接口
│   │   ├── HomeController.java           # 首页控制器
│   │   ├── PlcAddressController.java     # 地址管理接口
│   │   ├── PlcControlController.java     # PLC控制接口
│   │   ├── PlcDeviceController.java      # 设备管理接口
│   │   └── PlcWebSocketController.java   # WebSocket控制器
│   ├── entity/                           # 数据实体类
│   │   ├── Button.java                   # 按钮实体
│   │   ├── ButtonScheme.java             # 按钮方案实体
│   │   ├── PlcAddress.java               # PLC地址实体
│   │   └── PlcDevice.java                # PLC设备实体
│   ├── listener/                         # 事件监听器
│   │   └── WebSocketEventListener.java   # WebSocket事件监听
│   ├── repository/                       # 数据访问层
│   │   ├── ButtonRepository.java
│   │   ├── ButtonSchemeRepository.java
│   │   ├── PlcAddressRepository.java
│   │   └── PlcDeviceRepository.java
│   └── service/                          # 业务逻辑层
│       ├── AddressInitializer.java       # 地址初始化服务
│       ├── ButtonSchemeService.java      # 按钮方案业务逻辑
│       ├── PlcAddressService.java        # 地址业务逻辑
│       ├── PlcCommunicationService.java  # PLC通信服务
│       ├── PlcConnectionLogService.java  # 连接日志服务
│       ├── PlcControlService.java        # 控制服务
│       ├── PlcDataCollectionService.java # 数据采集服务
│       └── PlcDeviceService.java         # 设备业务逻辑
├── src/main/resources/
│   └── application.properties            # 应用程序配置
└── data/                                 # H2数据库文件存储目录
    ├── plc_db.mv.db
    └── plc_db.trace.db
```

### 前端目录结构

```
plc-controller-frontend/
├── src/
│   ├── api/
│   │   └── index.js                      # API请求封装
│   ├── components/                       # Vue组件
│   │   ├── card/                         # 卡片组件
│   │   │   ├── BaseCard.vue              # 基础卡片
│   │   │   ├── BooleanInputCard.vue      # 布尔输入卡片
│   │   │   ├── BooleanOutputCard.vue     # 布尔输出卡片
│   │   │   ├── NumberInputCard.vue       # 数值输入卡片
│   │   │   └── NumberOutputCard.vue      # 数值输出卡片
│   │   └── layout/                       # 布局组件
│   │       ├── Footer.vue                # 页脚
│   │       └── Navbar.vue                # 导航栏
│   ├── router/
│   │   └── index.js                      # Vue Router配置
│   ├── services/
│   │   └── WebSocketService.js           # WebSocket服务
│   ├── views/                            # 页面视图
│   │   ├── AddressesView.vue             # 地址管理页面
│   │   ├── ButtonSchemesView.vue         # 按钮方案页面
│   │   ├── DevicesView.vue               # 设备管理页面
│   │   ├── HomeView.vue                  # 首页
│   │   └── MonitorView.vue               # 监控页面
│   ├── App.vue                           # Vue根组件
│   └── main.js                           # 应用入口
├── index.html                            # HTML模板
├── package.json                          # npm依赖配置
└── vite.config.js                        # Vite构建配置
```

## 快速开始

### 环境要求

在开始运行本项目之前，请确保开发环境满足以下要求。Java Development Kit（JDK）版本需要为8或更高版本，建议使用JDK 8以确保最佳兼容性。Maven版本应为3.x系列，用于后端项目的构建和管理。前端开发需要Node.js环境，版本建议为16.x或18.x LTS版本，同时需要安装npm包管理器（通常随Node.js一同安装）。推荐使用现代化的代码编辑器如IntelliJ IDEA或Visual Studio Code进行开发，并安装相应的语言支持插件以获得更好的开发体验。

### 后端启动

首先进入后端项目目录，使用Maven命令进行项目编译。执行命令`mvn clean package`可以清理之前的构建产物并重新编译项目，编译过程会自动下载所需依赖并生成可执行的JAR包。编译完成后，使用命令`java -jar target/plc-controller-1.0.0.jar`启动后端服务。服务启动后默认监听19999端口，你可以在控制台看到启动日志，确认没有错误信息输出即为启动成功。首次启动时，系统会自动创建H2数据库文件并初始化表结构。

如果希望在开发过程中进行调试，可以直接在IDE中运行`PlcControllerApplication`类的main方法，这样可以通过IDE的调试功能进行断点调试和热重载。开发模式下，修改代码后可以快速重启服务，缩短开发周期。

### 前端启动

前端项目的启动需要先安装依赖包。进入前端项目目录后，执行`npm install`命令，npm会自动读取package.json中的依赖配置并下载所有需要的包到node_modules目录。依赖安装完成后，执行`npm run dev`命令启动开发服务器。Vite开发服务器启动后默认监听5173端口，并提供热模块替换功能，代码修改后会自动刷新浏览器。

前端开发服务器运行后，可以在浏览器中访问`http://localhost:5173`查看应用。如果需要构建生产版本，执行`npm run build`命令，Vite会将源代码打包编译为静态文件，输出到dist目录，这些文件可以直接部署到任何Web服务器上。

### 访问地址

后端服务启动后，可以通过以下地址访问各个功能模块。API基础地址为`http://localhost:19999`，所有REST接口都以此为基础路径。H2数据库控制台可以通过`http://localhost:19999/h2-console`访问，用于直接查看和操作数据库（数据库URL为`jdbc:h2:file:./data/plc_db`，用户名sa，密码password）。前端应用开发模式下访问`http://localhost:5173`，生产构建后可以部署到`http://localhost:19999`通过Spring Boot的静态资源服务访问。

## 配置说明

### 应用程序配置

后端应用程序的主要配置位于`src/main/resources/application.properties`文件中。该文件包含了服务器端口、数据库连接、WebSocket等各项配置参数。以下是主要配置项的详细说明：

```properties
# 应用名称配置
spring.application.name=plc-controller

# H2数据库配置
spring.datasource.url=jdbc:h2:file:./data/plc_db
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA配置
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# 服务器配置
server.port=19999

# PLC数据采集配置
plc.refresh-interval=1000
```

数据库配置中，`spring.datasource.url`指定了H2数据库的文件存储路径，`./data/plc_db`表示数据库文件存储在项目data目录下。`spring.jpa.hibernate.ddl-auto=update`配置表示JPA会自动根据实体类定义更新数据库表结构，无需手动创建表。`plc.refresh-interval`配置数据采集的间隔时间，单位为毫秒，默认为1000毫秒即1秒采集一次。

### PLC设备配置

在系统中，PLC设备信息通过数据库进行管理，不再使用配置文件配置。每个设备需要配置以下参数：

- **名称**：设备的显示名称，用于识别设备
- **IP地址**：PLC设备的网络地址
- **端口号**：PLC设备监听的端口，Modbus TCP通常为502
- **协议类型**：通信协议，支持ModbusTCP和OPCUA
- **描述**：设备的详细描述信息
- **启用状态**：控制设备是否参与数据采集

设备配置完成后，系统会根据设备的协议类型自动选择对应的通信客户端进行连接和数据交互。禁用的设备不会被采集服务处理，但配置信息会保留在数据库中。

## API接口文档

### 设备管理接口

设备管理模块提供了对PLC设备的增删改查接口，支持通过REST API进行设备配置管理。

获取所有设备列表的接口为`GET /api/devices`，该接口返回系统中所有已配置的PLC设备信息，每个设备包含ID、名称、IP地址、端口、协议类型、描述和启用状态等字段。获取单个设备详情的接口为`GET /api/devices/{id}`，通过设备ID查询指定设备的详细信息。添加新设备的接口为`POST /api/devices`，请求体需要包含name、ip、port、protocol、description和enabled字段，添加成功返回新设备的ID。更新设备信息的接口为`PUT /api/devices/{id}`，请求体同样包含需要更新的字段，支持部分更新。删除设备的接口为`DELETE /api/devices/{id}`，删除指定ID的设备及其关联的地址配置。

### 地址管理接口

地址管理模块提供了对PLC地址点位的CRUD接口，用于管理每个设备下的数据地址。

获取所有地址的接口为`GET /api/addresses`，返回系统中所有已配置的地址信息，每个地址包含ID、名称、地址字符串、类型（输入或输出）、数据类型（布尔或数值）、描述、关联设备ID和关联按钮方案ID。获取单个地址详情的接口为`GET /api/addresses/{id}`。添加新地址的接口为`POST /api/addresses`，请求体需要包含name、address、type、dataType、description、deviceId和可选的buttonSchemeId字段。更新地址信息的接口为`PUT /api/addresses/{id}`。删除地址的接口为`DELETE /api/addresses/{id}`。

地址还支持批量操作接口`POST /api/addresses/batch-update`，该接口接收设备ID和地址列表作为请求体，可以一次性更新某个设备下的所有地址配置，适合大批量地址配置场景。

### 控制接口

控制模块提供了对PLC地址进行读写操作的接口，支持单个地址的读写和批量读写。

读取单个地址当前值的接口为`GET /api/control/{addressId}`，该接口会立即从PLC设备读取指定地址的当前值并返回。控制单个地址的接口为`POST /api/control/{addressId}`，请求体需要包含value字段，值为期望写入的数据。对于布尔类型地址，value为true或false；对于数值类型地址，value为数值。批量读取接口为`POST /api/control/batch-read`，请求体为地址ID列表，返回各地址的当前值。批量控制接口为`POST /api/control/batch-write`，请求体包含地址ID和值的映射对象，系统会依次对各地址进行写入操作。

### 按钮方案接口

按钮方案模块提供了对控制按钮组合的管理接口，支持方案和按钮的增删改查。

获取所有按钮方案的接口为`GET /api/button-schemes`，返回所有方案及其包含的按钮列表。获取单个方案的接口为`GET /api/button-schemes/{id}`。添加新方案的接口为`POST /api/button-schemes`，请求体包含name、description和buttons字段，buttons为按钮数组，每个按钮包含addressId和value。更新方案的接口为`PUT /api/button-schemes/{id}`。删除方案的接口为`DELETE /api/button-schemes/{id}`。

执行按钮方案时调用`POST /api/button-schemes/{id}/execute`，该接口会依次执行方案中所有按钮对应的控制操作，实现一键批量控制。

## WebSocket接口

### 连接方式

前端通过WebSocket连接到后端服务，连接地址为`ws://localhost:19999/ws`，使用STOMP协议进行消息传递。连接时需要在请求头中携带用户认证信息（如果启用了认证），连接成功后系统会发送JOIN消息通知客户端已成功订阅。

前端WebSocket服务封装在`WebSocketService.js`文件中，提供了连接、订阅、发送和断开等方法。连接时会自动重连，在网络不稳定情况下能够自动恢复连接。消息接收采用观察者模式，通过回调函数处理接收到的消息。

### 订阅主题

系统定义了以下STOMP消息主题供前端订阅：

主题`/topic/plc/data`用于订阅所有PLC地址的数据更新，后端会定期向该主题推送所有已启用地址的当前值，消息体包含地址ID和对应数值。主题`/topic/plc/{deviceId}/data`用于订阅特定设备的数据更新，只推送指定设备下的地址数据。主题`/topic/plc/connection`用于订阅设备连接状态变更通知，消息体包含设备ID和连接状态（CONNECTED或DISCONNECTED）。主题`/topic/plc/logs`用于订阅连接日志推送，当产生新的日志记录时会向该主题发送消息。

### 消息格式

后端向前端推送的数据消息采用JSON格式，PLC数据更新消息的结构如下：

```json
{
  "type": "PLC_DATA",
  "deviceId": 1,
  "timestamp": "2024-01-01T12:00:00",
  "data": [
    {
      "addressId": 1,
      "value": true
    },
    {
      "addressId": 2,
      "value": 123
    }
  ]
}
```

连接状态变更消息的结构如下：

```json
{
  "type": "CONNECTION_STATUS",
  "deviceId": 1,
  "status": "CONNECTED",
  "timestamp": "2024-01-01T12:00:00"
}
```

日志消息的结构如下：

```json
{
  "type": "LOG",
  "level": "INFO",
  "message": "与PLC设备建立连接成功",
  "timestamp": "2024-01-01T12:00:00"
}
```

## Modbus地址格式

本系统支持标准的Modbus地址格式，不同类型的地址对应不同的功能码和数据区域。以下是各类地址的详细说明：

### 线圈地址（0x）

线圈地址使用0x前缀标识，如`0x0001`、`0x0010`等。线圈是Modbus协议中的最小数据单位，代表一个位的状态，可以读取也可以写入。在本系统中，线圈地址用于表示开关量输出，可以控制继电器、指示灯等开关设备。读取线圈使用功能码01，写入单个线圈使用功能码05，写入多个线圈使用功能码15。地址数值范围通常从0开始，可以根据PLC设备的实际地址映射进行调整。

### 离散输入地址（1x）

离散输入地址使用1x前缀标识，如`1x0001`、`1x0010`等。离散输入是PLC的开关量输入端口，用于采集外部信号，如按钮、传感器等。离散输入是只读类型，只能读取不能写入。在本系统中，离散输入地址用于监控外部设备的状态。读取离散输入使用功能码02。离散输入与线圈的主要区别在于方向性，输入是外部到PLC，输出是PLC到外部。

### 输入寄存器地址（3x）

输入寄存器地址使用3x前缀标识，如`3x0001`、`3x0010`等。输入寄存器用于存储16位的模拟量数据，通常来自模拟量输入模块或经过转换的数值。输入寄存器是只读类型，只能读取不能写入。在本系统中，输入寄存器地址用于采集温度、压力、流量等模拟量传感器的数据。读取输入寄存器使用功能码04。每个寄存器包含16位数据，可以表示0-65535的整数值，或通过缩放因子转换为实际物理量。

### 保持寄存器地址（4x）

保持寄存器地址使用4x前缀标识，如`4x0001`、`4x0010`等。保持寄存器是Modbus协议中功能最强大的数据区域，既可以读取也可以写入。保持寄存器用于存储PLC中的各种数据，包括配置参数、运行状态、控制命令等。在本系统中，保持寄存器地址用于读写各类控制数据。读取保持寄存器使用功能码03，写入单个保持寄存器使用功能码06，写入多个保持寄存器使用功能码16。多个连续的保持寄存器可以组合表示32位整数、浮点数等复杂数据类型。

## 部署指南

### 开发环境部署

开发环境部署相对简单，首先确保已安装JDK 8、Maven和Node.js环境。克隆项目代码到本地后，分别进入后端和前端目录安装依赖。后端使用`mvn clean package`命令构建，前端使用`npm install`命令安装依赖。启动时先运行后端服务，再运行前端开发服务器。开发过程中可以利用热重载功能加快迭代速度，修改代码后无需手动重启服务。数据库使用H2嵌入式数据库，无需额外安装数据库服务器。

### 生产环境部署

生产环境部署需要考虑更多的因素，包括安全性、性能和可靠性。后端建议使用`mvn clean package -DskipTests`命令构建，生成独立的可执行JAR包。可以将JAR包部署到任何支持Java运行环境的服务器上，使用`nohup java -jar plc-controller-1.0.0.jar &`命令实现后台运行。为了提高系统稳定性，建议配置JVM参数如`-Xms512m -Xmx1024m`限制内存使用，并配置日志文件路径以便问题排查。

前端生产部署可以使用`npm run build`命令生成静态文件，将dist目录下的所有文件复制到后端项目的`src/main/resources/static`目录下，这样Spring Boot会自动提供这些静态资源。也可以将前端静态文件部署到独立的Web服务器（如Nginx）上，通过反向代理将API请求转发到后端服务。这种分离部署的方式更有利于前后端的独立扩展和维护。

Nginx配置示例：将前端文件放在`/var/www/plc-controller`目录下，配置Nginx监听80端口，将根路径请求指向前端静态文件，将`/api`和`/ws`路径的请求代理到后端服务。这样可以通过域名直接访问应用，API请求通过代理转发到后端。

## 常见问题

### PLC连接失败

当遇到PLC设备连接失败的问题时，首先检查PLC设备的IP地址和端口配置是否正确，确保网络连通性。可以使用ping命令测试网络连接，使用telnet命令测试端口是否开放。确认PLC设备已正确配置Modbus TCP协议，并允许外部连接。检查防火墙设置，确保19999端口和502端口未被阻止。如果使用虚拟化或Docker环境，需要确保容器网络模式配置正确。

### 数据采集不及时

如果发现监控界面的数据更新不及时，检查`plc.refresh-interval`配置值是否过大，适当减小该值可以提高采集频率。但需要注意，采集频率过高可能会对PLC设备造成负担，甚至导致通信超时。根据实际网络状况和PLC性能调整该参数，通常1000-5000毫秒是比较合适的范围。确认WebSocket连接正常，可以在浏览器开发者工具中查看是否有WebSocket消息接收。

### 地址读取错误

当地址读取返回错误时，首先确认Modbus地址格式是否正确，包括前缀和数值。不同的PLC设备地址映射可能不同，需要参考PLC的寄存器地址说明。部分地址可能是保留地址或不可访问地址，需要排除这些地址。检查地址类型配置是否与实际地址类型匹配，如只读地址不应配置为输出类型。确认设备是否已启用，禁用的设备不会进行数据采集。

### 前端页面空白

如果访问前端页面显示空白，首先检查浏览器控制台是否有JavaScript错误。确保已正确运行`npm install`安装所有依赖。检查Vite开发服务器是否正常运行，确认端口5173未被占用。如果是生产构建后部署，检查静态文件路径配置是否正确，确认dist目录下的文件已正确复制到目标位置。

## 许可证

本项目采用MIT许可证开源，这意味着你可以自由使用、修改和分发本项目的代码，包括商业用途。使用本项目时请保留原始的版权声明和许可证声明。对于因使用本项目造成的任何直接或间接损失，作者不承担任何责任。

如有任何问题或建议，欢迎通过项目仓库提交Issue或Pull Request。
