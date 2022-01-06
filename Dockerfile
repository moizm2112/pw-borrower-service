FROM openjdk:11-jre-slim
COPY target/borrower-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9060
CMD ["java","-Dspring.profiles.active=dev","-jar","app.jar"]
