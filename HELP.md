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


### LOCAL GRAALVM REQUIRE: build native image in local machine
```shell
mvn -Pnative native:compile
```

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



### DOCKER BUILD 

# DOCKER REQUIRE: To build the image, you can run the spring-boot:build-image goal with the native profile active:
#### Add this build argument in arm64 paketobuildpacks/builder-jammy-java-tiny:latest
```shell

mvn -Pnative spring-boot:build-image
```

## Run docker container
### --rm for remove on stop
```shell
docker network create my_network
docker network connect my_network postgres
docker run --rm --network my_network -p 8080:8080 docker.io/library/light-message:0.0.1-SNAPSHOT -Xmx64m --server.tomcat.threads.max=4  --spring.datasource.url=jdbc:postgresql://postgres:5432/mydatabase
```

### Option if not config in Native Compiler maven plugin
```shell
mvn -Pnative spring-boot:build-image -Denv.BP_NATIVE_IMAGE_BUILD_ARGUMENTS="--vm.DmaxHeapSize=8G"
```



```shell
k6 run -e BASE_URL=https://light-message.onrender.com ./k6/send_message.js
```