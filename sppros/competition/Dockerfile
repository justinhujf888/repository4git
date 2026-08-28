FROM eclipse-temurin:22-jre

WORKDIR /app

# 先拷贝 lib 依赖（变更频率低，利用 Docker 层缓存）
COPY target/lib/ ./lib/

# 再拷贝你的应用 jar
COPY target/cpt-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8091

ENTRYPOINT ["java", "-jar", "app.jar"]