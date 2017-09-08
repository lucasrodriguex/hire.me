#!/bin/bash

#clone repository
#git clone https://github.com/lucasrodriguex/hire.me.git
#cd hire.me

docker run -p 3306:3306 -d \
    --name mysql-dev \
    -e MYSQL_ROOT_PASSWORD=root \
    -e MYSQL_DATABASE=shorturl \
    -e MYSQL_USER=shorturl \
    -e MYSQL_PASSWORD=shorturl \
    mysql:5.6

#test and generate package
mvn clean package docker:build

#application on!
docker run -d \
    --name shorturl \
    --link mysql-dev:mysql \
    -p 8080:8080 \
    -e DATABASE_HOST=mysql \
    -e DATABASE_PORT=3306 \
    -e DATABASE_NAME=shorturl \
    -e DATABASE_USER=shorturl \
    -e DATABASE_PASSWORD=shorturl \
    shorturl/shorturl