
FROM maven:3.9.6-eclipse-temurin-17 as builder

WORKDIR /app


COPY . .


RUN mvn clean package -DskipTests

# ----


FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app


COPY --from=builder /app/target/*.jar app.jar

EXPOSE 10000

ENTRYPOINT ["java", "-jar", "app.jar"]
