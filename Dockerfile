FROM eclipse-temurin:17-jdk

WORKDIR /app

# Set Spring active profile
ARG ENVIRONMENT
ENV SPRING_PROFILES_ACTIVE=$ENVIRONMENT
ENV JAVA_DEBUG_OPT="-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:4851"
ARG PORT=80
ENV PORT ${PORT}
ARG JAR=build/libs/sis-batch-0.0.1-SNAPSHOT.jar
ADD $JAR sis-batch.jar
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

CMD ["sh", "-c", "java $JAVA_DEBUG_OPT -jar sis-batch.jar -Xms256M -Xmx1G --server.port=${PORT}"]