#########################################################
# JTetris2024
# - author: Jorge Alberto Medina Rosas
#########################################################
# 1) Pre-requisites
# Having installed:
# - Java: v7.0.6
# - Maven: v3.9.6
# - Node: v18.16.0
# - Npm: v9.5.1
# - Angular CLI: v16.0.2
#########################################################
# 2) Requisites
# Install and run the following:
# - Kafka: v2.13-3.7.0 using port 9092 (optional for tests using the embedded kafka)
# - MongoDB: v6.0.14 using port 27017 (with database name: jtetris)
# - MySQL: v8.0.36 using port 3306 (with database name: jtetris)
#########################################################
# 3) Compile and run the node-server app using port 4444 (included)
#########################################################

# Compilation ( Java and Typescript )
mvn clean compile

# For executing unit tests
mvn clean test

# For executing integration tests
mvn clean test-compile failsafe:integration-test

# For executing unit tests and integration tests
mnv clean verify

# For sonar
mvn clean verify sonar:sonar

# For running the node-server app using port 4444 (socket.io communication with angular)
cd ./node-server

./run.sh

# For running the front-end with npm
cd ./frontend

./run.sh

# For generating wars for spring-boot
mvn clean package -DskipTests -Ptomcat

# For running with spring-boot
mvn clean spring-boot:run -DskipTests -Ptomcat

# For generating wars for Wildfly Preview v26.1.3.Final
mvn clean package -DskipTests -Pjboss

# For deployment in Wildfly Preview v26.1.3.Final
mvn clean wildfly:deploy -DskipTests -Pjboss

# For generating a docker image
docker image build -t jtetris .

# For running the docker image
docker run -p 8080:8080 -p 9990:9990 -p 4444:4444 -p 9083:9083 -p 27017:27017 jtetris

# For clean
mvn clean

#########################################################
# ENTRY URL
#########################################################
http://localhost:8080/api
#########################################################
