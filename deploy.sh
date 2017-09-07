#!/bin/bash

#clone repository
#git clone https://github.com/lucasrodriguex/hire.me.git
#cd hire.me

docker run -p 3306:3306 --name mysql-dev -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=shorturl -e MYSQL_USER=root -e MYSQL_PASSWORD=root -d mysql:5.6
docker start mysql-dev

#test and generate package
mvn clean package docker:build

#application on!
docker run --name shorturl --link mysql-dev:mysql -p 8080:8080 -d shorturl/shorturl
docker start shorturl