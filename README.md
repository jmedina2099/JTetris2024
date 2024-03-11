# JTetris2024

# For executing unit tests
mvn clean test

# For executing integration tests
mvn clean test-compile failsafe:integration-test

# For executing unit tests and integration tests
mnv clean verify

# For sonar
mvn clean verify sonar:sonar

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
