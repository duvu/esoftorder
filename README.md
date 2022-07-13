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