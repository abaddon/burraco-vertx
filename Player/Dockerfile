FROM base-builder AS builder

FROM openjdk:21-slim

ENV VERTICLE_ORIGIN_PATH=/app/Player/build/libs/Player-all.jar
ENV VERTICLE_FILE=player-service.jar

# Set the location of the verticles
ENV VERTICLE_HOME=/usr/verticles

EXPOSE 8081

COPY --from=builder $VERTICLE_ORIGIN_PATH $VERTICLE_HOME/$VERTICLE_FILE


# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE"]
#ENTRYPOINT ["java", "-jar", "$VERTICLE_FILE"]



#FROM gradle:8.13-jdk21-alpine as builder
#COPY --chown=gradle:gradle ../ /home/gradle/src
#WORKDIR /home/gradle/src
#RUN ./gradlew :Game:shadowJar
#
#FROM openjdk:21-slim
#EXPOSE 8080
#ENV VERTICLE_FILE Game-1.0-SNAPSHOT-all.jar
#ENV VERTICLE_HOME /usr/verticles
#
#COPY --from=builder /home/gradle/src/Game/build/libs/$VERTICLE_FILE $VERTICLE_HOME/
#WORKDIR $VERTICLE_HOME
#ENTRYPOINT ["sh", "-c"]
#CMD ["exec java -jar $VERTICLE_FILE"]