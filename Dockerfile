FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8082
RUN mkdir -p /app/
RUN mkdir -p /app/logs/
ADD target/challenge-0.0.1.jar /app/app.jar
CMD ["/usr/bin/java", "-jar", "-Dspring.profiles.active=default", "/app/app.jar"]