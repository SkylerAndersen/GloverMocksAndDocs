# MOCK DETECTOR SECTION: GloverMocksAndDocs Official Release v1.0

This is the first official release. This tool is available on limited platforms.

Currently Available On:
 - MacOS (Apple Silicon)

Functions without internet. Install the CLI in one command. Run the following in your terminal:
```
bash -c "$(curl -fsSL https://raw.githubusercontent.com/SkylerAndersen/GloverMocksAndDocs/main/mock-detector/install.sh)" && source ~/.zshrc
```

To run it, run `chord-detector` or `chord-detector -h` to bring you to the help screen.

# How is this similar to production?

Running `chord-detector --IP http://localhost:80 -Latest-Index 0` will mock up data and return in a format identical to the final chord detector. This can be used to understand what you need to parse.
