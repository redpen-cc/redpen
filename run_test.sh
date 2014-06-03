#!/bin/bash

FAILED=0
VERSION=0.6

run_test() {
    echo "Building RedPen Version $VERSION"
    mvn install
    if [ $? -ne 0 ]; then
	echo "Error in the build..."
	FAILED=0
    fi

    echo "Running application"
    cd redpen-app/target;\
	tar zxvf redpen-app-$VERSION-assembled.tar.gz;\
	cd redpen-app-$VERSION;\
        bin/redpen -c conf/dv-conf-en.xml doc/txt/en/sampledoc-en.txt
}

[ ${#BASH_SOURCE[@]} = 1 ] && run_test "$@"
exit $FAILED
