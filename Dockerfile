FROM balenalib/raspberrypi3-alpine-openjdk
ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} /app/
ENTRYPOINT ["java","-jar","app/app.jar"]
