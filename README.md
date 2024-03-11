#########################################################
# JTetris2024
# - author: Jorge Alberto Medina Rosas
#########################################################
# 1) Pre-requesites
#
# Having installed:
# - Java v17.0.6
# - Maven v3.9.6
# - Node v18.16.0
# - Npm v9.5.1
#########################################################
# 2) Requesites
#
# Install and run the following:
# - Kafka v2.13-3.7.0 using port 9092 (optional for tests using the embedded kafka)
# - MongoDB V6.0.14 using port 27017 (with database name: jtetris)
# - MySQL v.8.0.36 using port 3306 (with database name: jtetris)
# 
# 3) Compile and run the node-server app using port 4444 (included)
#########################################################

# Compilation ( java, angular )
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
npm start

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
docker run -p 8080:8080 -p 9990:9990 -p 4444:4444 -p 9083:9083 jtetris
