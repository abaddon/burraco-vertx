## Extend vert.x image                       (1)
#FROM vertx/vertx3
#
## Set the name of the verticle to deploy    (2)
#ENV VERTICLE_NAME com.abaddon83.vertx.burraco.engine.Starter
#ENV VERTICLE_FILE ./build/libs/Engine-1.0-SNAPSHOT-fat.jar
#
## Set the location of the verticles         (3)
#ENV VERTICLE_HOME /usr/verticles
#
#EXPOSE 8080
#
## Copy your verticle to the container       (4)
#COPY $VERTICLE_FILE $VERTICLE_HOME/
#
## Launch the verticle                       (5)
#WORKDIR $VERTICLE_HOME
#ENTRYPOINT ["sh", "-c"]
#CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]

FROM openjdk:14-slim

ENV VERTICLE_ORIGIN_PATH ./build/libs
ENV VERTICLE_FILE EventStore-1.0-SNAPSHOT-fat.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

#EXPOSE 8080

COPY $VERTICLE_ORIGIN_PATH/$VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE"]