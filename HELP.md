

### Build and deploy to docker
```shell
mvn clean package -DskipTests
docker-compose build sba
docker-compose up -d sba
mvn gatling:test
```

### Build and deploy to docker
```shell
mvn clean package -DskipTests
docker-compose build sba
docker-compose up -d sba
```
```shell
docker-compose build sba
```
```shell
docker-compose up -d sba
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





