FROM amazoncorretto:21-alpine
RUN mkdir -p /opt/laa-crime-hardship/
WORKDIR /opt/laa-crime-hardship/
COPY ./build/libs/crime-hardship.jar /opt/laa-crime-hardship/app.jar
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup
USER 1001
EXPOSE 8289 8299
CMD java -jar app.jar
