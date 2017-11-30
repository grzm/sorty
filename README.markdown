# sorty

Simple, web-based, keyboard-driven classifier.

## Architecture

- Pedestal server
- Fulcro SPA front-end
- Postgres storage (though this should be pluggable)
- Session management with Redis?

## Affordances

- Devcards for UI development
- Emacs-based repls for Clojure and ClojureScript
- Easy versioning, building, packaging
- use boot or leiningen where it's best-served
- Suitable for deployment to AWS
- Dockerized?

## Development


### Client

To start figwheel from the command line, use the `fw` leiningen alias:

```bash
lein fw
```

To start figwheel from Cider, open `src/dev/user.clj` in a buffer and
run `cider-jack-in-clojurescript`. This will start one JVM repl and one cljs repl.
From the JVM repl, run `(user/start-figwheel)`.



### Server
Starting server in dev

```bash
lein run-dev
```

```bash
curl http://localhost:8765/hallo
```

Or, using [`reloaded.repl`](https://github.com/weavejester/reloaded.repl):

```clojure
(require
  '[com.grzm.sorty.server.reloaded :as reloaded]
  '[reloaded.repl])
(reloaded/init!)
(reloaded.repl/go)
```

## Testing

To test the server backend

```bash
lein test
```
