# JTetris2024

# For generating wars
mvn package -DskipTests -Pjboss

# For generating a docker image
docker image build -t jtetris .

docker run -p 8080:8080 -p 9990:9990 -p 4444:4444 -p 9083:9083 jtetris

# For deployment
mvn package -DskipTests -Pjboss