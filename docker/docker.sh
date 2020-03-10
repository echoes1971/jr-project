#!/usr/bin/env bash

PRJ_HOME=`cd ..; pwd`
MYSQL_APP=java-rprj-mysql

mkdir -f $PRJ_HOME/data

# MySQL
MYSQL_EXISTS=`docker container ls -a | grep $MYSQL_APP`
#echo $MYSQL_EXISTS
if [ -n "$MYSQL_EXISTS" ]; then
 echo "* Container $MYSQL_APP exists"
 #docker container stop $MYSQL_APP
 #docker container rm $MYSQL_APP
 echo "Access mysql with: docker exec -it $MYSQL_APP mysql -pmysecret rproject"
 docker container start $MYSQL_APP
fi
if [ -z "$MYSQL_EXISTS" ]; then
 echo "* Creating container $MYSQL_APP"
 docker run -p 3306:3306 --name $MYSQL_APP \
  -v $PRJ_HOME/data:/var/lib/mysql \
  -v $PRJ_HOME/config/mysql:/etc/mysql/conf.d \
  -e MYSQL_ROOT_PASSWORD=mysecret \
  -d mysql:5.7
fi
echo "Initialize/reset DB with: docker exec -i $MYSQL_APP mysql -pmysecret < ../sql/db_rproject.sql"

#echo "Point your browser to: http://localhost:8080/"
read -p "Press any key to continue... " -n1 -s
echo

docker container stop $MYSQL_APP

echo "docker container rm $MYSQL_APP"
