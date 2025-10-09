FROM base-builder AS builder

FROM openjdk:21-slim

ENV VERTICLE_ORIGIN_PATH=/app/Dealer/build/libs/Dealer-all.jar
ENV VERTICLE_FILE=dealer-service.jar

# Set the location of the verticles
ENV VERTICLE_HOME=/usr/verticles

COPY --from=builder $VERTICLE_ORIGIN_PATH $VERTICLE_HOME/$VERTICLE_FILE

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE"]
#ENTRYPOINT ["java", "-jar", "$VERTICLE_FILE"]