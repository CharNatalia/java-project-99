FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace
COPY app/ .

RUN chmod +x gradlew
RUN ./gradlew --no-daemon clean bootJar
RUN find build/libs -name '*.jar' ! -name '*-plain.jar' -exec cp {} /workspace/app.jar \;

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /workspace/app.jar app.jar

CMD ["java", "-jar", "app.jar"]