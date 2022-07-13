###1. Development
To simplify development:
####1.1. Use docker to run mysql
```shell script
docker-compose -f src/main/docker/mysql.yml up -d
```

####1.2. Run whole project
```shell script
mvn spring-boot:run
```

####1.3 Test
Run all tests
```shell script
mvn test
```
Run specific test
```shell script
mvn test -Dtest=ReportResourceTests#summarySpecificTime
```

###2. Deployment
Build docker image
```shell script
mvn clean compile jib:dockerBuild

```
Use docker to deploy.
```shell script
docker-compose -f src/main/docker/app.yml up -d
```

---
###Run with specific profile
```shell script
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

###Build Docker
```shell script
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=me.duvu/esoftorder
```
```shell script
mvn compile jib:dockerBuild

```

###Run docker
```shell script
docker run -p 8081:8081 -t me.duvu/esoftorder
```

```shell script
docker-compose -f src/main/docker/app.yml up
```