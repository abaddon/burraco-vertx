FROM openjdk:11-jre-slim

ENV VERTICLE_ORIGIN_PATH ./build/libs
ENV VERTICLE_FILE ReadModel-1.0-SNAPSHOT-fat.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

COPY $VERTICLE_ORIGIN_PATH/$VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE"]