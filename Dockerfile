# -----------------------------
# Fase 1: build do JAR com Maven
# -----------------------------
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# -----------------------------
# Fase 2: imagem final, leve
# -----------------------------
FROM openjdk:21-jdk-slim

WORKDIR /app
COPY --from=build /build/target/worklyze.jar /app/worklyze.jar

# Locale e timezone (mantidos conforme sua config)
RUN apt-get update && apt-get install -y locales && \
    sed -i -e 's/# pt_BR.UTF-8 UTF-8/pt_BR.UTF-8 UTF-8/' /etc/locale.gen && \
    echo 'LANG="pt_BR.UTF-8"' > /etc/default/locale && \
    dpkg-reconfigure --frontend=noninteractive locales && \
    update-locale LANG=pt_BR.UTF-8 && \
    apt-get clean

ENV LANG=pt_BR.UTF-8
ENV LANGUAGE=pt_BR.UTF-8
ENV LC_ALL=pt_BR.UTF-8

ENV TZ=America/Argentina/San_Luis
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 8080
CMD ["java", "-jar", "worklyze.jar"]