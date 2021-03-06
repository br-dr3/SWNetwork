# Star Wars Social Network

## How to Run Project
After cloned, you should execute `sh/runDev.sh` (if you are using a Shell based SO), you may want to give permission 
to it by running `chmod 755 sh/runDev.sh`. If you are using other SO, just execute the following: 

```
mvn clean install -DskipTests
docker build -t swsnetwork .
docker run -p 8080:8080 -p 5005:5005 swsnetwork:latest
```

You should be capable of use `localhost:8080/swagger` to see available endpoints.

## How to Run Project in Debug mode
After cloned, you should execute `sh/runDevWithDebugger.sh` (if you are using a Shell based SO), you may want to give permission
to it by running `chmod 755 sh/runDevWithDebugger.sh`. If you are using other SO, just execute the following:

```
mvn clean install -DskipTests
docker build --tag swsnetworkdebug --file DockerfileDebug .
docker run -p 8080:8080 -p 5005:5005 swsnetworkdebug:latest
```

You should be capable of use `localhost:8080/swagger` to see available endpoints as well. 
You **have to** attach debugger to `localhost:5005`