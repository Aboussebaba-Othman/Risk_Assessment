# Stage 1: Extract the Spring Boot layered jar
FROM eclipse-temurin:17-jre-alpine AS builder
WORKDIR /builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# Stage 2: Create the optimized runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy layers
COPY --from=builder /builder/dependencies/ ./
COPY --from=builder /builder/spring-boot-loader/ ./
COPY --from=builder /builder/snapshot-dependencies/ ./
COPY --from=builder /builder/application/ ./

EXPOSE 9000
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
