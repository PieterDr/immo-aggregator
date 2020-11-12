FROM openjdk:15-jdk-alpine
RUN addgroup -S immo-aggregator && adduser -S immo-aggregator -G immo-aggregator
USER immo-aggregator:immo-aggregator
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
