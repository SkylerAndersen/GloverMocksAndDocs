#!/bin/bash

mkdir -p ~/.local/bin/temp
cd ~/.local/bin/temp

GITHUB=https://raw.githubusercontent.com/SkylerAndersen/GloverMocksAndDocs/main

curl -fsSL -O ${GITHUB}/HTTPRequests.java
curl -fsSL -O ${GITHUB}/MockHTTPServer.java
curl -fsSL -O ${GITHUB}/Main.java
curl -fsSL -O ${GITHUB}/Makefile
curl -fsSL -O ${GITHUB}/mock

make && cp ./mock ../mock && cp ./mock.jar ../mock.jar

cd .. && rm -rf ./temp

xattr -dr com.apple.quarantine ./mock
chmod +x ./mock

echo 'export PATH="${PATH}:/Users/$USER/.local/bin"' >> ~/.zshrc
