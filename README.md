# MES云平台
## 核心依赖

|      名称      |   版本    |
| --------- | -------- |
| `Spring Boot`    | `2.2.7.RELEASE`  |
| `Spring Cloud`   | `Hoxton.SR4`  |

### 基础服务
|  服务     | 使用技术                 |    备注   |
|----------|-------------------------|-----------|
|  注册中心 | Nacos                   | [Nacos官网地址](https://nacos.io/zh-cn/index.html) |
|  配置中心 | Nacos                   |           |
|  服务调用 | SpringCloud OpenFeign   |           |
|  服务容错 | SpringCloud Sentinel    |           |
|  动态网关 | SpringCloud Gateway     |           |
|  授权认证 | Spring Security OAuth2  |Jwt模式     |


### 开发环境搭建
1. IDE安装lombok插件

### 认证
####初使化的客户端与用户账号
web_app:mes-web-secret
client_id:     `web_app`
client_secret: `mes-web-secret`

username: `admin`
password: `admin@mes`


### 数据库设计规范

TODO

### 开发规范

#### RESTFUL URL命名规范

API URI design
API URI 设计最重要的一个原则： nouns (not verbs!) ，名词（而不是动词）。

CRUD 简单 URI：

|  方法   | URL       |       功能       |
|--------|-----------|------------------|
| GET    | /user    | 获取用户列表       |
| GET    | /user/1  | 获取 id 为 1 的用户|
| POST   | /user    | 创建一个用户       |
| PUT    | /user/1  | 替换 id 为 1 的用户|
| PATCH  | /user/1  | 修改 id 为 1 的用户|
| DELETE | /user/1  | 删除 id 为 1 的用户|

上面是对某一种资源进行操作的 URI，那如果是有关联的资源，或者称为级联的资源，该如何设计 URI 呢？比如某一用户下的产品：

|  方法   | URL                 |             功能                   |
|--------|---------------------|------------------------------------|
| GET    | /user/1/product   | 获取 Id 为 1 用户下的产品列表         |
| GET    | /user/1/product/2 | 获取 Id 为 1 用户下 Id 为 2 的产品    |
| POST   | /user/1/product   | 在 Id 为 1 用户下，创建一个产品       |
| PUT    | /user/1/product/2 | 在 Id 为 1 用户下，替换 Id 为 2 的产品|
| PATCH  | /user/1/product/2 | 修改 Id 为 1 的用户下 Id 为 2 的产品  |
| DELETE | /user/1/product/2 | 删除 Id 为 1 的用户下 Id 为 2 的产品  |

#### 方法命名规范

1. Mapper

简单的CRUD请按如下规则命名

|  操作   | 例子                |             备注                  |
|--------|---------------------|----------------------------------|
|  增加   | insert/add          |                                  |
|  删除   | delete              |                                  |
|  修改   | update              |                                  |
|  查询   | query               |                                  |
|  搜索   | search              |                                  |

2. Service

简单的CRUD请按如下规则命名，其它操作请按业务动作命名，使用动词

|  操作   | 例子                |             备注                  |
|--------|---------------------|----------------------------------|
|  增加   | add                 |                                  |
|  获取   | get                 |   获取到单条记录                   |
|  删除   | remove/delete       |                                  |
|  更新   | update              |   更新存在的记录                   |
|  保存   | save                |   mybatis-plus 更新，不存在则新增 |
|  查询   | query               |   根据id等简单条件查询              |
|  搜索   | search              |   根据时间范围或模糊搜索            |

3. Rest

简单的CRUD请按如下规则命名，其它操作请按业务动作命名，使用动词

|  操作   | 例子                |             备注                  |
|--------|---------------------|----------------------------------|
|  增加   | add                 |                                  |
|  保存   | save                |   更新，不存在则新增                |
|  删除   | remove/delete       |                                  |
|  获取   | get                 |   获取到单条记录                   |
|  更新   | update              |   更新存在的记录                   |
|  查询   | query               |   根据id等简单条件查询              |
|  搜索   | search              |   根据时间范围或模糊搜索            |