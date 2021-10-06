FROM adoptopenjdk:16.0.1_9-jre-hotspot
RUN apt update && apt install netcat
WORKDIR /app
COPY server.jar .
CMD java -jar server.jar
HEALTHCHECK --interval=60s CMD echo '{"type":"ReqWelcome"}' | nc -q 5 localhost 51015