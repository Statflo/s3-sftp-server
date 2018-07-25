# Builder Dockerfile
FROM maven:3-jdk-8-alpine AS BUILDER
MAINTAINER Statflo Inc. <development@statflo.com>
ENV APP_HOME=/usr/share/statflo
RUN mkdir -p $APP_HOME
WORKDIR $APP_HOME

COPY ./pom.xml ./pom.xml

# Build executable code
COPY ./src ./src
RUN mvn clean install -DskipTests=true

# Runtime Dockerfile
FROM openjdk:jre-alpine
MAINTAINER Statflo Inc. <development@statflo.com>
ENV APP_HOME=/usr/share/statflo
RUN mkdir -p $APP_HOME
WORKDIR $APP_HOME

EXPOSE 2222 8090
ENTRYPOINT ["/usr/bin/java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "service.jar"]
COPY --from=BUILDER $APP_HOME/target/service.jar ./service.jar