#!/bin/bash

mkdir -p ~/.local/bin
cd ~/.local/bin

GITHUB=https://raw.githubusercontent.com/SkylerAndersen/GloverMocksAndDocs/main

curl -fsSL -O ${GITHUB}/mock-detector/main.cpp

g++ -std=c++11 -o chord-detector main.cpp && rm -f ./main.cpp

xattr -dr com.apple.quarantine ./chord-detector
chmod +x ./chord-detector

echo 'export PATH="${PATH}:/Users/$USER/.local/bin"' >> ~/.zshrc
