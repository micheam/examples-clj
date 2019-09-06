# todo-list

This is a sample implementation of Web-Application with clojure and ring,  
which provide manage your todo-list.

## Usage

Run the server directly:

    $ clj -m todo-list.web.main

Run the project's tests:

    $ clj -A:test:runner

## Endpoints

- `GET /todo`
- `POST /todo`
- `GET /todo/:id`
- `PATCH /todo/:id`
- `DELETE /todo/:id`

## Examples

Create new item:
```sh
curl -i -X POST \
  -H'Content-Type: application/json' \
  -d'{"title":"read clojure book: Programming Clojure"}' \
  http://localhost:3000/todo

# HTTP/1.1 201 Created
# Date: Fri, 06 Sep 2019 16:20:07 GMT
# Location: /todo/d05bcd56-3616-4f02-a8c3-279c08ae790b
# Content-Length: 0
# Server: Jetty(9.4.12.v20180830)
```

Show item:
```sh
curl -s http://localhost:3000/todo/d05bcd56-3616-4f02-a8c3-279c08ae790b | jq '.'

# {
#   "title": "read clojure book: Programming Clojure",
#   "description": null,
#   "done": false,
#   "id": "d05bcd56-3616-4f02-a8c3-279c08ae790b",
#   "created-at": "2019-09-07T01:20:07.363"
# }
```

Edit item:
```sh
curl -i -X PATCH \
  -d'{"descriptin":"https://www.amazon.co.jp/Programming-Clojure-Pragmatic-Programmers-Miller/dp/1680502468/ref=dp_ob_title_bk"}' \
  http://localhost:3000/todo/d05bcd56-3616-4f02-a8c3-279c08ae790b
 
# HTTP/1.1 200 OK
# Date: Fri, 06 Sep 2019 16:24:44 GMT
# Content-Length: 0
# Server: Jetty(9.4.12.v20180830)sh
```

Delete item:
```sh
curl -i -X DELETE \
  http://localhost:3000/todo/d05bcd56-3616-4f02-a8c3-279c08ae790b

# HTTP/1.1 200 OK
# Date: Fri, 06 Sep 2019 16:27:23 GMT
# Content-Length: 0
# Server: Jetty(9.4.12.v20180830)
```

