FROM gradle:8.12-jdk23 as builder
USER root
COPY . .
RUN gradle -x test --no-daemon build

FROM openjdk:23-jdk
ENV JAVA_TOOL_OPTIONS -XX:+ExitOnOutOfMemoryError
COPY --from=builder /home/gradle/build/libs/fint-kontroll-messenger-*.jar /data/app.jar
CMD ["/data/app.jar"]
