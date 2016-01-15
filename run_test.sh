#!/bin/bash -e

run_test() {
    echo "Building RedPen"
    mvn clean install

    echo "Running application"
    cd redpen-cli/target;
    tar zxvf redpen-cli-*-assembled.tar.gz;
    cd redpen-cli-*
    bin/redpen -c conf/redpen-conf-en.xml -l 100 sample-doc/en/sampledoc-en.txt
    cd ../../..

    echo "Running sample server"
    cd redpen-server
    java -jar target/redpen-server.war &
    if [ -z "$(pgrep redpen)" ]
    then
        echo "RedPen server is nunning as expected ..."
        mvn test

        echo "Killing sample server"
        pgrep -f redpen | xargs kill
    else
        echo "RedPen server failed to start ..."
        exit 1
    fi

}

run_test "$@"
echo "Succeeded to run tests"
exit 0
