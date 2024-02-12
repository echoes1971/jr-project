#!/usr/bin/env bash

PRJ_HOME=`cd ..; pwd`
MYSQL_APP=java-rprj-db
MYSQL_DB=rproject
MYSQL_PASSWORD=mysecret

mkdir -p $PRJ_HOME/data_maria

# MySQL
MYSQL_EXISTS=`docker container ls -a | grep $MYSQL_APP`
#echo $MYSQL_EXISTS
if [ -n "$MYSQL_EXISTS" ]; then
 echo "* Container $MYSQL_APP exists"
 #docker container stop $MYSQL_APP
 #docker container rm $MYSQL_APP
 echo "Access mysql with: docker exec -it $MYSQL_APP mariadb -p$MYSQL_PASSWORD $MYSQL_DB"
 docker container start $MYSQL_APP
fi
if [ -z "$MYSQL_EXISTS" ]; then
 echo "* Creating container $MYSQL_APP"
 docker run -p 3306:3306 --name $MYSQL_APP \
  -v $PRJ_HOME/data_maria:/var/lib/mysql \
  -v $PRJ_HOME/config/mysql:/etc/mysql/conf.d \
  -v $PRJ_HOME/sql:/tmp/sql \
  -e MYSQL_ROOT_PASSWORD=mysecret \
  -d mariadb:latest

  # Create DB
  last_exit_code=1
  while [ $last_exit_code -eq 1 ]
  do
    docker exec -i $MYSQL_APP mariadb -p$MYSQL_PASSWORD -e "create database if not exists $MYSQL_DB;"
    last_exit_code=$?
    if [ $last_exit_code -ne 0 ]
    then
      echo -n "."
      sleep 5
    fi
  done

  # Import DB
  last_exit_code=1
  while [ $last_exit_code -eq 1 ]
  do
    #docker exec -i $MYSQL_APP ls -l /tmp/sql
    docker exec -i $MYSQL_APP mariadb -p$MYSQL_PASSWORD -e "source /tmp/sql/db_rproject.sql"
    last_exit_code=$?
    if [ $last_exit_code -ne 0 ]
    then
      echo -n "."
      sleep 5
    fi
  done
fi
echo "Access mysql with: docker exec -it $MYSQL_APP mariadb -p$MYSQL_PASSWORD $MYSQL_DB"
echo "Interact with the containers with:"
echo " docker exec -it $MYSQL_APP bash"

#echo "Point your browser to: http://localhost:8080/"
read -p "Press any key to continue... " -n1 -s
echo

docker container stop $MYSQL_APP

echo "docker container rm $MYSQL_APP"
