FROM ubuntu:20.04 as base
RUN  echo "5bda40601d244532ad67fc43c6d58717" >> /etc/machine-id

FROM adoptopenjdk:11-jre-hotspot as builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk:11-jre-hotspot
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
COPY --from=base /etc/machine-id /etc/

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]