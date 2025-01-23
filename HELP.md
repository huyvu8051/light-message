### JDK21 maven

### Build and deploy to docker
```shell
mvn clean package -DskipTests
docker-compose build sba
docker-compose up -d sba
```

### Run stress test
```shell
k6 run ./k6/send_message.js
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


### To build the image, you can run the spring-boot:build-image goal with the native profile active:
```shell
mvn -Pnative spring-boot:build-image
```

### Option if not config in Native Compiler maven plugin

### Use SDKMAN to manage JDK versions
### Use graalvm jdk version of nik.
```shell
sdk install java 22.3.r17-nik
sdk use java 22.3.r17-nik
```

### Run native file
```shell
target/light-message -Xmx64m --server.tomcat.threads.max=4 
```




### For Docker Apple Silicon ARM64, rebuild maven build image result for ARM64 architect
```shell
docker buildx build --platform linux/arm64 -t light-message:0.0.1-SNAPSHOT . 
```
docker buildx build --platform linux/arm64 -t docker.io/library/light-message:0.0.1-SNAPSHOT .
docker run --rm -p 8080:8080 docker.io/library/light-message:0.0.1-SNAPSHOT --D
