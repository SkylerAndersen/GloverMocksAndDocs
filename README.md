# GloverMocksAndDocs Official Release v1.0

This is the first official release. This tool is available on limited platforms.

Currently Available On:
 - MacOS (Apple Silicon)

Functions without internet. Install the CLI in one command. Run the following in your terminal:
```
bash -c "$(curl -fsSL https://raw.githubusercontent.com/SkylerAndersen/GloverMocksAndDocs/main/install.sh)" && source ~/.zshrc
```

To run it, run `mock` or `mock -h` to bring you to the help screen.

# How is this similar to production?

Running `mock --fake-get-request /gloves` will mock up data and return in a format identical to the final Glover API. This can be used to understand what you need to parse.

Running `mock --start` will start up an http server on either port 80, or port 8080. If your API can correctly make get requests to the mock API, then it will be able to do so on the final API.

# How do I shut the test api down once launched?

Run `mock --kill` or `mock -k`. If you want to be certain, you can search for "MockHTTPServer" in your process table with `ps` and `grep`, but `mock --kill` has multiple ways of killing the process, so unless `kill -9` is needed, the api will go down will `mock --kill` just fine.

# How might I query the API

From the command line, you can use `curl http://localhost:80/gloves`. In C++, that task is left up to the reader. If you manage it, then your C++ project will also be able to query the production API, and you are all set.
