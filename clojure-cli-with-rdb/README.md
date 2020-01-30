# Todo manager
This is a sample cli-app with clojure.

```
Usage: todo [options] action

Options:
  -h, --help

Actions:
  list                  list all todos
  get id                get todo by `id`
  new title             create new todo with `title`
```

## Examples

Crearte new todo:

```sh
$ clj -m micheam.todo new "My Awesome todo"
#uuid "687a0209-a1fb-40b9-a916-50a5c38cf489"
```

Get Todo by id:

```sh
$ clj -m micheam.todo get 687a0209-a1fb-40b9-a916-50a5c38cf489
#:todo{:id #uuid "687a0209-a1fb-40b9-a916-50a5c38cf489", :title My Awesome todo, :created-at #inst "2020-01-30T12:56:33.560000000-00:00"}
```

List All Todos:

```sh
$ clj -m micheam.todo list
[#:todo{:id #uuid "cac69f1d-1471-44a0-aca3-b0c614092676", ... },
 #:todo{:id #uuid "d67f9ce2-8950-4763-85a3-0ee20c5b5ca9", ... }]
```

## License

Copyright © 2020 Michito Maeda <https://github.com/micheam>

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
