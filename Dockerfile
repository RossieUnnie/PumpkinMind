FROM gradle:latest AS BUILD
WORKDIR /home/user/otocyon-knowledge-base

COPY . .
RUN gradle build

FROM amazoncorretto:17
WORKDIR /home/user/otocyon-knowledge-base
ENV JAR_NAME=otocyon-knowledge-base-0.0.1-SNAPSHOT.jar
ENV JAR_HOME=/home/user/otocyon-knowledge-base
COPY --from=BUILD $JAR_HOME .
EXPOSE 8080
ENTRYPOINT exec java -jar $JAR_HOME/build/libs/$JAR_NAME

