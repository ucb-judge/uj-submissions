#FROM eclipse-temurin:11-jdk-alpine
FROM --platform=linux/x86_64 eclipse-temurin:11-jdk
#FROM arm64v8/eclipse-temurin:11-jdk

EXPOSE 8084

VOLUME /tmp

# Server
ENV PORT="PORT"

# Logstash
ENV LOGSTASH_SERVER_URI="LOGSTASH_SERVER_URI"

# Config server
ENV CONFIG_SERVER_URI="CONFIG_SERVER_URI"
ENV CONFIG_SERVER_PROFILE="CONFIG_SERVER_PROFILE"

# Database
ENV POSTGRES_USERNAME="POSTGRES_USERNAME"
ENV POSTGRES_PASSWORD="POSTGRES_PASSWORD"
ENV POSTGRES_URL="POSTGRES_URL"

#Zipkin
ENV ZIPKIN_SERVER_URI="ZIPKIN_SERVER_URI"

# Eureka
ENV EUREKA_SERVER_URI="EUREKA_SERVER_URI"

# Keycloak
ENV KEYCLOAK_SERVER_URI="KEYCLOAK_SERVER_URI"
ENV KEYCLOAK_CLIENT_SECRET="KEYCLOAK_CLIENT_SECRET"
ENV KEYCLOAK_REALM="KEYCLOAK_REALM"
ENV KEYCLOAK_CLIENT_ID="KEYCLOAK_CLIENT_ID"

# RabbitMQ
ENV RABBITMQ_HOST="RABBITMQ_HOST"
ENV RABBITMQ_PORT="RABBITMQ_PORT"
ENV RABBITMQ_USERNAME="RABBITMQ_USERNAME"
ENV RABBITMQ_PASSWORD="RABBITMQ_PASSWORD"
ENV RABBITMQ_VIRTUAL_HOST="RABBITMQ_VIRTUAL_HOST"

# Minio
ENV MINIO_SERVER_URI="MINIO_URL"
ENV MINIO_ACCESS_KEY="MINIO_ACCESS_KEY"
ENV MINIO_SECRET_KEY="MINIO_SECRET_KEY"

ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","ucb.judge.ujsubmissions.UjSubmissionsApplicationKt"]