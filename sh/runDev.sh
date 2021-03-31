#!/bin/bash

mvn clean install && docker build -t swsnetwork . && docker run -p 8080:8080 swsnetwork:latest