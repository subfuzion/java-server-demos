FROM maven:3.8-jdk-11 as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

# Important! Use OpenJDK 8u191 or later with container support enabled
FROM adoptopenjdk/openjdk11:alpine-jre
COPY --from=builder /app/target/helloworld-*.jar /helloworld.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/helloworld.jar"]
