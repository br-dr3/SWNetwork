#!/bin/bash

mvn clean install -DskipTests && docker build -t swsnetwork . && docker run -p 8080:8080 -p 5005:5005 swsnetwork:latest