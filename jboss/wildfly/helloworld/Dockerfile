FROM adoptopenjdk:11-jdk-hotspot as BUILD
RUN curl -#L https://apache.claz.org/maven/maven-3/3.8.1/binaries/apache-maven-3.8.1-bin.tar.gz \
    | tar -C /opt/java -xzf -
ENV PATH=$PATH:/opt/java/apache-maven-3.8.1/bin
WORKDIR /app
COPY . ./
RUN mvn -e clean install

FROM jboss/wildfly:24.0.0.Final
RUN sed -i 's/127\.0\.0\.1/0.0.0.0/g' /opt/jboss/wildfly/standalone/configuration/standalone.xml
ENV PATH=$PATH:/opt/jboss/wildfly/bin
WORKDIR /opt/jboss/wildfly/standalone/deployments/
COPY --from=BUILD /app/target/webapp.war ./
