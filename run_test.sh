#!/bin/bash

VERSION=1.1

run_test() {
    echo "Building RedPen Version $VERSION"
    mvn clean install
    if [ $? -ne 0 ]; then
        echo "Error in the build..."
        exit 1
    fi

    echo "Running application"
    cd redpen-cli/target;
    tar zxvf redpen-cli-$VERSION-assembled.tar.gz;
    cd redpen-cli-$VERSION
    bin/redpen -c conf/redpen-conf-en.xml -l 100 sample-doc/en/sampledoc-en.txt
    if [ $? -ne 0 ]; then
        echo "Error runnig application..."
        exit 1
    fi
    cd ../../..

    echo "Running sample server"
    java -jar redpen-server/target/redpen-server.war &
    if [ -z "$(pgrep redpen)" ]
    then
        echo "RedPen server is nunning as expected ..."
        sleep 5
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
