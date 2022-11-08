FROM amazoncorretto:11

VOLUME /tmp

LABEL microservices="crypto"
LABEL description="Rest service for providing crypto statistic info"

COPY target/crypto-0.0.1-SNAPSHOT.jar /crypto-0.0.1-SNAPSHOT.jar
COPY target/classes/prices /tmp/prices

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "crypto-0.0.1-SNAPSHOT.jar"]