# Star Wars Social Network

## How to Run Project
After cloned, you should execute sh/runDev.sh (if you are using a Shell based SO),
or just execute

```
mvn clean install
docker build -t swsnetwork .
docker run -p 8080:8080 swsnetwork:latest
```

You should be capable of use `localhost:8080/swagger` to see available endpoints