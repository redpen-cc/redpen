#!/bin/bash

VERSION=0.0.1-SNAPSHOT
RUN_SCRIPT="$(basename "${0}")"

usage() {
    cat <<_EOF_
Usage:
${RUN_SCRIPT} [-v validator-config-file] [-c character-config-file] [-i input-file]

Options:
-vSpecfy validation configuration file.
-cSpecfy character configuration file.
-iSpecify input file.
_EOF_
}

main() {
    [ $# -lt 1 ] && ( usage; exit 1 );
    while getopts "v:i:c:h" OPT; do
        case "${OPT}" in
            "v" )
                VALIDATOR_CONFIG_FILE=$OPTARG;;
            "c" )
                CHARACTER_CONFIG_FILE=$OPTARG;;
            "i" )
                INPUT_FILE=$OPTARG ;;
            * )
                usage
                exit 1
                ;;
        esac
    done
    java -jar lib/document-validator-${VERSION}.jar --conf ${VALIDATOR_CONFIG_FILE} --char ${CHARACTER_CONFIG_FILE} --input ${INPUT_FILE}
}

[ ${#BASH_SOURCE[@]} = 1 ] && main "$@"
