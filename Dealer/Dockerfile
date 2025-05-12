FROM openjdk:21-slim

ENV VERTICLE_ORIGIN_PATH ./build/libs
ENV VERTICLE_FILE Dealer-1.0-SNAPSHOT-all.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

COPY $VERTICLE_ORIGIN_PATH/$VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE"]
