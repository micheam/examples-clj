# ring-json

This is a simple json web api sample with [ring-json](https://github.com/ring-clojure/ring-json).

## Usage

start server:

```sh
$ clj -m greeter
```

post message:

```sh
$ curl --dump-header - \
    -H 'Content-Type:application/json' \
    -d '{"user":"micheam"}' \
    'http://localhost:3000'

HTTP/1.1 200 OK
Date: Wed, 24 Jul 2019 15:06:08 GMT
Content-Type: application/json;charset=utf-8
Content-Length: 28
Server: Jetty(9.4.12.v20180830)

{"message":"Hello, micheam"}
```

## packaging

### uberjar

build uberjar:

```sh
$ clj -A:uberjar
```
run it:

```sh
$ java -jar target/ring-json-1.0.0-SNAPSHOT-standalone.jar
```

### native-image

(require GraalVm and native-image)

build native-image:

```sh
$ clj -A:native-image
```

run it:

```sh
$ ./greeter
```

## Requirements

- clojure 1.10.1 or later
- graalvm 19.1.1 or later (optional)

## License
MIT

## Author

michito maeda <michito.maeda@gmail.com>
