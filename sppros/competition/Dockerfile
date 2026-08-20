# 1. 构建阶段 (Builder)
# 使用包含 Maven 和 OpenJDK 22 的镜像
FROM maven:3.9.9-eclipse-temurin-22 AS builder
WORKDIR /build

# 2. 优化依赖缓存：先复制 pom.xml 并下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 3. 复制源代码并打包
COPY src ./src
RUN mvn clean package -DskipTests -B

# 4. 【关键步骤】使用 layertools 提取分层 Jar
# 此命令将 Spring Boot Fat Jar 解压为四层：dependencies, spring-boot-loader, snapshot-dependencies, application
RUN java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

# 5. 运行阶段 (Runtime)
# 使用轻量的 OpenJDK 22 JRE 基础镜像（采用 Alpine 以减小体积）
FROM eclipse-temurin:22-jre-alpine
WORKDIR /app

# 6. 创建非 root 用户运行应用（安全最佳实践）
RUN addgroup -S app && adduser -S app -G app
USER app:app

# 7. 【核心优化】按层复制，利用 Docker 缓存
# 分层复制，使得频繁修改的 application 层不会使依赖层缓存失效
COPY --from=builder /build/target/extracted/dependencies/ ./
COPY --from=builder /build/target/extracted/spring-boot-loader/ ./
COPY --from=builder /build/target/extracted/snapshot-dependencies/ ./
COPY --from=builder /build/target/extracted/application/ ./

# 8. 【重要】设置 JVM 容器感知参数，适应容器内存限制
# -XX:MaxRAMPercentage=80.0 让 JVM 使用容器可用内存的 80%
# -XX:MinRAMPercentage=50.0 当容器内存较小时使用更大比例
ENV JAVA_OPTS="-XX:MaxRAMPercentage=80.0 -XX:MinRAMPercentage=50.0 -XX:+UseContainerSupport"

# 9. 【关键】Spring Boot 3.2 启动类路径（与 3.2 以前的版本不同）
# 路径为 org.springframework.boot.loader.launch.JarLauncher
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} org.springframework.boot.loader.launch.JarLauncher"]