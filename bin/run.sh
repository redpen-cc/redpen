#!/bin/bash

RUN_SCRIPT="$(basename "${0}")"

usage() {
    cat <<_EOF_
Usage:
${RUN_SCRIPT} [-c config-file] [-i input-file]

Options:
-cSpecfy configuration file.
-iSpecify input file.
_EOF_
}

main() {
    [ $# -lt 1 ] && ( usage; exit 1 );

    # See how we're called.
    AUTO_INSTALL="no"
    BUILD_ALL="no"
    BUILD_JUBATUS="no"
    BUILD_PKGS="no"
    UNINSTALL_MODE="no"
    CLEAN_MODE="no"
    while getopts "c:i:h" OPT; do
        case "${OPT}" in
            "c" )
                CONFIG_FILE=$OPTARG;;
            "i" )
                INPUT_FILE=$OPTARG ;;
            * )
                usage
                exit 1
                ;;
        esac
    done
    java -jar lib/document-validator-0.0.1-SNAPSHOT.jar --conf ${CONFIG_FILE} --input ${INPUT_FILE}
}

[ ${#BASH_SOURCE[@]} = 1 ] && main "$@"


