#!/bin/bash

host="0.0.0.0"

mysqlUser="root"
mysqlPassword="root"
mysqlContainerName="iddd-mysql"
mysqlPort="3306"

rabbitmqNodeName="$(hostname)"
rabbitmqContainerName="iddd-rabbitmq"
rabbitmqManagementHttpPort="8080"

containers[0]="${mysqlContainerName}"
containers[1]="${rabbitmqContainerName}"

function start() {
    echo "Starting MySQL Server container..."
    docker rm -f "${mysqlContainerName}"
    docker run --name "${mysqlContainerName}" -p "${mysqlPort}":3306 -e MYSQL_ROOT_PASSWORD="${mysqlPassword}" -d mysql

    echo "Waiting for MySQL Server to be up and running..."
    waitForContainer "${mysqlContainerName}" "mysqld: ready for connections."

    local testSqlFiles="$(find $(pwd) -name *.sql | grep -i "test")"
    local sqlFiles="$(find $(pwd) -name *.sql | grep -vi "test")"
    if which mysql > /dev/null; then
        for sql in ${testSqlFiles}; do
            echo "Importing [${sql}]"
            $(mysql --host="${host}"  --port=3306 --protocol=TCP --user="${mysqlUser}" --password="${mysqlPassword}" < ${sql})
        done
        for sql in ${sqlFiles}; do
            echo "Importing [${sql}]"
            $(mysql --host="${host}" --port=3306 --protocol=TCP --user="${mysqlUser}" --password="${mysqlPassword}" < ${sql})
        done
    else
        echo -e
        echo "!! mysql command not found"
        echo "!! You need to import the following SQL files into MySQL Server, yourself:"
        for sql in ${testSqlFiles}; do
            echo ${sql}
        done
        for sql in ${sqlFiles}; do
            echo ${sql}
        done
        echo -e
        echo "You can find MySQL Server on [localhost] port [${mysqlPort}]"
        echo -e
        read -rsp "Press any key to continue..."
        echo -e
    fi

    echo "Starting RabbitMQ container..."
    docker rm -f "${rabbitmqContainerName}"
    docker run --name "${rabbitmqContainerName}" -p 5672:5672 -p "${rabbitmqManagementHttpPort}":15672 -e RABBITMQ_NODENAME="${rabbitmqNodeName}" -d rabbitmq:3-management
    echo "Waiting for RabbitMQ to be up and running..."
    waitForContainer "${rabbitmqContainerName}" "Server startup complete;"

    echo -e
    echo "RabbitMQ Management available at [http://localhost:${rabbitmqManagementHttpPort}]"
    echo "(Login with user/pass of [guest/guest])"
}

function status() {
    docker ps -a | head -1
    for name in ${containers[@]}; do
        docker ps -a | grep "${name}"
    done
}

function stop() {
    for name in ${containers[@]}; do
        echo "Stopping container [${name}]..."
        docker stop "${name}" > /dev/null 2>&1
    done
}

function usage() {
    echo "Usage: $(basename $0) <command> [<args>]"
    echo -e
    echo "Available commands:"
    echo "  start          Start external dependencies for this project in Docker containers"
    echo "                 (incl. MySQL Server and RabbitMQ)"
    echo "  stop           Stop the Docker containers"
    echo -e
}

function requires() {
    local command=$1
    which ${command} > /dev/null 2>&1 || { echo "!! This script requires [${command}] to be installed" && exit 1; }
}

function waitForContainer() {
    local containerName="$1"
    local logMsg="$2"
    until docker logs ${containerName} 2>&1 | grep "${logMsg}" > /dev/null; do
        sleep 1s
    done
}


requires 'docker'

command=$1; shift
case "${command}" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    status)
        status
        ;;
    *)
        usage
        exit 1
       ;;
esac
