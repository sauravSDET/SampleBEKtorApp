# Dockerfile for ktor-server
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/SampleBackendKtorApp-all.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

