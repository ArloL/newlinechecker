#!/bin/sh

set -o errexit
set -o nounset
#set -o xtrace

OS="$(uname)"
if [ "${OS}" = "Linux" ]; then
  	platform=linux
elif [ "${OS}" = "Darwin" ]; then
  	platform=macos
else
	platform=windows
fi

cleanup() {
    currentExitCode=$?
    rm -f "./newlinechecker"
    exit ${currentExitCode}
}

trap cleanup INT TERM EXIT

wget --quiet \
    --output-document="./newlinechecker" \
    "https://github.com/ArloL/newlinechecker/releases/latest/download/newlinechecker-${platform}"

chmod +x "./newlinechecker"

"./newlinechecker" --version

"./newlinechecker" "$@"
