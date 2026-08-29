FROM eclipse-temurin:22-jdk-jammy AS extractor

WORKDIR /app

# 先拷贝 lib 依赖（变更频率低，利用 Docker 层缓存）
COPY target/lib/ ./lib/

# 再拷贝你的应用 jar
COPY target/light-0.0.1-SNAPSHOT.jar ./app.jar

RUN java -Djarmode=layertools -jar app.jar extract


FROM eclipse-temurin:22-jre-jammy
COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/ ./
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

EXPOSE 8090 1883

#ENTRYPOINT ["java", "-jar", "app.jar"]
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]