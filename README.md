# expense-service

家庭自驾游规划服务的费用微服务骨架。项目只提供 Spring Boot 与 `zy-starter-parent` 的集成配置，不包含业务接口、领域模型、数据表、SQL 脚本或外部服务客户端。

## 技术基线

- Java 21
- Spring Boot 3.5.16
- Spring Cloud 2025.0.0
- `zy-starter-parent:1.0.0-SNAPSHOT`
- 基础包：`com.zy.travel.expense`

## 已接入能力

- `zy-common-web`：统一响应与异常处理、参数校验、CORS、TraceId
- `zy-common-mybatis`：MyBatis-Plus 通用配置
- `zy-common-redis`：Redis、缓存与分布式锁支持
- `zy-common-feign`：OpenFeign 调用基础能力
- `zy-common-doc`：Springdoc/OpenAPI 文档
- Actuator、Nacos Discovery 与 Nacos Config

认证与鉴权由 Kong 网关统一处理：`user` 服务负责签发 Token，Kong 负责校验并转发可信身份头；本服务不持有 JWT 密钥，也不引入 `zy-common-security`。服务必须部署在仅允许网关访问的内网，由 Kong 清除客户端伪造的身份头后再注入。

运行配置按环境保存在 `nacos/travel-expense-{profile}.yaml`。服务默认使用开发环境，可通过 `TRAVEL_PROFILES_ACTIVE` 切换；当前已准备配置 `nacos/travel-expense-dev.yaml`。该文件目前使用没有表结构的内存 H2 数据源，Redis 自动配置保持关闭，便于后续替换为实际基础设施配置。

## 配置上传

Nacos 部署完成后，在配置管理中创建以下配置：

- Data ID：`travel-expense-dev.yaml`
- Group：`DEFAULT_GROUP`
- Nacos 地址：`pi.home:8848`
- 命名空间：`dev`
- 格式：YAML
- 内容：根目录 `nacos/travel-expense-dev.yaml` 的完整内容

应用通过 `spring.config.import` 强制导入 `travel-expense-{profile}.yaml`，不使用 `bootstrap.yml`。`profile` 由 `TRAVEL_PROFILES_ACTIVE` 决定，默认值为 `dev`；切换环境前，必须先上传同名 Nacos 配置。Nacos 地址、命名空间、分组与默认 `nacos` 凭据均已固化在 `application.yml`；Nacos 不可用或配置未上传时，应用会启动失败。

## Nacos 启动

Nacos 配置上传后即可启动：

```bash
export TRAVEL_PROFILES_ACTIVE=dev
mvn -B spring-boot:run
```

如 Nacos 未使用默认 `nacos` 凭据，需同步修改 `application.yml`。启动后可访问：

- 健康检查：`/actuator/health`
- OpenAPI 描述：`/v3/api-docs`
- Swagger UI：`/swagger-ui/index.html`

本服务不做 Token 校验；对外暴露时必须经由 Kong 网关认证后转发，禁止直接对公网开放。

## 后续调整

启用 Redis 时，在开发环境 Nacos 配置中移除 `spring.autoconfigure.exclude` 的 Redis 和 Redisson 自动配置，并补充 `spring.data.redis` 连接信息。新增生产环境时，创建 `nacos/travel-expense-prod.yaml`，上传为同名 Data ID，并设置 `TRAVEL_PROFILES_ACTIVE=prod`。数据库、Redis 与 Nacos 凭据不得提交到仓库。
