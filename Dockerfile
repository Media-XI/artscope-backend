FROM openjdk:11

ARG JAVA_OPTS

ENV JAVA_OPTS=$JAVA_OPTS

COPY build/libs/art-be-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT exec java $JAVA_OPTS -jar -Dspring.profiles.active=prod app.jar