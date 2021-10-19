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

wget --quiet \
    --output-document="~/bin/newlinechecker" \
    "https://github.com/ArloL/newlinechecker/releases/latest/download/newlinechecker-${platform}"

chmod +x "~/bin/newlinechecker"
