#!/bin/bash

mvn clean install -DskipTests \
&& docker build --tag swsnetworkdebug --file DockerfileDebug . \
&& docker run -p 8080:8080 -p 5005:5005 swsnetworkdebug:latest