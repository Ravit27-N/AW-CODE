FROM openjdk:8-jdk-alpine as build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw

# Clean spaces and CR characters for Windows OS
# RUN sed -i 's/\r$//' ./mvnw

# download the dependency if needed or if the pom file is changed
RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean install

# Production Stage for Spring boot application image
FROM openjdk:8-jre-alpine as production

# Set timezone
RUN apk update
RUN apk add --no-cache tzdata
RUN cp /usr/share/zoneinfo/Asia/Phnom_Penh /etc/localtime
RUN apk del tzdata

COPY --from=build /app/target/recruit-0.0.1-SNAPSHOT.jar ./recruit-0.0.1-SNAPSHOT.jar
COPY entrypoint.sh  ./entrypoint.sh
