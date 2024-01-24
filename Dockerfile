FROM openjdk:17
ADD /target/registration_bot-0.0.1-SNAPSHOT.jar backend.jar
LABEL authors="yuriy"

ENTRYPOINT ["java", "-jar", "backend.jar"]