

### Build and deploy to docker
```shell
mvn clean package -DskipTests
docker-compose build spring-boot-app
docker-compose up -d spring-boot-app
mvn gatling:test
```

### Build and deploy to docker
```shell
mvn clean package -DskipTests
docker-compose build spring-boot-app
docker-compose up -d spring-boot-app
```
```shell
docker-compose build spring-boot-app
```
```shell
docker-compose up -d spring-boot-app
```
### Run stress test
```shell
mvn gatling:test
```


```shell
docker ps
```
```shell
docker exec -it <container-username/name> /bin/bash
```
```shell
docker attach <container-username/name>
```





